package pt.webdetails.cpf.repository.pentaho.unified;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.platform.api.repository2.unified.IRepositoryFileData;
import org.pentaho.platform.api.repository2.unified.IUnifiedRepository;
import org.pentaho.platform.api.repository2.unified.RepositoryFile;
import org.pentaho.platform.api.repository2.unified.RepositoryFileTree;
import org.pentaho.platform.api.repository2.unified.UnifiedRepositoryException;
import org.pentaho.platform.api.repository2.unified.data.simple.SimpleRepositoryFileData;
import org.pentaho.platform.repository.RepositoryFilenameUtils;

import pt.webdetails.cpf.repository.api.IBasicFile;
import pt.webdetails.cpf.repository.api.IBasicFileFilter;
import pt.webdetails.cpf.repository.util.RepositoryHelper;
import pt.webdetails.cpf.utils.CharsetHelper;
import pt.webdetails.cpf.utils.MimeTypes;

public abstract class UnifiedRepositoryAccess {// implements IPluginResourceRWAccess, IUserContentAccess {

  private static final Log logger = LogFactory.getLog(UnifiedRepositoryAccess.class);
  protected String basePath;

  protected abstract IUnifiedRepository getRepository();

  public boolean fileExists(String path) {
    try {
      return getRepositoryFile(path) != null;
    }
    catch (UnifiedRepositoryException e) {
      return false;
    }
  }

  public String getFileContents(String path) throws IOException {
    InputStream input = getFileInputStream(path);
  
    if(input == null) {
      return null;
    }
  
    try {
      return IOUtils.toString(input);
    }
    finally {
      IOUtils.closeQuietly(input);
    }
  }

  public InputStream getFileInputStream(String path) throws IOException {
  
    RepositoryFile file = getRepositoryFile(path);
  
    if (file == null) {
      return null;
    }
  
    SimpleRepositoryFileData data = getRepository().getDataForRead(file.getId(), SimpleRepositoryFileData.class);
    return data.getInputStream();
  }

  public long getLastModified(String path) {
    RepositoryFile file = getRepositoryFile(path);
    if (file != null) {
      return file.getLastModifiedDate().getTime();
    }
    return 0L;
  }

  protected RepositoryFile getRepositoryFile(String path) throws UnifiedRepositoryException {
    return getRepository().getFile(getFullPath(path));
  }

  private IRepositoryFileData createFileData(InputStream input, String mimeType) {
    return new SimpleRepositoryFileData(input, CharsetHelper.getEncoding(), mimeType);
  }

  private boolean rawPathExists(IUnifiedRepository repo, String fullRepoPath) {
    try {
      return repo.getFile(fullRepoPath) != null;
    }
    catch (UnifiedRepositoryException e) {
      return false;
    }
  }

  public boolean saveFile(String path, InputStream input) {
    IUnifiedRepository repo = getRepository();
    IRepositoryFileData data = createFileData(input, MimeTypes.getMimeType(path));
    RepositoryFile savedFile = null;
    if (fileExists(path)) {
      //yay, just update: no muss, no fuss!
      RepositoryFile file = getRepositoryFile(path);
      //TODO: preserve mimeType from file data
      savedFile = repo.updateFile(file, data, null);//TODO: what happens here when things go wrong?
    }
    else {
      // oh...
      RepositoryFile parentDir = getOrCreateFolder(repo, RepositoryFilenameUtils.getPathNoEndSeparator(path));
      int sepIdx = RepositoryFilenameUtils.indexOfLastSeparator(path);
      String fileName = sepIdx >= 0 ? path.substring(sepIdx+1) : path;
      if (parentDir == null) {
        logger.error("Unable to ensure parent folder for " + path + ". Check permissions?");
      }
      savedFile = repo.createFile(parentDir.getId(), new RepositoryFile.Builder(fileName).build(), data, null);
    }
    return savedFile != null && savedFile.getId() != null;
  }
  
  private RepositoryFile getOrCreateFolder(IUnifiedRepository repo, String path) {
    String fullPath = getFullPath( path );

    // backtrack path to get list of folders to create
    List<String> foldersToCreate = new ArrayList<String>(); 
    while (!rawPathExists( repo, fullPath )) {
      // "a / b / c"
      // path<-|^|-> to create
      int sepIdx = RepositoryFilenameUtils.indexOfLastSeparator(fullPath);
      if (sepIdx < 0) {
        break;
      }
      foldersToCreate.add(fullPath.substring(sepIdx + 1));
      fullPath = fullPath.substring(0, sepIdx);
    }

    //in case we reached root
    if (StringUtils.isEmpty( fullPath )) {
      fullPath = RepositoryHelper.appendPath( "/", fullPath );
    }

    RepositoryFile baseFolder = repo.getFile(fullPath);

    if (baseFolder == null) {
      logger.error("Path " + fullPath + " doesn't exist");
      return null;
    }
    // reverse iterate 
    if (foldersToCreate.size() > 0) {
      for (int i = foldersToCreate.size() -1 ; i >= 0; i--) {
        String folder = foldersToCreate.get(i);
        baseFolder = repo.createFolder(baseFolder.getId(), new RepositoryFile.Builder(folder).folder(true).build(), null); // "creating path " + path);//TODO: ?
      }
    }
    return baseFolder;
  }

