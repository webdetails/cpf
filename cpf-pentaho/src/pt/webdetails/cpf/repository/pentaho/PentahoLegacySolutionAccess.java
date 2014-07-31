/*!
* Copyright 2002 - 2014 Webdetails, a Pentaho company. All rights reserved.
*
* This software was developed by Webdetails and is provided under the terms
* of the Mozilla Public License, Version 2.0, or any later version. You may not use
* this file except in compliance with the license. If you need a copy of the license,
* please go to http://mozilla.org/MPL/2.0/. The Initial Developer is Webdetails.
*
* Software distributed under the Mozilla Public License is distributed on an "AS IS"
* basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. Please refer to
* the license for the specific language governing your rights and limitations.
*/

package pt.webdetails.cpf.repository.pentaho;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.platform.api.engine.IAclSolutionFile;
import org.pentaho.platform.api.engine.IContentGeneratorInfo;
import org.pentaho.platform.api.engine.IFileFilter;
import org.pentaho.platform.api.engine.IPentahoAclEntry;
import org.pentaho.platform.api.engine.IPentahoSession;
import org.pentaho.platform.api.engine.IPluginManager;
import org.pentaho.platform.api.engine.ISolutionFile;
import org.pentaho.platform.api.engine.PentahoAccessControlException;
import org.pentaho.platform.api.repository.ISolutionRepository;
import org.pentaho.platform.api.repository.ISolutionRepositoryService;
import org.pentaho.platform.engine.core.system.PentahoSystem;
import org.pentaho.platform.engine.security.SecurityHelper;

import pt.webdetails.cpf.repository.api.FileAccess;
import pt.webdetails.cpf.repository.api.IBasicFile;
import pt.webdetails.cpf.repository.api.IBasicFileFilter;
import pt.webdetails.cpf.repository.api.IUserContentAccess;
import pt.webdetails.cpf.repository.util.RepositoryHelper;

@SuppressWarnings( "deprecation" )
public class PentahoLegacySolutionAccess implements IUserContentAccess {

  private static Log logger = LogFactory.getLog( PentahoLegacySolutionAccess.class );

  private static String DEFAULT_PATH = "/";
  private static final String CPK_CONTENT_GENERATOR_CLASS_NAME = "pt.webdetails.cpk.CpkContentGenerator";

  protected ISolutionRepository repository;
  protected ISolutionRepositoryService repositoryService;
  private String basePath;
  private IPentahoSession userSession;

  public PentahoLegacySolutionAccess( String basePath, IPentahoSession session ) {
    this.basePath = StringUtils.isEmpty( basePath ) ? DEFAULT_PATH : basePath;
    this.repository = initSolutionRepository( session );
    this.repositoryService = initRepositoryService( session );
    this.userSession = session;
    ensureBasePathExists();
  }

  protected ISolutionRepository initSolutionRepository( IPentahoSession session ) {
    return PentahoSystem.get( ISolutionRepository.class, session );
  }

  protected ISolutionRepositoryService initRepositoryService( IPentahoSession session ) {
    return PentahoSystem.get( ISolutionRepositoryService.class, session );
  }

  @Override
  public boolean saveFile( String path, InputStream contents ) {
    try {
      path = getPath( path );
      int status =
          getRepository().publish(
              FilenameUtils.separatorsToUnix( PentahoSystem.getApplicationContext().getSolutionPath( "" ) ),
              FilenameUtils.getFullPath( path ), FilenameUtils.getName( path ), IOUtils.toByteArray( contents ), true );
      switch ( status ) {
        case ISolutionRepository.FILE_ADD_SUCCESSFUL:
          return true;
        case ISolutionRepository.FILE_ADD_FAILED:
        case ISolutionRepository.FILE_ADD_INVALID_PUBLISH_PASSWORD:
        case ISolutionRepository.FILE_ADD_INVALID_USER_CREDENTIALS:
        default:
          return false;
      }
    } catch ( PentahoAccessControlException e ) {
      logger.error( e );
      return false;
    } catch ( IOException e ) {
      logger.error( e );
      return false;
    }
  }

  protected String getPath( String path ) {
    if ( path != null ) {
      return RepositoryHelper.normalize( RepositoryHelper.appendPath( basePath, path ) );
    } else {
      return basePath;
    }
  }

  protected ISolutionRepository getRepository() {
    return repository;
  }


