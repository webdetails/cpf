package pt.webdetails.cpf.repository.pentaho.unified;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.NotImplementedException;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.platform.api.repository2.unified.IRepositoryFileData;
import org.pentaho.platform.api.repository2.unified.IUnifiedRepository;
import org.pentaho.platform.api.repository2.unified.RepositoryFile;
import org.pentaho.platform.api.repository2.unified.RepositoryFileTree;
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
    return getRepositoryFile(path) != null;
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

  protected RepositoryFile getRepositoryFile(String path) {
    return getRepository().getFile(getFullPath(path));
  }

  private IRepositoryFileData createFileData(InputStream input, String mimeType) {
    return new SimpleRepositoryFileData(input, CharsetHelper.getEncoding(), mimeType);
  }

  public boolean saveFile(String path, InputStream input) {
    //TODO: convert to abs before
//    getRepository().
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
    List<String> foldersToCreate = new ArrayList<String>(); 
    while (!fileExists(path)) {
      // "a / b / c"
      // path<-|^|-> to create
      int sepIdx = RepositoryFilenameUtils.indexOfLastSeparator(path);
      foldersToCreate.add(path.substring(sepIdx + 1));
      path = path.substring(0, sepIdx);
    }
    RepositoryFile baseFolder = repo.getFile(path);
    if (baseFolder == null) {
      logger.error("Path " + path + " doesn't exist");
      return null;
    }
    // reverse iterate 
    if (foldersToCreate.size() > 0) {
      for (int i = foldersToCreate.size() -1 ; i >= 0; i--) {
        String folder = RepositoryFilenameUtils.getPathNoEndSeparator(foldersToCreate.get(i));
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
    //FIXME not implemented
    return null;
  }

  public boolean saveFile(String path, String contents) {
    return saveFile(path, IOUtils.toInputStream(contents));
  }

//  //TODO: remove! i mean.. delete!
//  public boolean removeFile(String path) {
//    return deleteFile(path);
//  }

  public IBasicFile fetchFile(String path) {
    RepositoryFile file = getRepositoryFile(path);
    return (file == null)? null : asBasicFile(file, path);
  }

  public boolean copyFile(String pathFrom, String pathTo) {
    try {
      // copyFile api didn't seem that linear, implemented as saveAs
      return saveFile(pathTo, getFileInputStream(pathFrom));

//      pathTo = getPath(pathTo);
//      pathFrom = getPath(pathFrom);
//      //TODO: will probably give error if 
//      getRepository().copyFile(getRepositoryFile(pathFrom).getId(), getPath(pathTo), null);
//      return true; 
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
    return listFiles(getFullPath(path), false, filter, false, new ArrayList<IBasicFile>());
  }

  public List<IBasicFile> listFiles(String path, IBasicFileFilter filter, int maxDepth, boolean includeDirs) {
    return listFiles(getFullPath(path), includeDirs, filter, false, new ArrayList<IBasicFile>());
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
   * @param output
   * @return List of files by the order they're found.
   */
  protected List<IBasicFile> listFiles(
      String path,
      boolean includeDirs,
      IBasicFileFilter filter,
      boolean showHiddenFiles,
      final List<IBasicFile> output)
  {
    RepositoryFileTree tree = getRepository().getTree(path, -1, null, showHiddenFiles);
    RepositoryFile root = tree.getFile();
    //XXX includeDirs is right? will order make sense?
    if(showHiddenFiles || !root.isHidden()){
      IBasicFile file = asBasicFile(root, relativizePath(path));
      if(tree.getChildren().size() > 0){
        for(RepositoryFileTree element : tree.getChildren()){
          listFiles(element.getFile().getPath(), includeDirs, filter, showHiddenFiles, output);
        } 
      } else if(root.isFolder() && includeDirs){
        output.add(file); 
      } else if(!root.isFolder()){ //is a folder
        if(filter.accept(file)){
          if(showHiddenFiles){
              output.add(file);
          } else if(!root.isHidden()){
              output.add(file);
          }
        }
      }
    }
    
    return output;
  }

}