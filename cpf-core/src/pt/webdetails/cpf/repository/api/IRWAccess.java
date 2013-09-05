package pt.webdetails.cpf.repository.api;

import java.io.InputStream;

public interface IRWAccess extends IReadAccess {

  boolean saveFile(String path, InputStream contents);
  boolean copyFile(String pathFrom, String pathTo);
  boolean deleteFile(String path);

}
