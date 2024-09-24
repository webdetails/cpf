package pt.webdetails.cpf.impl;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.FilenameUtils;

import pt.webdetails.cpf.api.IFileContent;

public class FileContent implements IFileContent {

  private String name;
  private String path;
  private String fullPath;
  private String title;
  private String description;
  private boolean directory;
  private boolean hidden;
  private InputStream contents;
  
  @Override
  public InputStream getContents() throws IOException {
    return contents;
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public String getFullPath() {
    return fullPath;
  }

  @Override
  public String getPath() {
    return path;
  }

  @Override
  public String getExtension() {
    return FilenameUtils.getExtension( name );
  }

  @Override
  public boolean isDirectory() {
    return directory;
  }

  @Override
  public String getTitle() {
    return title;
  }

  @Override
  public String getDescription() {
    return description;
  }

  @Override
  public boolean isHidden() {
    return hidden;
  }

  public void setName( String name ) {
    this.name = name;
  }

  public void setPath( String path ) {
    this.path = path;
  }

  public void setFullPath( String fullPath ) {
    this.fullPath = fullPath;
  }

  public void setTitle( String title ) {
    this.title = title;
  }

  public void setDescription( String description ) {
    this.description = description;
  }

  public void setDirectory( boolean directory ) {
    this.directory = directory;
  }

  public void setHidden( boolean hidden ) {
    this.hidden = hidden;
  }

  public void setContents( InputStream contents ) {
    this.contents = contents;
  }
}
