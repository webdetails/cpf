package pt.webdetails.cpf.repository.api;

public interface IACAccess {

  boolean hasAccess(String filePath, FileAccess access);

}
