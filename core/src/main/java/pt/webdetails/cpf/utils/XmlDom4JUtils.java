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
