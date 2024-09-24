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
package pt.webdetails.cpf.utils;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.Node;
import pt.webdetails.cpf.repository.api.IBasicFile;
import pt.webdetails.cpf.repository.api.IReadAccess;

import java.io.IOException;
import java.io.InputStream;

public class XmlDom4JUtils {

  protected static Log logger = LogFactory.getLog( XmlDom4JUtils.class );

  public static Document getDocumentFromStream( InputStream is ) {

    if ( is == null ) {
      return null;
    }

    try {
      return XmlParserFactoryProducer.getSAXReader( null ).read( is );
    } catch ( Exception ex ) {
      logger.error( ex );
    } finally {
      IOUtils.closeQuietly( is );
    }
    return null;
  }

  public static Document getDocumentFromFile( final IBasicFile file ) throws IOException {
    return ( file != null ) ? getDocumentFromStream( file.getContents() ) : null;
  }

  public static Document getDocumentFromFile( final IReadAccess access, final String filePath ) throws IOException {
    if ( access != null && filePath != null && access.fileExists( filePath ) ) {
      return getDocumentFromFile( access.fetchFile( filePath ) );
    }
    return null;
  }

  public static String getNodeText( final String xpath, final Node node ) {
    return getNodeText( xpath, node, null );
  }

  public static String getNodeText( final String xpath, final Node node, final String defaultValue ) {
    if ( node == null ) {
      return defaultValue;
    }
    Node n = node.selectSingleNode( xpath );
    if ( n == null ) {
      return defaultValue;
    }
    return n.getText();
  }
}
