package pt.webdetails.cpf.repository.impl;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;

import pt.webdetails.cpf.repository.api.IBasicFile;
import pt.webdetails.cpf.repository.api.IBasicFileFilter;
import pt.webdetails.cpf.repository.api.IRWAccess;
import pt.webdetails.cpf.utils.CharsetHelper;

/**
 * This one is based on files. How they are fetched is anyone's guess
 */
public abstract class FileBasedResourceAccess implements IRWAccess {

  public InputStream getFileInputStream(String path) throws IOException {
    File file = getFile(path);
    if (file.exists()) {
      return new FileInputStream(file);
    }
    return null;
  }

  public String getFileContents(String path) throws IOException {
    InputStream input = null;
    try {
      input = getFileInputStream(path);
      return IOUtils.toString(input, CharsetHelper.getEncoding());
    }
    finally {
      IOUtils.closeQuietly(input);
    }
  }

  public boolean fileExists(String path) {
    return getFile(path).exists();
  }

  public long getLastModified(String path) {
    return getFile(path).lastModified();
  }

  public boolean saveFile(String path, String contents) {
    FileOutputStream out = null;
    try {
      out = new FileOutputStream(getFile(path));
      IOUtils.write(contents, out);
    } catch (IOException e) {
      return false;
    }
    finally {
      IOUtils.closeQuietly(out);
    }
    return true;
  }

  public boolean deleteFile(String path) {
    return getFile(path).delete();
  }

  public boolean copyFile(String pathFrom, String pathTo) {
    try {
      return saveFile(pathTo, getFileContents(pathFrom));
    } catch (IOException e) {
      return false;
    }
  }
  
//  protected File getFile(String path) { return null; }
  protected abstract File getFile(String path);

  public IBasicFile fetchFile(String path) {
    return asBasicFile(getFile(path), path);
  }

  public List<IBasicFile> listFiles(String path, IBasicFileFilter filter) {
    return listFiles(new ArrayList<IBasicFile>(), getFile(path), asFileFilter(filter), false, -1);
  }
  public List<IBasicFile> listFiles(String path, IBasicFileFilter filter, int maxDepth) {
    return listFiles(new ArrayList<IBasicFile>(), getFile(path), asFileFilter(filter), false, maxDepth);
  }

  private List<IBasicFile> listFiles(List<IBasicFile> list, File root, FileFilter filter, boolean includeDirs, int depth) {

    if (root.isDirectory()) {
      if (includeDirs && filter.accept(root)) {
        list.add(asBasicFile(root, relativizePath(root)));
      }
      if (depth > 0) {
        for (File file : root.listFiles(filter)) {
          listFiles(list, file, filter, includeDirs, depth -1);
        }
      }
    }
    else if (filter.accept(root)) {
      list.add(asBasicFile(root, relativizePath(root)));
    }
    return list;
  }

  private String relativizePath(File file) {
    //FIXME implement
    return null;
  }

  private FileFilter asFileFilter(final IBasicFileFilter filter) {
    return new FileFilter() {
      
      public boolean accept(File file) {
        return filter.accept(asBasicFile(file, relativizePath(file)));
      }
    };
  }

  protected IBasicFile asBasicFile(final File file, final String relPath) {
    if (file == null) return null;

    return new IBasicFile () {

      public InputStream getContents() throws IOException{
        return new FileInputStream(file);
      }

      public String getName() {
        return file.getName();
      }

      public String getFullPath() {
        return file.getAbsolutePath();//TODO: . ..
      }

      public String getPath() {
        return relPath;
      }

      public String getExtension() {
        //TODO: do we need to account for .<file>?
        //TODO: another one for utils
        return StringUtils.lowerCase(FilenameUtils.getExtension(getName()));
      }
      
    };
  }

  public boolean saveFile(String path, InputStream in) {
    File file = getFile(path);
    FileOutputStream fout = null;
    try {
      fout = new FileOutputStream(file);
      IOUtils.copy(in, fout);
      return true;
    } catch (IOException e) {
      return false;
    }
    finally {
      IOUtils.closeQuietly(fout);
    }
  }

  
}
