package pt.webdetails.cpf.repository.api;

public interface IACAccess {

  /**
   * Access control check
   * @param filePath path to file within this content access
   * @param access access type
   * @return if current user has access
   */
  boolean hasAccess(String filePath, FileAccess access);

}
