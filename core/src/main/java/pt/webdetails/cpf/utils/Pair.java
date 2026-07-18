/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 - 2026 by Pentaho Canada Inc. : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2030-06-15
 ******************************************************************************/

package pt.webdetails.cpf.utils;

/**
 * Struct for two objects. For when you don't really need a map.
 */
public class Pair<T, U> {

  public Pair( T first, U second ) {
    this.first = first;
    this.second = second;
  }

  public Pair() {
  }

  public T first;
  public U second;

}