  public boolean createFolder(String path){
    RepositoryFile folder = getOrCreateFolder(getRepository(), path);
    if(folder == null){
      return false;
    } else {
      return true;
    }
  }

  protected String getFullPath(String path) {
    return FilenameUtils.normalize(RepositoryHelper.appendPath(basePath, path), true);
  }

  //reverse of getFullPath
  protected String relativizePath(String fullPath) {
    return RepositoryHelper.relativizePath( basePath, fullPath, true );
  }

  public boolean saveFile(String path, String contents) {
    return saveFile(path, IOUtils.toInputStream(contents));
  }

  public IBasicFile fetchFile(String path) {
    RepositoryFile file = getRepositoryFile(path);
    return (file == null)? null : asBasicFile(file, path);
  }

  public boolean copyFile(String pathFrom, String pathTo) {
    try {
      // copyFile api didn't seem that linear, implemented as saveAs
      return saveFile(pathTo, getFileInputStream(pathFrom));
    } catch (Exception e) {
      return false;
    }
  }

  public boolean deleteFile(String path) {
    RepositoryFile repositoryFile = getRepositoryFile(path);
    if (repositoryFile == null) {
      return false;
    }
    getRepository().deleteFile(repositoryFile.getId(), null);
    return true;
  }

  public List<IBasicFile> listFiles(String path, IBasicFileFilter filter) {
    return listFiles(getFullPath(path), -1, false, filter, false, new ArrayList<IBasicFile>());
  }

  public List<IBasicFile> listFiles(String path, IBasicFileFilter filter, int maxDepth, boolean includeDirs) {
    return listFiles(getFullPath(path), maxDepth, includeDirs, filter, false, new ArrayList<IBasicFile>());
  }

  public List<IBasicFile> listFiles(String path, IBasicFileFilter filter, int maxDepth) {
    return listFiles(path, filter, maxDepth, false);
  }
  protected IBasicFile asBasicFile(final RepositoryFile file, final String path) {
    final String relativePath = 
        (path == null) ? relativizePath(RepositoryHelper.appendPath(file.getPath(), file.getName()))
                       : path;
    return new IBasicFile () {

      public InputStream getContents() {
        try {
          return UnifiedRepositoryAccess.this.getFileInputStream(relativePath);
        } catch (IOException e) {
          return null;
        }
      }

      public String getName() {
        return file.getName();
      }

      public String getFullPath() {
        return file.getPath();
      }

      public String getPath() {
        return relativePath;
      }

      public String getExtension() {
        return StringUtils.lowerCase(FilenameUtils.getExtension(getName()));
      }

	  public boolean isDirectory() {
	    return file.isFolder();
	  }

    };
  }

  /**
   * DFS files matching filter. 
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
      final List<IBasicFile> listOut)
  {
    // TODO: check for depth coherence with other types
    // TODO: better impl. how i regret this iface
    RepositoryFileTree tree = getRepository().getTree(path, depth, null, showHiddenFiles);
    populateList(listOut, tree, filter, includeDirs, showHiddenFiles);
    return listOut;
  }

  // painful thing to do, but it's a unifying interface
  protected void populateList(
      List<IBasicFile> list,
      RepositoryFileTree tree,
      IBasicFileFilter filter,
      final boolean includeDirs,
      final boolean showHidden)
  {
    RepositoryFile file = tree.getFile();
    if (!showHidden && file.isHidden()) {
      return;
    }

//    if ( includeDirs || !file.isFolder() ) {
//      IBasicFile bFile = asBasicFile( file, null );
//      if ( filter.accept( bFile ) ) {
//        list.add( bFile );
//      }
//    }
    //TODO: TREE ONLY HAS FOLDERS!
    if (file.isFolder()) {
      for (RepositoryFile actualFile : getRepository().getChildren( file.getId() )) {//, "FILES"
        if ( includeDirs || !actualFile.isFolder() ) {
          IBasicFile bFile = asBasicFile( actualFile, null );
          if ( filter.accept( bFile ) ) {
            list.add( bFile );
          }
        }
      }
    }
    for (RepositoryFileTree branch : tree.getChildren()) {
      populateList(list, branch, filter, includeDirs, showHidden);
    }
  }

}