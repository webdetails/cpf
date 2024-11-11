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

package pt.webdetails.cpf.bean;

public interface IBeanFactory {

  String getSpringXMLFilename();

  Object getBean( String id );

  boolean containsBean( String id );

  String[] getBeanNamesForType( Class<?> clazz );

}
