/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2029-07-20
 ******************************************************************************/


package pt.webdetails.cpf.utils;

import org.junit.Before;
import org.junit.Test;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class AbstractCorsUtilTest {

  private AbstractCorsUtil corsUtilSpy;
  private final List<String> fakeLocal = Collections.singletonList( "http://fakelocal:1234" );


  @Before
  public void setUp() {
    AbstractCorsUtil corsUtil = new AbstractCorsUtil() {
      @Override protected boolean isCorsAllowed() {
        return false;
      }

      @Override protected Collection<String> getDomainWhitelist() {
        return null;
      }
    };
    corsUtilSpy = spy( corsUtil );
  }

  @Test
  public void testCorsDisabled() {
    HttpServletRequest request = mock( HttpServletRequest.class );
    doReturn( fakeLocal.get( 0 ) ).when( request ).getHeader( "ORIGIN" );
    HttpServletResponse response = mock( HttpServletResponse.class );

    doReturn( false ).when( corsUtilSpy ).isCorsAllowed();

    doReturn( Collections.emptyList() ).when( corsUtilSpy ).getDomainWhitelist();
    corsUtilSpy.setCorsHeaders( request, response );
    verify( request, never() ).getHeader( "ORIGIN" );
    verify( response, never() ).setHeader( "Access-Control-Allow-Origin", fakeLocal.get( 0 ) );
    verify( response, never() ).setHeader( "Access-Control-Allow-Credentials", "true" );
  }

  @Test
  public void testCorsEnabledEmptyWhitelist() {
    HttpServletRequest request = mock( HttpServletRequest.class );
    doReturn( fakeLocal.get( 0 ) ).when( request ).getHeader( "ORIGIN" );
    HttpServletResponse response = mock( HttpServletResponse.class );

    doReturn( true ).when( corsUtilSpy ).isCorsAllowed();

    doReturn( Collections.emptyList() ).when( corsUtilSpy ).getDomainWhitelist();
    corsUtilSpy.setCorsHeaders( request, response );
    verify( request, times( 1 ) ).getHeader( "ORIGIN" );
    verify( response, never() ).setHeader( "Access-Control-Allow-Origin", fakeLocal.get( 0 ) );
    verify( response, never() ).setHeader( "Access-Control-Allow-Credentials", "true" );
  }

  @Test
  public void testCorsEnabledWrongDomain() {
    HttpServletRequest request = mock( HttpServletRequest.class );
    doReturn( fakeLocal.get( 0 ) ).when( request ).getHeader( "ORIGIN" );
    HttpServletResponse response = mock( HttpServletResponse.class );

    doReturn( true ).when( corsUtilSpy ).isCorsAllowed();

    doReturn( Collections.singletonList( "www.pentaho.com" ) ).when( corsUtilSpy ).getDomainWhitelist();
    corsUtilSpy.setCorsHeaders( request, response );
    verify( request, times( 1 ) ).getHeader( "ORIGIN" );
    verify( response, never() ).setHeader( "Access-Control-Allow-Origin", fakeLocal.get( 0 ) );
    verify( response, never() ).setHeader( "Access-Control-Allow-Credentials", "true" );
  }

  @Test
  public void testCorsEnabledSuccess() {
    HttpServletRequest request = mock( HttpServletRequest.class );
    doReturn( fakeLocal.get( 0 ) ).when( request ).getHeader( "ORIGIN" );
    HttpServletResponse response = mock( HttpServletResponse.class );

    doReturn( true ).when( corsUtilSpy ).isCorsAllowed();

    doReturn( fakeLocal ).when( corsUtilSpy ).getDomainWhitelist();
    corsUtilSpy.setCorsHeaders( request, response );
    verify( request, times( 1 ) ).getHeader( "ORIGIN" );
    verify( response, times( 1 ) ).setHeader( "Access-Control-Allow-Origin", fakeLocal.get( 0 ) );
    verify( response, times( 1 ) ).setHeader( "Access-Control-Allow-Credentials", "true" );
  }

  @Test
  public void testEmptyHeader() {
    HttpServletRequest request = mock( HttpServletRequest.class );
    doReturn( "" ).when( request ).getHeader( "ORIGIN" );
    HttpServletResponse response = mock( HttpServletResponse.class );

    doReturn( true ).when( corsUtilSpy ).isCorsAllowed();

    doReturn( Collections.emptyList() ).when( corsUtilSpy ).getDomainWhitelist();
    corsUtilSpy.setCorsHeaders( request, response );
    verify( request, times( 1 ) ).getHeader( "ORIGIN" );
    verify( response, never() ).setHeader( "Access-Control-Allow-Origin", fakeLocal.get( 0 ) );
    verify( response, never() ).setHeader( "Access-Control-Allow-Credentials", "true" );
  }
}
