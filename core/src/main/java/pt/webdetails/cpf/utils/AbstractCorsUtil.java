/*!
 * Copyright 2002 - 2018 Webdetails, a Hitachi Vantara company. All rights reserved.
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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Collection;

/**
 * An abstract class to provide a carcass for a CORS implementation in plugins.
 * The idea is in populating an HTTP response with a CORS headers, allowing access
 * from a domain specified by a user in an {@link #ORIGIN} header if it is also listed
 * in an whitelist {@link #getDomainWhitelist()}.
 */
public abstract class AbstractCorsUtil {

  private static final String TRUE = "true";
  private static final String ORIGIN = "ORIGIN";
  private static final String ACCESS_CONTROL_ALLOW_ORIGIN = "Access-Control-Allow-Origin";
  private static final String ACCESS_CONTROL_ALLOW_CREDENTIALS = "Access-Control-Allow-Credentials";

  /**
   * The value is assumed to be retrieved from a plugin configuration file.
   * In the default implementation we use a property with a name <i>allow-cross-domain-resources</i>,
   * so it is a good idea to use the same name in your own implementation.
   *
   * @return {@link java.lang.Boolean#TRUE} if cross-domain calls are allowed
   */
  protected abstract boolean isCorsAllowed();

  /**
   * The values are assumed to be retrieved from a plugin configuration file.
   * In the default implementation we use a property with a name <i>cross-domain-resources-whitelist</i>,
   * so it is a good idea to use the same name in your own implementation
   *
   * @return collection of allowed domains
   */
  protected abstract Collection<String> getDomainWhitelist();

  /**
   * Sets CORS headers to the response if the domain provided by a user is in a whitelist
   *
   * @param request  a request, nothing special
   * @param response a response, nothing special
   */
  public void setCorsHeaders( HttpServletRequest request, HttpServletResponse response ) {
    if ( isCorsAllowed() ) {
      final String origin = request.getHeader( ORIGIN );
      if ( isDomainAllowed( origin ) ) {
        response.setHeader( ACCESS_CONTROL_ALLOW_ORIGIN, origin );
        response.setHeader( ACCESS_CONTROL_ALLOW_CREDENTIALS, TRUE );
      }
    }
  }


  /**
   * Checks if a domain is a whitelist provided by a {@link #getDomainWhitelist()}.
   *
   * @param untrustedDomain a value provided by a user, not safe
   * @return If the parameter value is empty, not present in a whitelist, or there is no values in a list {@link
   * java.lang.Boolean#FALSE} is returned. Otherwise {@link java.lang.Boolean#TRUE is returned}
   */
  private boolean isDomainAllowed( final String untrustedDomain ) {

    if ( StringUtils.isBlank( untrustedDomain ) ) {
      return false;
    }

    final Collection<String> domainWhitelist = getDomainWhitelist();

    if ( domainWhitelist == null || domainWhitelist.isEmpty() ) {
      return false;
    }

    for ( String domain : domainWhitelist ) {
      if ( StringUtils.equalsIgnoreCase( domain, untrustedDomain ) ) {
        return true;
      }
    }

    return false;
  }

}
