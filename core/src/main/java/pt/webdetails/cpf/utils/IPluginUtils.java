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

import org.dom4j.DocumentException;
import pt.webdetails.cpf.http.ICommonParameterProvider;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Collection;
import java.util.Map;

/**
 * @author joao
 */
//TODO: Utils names generally used for static helper classes, change this to something descriptive
public interface IPluginUtils {

  public File getPluginDirectory();

  public void setPluginDirectory( File pluginDirectory );

  public String getPluginName();

  public void setPluginName( String pluginName );

  public void initialize() throws IOException, DocumentException;

  public Collection<File> getPluginResources( String elementPath, Boolean recursive, String pattern );

  public String getPluginRelativeDirectory( String fullPath, boolean includePluginDir ) throws FileNotFoundException;

  public Collection<File> getPluginResources( String elementPath, Boolean recursive );

  public Collection<File> getPluginResources( String elementPath, String pattern );

  public void setResponseHeaders( Map<String, ICommonParameterProvider> parameterProviders, final String mimeType );

  public void setResponseHeaders( Map<String, ICommonParameterProvider> parameterProviders, final String mimeType,
                                  final String attachmentName, long attachmentSize );

  public void redirect( Map<String, ICommonParameterProvider> parameterProviders, String url );

  public ICommonParameterProvider getRequestParameters( Map<String, ICommonParameterProvider> parameterProviders );

  public ICommonParameterProvider getPathParameters( Map<String, ICommonParameterProvider> parameterProviders );

  public OutputStream getOutputStream( Map<String, ICommonParameterProvider> parameterProviders ) throws IOException;

}
