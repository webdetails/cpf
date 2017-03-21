/*!
 * Copyright 2002 - 2017 Webdetails, a Pentaho company. All rights reserved.
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

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class AbstractCorsUtilTest {

  private AbstractCorsUtil corsUtilSpy;
  private final List<String> fakeLocal = Collections.singletonList( "http://fakelocal:1234" );


  @Before
  public void setUp() throws Exception {
    AbstractCorsUtil corsUtil = new AbstractCorsUtil() {
      @Override protected boolean isCorsAllowed() {
        return false;
      }

      @Override protected Collection<String> getDomainWhitelist() {
        return null;
      }
    };
    corsUtilSpy = Mockito.spy( corsUtil );
  }

  @Test
  public void testCorsDisabled() {
    HttpServletRequest request = Mockito.mock( HttpServletRequest.class );
    Mockito.doReturn( fakeLocal.get( 0 ) ).when( request ).getHeader( "ORIGIN" );
    HttpServletResponse response = Mockito.mock( HttpServletResponse.class );

    Mockito.doReturn( false ).when( corsUtilSpy ).isCorsAllowed();

    Mockito.doReturn( Collections.emptyList() ).when( corsUtilSpy ).getDomainWhitelist();
    corsUtilSpy.setCorsHeaders( request, response );
    Mockito.verify( request, Mockito.never() ).getHeader( "ORIGIN" );
    Mockito.verify( response, Mockito.never() ).setHeader( "Access-Control-Allow-Origin", fakeLocal.get( 0 ) );
    Mockito.verify( response, Mockito.never() ).setHeader( "Access-Control-Allow-Credentials", "true" );
  }

  @Test
  public void testCorsEnabledEmptyWhitelist() {
    HttpServletRequest request = Mockito.mock( HttpServletRequest.class );
    Mockito.doReturn( fakeLocal.get( 0 ) ).when( request ).getHeader( "ORIGIN" );
    HttpServletResponse response = Mockito.mock( HttpServletResponse.class );

    Mockito.doReturn( true ).when( corsUtilSpy ).isCorsAllowed();

    Mockito.doReturn( Collections.emptyList() ).when( corsUtilSpy ).getDomainWhitelist();
    corsUtilSpy.setCorsHeaders( request, response );
    Mockito.verify( request, Mockito.times( 1 ) ).getHeader( "ORIGIN" );
    Mockito.verify( response, Mockito.never() ).setHeader( "Access-Control-Allow-Origin", fakeLocal.get( 0 ) );
    Mockito.verify( response, Mockito.never() ).setHeader( "Access-Control-Allow-Credentials", "true" );
  }

  @Test
  public void testCorsEnabledWrongDomain() {
    HttpServletRequest request = Mockito.mock( HttpServletRequest.class );
    Mockito.doReturn( fakeLocal.get( 0 ) ).when( request ).getHeader( "ORIGIN" );
    HttpServletResponse response = Mockito.mock( HttpServletResponse.class );

    Mockito.doReturn( true ).when( corsUtilSpy ).isCorsAllowed();

    Mockito.doReturn( Collections.singletonList( "www.pentaho.com" ) ).when( corsUtilSpy ).getDomainWhitelist();
    corsUtilSpy.setCorsHeaders( request, response );
    Mockito.verify( request, Mockito.times( 1 ) ).getHeader( "ORIGIN" );
    Mockito.verify( response, Mockito.never() ).setHeader( "Access-Control-Allow-Origin", fakeLocal.get( 0 ) );
    Mockito.verify( response, Mockito.never() ).setHeader( "Access-Control-Allow-Credentials", "true" );
  }

  @Test
  public void testCorsEnabledSuccess() {
    HttpServletRequest request = Mockito.mock( HttpServletRequest.class );
    Mockito.doReturn( fakeLocal.get( 0 ) ).when( request ).getHeader( "ORIGIN" );
    HttpServletResponse response = Mockito.mock( HttpServletResponse.class );

    Mockito.doReturn( true ).when( corsUtilSpy ).isCorsAllowed();

    Mockito.doReturn( fakeLocal ).when( corsUtilSpy ).getDomainWhitelist();
    corsUtilSpy.setCorsHeaders( request, response );
    Mockito.verify( request, Mockito.times( 1 ) ).getHeader( "ORIGIN" );
    Mockito.verify( response, Mockito.times( 1 ) ).setHeader( "Access-Control-Allow-Origin", fakeLocal.get( 0 ) );
    Mockito.verify( response, Mockito.times( 1 ) ).setHeader( "Access-Control-Allow-Credentials", "true" );
  }

  @Test
  public void testEmptyHeader() {
    HttpServletRequest request = Mockito.mock( HttpServletRequest.class );
    Mockito.doReturn( "" ).when( request ).getHeader( "ORIGIN" );
    HttpServletResponse response = Mockito.mock( HttpServletResponse.class );

    Mockito.doReturn( true ).when( corsUtilSpy ).isCorsAllowed();

    Mockito.doReturn( Collections.emptyList() ).when( corsUtilSpy ).getDomainWhitelist();
    corsUtilSpy.setCorsHeaders( request, response );
    Mockito.verify( request, Mockito.times( 1 ) ).getHeader( "ORIGIN" );
    Mockito.verify( response, Mockito.never() ).setHeader( "Access-Control-Allow-Origin", fakeLocal.get( 0 ) );
    Mockito.verify( response, Mockito.never() ).setHeader( "Access-Control-Allow-Credentials", "true" );
  }

}
