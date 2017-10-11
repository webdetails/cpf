/*!
 * Copyright 2002 - 2017 Webdetails, a Hitachi Vantara company. All rights reserved.
 *
 * This software was developed by Webdetails and is provided under the terms
 * of the Mozilla Public License, Version 2.0, or any later version. You may not use
 * this file except in compliance with the license. If you need a copy of the license,
 * please go to http://mozilla.org/MPL/2.0/. The Initial Developer is Webdetails.
 *
 * Software distributed under the Mozilla Public License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. Please refer to
 * the license for the specific language governing your rights and limitations.
 */

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
