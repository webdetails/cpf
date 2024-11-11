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

package pt.webdetails.cpf.persistence;


import java.util.Collection;
import java.util.List;

public interface ISimplePersistence {

  public void storeAll( Collection<? extends Persistable> items );
  public void deleteAll( Collection<? extends Persistable> items );
  public void delete( Class<? extends Persistable> klass, Filter filter );
  public <T extends Persistable> List<T> load( Class<T> klass, Filter filter );
  public <T extends Persistable> List<T> loadAll( Class<T> klass );

}
