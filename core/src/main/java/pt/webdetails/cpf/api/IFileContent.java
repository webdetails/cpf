package pt.webdetails.cpf.api;

import pt.webdetails.cpf.repository.api.IBasicFile;

public interface IFileContent extends IBasicFile {

  /**
   * @return the title of the file
   */
  String getTitle();

  /**
   * @return the description of the file
   */
  String getDescription();

  /**
   * @return flag hidden
   */
  boolean isHidden();

}
