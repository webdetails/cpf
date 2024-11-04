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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.io.SAXReader;
import org.xml.sax.EntityResolver;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;

public class XmlParserFactoryProducer {
  private static final Log logger = LogFactory.getLog( XmlParserFactoryProducer.class );

  /**
   * Creates an instance of {@link SAXReader} class
   * with features that prevent from some XXE attacks (e.g. XML bomb)
   * See PPP-3506 for more details.
   * See also https://www.owasp.org/index.php/XML_External_Entity_(XXE)_Prevention_Cheat_Sheet
   *
   * @param resolver Is {@link EntityResolver} or null
   * @return {@link SAXReader}
   */
  public static SAXReader getSAXReader( final EntityResolver resolver ) {
    SAXReader reader = new SAXReader();
    if ( resolver != null ) {
      reader.setEntityResolver( resolver );
    }
    try {
      reader.setFeature( XMLConstants.FEATURE_SECURE_PROCESSING, true );
      reader.setFeature( "http://xml.org/sax/features/external-general-entities", false );
      reader.setFeature( "http://xml.org/sax/features/external-parameter-entities", false );
      reader.setFeature( "http://apache.org/xml/features/nonvalidating/load-external-dtd", false );
    } catch ( SAXException e ) {
      logger.error( "Some parser properties are not supported." );
    }
    reader.setIncludeExternalDTDDeclarations( false );
    reader.setIncludeInternalDTDDeclarations( false );
    return reader;
  }
}
