/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2028-08-13
 ******************************************************************************/


package pt.webdetails.cpf.utils;

import org.apache.commons.lang.StringUtils;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

/**
 * A simple utility class for operations with comma-separated values
 */
public class CsvUtil {
  private static final String COMMA = ",";

  /**
   * An utility method used in the default implementations.
   * Is intentionally left here in order not to implement it in each plugin.
   *
   * @param csvString a comma separated string
   * @return a collection of strings, never a null
   */
  public static Collection<String> parseCsvString( final String csvString ) {
    if ( StringUtils.isNotBlank( csvString ) ) {
      final String[] strings = csvString.split( COMMA );
      return new HashSet<>( Arrays.asList( strings ) );
    }
    return Collections.emptyList();
  }
}
