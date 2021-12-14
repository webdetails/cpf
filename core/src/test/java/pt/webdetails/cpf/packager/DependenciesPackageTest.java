/*!
 * Copyright 2002 - 2021 Webdetails, a Hitachi Vantara company.  All rights reserved.
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

package pt.webdetails.cpf.packager;

import org.junit.Before;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import pt.webdetails.cpf.context.api.IUrlProvider;
import pt.webdetails.cpf.packager.dependencies.CssMinifiedDependency;
import pt.webdetails.cpf.packager.dependencies.Dependency;
import pt.webdetails.cpf.packager.dependencies.JsMinifiedDependency;
import pt.webdetails.cpf.packager.dependencies.MapDependency;
import pt.webdetails.cpf.packager.origin.PathOrigin;
import pt.webdetails.cpf.repository.api.IContentAccessFactory;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class DependenciesPackageTest {

  private static DependenciesPackage jsDepPackage;
  private static DependenciesPackage cssDepPackage;
  private static DependenciesPackage mapDepPackage;

  private static final String JS_PACKAGE_NAME = "js-package";
  private static final String CSS_PACKAGE_NAME = "css-package";
  private static final String MAP_FILE_NAME = "map-file.css.map";

  private static IContentAccessFactory mockFactory;
  private static IUrlProvider mockUrlProvider;
  private static PathOrigin mockPathOrigin;

  @Before
  public void setUp() {
    mockFactory = mock( IContentAccessFactory.class );
    mockUrlProvider = mock( IUrlProvider.class );
    mockPathOrigin = mock( PathOrigin.class );
    when( mockPathOrigin.getUrl( anyString(), any( IUrlProvider.class ) ) ).thenAnswer( new Answer<String>() {
      @Override
      public String answer( InvocationOnMock invocation ) throws Throwable {
        return (String) invocation.getArguments()[0];
      }
    } );

    jsDepPackage =
      new DependenciesPackage( JS_PACKAGE_NAME, DependenciesPackage.PackageType.JS, mockFactory, mockUrlProvider );

    cssDepPackage =
      new DependenciesPackage( CSS_PACKAGE_NAME, DependenciesPackage.PackageType.CSS, mockFactory, mockUrlProvider );

    mapDepPackage =
      new DependenciesPackage( MAP_FILE_NAME, DependenciesPackage.PackageType.MAP, mockFactory, mockUrlProvider );
  }

  @Test
  public void testRegisterFileDependency() {
    String[] fileNames = new String[]{"file1", "file2"};
    String[] fileVersions = new String[]{"v1", "v2"};
    String[] filePaths = new String[]{"path1", "path2"};

    for ( int i = 0; i < fileNames.length; i++ ) {
      assertTrue( jsDepPackage.registerFileDependency(
          fileNames[ i ] + ".js", fileVersions[ i ], mockPathOrigin, filePaths[ i ] ) );
      assertTrue( cssDepPackage.registerFileDependency(
          fileNames[ i ] + ".css", fileVersions[ i ], mockPathOrigin, filePaths[ i ] ) );
      assertTrue( mapDepPackage.registerFileDependency(
          fileNames[ i ] + ".css.map", fileVersions[ i ], mockPathOrigin, filePaths[ i ] ) );
    }
  }

  @Test
  public void testRegisterRawDependency() {
    String[] fileNames = new String[]{"file1", "file2"};
    String[] fileVersions = new String[]{"v1", "v2"};
    String[] fileContents = new String[]{"content1", "content2"};

    for ( int i = 0; i < fileNames.length; i++ ) {
      assertTrue( jsDepPackage.registerRawDependency(
          fileNames[ i ] + ".js", fileVersions[ i ], fileContents[ i ] ) );
      assertTrue( cssDepPackage.registerRawDependency(
          fileNames[ i ] + ".css", fileVersions[ i ], fileContents[ i ] ) );
      assertTrue( mapDepPackage.registerRawDependency(
          fileNames[ i ] + ".css.map", fileVersions[ i ], fileContents[ i ] ) );
    }
  }

  @Test
  public void testRegisterDependency() {
    String[] fileNames = new String[]{"file1", "file2"};
    Map<String, Dependency> registry = new HashMap<>();

    JsMinifiedDependency jsMinifiedDependency = mock( JsMinifiedDependency.class );
    CssMinifiedDependency cssMinifiedDependency = mock( CssMinifiedDependency.class );
    MapDependency mapDependency = mock( MapDependency.class );

    for ( String fileName : fileNames ) {
      assertTrue( jsDepPackage.registerDependency(
        fileName + ".js", jsMinifiedDependency, registry ) );
      assertTrue( cssDepPackage.registerDependency(
        fileName + ".css", cssMinifiedDependency, registry ) );
      assertTrue( mapDepPackage.registerDependency(
        fileName + ".css.map", mapDependency, registry ) );
    }
    assertEquals( fileNames.length * 3, registry.size() );
  }

  @Test
  public void testRegisterNewerDependency() {
    String jsFileName = "file.js";
    String cssFileName = "file.css";
    String mapFileName = "file.css.map";
    Map<String, Dependency> registry = new HashMap<>();

    // Each array contains three (3) dependencies whose versions order like the following:
    // [0] older than [2] older than [1]
    // This means that, at the end, the registry should hold [1]
    int testSize = 3;
    JsMinifiedDependency[] jsMinifiedDependency = new JsMinifiedDependency[ testSize ];
    CssMinifiedDependency[] cssMinifiedDependency = new CssMinifiedDependency[ testSize ];
    MapDependency[] mapDependency = new MapDependency[ testSize ];

    // Mock [0]
    jsMinifiedDependency[ 0 ] = mock( JsMinifiedDependency.class );
    doCallRealMethod().when( jsMinifiedDependency[ 0 ] ).isOlderVersionThan( any() );
    doReturn( "11111" ).when( jsMinifiedDependency[ 0 ] ).getVersion();
    cssMinifiedDependency[ 0 ] = mock( CssMinifiedDependency.class );
    doCallRealMethod().when( cssMinifiedDependency[ 0 ] ).isOlderVersionThan( any() );
    doReturn( "11111" ).when( cssMinifiedDependency[ 0 ] ).getVersion();
    mapDependency[ 0 ] = mock( MapDependency.class );
    doCallRealMethod().when( mapDependency[ 0 ] ).isOlderVersionThan( any() );
    doReturn( "11111" ).when( mapDependency[ 0 ] ).getVersion();

    // Mock [1]
    jsMinifiedDependency[ 1 ] = mock( JsMinifiedDependency.class );
    doCallRealMethod().when( jsMinifiedDependency[ 1 ] ).isOlderVersionThan( any() );
    doReturn( "55555" ).when( jsMinifiedDependency[ 1 ] ).getVersion();
    cssMinifiedDependency[ 1 ] = mock( CssMinifiedDependency.class );
    doCallRealMethod().when( cssMinifiedDependency[ 1 ] ).isOlderVersionThan( any() );
    doReturn( "55555" ).when( cssMinifiedDependency[ 1 ] ).getVersion();
    mapDependency[ 1 ] = mock( MapDependency.class );
    doCallRealMethod().when( mapDependency[ 1 ] ).isOlderVersionThan( any() );
    doReturn( "55555" ).when( mapDependency[ 1 ] ).getVersion();

    // Mock [2]
    jsMinifiedDependency[ 2 ] = mock( JsMinifiedDependency.class );
    doCallRealMethod().when( jsMinifiedDependency[ 2 ] ).isOlderVersionThan( any() );
    doReturn( "33333" ).when( jsMinifiedDependency[ 2 ] ).getVersion();
    cssMinifiedDependency[ 2 ] = mock( CssMinifiedDependency.class );
    doCallRealMethod().when( cssMinifiedDependency[ 2 ] ).isOlderVersionThan( any() );
    doReturn( "33333" ).when( cssMinifiedDependency[ 2 ] ).getVersion();
    mapDependency[ 2 ] = mock( MapDependency.class );
    doCallRealMethod().when( mapDependency[ 2 ] ).isOlderVersionThan( any() );
    doReturn( "33333" ).when( mapDependency[ 2 ] ).getVersion();

    // It is expected to register the first two dependencies ([0] and [1]) and fail the last ([2])
    Boolean[] registerDependencyExpectedReturn = new Boolean[] { Boolean.TRUE, Boolean.TRUE, Boolean.FALSE };

    // Register
    for ( int i = 0; i < testSize; ++i ) {
      assertEquals( registerDependencyExpectedReturn[ i ], jsDepPackage.registerDependency(
        jsFileName, jsMinifiedDependency[ i ], registry ) );
      assertEquals( registerDependencyExpectedReturn[ i ], cssDepPackage.registerDependency(
        cssFileName, cssMinifiedDependency[ i ], registry ) );
      assertEquals( registerDependencyExpectedReturn[ i ], mapDepPackage.registerDependency(
        mapFileName, mapDependency[ i ], registry ) );
    }

    // Only one dependency for each file
    assertEquals( testSize, registry.size() );
    // And the dependency should be the newest ([1])
    assertEquals( jsMinifiedDependency[ 1 ], registry.get( jsFileName ) );
    assertEquals( cssMinifiedDependency[ 1 ], registry.get( cssFileName ) );
    assertEquals( mapDependency[ 1 ], registry.get( mapFileName ) );
  }

  @Test
  public void testGetDependencies() {

    String jsPackagedDeps = jsDepPackage.getDependencies( true ).trim();
    assertEquals( "<script language=\"javascript\" type=\"text/javascript\" src=\"/js/"
        + JS_PACKAGE_NAME + ".js\"></script>", jsPackagedDeps );

    String cssPackagedDeps = cssDepPackage.getDependencies( true ).trim();
    assertEquals( "<link href=\"/css/"
        + CSS_PACKAGE_NAME + ".css\" rel=\"stylesheet\" type=\"text/css\" />", cssPackagedDeps );

    String mapPackagedDeps = mapDepPackage.getDependencies( true ).trim();
    assertEquals( "", mapPackagedDeps );

    String[] filePaths = new String[]{"path1", "path2"};
    addFileDependencies( filePaths );

    String jsUnpackagedDeps = jsDepPackage.getDependencies( false ).replaceAll( "\n", "" ).replaceAll( "\t", "" );
    String jsUnpackedExpected = "";
    for ( String path : filePaths ) {
      jsUnpackedExpected +=
        "<script language=\"javascript\" type=\"text/javascript\" src=\"" + path + "\"></script>";
    }
    assertEquals( jsUnpackedExpected, jsUnpackagedDeps );

    String cssUnpackagedDeps = cssDepPackage.getDependencies( false ).replaceAll( "\n", "" ).replaceAll( "\t", "" );
    String cssUnpackedExpected = "";
    for ( String filePath : filePaths ) {
      cssUnpackedExpected +=
        "<link href=\"" + filePath + "\" rel=\"stylesheet\" type=\"text/css\" />";
    }
    assertEquals( cssUnpackedExpected, cssUnpackagedDeps );

    String mapUnpackagedDeps = mapDepPackage.getDependencies( false ).trim();
    assertEquals( "", mapUnpackagedDeps );
  }

  @Test
  public void testGetDefaultFilter() {
    StringFilter jsFilter = jsDepPackage.getDefaultFilter();
    StringFilter cssFilter = cssDepPackage.getDefaultFilter();
    StringFilter mapFilter = mapDepPackage.getDefaultFilter();

    assertEquals( "<script language=\"javascript\" type=\"text/javascript\" src=\"JS-FILTER\"></script>",
        jsFilter.filter( "JS-FILTER" ).trim() );
    assertEquals( "<link href=\"CSS-FILTER\" rel=\"stylesheet\" type=\"text/css\" />",
        cssFilter.filter( "CSS-FILTER" ).trim() );
    assertEquals( "", mapFilter.filter( "MAP-FILTER" ) );
  }

  @Test
  public void testGetDefaultFilterEscapesUntrustedBaseUrl() {
    StringFilter jsFilter = jsDepPackage.getDefaultFilter();
    StringFilter cssFilter = cssDepPackage.getDefaultFilter();
    StringFilter mapFilter = mapDepPackage.getDefaultFilter();

    String untrustedBaseUrl = "http://foo\"/";

    assertEquals( "<script language=\"javascript\" type=\"text/javascript\" src=\"http://foo&#34;/JS-FILTER\"></script>",
            jsFilter.filter( "JS-FILTER", untrustedBaseUrl ).trim() );

    assertEquals( "<link href=\"http://foo&#34;/CSS-FILTER\" rel=\"stylesheet\" type=\"text/css\" />",
            cssFilter.filter( "CSS-FILTER", untrustedBaseUrl ).trim() );

    assertEquals( "", mapFilter.filter( "MAP-FILTER", untrustedBaseUrl ) );
  }

  private static void addFileDependencies( String[] filePaths ) {
    String[] fileNames = new String[]{"file1", "file2"};
    String[] fileVersions = new String[]{"v1", "v2"};
    for ( int i = 0; i < fileNames.length; i++ ) {
      jsDepPackage.registerFileDependency( fileNames[ i ] + ".js", fileVersions[ i ], mockPathOrigin, filePaths[ i ] );
      cssDepPackage.registerFileDependency(
          fileNames[ i ] + ".css", fileVersions[ i ], mockPathOrigin, filePaths[ i ] );
      mapDepPackage.registerFileDependency(
          fileNames[ i ] + ".css.map", fileVersions[ i ], mockPathOrigin, filePaths[ i ] );
    }
  }
}
