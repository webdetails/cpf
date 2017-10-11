/*!
* Copyright 2002 - 2017 Webdetails, a Hitachi Vantara company.  All rights reserved.
* 
* This software was developed by Webdetails and is provided under the terms
* of the Mozilla Public License, Version 2.0, or any later version. You may not use
* this file except in compliance with the license. If you need a copy of the license,
* please go to  http://mozilla.org/MPL/2.0/. The Initial Developer is Webdetails.
*
* Software distributed under the Mozilla Public License is distributed on an "AS IS"
* basis, WITHOUT WARRANTY OF ANY KIND, either express or  implied. Please refer to
* the license for the specific language governing your rights and limitations.
*/

package pt.webdetails.cpf.repository.vfs;


import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.vfs.FileObject;
import org.apache.commons.vfs.FileSystemException;
import org.apache.commons.vfs.FileSystemManager;
import org.apache.commons.vfs.FileType;
import org.apache.commons.vfs.Selectors;
import org.apache.commons.vfs.VFS;
import org.dom4j.Document;
import pt.webdetails.cpf.plugin.CorePlugin;
import pt.webdetails.cpf.repository.IRepositoryAccess;
import pt.webdetails.cpf.repository.IRepositoryFile;
import pt.webdetails.cpf.repository.IRepositoryFileFilter;
import pt.webdetails.cpf.session.IUserSession;
import pt.webdetails.cpf.utils.CharsetHelper;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class VfsRepositoryAccess implements IRepositoryAccess {

  protected static final Log log = LogFactory.getLog( VfsRepositoryAccess.class );

  private final String DEFAULT_REPO;
  private final String DEFAULT_SETTINGS;
  protected FileObject settings;
  protected FileObject repo;
  protected CorePlugin plugin;
  protected IUserSession session;

  //TODO: unused should be removed from interface
  //    private IUserSession session;

  public VfsRepositoryAccess() throws IOException {
    this.DEFAULT_REPO = createDefaultRepo();
    this.DEFAULT_SETTINGS = createDefaultSettings();

    try {
      setRepository( DEFAULT_REPO );
      setSettings( DEFAULT_SETTINGS );
    } catch ( Exception e ) {
      log.error( "Cannot initialize VfsRepository", e );
    }
  }


  public VfsRepositoryAccess( String repo, String settings ) {
    this.DEFAULT_REPO = "";
    this.DEFAULT_SETTINGS = "";
    try {
      setRepository( repo );
      setSettings( settings );
    } catch ( Exception e ) {
      log.error( "Cannot initialize VfsRepository", e );
    }

  }

  public void setRepository( String path ) {
    repo = setRepoPath( path );
  }

  public void setSettings( String path ) {
    settings = setRepoPath( path );
  }

  private FileObject setRepoPath( String path ) {
    FileSystemManager fileSystemManager;
    try {
      if ( !path.endsWith( "" + File.separatorChar ) ) {
        path += File.separatorChar;
      }
      fileSystemManager = VFS.getManager();
      FileObject fileObject;
      fileObject = fileSystemManager.resolveFile( path );
      if ( fileObject == null ) {
        throw new IOException( "File cannot be resolved: " + path );
      }
      if ( !fileObject.exists() ) {
        throw new IOException( "File does not exist: " + path );
      }
      return fileObject;
    } catch ( Exception e ) {
      log.error( "Error setting path for repository: " + path, e );
    }
    return null;
  }
  //
  //    @Override
  //    public void setPlugin(CorePlugin plugin) {
  //        this.plugin = plugin;
  //    }

  //    @Override
  public void setUserSession( IUserSession session ) {
    this.session = session;
  }

  protected FileObject resolveFile( FileObject folder, String file ) throws Exception {
    if ( file == null || file.startsWith( "/" ) || file.startsWith( "." ) || file.contains( "/../" ) ) {
      throw new IllegalArgumentException( "Path cannot be null or start with \"/\" or \".\" - Illegal Path: " + file );
    }
    FileObject repoFile = folder.resolveFile( file );
    return repoFile;
  }

  public boolean canWrite( String file ) {
    try {
      FileObject f = resolveFile( repo, file );
      return ( f != null && f.isWriteable() );
    } catch ( Exception e ) {
      log.error( "Cannot check canWrite for " + file, e );
    }
    return false;
  }

  public SaveFileStatus copySolutionFile( String fromFilePath, String toFilePath ) throws IOException {
    try {
      FileObject to = resolveFile( repo, toFilePath );
      FileObject from = resolveFile( repo, fromFilePath );
      to.copyFrom( from, Selectors.SELECT_SELF );
      if ( to != null && to.exists() && to.isReadable() ) {
        return SaveFileStatus.OK;
      }
    } catch ( Exception e ) {
      log.error( "Cannot copy from " + fromFilePath + " to " + toFilePath, e );
    }
    return SaveFileStatus.FAIL;
  }

  public boolean createFolder( String file ) throws IOException {
    try {
      FileObject f = resolveFile( repo, file );
      f.createFolder();
      return true;
    } catch ( Exception e ) {
      log.error( "Cannot create folder at: " + file, e );
    }
    return false;
  }

  public String getEncoding() {
    return CharsetHelper.getEncoding();
  }

  public String getJSON( String arg0, String arg1, String arg2 ) {
    throw new UnsupportedOperationException( "getJSON is deprecated, not supported!" );
  }

  public String getJqueryFileTree( String arg0, String arg1, String arg2 ) {
    throw new UnsupportedOperationException( "getJqueryFileTree is deprecated, not supported!" );
  }

  public IRepositoryFile getRepositoryFile( String file, FileAccess fa ) {
    try {
      FileObject f = resolveFile( repo, file );
      return new VfsRepositoryFile( repo, f );
    } catch ( Exception e ) {
      log.error( "Cannot get repository file: " + file, e );
    }
    return null;
  }

  public Document getResourceAsDocument( String arg0 ) throws IOException {
    throw new UnsupportedOperationException( "getResourceAsDocument is deprecated, not supported!" );
  }

  public Document getResourceAsDocument( String arg0, FileAccess arg1 ) throws IOException {
    throw new UnsupportedOperationException( "getResourceAsDocument is deprecated, not supported!" );
  }

  public String getResourceAsString( String file ) throws IOException {
    return getResourceAsString( file, FileAccess.READ );
  }

  public String getResourceAsString( String file, FileAccess access ) throws IOException {
    try {
      IRepositoryFile rf = getRepositoryFile( file, access );
      return new String( rf.getData() );
    } catch ( Exception e ) {
      log.error( "Cannot get resource as string for: " + file, e );
    }
    return null;
  }

  public InputStream getResourceInputStream( String file ) throws FileNotFoundException {
    try {
      FileObject fo = repo.resolveFile( file );
      if ( fo.exists() ) {
        return fo.getContent().getInputStream();
      }
    } catch ( Exception e ) {
      log.error( "Cannot getResourceInputStream for: " + file, e );
    }
    throw new FileNotFoundException( "Cannot get input stream for: " + file );
  }

  public InputStream getResourceInputStream( String file, FileAccess access ) throws FileNotFoundException {
    return getResourceInputStream( file );
  }

  public InputStream getResourceInputStream( String file, FileAccess acess, boolean getLocalizedResource )
    throws FileNotFoundException {
    return getResourceInputStream( file );
  }

  private FileObject resolvePluginDirectory( FileObject dir, String directory ) throws Exception {
    // TODO: is this the right approach? if the plugin is set, look in specific folder, otherwise
    // in the settings directory?
    FileObject base = dir;
    if ( plugin != null ) {
      base = resolveFile( base, plugin.getName() );
    }
    return resolveFile( base, directory );
  }

  public IRepositoryFile[] getPluginFiles( String directory, FileAccess access ) {
    try {
      if ( settings != null ) {
        FileObject p = resolvePluginDirectory( settings, directory );
        IRepositoryFile ir = new VfsRepositoryFile( settings, p );
        return ir.listFiles();
      }
    } catch ( Exception e ) {
      log.error( "Error getting plugin files for (" + plugin + ")  at: " + directory, e );
    }
    return new IRepositoryFile[ 0 ];
  }

  public IRepositoryFile getSettingsFile( String file, FileAccess arg1 ) {
    try {
      if ( settings != null ) {
        FileObject p = resolvePluginDirectory( settings, file );
        return new VfsRepositoryFile( settings, p );
      }
    } catch ( Exception e ) {
      log.error( "Error getting plugin file for (" + plugin + ")  at: " + file, e );
    }
    return null;
  }

  public IRepositoryFile[] getSettingsFileTree( final String directory, final String fileExtension,
                                                FileAccess access ) {
    try {
      if ( settings != null ) {
        FileObject p = resolvePluginDirectory( settings, directory );
        IRepositoryFile ir = new VfsRepositoryFile( settings, p );
        IRepositoryFile[] files = ir.listFiles( new IRepositoryFileFilter() {
          @Override
          public boolean accept( IRepositoryFile isf ) {
            return ( fileExtension != null && fileExtension.equals( isf.getExtension() ) );
          }
        } );
        return files;
      }
    } catch ( Exception e ) {
      log.error( "Error getting plugin files for (" + plugin + ")  at: " + directory + " with extension: "
          + fileExtension, e );
    }
    return null;

  }

  public String getSettingsResourceAsString( String file ) throws IOException {
    try {
      IRepositoryFile rf = getSettingsFile( file, FileAccess.READ );
      return new String( rf.getData() );
    } catch ( Exception e ) {
      throw new IOException( "Cannot get settings resource as string for: " + file, e );
    }
  }

  public String getSolutionPath( String arg0 ) {
    String path = "";
    FileObject file = null;
    try {
      path = repo.getURL().toURI().getPath();
    } catch ( FileSystemException ex ) {
      Logger.getLogger( VfsRepositoryAccess.class.getName() ).log( Level.SEVERE, null, ex );
    } catch ( URISyntaxException ex ) {
      Logger.getLogger( VfsRepositoryAccess.class.getName() ).log( Level.SEVERE, null, ex );
    }
    if ( arg0 == null || arg0.isEmpty() ) {
      return path;
    }
    if ( arg0.startsWith( "/" ) ) {
      arg0 = arg0.substring( 1 );
    }
    try {
      file = resolveFile( repo, arg0 );
      if ( file != null && file.exists() ) {
        return path + "/" + arg0;
      }
    } catch ( Exception ex ) {
      Logger.getLogger( VfsRepositoryAccess.class.getName() ).log( Level.SEVERE, null, ex );
    }
    return null;
  }

  public boolean hasAccess( String file, FileAccess access ) {
    IRepositoryFile ir = getRepositoryFile( file, access );
    return ir.exists();
  }

  public SaveFileStatus publishFile( String file, String contents, boolean overwrite )
    throws UnsupportedEncodingException {
    if ( contents != null ) {
      return publishFile( file, contents.getBytes( getEncoding() ), overwrite );
    }
    return SaveFileStatus.FAIL;
  }

  public SaveFileStatus publishFile( String file, byte[] content, boolean overwrite ) {
    return publishFile( null, file, content, overwrite );
  }

  public SaveFileStatus publishFile( String solutionPath, String file, byte[] content, boolean overwrite ) {
    return publishFile( null, null, file, content, overwrite );

  }

  public SaveFileStatus publishFile( String solution, String path, String fileName, byte[] data, boolean overwrite ) {
    try {
      if ( data != null ) {
        String file = getRelativePath( path, solution, fileName );
        if ( resourceExists( file ) ) {
          if ( canWrite( file ) && overwrite ) {
            FileObject fileObject = resolveFile( repo, file );
            fileObject.getContent().getOutputStream().write( data );
            fileObject.getContent().close();
            return SaveFileStatus.OK;
          } else {
            return SaveFileStatus.FAIL;
          }
        } else {
          FileObject f = resolveFile( repo, file );
          f.getContent().getOutputStream().write( data );
          f.getContent().close();
          return SaveFileStatus.OK;
        }

      }
    } catch ( Exception e ) {
      throw new RuntimeException(
        "Cannot publish file! solution: " + solution + " path: " + path + " file: " + fileName, e );
    }
    return SaveFileStatus.FAIL;
  }

  public boolean removeFile( String file ) {
    try {
      FileObject f = resolveFile( repo, file );
      if ( f.exists() ) {
        return f.delete();
      }
      return true;
    } catch ( Exception e ) {
      throw new RuntimeException( "Cannot delete file: " + file, e );
    }
  }

  public boolean removeFileIfExists( String file ) {
    return !resourceExists( file ) || removeFile( file );
  }

  public boolean resourceExists( String file ) {
    try {
      FileObject ir = resolveFile( repo, file );
      return ir.exists();
    } catch ( Exception e ) {
      throw new RuntimeException( "Cannot check if repo file exists: " + file, e );
    }
  }

  private String getRelativePath( final String originalPath, final String solution, final String file )
    throws UnsupportedEncodingException {
    String joined = "";
    joined += ( StringUtils.isEmpty( solution ) ? "" : solution + "/" );
    joined += ( StringUtils.isEmpty( originalPath ) ? "" : originalPath + "/" );
    joined += ( StringUtils.isEmpty( file ) ? "" : file );
    joined = joined.replaceAll( "//", "/" );
    return joined;
  }

  private String createDefaultRepo() throws IOException {
    String repo = System.getProperty( "user.dir" );
    setRepository( repo );
    repo += "/cpf/repository";
    createFolder( "cpf/repository" );
    return repo;
  }

  private String createDefaultSettings() throws IOException {
    //TODO do we always want user.dir here?
    String sett = System.getProperty( "user.dir" );
    sett += "/cpf/settings";
    createFolder( "cpf/settings" );
    return sett;
  }


  public IRepositoryFile[] listRepositoryFiles( IRepositoryFileFilter fileFilter ) {
    try {
      FileObject[] files = repo.getChildren();
      List<IRepositoryFile> repoFiles = new ArrayList<IRepositoryFile>();
      for ( FileObject file : files ) {
        if ( file.exists() && file.isReadable() && file.getType().equals( FileType.FILE ) ) {
          IRepositoryFile repoFile = new VfsRepositoryFile( repo, file );
          if ( fileFilter == null || fileFilter.accept( repoFile ) ) {
            repoFiles.add( repoFile );
          }
        }
      }
      return repoFiles.toArray( new IRepositoryFile[] {} );
    } catch ( FileSystemException e ) {
      throw new RuntimeException( "Cannot list repo files", e );
    }
  }


}