  protected ISolutionRepositoryService getRepositoryService() {
    return repositoryService;
  }

  protected ISolutionFile getRepositoryFile( String path ) {
    return getRepository().getSolutionFile( getPath( path ), IPentahoAclEntry.PERM_EXECUTE );
  }

  @Override
  public boolean copyFile( String pathFrom, String pathTo ) {
    try {
      saveFile( pathTo, getFileInputStream( pathFrom ) );
      return true;
    } catch ( IOException e ) {
      logger.error( e );
      return false;
    }
  }

  @Override
  public boolean deleteFile( String pathFrom ) {
    return getRepository().removeSolutionFile( getPath( pathFrom ) );
  }

  @Override
  public boolean createFolder( String path ) { // TODO: shouldn't this be recursive?
    path = StringUtils.chomp( path, "/" ); // strip trailing / if there
    String folderName = FilenameUtils.getBaseName( getPath( path ) );
    String folderPath = getPath( path ).substring( 0, StringUtils.lastIndexOf( getPath( path ), folderName ) );

    try {
      return getRepositoryService().createFolder( userSession, "", folderPath, folderName, "" );
    } catch ( IOException ex ) {
      logger.error( ex );
      return false;
    }
  }

  protected void ensureBasePathExists() {
    if ( !fileExists( null ) ) {
      createFolder( "" );
    }
  }


  @Override
  public InputStream getFileInputStream( String path ) throws IOException {
    return getRepository().getResourceInputStream( getPath( path ), true, IPentahoAclEntry.PERM_EXECUTE );
  }

  @Override
  public boolean fileExists( String path ) {
    return getRepository().resourceExists( getPath( path ), IPentahoAclEntry.PERM_EXECUTE );
  }

  @Override
  public long getLastModified( String path ) {
    ISolutionFile solutionFile = getRepositoryFile( path );
    return ( solutionFile == null ) ? 0L : solutionFile.getLastModified();
  }

  public List<IBasicFile> listFiles( String path, final IBasicFileFilter filter ) {
    return listFiles( path, filter, -1 );
  }

  public List<IBasicFile> listFiles( String path, final IBasicFileFilter filter, int maxDepth ) {
    return listFiles( path, filter, maxDepth, false );
  }

  public List<IBasicFile> listFiles( String path, final IBasicFileFilter filter, int maxDepth, boolean includeDirs ) {
    return listFiles( path, filter, maxDepth, includeDirs, false );
  }

  public List<IBasicFile> listFiles( String path, final IBasicFileFilter filter, int maxDepth, boolean includeDirs,
      boolean showHiddenFilesAndFolders ) {

    path = getPath( path );
    IFileFilter fileFilter = null;

    if ( filter != null ) {

      fileFilter = new IFileFilter() {
        @Override
        public boolean accept( ISolutionFile isf ) {
          return filter.accept( asBasicFile( isf ) );
        }
      };

    } else {
      // 'accept all' filter
      fileFilter = new IFileFilter() {
        @Override
        public boolean accept( ISolutionFile isf ) {
          return true;
        }
      };
    }

    ISolutionFile baseDir = getRepository().getSolutionFile( path, toResourceAction( FileAccess.READ ) );
    return listFiles(
        new ArrayList<IBasicFile>(), baseDir, fileFilter, includeDirs, showHiddenFilesAndFolders, maxDepth, 0 );
  }

  private List<IBasicFile> listFiles( List<IBasicFile> list, ISolutionFile root, IFileFilter filter,
      boolean includeDirs, boolean showHiddenFilesAndFolders, int depth, int level ) {
    if ( root.isDirectory() ) {
      if ( includeDirs && level > 0 && filter.accept( root ) ) {
        list.add( asBasicFile( root ) );
      }
      if ( depth != 0 ) {
        for ( ISolutionFile file : root.listFiles() ) {
          listFiles( list, file, filter, includeDirs, showHiddenFilesAndFolders, depth - 1, level + 1 );
        }
      }
    } else if ( filter.accept( root ) ) {
      list.add( asBasicFile( root ) );
    }
    return list;
  }


  @Override
  public IBasicFile fetchFile( String path ) {
    return fileExists( path ) ? asBasicFile( getRepository().getFileByPath( getPath( path ) ) ) : null;
  }

