/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2029-07-20
 ******************************************************************************/


package pt.webdetails.cpf.repository;

/**
 * @author dfscm
 * @deprecated use {@link pt.webdetails.cpf.repository.api.IBasicFile}
 */
public interface IRepositoryFile {

  public boolean isDirectory();

  public String getFileName();

  public String getSolutionPath();

  @Deprecated
  public String getSolution();

  public String getFullPath();

  public IRepositoryFile[] listFiles();

  public IRepositoryFile[] listFiles( IRepositoryFileFilter iff );

  //TODO: root of what?
  public boolean isRoot();

  public IRepositoryFile retrieveParent();

  public byte[] getData();

  public boolean exists();

  public long getLastModified();

  public String getExtension();
}
