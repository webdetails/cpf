package pt.webdetails.cpf.repository.api;

import java.io.IOException;
import java.io.InputStream;

/**
 * Basic file info and its contents.
 */
public interface IBasicFile {

  /**
   * @return File contents
   */
  InputStream getContents() throws IOException;
  /**
   * @return just the name of the file, with extension
   */
  String getName();

  //TODO:this can be bad:
  /**
   * Full path for repository type used
   * @return path and filename
   */
  String getFullPath();
  /**
   * Path for the RepositoryAccess that supplied it.
   * @return path and filename 
   */
  String getPath();

  /**
   * @return the extension, lower case, no dot, or empty if not there.
   *         the extension is whatever comes after the last dot of the file name, if the dot isn't the first char
   */
  String getExtension();
}
