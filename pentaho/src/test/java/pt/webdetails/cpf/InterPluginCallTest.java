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

package pt.webdetails.cpf;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.pentaho.platform.api.engine.IPluginManager;
import org.springframework.beans.factory.ListableBeanFactory;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

public class InterPluginCallTest {

  private InterPluginCall.Plugin plugin = null;
  private InterPluginCall interPluginCall = null;
  private static final String TEST_RESULT_STRING = "Test is successful!!";

  private static final String METHOD_RETURNING_RESPONSE = "testMethodReturningResponse";
  private static final String METHOD_RETURNING_RESPONSE_WITH_TO_STRING_OVERRIDDEN =
    "testMethodReturningResponseToStringOverridden";
  private static final String METHOD_RETURNING_RESPONSE_WITH_TO_STRING_NOT_OVERRIDDEN =
    "testMethodReturningResponseToStringNotOverridden";
  private static final String METHOD_WRITING_CONTEXT_HTTP_SERVLET_RESPONSE = "testMethodWritingHttpServletResponse";
  private static final String METHOD_RETURNING_STRING = "testMethodReturningString";

  public static class BeanTest {

    public static class AnyObjectWithToStringOverridden {
      @Override
      public String toString() {
        return TEST_RESULT_STRING;
      }
    }

    public static class AnyObjectWithToStringNotOverridden {
    }

    // Note: Methods in this class are to be called through reflection by InterPluginCall

    public Response testMethodReturningResponse() {
      return Response.ok( TEST_RESULT_STRING ).build();
    }
    public Response testMethodReturningResponseToStringOverridden() {
      return Response.ok( new AnyObjectWithToStringOverridden() ).build();
    }
    public Response testMethodReturningResponseToStringNotOverridden() {
      return Response.ok( new AnyObjectWithToStringNotOverridden() ).build();
    }
    public void testMethodWritingHttpServletResponse( @Context HttpServletResponse response ) throws IOException {
      if ( response != null ) {
        response.getOutputStream().write( TEST_RESULT_STRING.getBytes() );
        response.getOutputStream().flush();
      }
    }
    public String testMethodReturningString() {
      return TEST_RESULT_STRING;
    }
  }

  @Before
  public void setUp() {
    plugin = new InterPluginCall.Plugin( "BeanTest", "Bean to test Inter Plugin Calls" );
  }

  private void initializeInterPluginCallForTesting( String method ) {
    InterPluginCall ipc = new InterPluginCall( plugin, method );
    interPluginCall = spy( ipc );

    ListableBeanFactory beanFactory = mock( ListableBeanFactory.class );
    when( beanFactory.containsBean( ipc.getService() ) ).thenReturn( true );
    when( beanFactory.getBean( ipc.getService() ) ).thenReturn( new BeanTest() );

    IPluginManager pluginManager = mock( IPluginManager.class );
    when( pluginManager.getBeanFactory( plugin.getName() ) ).thenReturn( beanFactory );

    when( interPluginCall.getPluginManager() ).thenReturn( pluginManager );
  }

  @Test
  public void testCallingMethodReturningResponse() {
    initializeInterPluginCallForTesting( METHOD_RETURNING_RESPONSE );
    String actualResult = interPluginCall.call();
    assertEquals( TEST_RESULT_STRING, actualResult );
  }

  @Test
  public void testCallingMethodReturningResponseWithToStringOverridden() {
    initializeInterPluginCallForTesting( METHOD_RETURNING_RESPONSE_WITH_TO_STRING_OVERRIDDEN );
    String actualResult = interPluginCall.call();
    assertEquals( TEST_RESULT_STRING, actualResult );
  }

  @Test
  public void testCallingMethodReturningResponseWithToStringNotOverridden() {
    initializeInterPluginCallForTesting( METHOD_RETURNING_RESPONSE_WITH_TO_STRING_NOT_OVERRIDDEN );
    String actualResult = interPluginCall.call();
    assertEquals( "", actualResult );
  }

  @Test
  public void testCallingMethodWritingContextHttpServletResponse() {
    initializeInterPluginCallForTesting( METHOD_WRITING_CONTEXT_HTTP_SERVLET_RESPONSE );
    String actualResult = interPluginCall.call();
    assertEquals( TEST_RESULT_STRING, actualResult );
  }

  @Test
  public void testCallingMethodReturningString() {
    initializeInterPluginCallForTesting( METHOD_RETURNING_STRING );
    String actualResult = interPluginCall.call();
    assertEquals( TEST_RESULT_STRING, actualResult );
  }

  @After
  public void tearDown() {
    plugin = null;
    interPluginCall = null;
  }
}
