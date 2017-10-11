/*!
* Copyright 2002 - 2017 Webdetails, a Hitachi Vantara company.  All rights reserved.
* 
* This software was developed by Webdetails and is provided under the terms
* of the Mozilla Public License, Version 2.0, or any later version. You may not use
* this file except in compliance with the license. If you need a copy of the license,
* please go to  http://mozilla.org/MPL/2.0/. The Initial Developer is Webdetails.
*
* Software distributed under the Mozilla Public License is distributed on an "AS IS"
* basis, WITHOUT WARRANTY OF ANY KIND, either express or  implied. Please refer to
* the license for the specific language governing your rights and limitations.
*/

package pt.webdetails.cpf.repository;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class RepositoryFileExplorer {

  private static final Log logger = LogFactory.getLog( RepositoryFileExplorer.class );

  public static String toJQueryFileTree( String baseDir, IRepositoryFile[] files ) {
    StringBuilder out = new StringBuilder();
    out.append( "<ul class=\"jqueryFileTree\" style=\"display: none;\">" );

    for ( IRepositoryFile file : files ) {
      if ( file.isDirectory() ) {
        out.append(
            "<li class=\"directory collapsed\"><a href=\"#\" rel=\"" + baseDir + file.getFileName() + "/\">" + file
              .getFileName() + "</a></li>" );
      }
    }

    for ( IRepositoryFile file : files ) {
      if ( !file.isDirectory() ) {
        int dotIndex = file.getFileName().lastIndexOf( '.' );
        String ext = dotIndex > 0 ? file.getFileName().substring( dotIndex + 1 ) : "";
        out.append(
            "<li class=\"file ext_" + ext + "\"><a href=\"#\" rel=\"" + baseDir + file.getFileName() + "\">" + file
              .getFileName() + "</a></li>" );
      }
    }
    out.append( "</ul>" );
    return out.toString();
  }

  public static String toJSON( String baseDir, IRepositoryFile[] files ) {

    JSONArray arr = new JSONArray();

    for ( IRepositoryFile file : files ) {
      JSONObject json = new JSONObject();
      try {
        json.put( "path", baseDir );

        json.put( "name", file.getFileName() );
        json.put( "label", file.getFileName() );

        if ( file.isDirectory() ) {
          json.put( "type", "dir" );
        } else {
          int dotIndex = file.getFileName().lastIndexOf( '.' );
          String ext = dotIndex > 0 ? file.getFileName().substring( dotIndex + 1 ) : "";
          json.put( "ext", ext );
          json.put( "type", "file" );
        }
        arr.put( json );
      } catch ( JSONException e ) {
        logger.error( e );
      }
    }

    return arr.toString();
  }
}
