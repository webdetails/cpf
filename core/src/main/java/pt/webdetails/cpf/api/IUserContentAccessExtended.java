package pt.webdetails.cpf.api;

import pt.webdetails.cpf.repository.api.IACAccess;
import pt.webdetails.cpf.repository.api.IRWAccess;
import pt.webdetails.cpf.repository.api.IReadAccess;
import pt.webdetails.cpf.repository.api.IUserContentAccess;

/**
 * For user interaction with the repository. Should always check permissions.<br>
 * 
 * @see {@link IReadAccess} {@link IRWAccess} {@link IACAccess}
 */
public interface IUserContentAccessExtended extends IUserContentAccess {

  /**
   * Saves a file.
   * 
   * @param IFileContent contents of file to be saved
   * @return true if file was saved, false otherwise
   */
  boolean saveFile( IFileContent file );

}
