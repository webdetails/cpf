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
