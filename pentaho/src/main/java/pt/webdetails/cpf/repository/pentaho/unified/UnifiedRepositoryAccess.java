/*!
* Copyright 2002 - 2017 Webdetails, a Hitachi Vantara company. All rights reserved.
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

package pt.webdetails.cpf.repository.pentaho.unified;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Collections;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.platform.api.engine.IAuthorizationPolicy;
import org.pentaho.platform.api.engine.IPluginManager;
import org.pentaho.platform.api.repository2.unified.IRepositoryFileData;
import org.pentaho.platform.api.repository2.unified.IUnifiedRepository;
import org.pentaho.platform.api.repository2.unified.RepositoryFile;
import org.pentaho.platform.api.repository2.unified.RepositoryFileTree;
import org.pentaho.platform.api.repository2.unified.Converter;
import org.pentaho.platform.api.repository2.unified.UnifiedRepositoryException;
import org.pentaho.platform.api.repository2.unified.data.node.NodeRepositoryFileData;
import org.pentaho.platform.api.repository2.unified.data.simple.SimpleRepositoryFileData;
import org.pentaho.platform.engine.core.system.PentahoSystem;
import org.pentaho.platform.security.policy.rolebased.actions.AdministerSecurityAction;

import pt.webdetails.cpf.api.IFileContent;
import pt.webdetails.cpf.impl.FileContent;
import pt.webdetails.cpf.repository.api.IBasicFile;
import pt.webdetails.cpf.repository.api.IBasicFileFilter;
import pt.webdetails.cpf.repository.util.RepositoryHelper;
import pt.webdetails.cpf.utils.CharsetHelper;
import pt.webdetails.cpf.utils.MimeTypes;

public abstract class UnifiedRepositoryAccess {

  private static final Log logger = LogFactory.getLog( UnifiedRepositoryAccess.class );
  protected String basePath;

  protected abstract IUnifiedRepository getRepository();

  public boolean fileExists( String path ) {
    try {
      return getRepositoryFile( path ) != null;
    } catch ( UnifiedRepositoryException e ) {
      return false;
    }
  }

  public String getFileContents( String path ) throws IOException {
    InputStream input = getFileInputStream( path );

    if ( input == null ) {
      return null;
    }

    try {
      return IOUtils.toString( input );
    } finally {
      IOUtils.closeQuietly( input );
    }
  }

  public InputStream getFileInputStream( String path ) throws IOException {

    RepositoryFile file = getRepositoryFile( path );

    if ( file == null ) {
      return null;
    }

    try {
      SimpleRepositoryFileData data = getRepository().getDataForRead( file.getId(), SimpleRepositoryFileData.class );
      return data.getInputStream();
    } catch ( UnifiedRepositoryException ure ) {
      // CDA-93: Since 5.1, ktrs are stored as NodeRepositoryFileData instead of SimpleRepositoryFileData on EE
      // We need to check:
      // 1. if we're on EE
      // 2. whether the resource is a ktr or a kjb to load the approriate Converter. Otherwise we'll return the
      // serialization of the node
      NodeRepositoryFileData data = getRepository().getDataForRead( file.getId(), NodeRepositoryFileData.class );
      IPluginManager pManager = PentahoSystem.get( IPluginManager.class );
      ClassLoader classLoader = pManager.getClassLoader( "pdi-platform-plugin" );
      if ( classLoader != null ) {
        //It's an EE server - get the converter class name given the file type
        String converterName = path.endsWith( ".ktr" )
          ? "PDITransformationStreamConverter"
          : path.endsWith( ".kjb" ) ? "PDIJobStreamConverter" : null;

        if ( converterName != null ) {
          Converter converter = PentahoSystem.get( Converter.class, null, Collections.singletonMap( "name", converterName ) );
          if ( converter != null ) {
            return converter.convert( file.getId() );
          } else {
            logger.error( "Did not find expected converter class for resource: " + converterName
                      + ". Returning default conversion" );
          }
        }
      }

      return new ByteArrayInputStream( data.getNode().toString().getBytes( "UTF8" ) );
    }
  }

  public long getLastModified( String path ) {
    RepositoryFile file = getRepositoryFile( path );
    if ( file != null && file.getLastModifiedDate() != null ) {
      return file.getLastModifiedDate().getTime();
    }
    return 0L;
  }

  protected RepositoryFile getRepositoryFile( String path ) throws UnifiedRepositoryException {
    return getRepository().getFile( getFullPath( path ) );
  }

  private IRepositoryFileData createFileData( InputStream input, String mimeType ) {
    return new SimpleRepositoryFileData( input, CharsetHelper.getEncoding(), mimeType );
  }

  private boolean rawPathExists( IUnifiedRepository repo, String fullRepoPath ) {
    try {
      return repo.getFile( fullRepoPath ) != null;
    } catch ( UnifiedRepositoryException e ) {
      return false;
    }
  }

  public boolean saveFile( String path, InputStream input ) {

    FileContent file = new FileContent();
    file.setPath( path );
    file.setContents( input );

    return saveFile( file );
  }

  public boolean saveFile( IFileContent file ) {
    IUnifiedRepository repo = getRepository();

    InputStream is = null;
    try {
      is = file.getContents();
    } catch ( IOException e ) {
      logger.error( e );
    }

    IRepositoryFileData data = createFileData( is, MimeTypes.getMimeType( file.getPath() ) );
    RepositoryFile savedFile = null;
    if ( fileExists( file.getPath() ) ) {
      //yay, just update: no muss, no fuss!
      RepositoryFile repositoryFile = getRepositoryFile( file.getPath() );
      // TODO: preserve mimeType from file data
      savedFile = repo.updateFile( repositoryFile, data, null );
      // TODO: what happens here when things go wrong?
    } else {
      // keeps '/'
      RepositoryFile parentDir = getOrCreateFolder( repo, FilenameUtils.getPathNoEndSeparator( file.getPath() ) );

      if ( parentDir == null ) {
        logger.error( "Unable to ensure parent folder for " + file.getPath() + ". Check permissions?" );
      }

      String name = !StringUtils.isEmpty( file.getName() ) ? file.getName() : FilenameUtils.getName( file.getPath() );

      RepositoryFile.Builder fileBuilder = new RepositoryFile.Builder( name );

      Map<String, Properties> localePropertiesMap = new HashMap<String, Properties>();
      String defaultLocale = "default"; // use default locale
      Properties props = new Properties();

      if ( !StringUtils.isEmpty( file.getTitle() ) ) {
        fileBuilder = fileBuilder.title( file.getTitle() );
        props.put( "file.title", file.getTitle() );
      }

      if ( !StringUtils.isEmpty( file.getDescription() ) ) {
        fileBuilder = fileBuilder.title( file.getDescription() );
        props.put( "file.description", file.getDescription() );
      }

      localePropertiesMap.put( defaultLocale, props );
      fileBuilder = fileBuilder.localePropertiesMap( localePropertiesMap );

      savedFile = repo.createFile( parentDir.getId(), fileBuilder.build(), data, null );
    }
    return savedFile != null && savedFile.getId() != null;
  }


  private RepositoryFile getOrCreateFolder( IUnifiedRepository repo, String path ) {
    return getOrCreateFolder( repo, path, false );
  }

  private RepositoryFile getOrCreateFolder( IUnifiedRepository repo, String path, boolean isHidden ) {
    // full path, no slash at end
    String fullPath = StringUtils.chomp( getFullPath( path ), "/" );
    // backtrack path to get list of folders to create
    List<String> foldersToCreate = new ArrayList<String>();
    while ( !rawPathExists( repo, fullPath ) ) {
      // "a / b / c"
      // path<-|^|-> to create
      // accepts '/'
      int sepIdx = FilenameUtils.indexOfLastSeparator( fullPath );
      if ( sepIdx < 0 ) {
        break;
      }
      foldersToCreate.add( fullPath.substring( sepIdx + 1 ) );
      fullPath = fullPath.substring( 0, sepIdx );
    }

    //in case we reached root
    if ( StringUtils.isEmpty( fullPath ) ) {
      fullPath = RepositoryHelper.appendPath( "/", fullPath );
    }

    RepositoryFile baseFolder = repo.getFile( fullPath );

    if ( baseFolder == null ) {
      logger.error( "Path " + fullPath + " doesn't exist" );
      return null;
    }
    // reverse iterate 
    if ( foldersToCreate.size() > 0 ) {
      for ( int i = foldersToCreate.size() - 1; i >= 0; i-- ) {
        String folder = foldersToCreate.get( i );
        baseFolder =
          repo.createFolder( baseFolder.getId(),
            new RepositoryFile.Builder( folder ).folder( true ).hidden( isHidden ).build(), null );
      }
    }
    return baseFolder;
  }

  public boolean createFolder( String path ) {
    return createFolder( path, false );
  }

  public boolean createFolder( String path, boolean isHidden ) {
    RepositoryFile folder = getOrCreateFolder( getRepository(), path, isHidden );
    if ( folder == null ) {
      return false;
    } else {
      return true;
    }
  }

  protected String getFullPath( String path ) {
    // forces '/'
    return FilenameUtils.normalize( RepositoryHelper.appendPath( basePath, path ), true );
  }

  //reverse of getFullPath
  protected String relativizePath( String fullPath ) {
    return RepositoryHelper.relativizePath( basePath, fullPath, true );
  }

  public boolean saveFile( String path, String contents ) {
    return saveFile( path, IOUtils.toInputStream( contents ) );
  }

  public IBasicFile fetchFile( String path ) {
    RepositoryFile file = getRepositoryFile( path );
    return ( file == null ) ? null : asBasicFile( file, path );
  }

  public boolean copyFile( String pathFrom, String pathTo ) {
    try {
      // copyFile api didn't seem that linear, implemented as saveAs
      return saveFile( pathTo, getFileInputStream( pathFrom ) );
    } catch ( Exception e ) {
      return false;
    }
  }

  public boolean deleteFile( String path ) {
    RepositoryFile repositoryFile = getRepositoryFile( path );
    if ( repositoryFile == null ) {
      return false;
    }
    getRepository().deleteFile( repositoryFile.getId(), null );
    return true;
  }

  /**
   * list all files under a given base path
   *
   * @param path   the base/root path
   * @param filter (optional) allows for filtering of the returned list of files
   * @return list of files under a given base path
   * @see pt.webdetails.cpf.repository.api.IBasicFileFilter
   */
  public List<IBasicFile> listFiles( String path, IBasicFileFilter filter ) {
    return listFiles( getFullPath( path ), 1, false, filter, false, new ArrayList<IBasicFile>() );
  }

  /**
   * list all files/folders under a given base path
   *
   * @param path        the base/root path
   * @param filter      (optional) allows for filtering of the returned list of files
   * @param maxDepth    the search and listing depth; -1 for full depth, zero for no depth (i.e only the base path's
   *                    direct children)
   * @param includeDirs true if list of IBasicFile returned should also include folders, false otherwise
   * @return list of files/folders under a given base path
   * @see pt.webdetails.cpf.repository.api.IBasicFileFilter
   */
  public List<IBasicFile> listFiles( String path, IBasicFileFilter filter, int maxDepth, boolean includeDirs ) {
    return listFiles( getFullPath( path ), maxDepth, includeDirs, filter, false, new ArrayList<IBasicFile>() );
  }

  /**
   * list all files/folders under a given base path
   *
   * @param path                      the base/root path
   * @param filter                    (optional) allows for filtering of the returned list of files
   * @param maxDepth                  the search and listing depth; -1 for full depth, zero for no depth (i.e only the
   *                                  base path's direct children)
   * @param includeDirs               true if list of IBasicFile returned should also include folders, false otherwise
   * @param showHiddenFilesAndFolders true if list of IBasicFile returned should also include files/folders marked as
   *                                  hidden, false otherwise
   * @return list of files/folders under a given base path
   * @see pt.webdetails.cpf.repository.api.IBasicFileFilter
   */
  public List<IBasicFile> listFiles( String path, IBasicFileFilter filter, int maxDepth, boolean includeDirs,
                                     boolean showHiddenFilesAndFolders ) {
    return listFiles( getFullPath( path ), maxDepth, includeDirs, filter, showHiddenFilesAndFolders,
      new ArrayList<IBasicFile>() );
  }

  public List<IBasicFile> listFiles( String path, IBasicFileFilter filter, int maxDepth ) {
    return listFiles( path, filter, maxDepth, false );
  }

  protected IBasicFile asBasicFile( final RepositoryFile file, final String path ) {
    final String relativePath = ( path == null ) ? relativizePath( file.getPath() ) : path;
    return new IBasicFile() {

      public InputStream getContents() {
        try {
          return UnifiedRepositoryAccess.this.getFileInputStream( relativePath );
        } catch ( IOException e ) {
          return null;
        }
      }

      public String getName() {
        return file.getName();
      }

      public String getFullPath() {
        return FilenameUtils.separatorsToUnix( file.getPath() );
      }

      public String getPath() {
        return relativePath;
      }

      public String getExtension() {
        return RepositoryHelper.getExtension( getName() );
      }

      public boolean isDirectory() {
        return file.isFolder();
      }

    };
  }

  /**
   * DFS files matching filter.
   *
   * @param path
   * @param includeDirs
   * @param filter
   * @param showHiddenFiles
   * @param listOut
   * @return List of files by the order they're found.
   */
  protected List<IBasicFile> listFiles(
    String path,
    int depth,
    boolean includeDirs,
    IBasicFileFilter filter,
    boolean showHiddenFiles,
    final List<IBasicFile> listOut ) {
    // TODO: check for depth coherence with other types, better impl
    RepositoryFileTree tree = getRepository().getTree( path, depth, null, showHiddenFiles );
    // TODO: in case there are no files / folder is hidden the tree could be null?
    if ( tree != null ) {
      populateList( listOut, tree, filter, includeDirs, showHiddenFiles );
    }
    return listOut;
  }

  // replicate pentaho web service behaviour
  protected boolean hideSystemFile( RepositoryFile file ) {
    IAuthorizationPolicy policy = PentahoSystem.get( IAuthorizationPolicy.class );
    boolean isAdmin = policy.isAllowed( AdministerSecurityAction.NAME );
    Map<String, Serializable> fileMeta = getRepository().getFileMetadata( file.getId() );
    boolean isSystemFolder =
      fileMeta.containsKey( IUnifiedRepository.SYSTEM_FOLDER )
        ? (Boolean) fileMeta.get( IUnifiedRepository.SYSTEM_FOLDER )
        : false;

    if ( !isAdmin && isSystemFolder ) {
      return true;
    }
    return false;
  }

  // painful thing to do, but it's a unifying interface
  protected void populateList(
    List<IBasicFile> list,
    RepositoryFileTree tree,
    IBasicFileFilter filter,
    final boolean includeDirs,
    final boolean showHidden ) {
    RepositoryFile file = tree.getFile();

    if ( !showHidden && file.isHidden() ) {
      return;
    }

    if ( hideSystemFile( file ) ) {
      return;
    }

    if ( filter == null ) {
      // no filter == 'accept all' filter
      filter = new IBasicFileFilter() {
        @Override
        public boolean accept( IBasicFile file ) {
          return true;
        }
      };
    }

    if ( file.isFolder() ) {
      // TODO "FILES" doesn't seem to work here
      for ( RepositoryFileTree actualFileTree : tree.getChildren() ) {
        RepositoryFile actualFile = actualFileTree.getFile();
        if ( includeDirs || !actualFile.isFolder() ) {
          if ( !showHidden && actualFile.isHidden() ) {
            continue;
          }
          IBasicFile bFile = asBasicFile( actualFile, null );
          if ( filter.accept( bFile ) && !hideSystemFile( actualFile ) ) {
            list.add( bFile );
          }
        }
        if ( actualFileTree.getChildren() != null ) {
          populateList( list, actualFileTree, filter, includeDirs, showHidden );
        }
      }
    }
  }

}