  private IBasicFile asBasicFile( final ISolutionFile file ) {
    return new IBasicFile() {

      public InputStream getContents() throws IOException {
        return new ByteArrayInputStream( file.getData() );
      }

      public String getExtension() {
        return RepositoryHelper.getExtension( file.getFileName() );
      }

      public String getFullPath() {
        return FilenameUtils.separatorsToUnix( file.getFullPath() );
      }

      public String getName() {
        return file.getFileName();
      }

      public String getPath() {
        return RepositoryHelper.relativizePath( basePath,
                RepositoryHelper.appendPath( getSolutionPath( file ), file.getFileName() ),
                true );
      }

      public boolean isDirectory() {
        return file.isDirectory();
      }

      private String getSolutionPath( ISolutionFile file ) {
        if ( file.isRoot() ) {
          return "/";
        }
        String path = FilenameUtils.separatorsToUnix( file.getSolutionPath() );
        return path;
      }

      public String toString() {
        return getFullPath();
      }
    };
  }

  @Override
  public boolean hasAccess( String filePath, FileAccess access ) {
    filePath = getPath( filePath );
    ISolutionFile file = getRepository().getSolutionFile( filePath, toResourceAction( access ) );
    if ( file == null ) {
      return false;
    } else if ( SecurityHelper.canHaveACLS( file )
        && ( file.retrieveParent() != null && !StringUtils.startsWith( file.getSolutionPath(), "system" ) ) ) {
      // has been checked
      return true;
    } else {
      if ( !SecurityHelper.canHaveACLS( file ) ) {
        logger.warn( "hasAccess: " + file.getExtension() + " extension not in acl-files." );
        // not declared in pentaho.xml:/pentaho-system/acl-files
        // try parent: folders have acl enabled unless in system
        ISolutionFile parent = file.retrieveParent();
        if ( parent instanceof IAclSolutionFile ) {
          return SecurityHelper.hasAccess( (IAclSolutionFile) parent, toResourceAction( access ), userSession );
        }
      }
      logger.warn( "hasAccess: Unable to check access control for " + filePath + " using default access settings." );
      if ( StringUtils.startsWith( FilenameUtils.separatorsToUnix( file.getSolutionPath() ), "system/" ) &&
          !isAcceptedPluginFile( filePath, userSession )) {
        return SecurityHelper.isPentahoAdministrator( userSession );
      }
      switch ( access ) {
        case EXECUTE:
        case READ:
          return true;
        default:
          return SecurityHelper.isPentahoAdministrator( userSession );
      }
    }
  }

  private boolean isAcceptedPluginFile( String filePath, IPentahoSession session ) {
    List<String> acceptedPluginIds = this.getSparklPluginIds( session );
    acceptedPluginIds.add( "cdb" );
    acceptedPluginIds.add( "cdv" );
    for ( String acceptedPluginId : acceptedPluginIds ) {
      if ( StringUtils.startsWith( filePath, "/system/" + acceptedPluginId ) ) {
        return true;
      }
    }
    return false;
  }

  private List<String> getSparklPluginIds( IPentahoSession session ) {
    if ( PentahoLegacySolutionAccess.sparklPluginIds == null ) {
      IPluginManager pluginManager = PentahoSystem.get( IPluginManager.class );
      List<String> pluginIds = pluginManager.getRegisteredPlugins();
      PentahoLegacySolutionAccess.sparklPluginIds = new ArrayList<String>();
      for ( String pluginId : pluginIds ) {
        IContentGeneratorInfo info = pluginManager.getContentGeneratorInfo( pluginId, session );
        if ( info != null && info.getClassname().equals( CPK_CONTENT_GENERATOR_CLASS_NAME ) ) {
          PentahoLegacySolutionAccess.sparklPluginIds.add( pluginId );
        }
      }
    }

    return PentahoLegacySolutionAccess.sparklPluginIds;
  }
  private static List<String> sparklPluginIds = null;

  private static int toResourceAction( FileAccess access ) {
    switch ( access ) {
      case DELETE:
        return IPentahoAclEntry.PERM_DELETE;
      case WRITE:
        return IPentahoAclEntry.PERM_UPDATE;
      case READ:
      case EXECUTE:
      default:
        return IPentahoAclEntry.PERM_EXECUTE;
    }
  }

  @Override
  public String toString() {
    StringBuffer sb = new StringBuffer( getClass().getSimpleName() );
    sb.append( ":" ).append( basePath );
    return sb.toString();
  }
}
