/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/. */

package pt.webdetails.cpf.packager;

import java.util.LinkedHashMap;
import java.util.Map;

import pt.webdetails.cpf.packager.dependencies.CssMinifiedDependency;
import pt.webdetails.cpf.packager.dependencies.Dependency;
import pt.webdetails.cpf.packager.dependencies.FileDependency;
import pt.webdetails.cpf.packager.dependencies.JsMinifiedDependency;
import pt.webdetails.cpf.packager.dependencies.PackagedFileDependency;
import pt.webdetails.cpf.packager.dependencies.SnippetDependency;
import pt.webdetails.cpf.context.api.IUrlProvider;
import pt.webdetails.cpf.packager.origin.PathOrigin;
import pt.webdetails.cpf.packager.origin.StaticSystemOrigin;
import pt.webdetails.cpf.repository.api.IContentAccessFactory;
import pt.webdetails.cpf.repository.api.IRWAccess;


/**
 * A set of css|js files that can be packaged into a single file.<br>
 * Encompasses former functionality of DependenciesEngine/Packager<br>
 * will be made abstract, more specific subclasses
 */
public class DependenciesPackage {

//  private static Log logger = LogFactory.getLog(DependenciesPackage.class);

  public enum PackagingMode
  {
    MINIFY, CONCATENATE
  };
  public enum PackageType
  {
    CSS, JS
  };

  private String name;
  private PackageType type;

  protected Map<String, FileDependency> fileDependencies;

  protected PackagedFileDependency packagedDependency;
  protected Object packagingLock = new Object();

  private Map<String, SnippetDependency> rawDependencies;

  protected IContentAccessFactory factory;
  protected IUrlProvider urlProvider;
  
  /**
   * 
   * @param name
   * @param type
   * @param factory
   */
  public DependenciesPackage(String name, PackageType type, IContentAccessFactory factory, IUrlProvider urlProvider) {
    this.name = name;
    fileDependencies = new LinkedHashMap<String, FileDependency>();
    rawDependencies = new LinkedHashMap<String, SnippetDependency>();
    this.type = type;
    this.factory = factory;
    this.urlProvider = urlProvider;
  }

  /**
   * Registers a dependency in this package
   * @param name
   * @param version
   * @param origin
   * @param path
   * @return
   */
  public boolean registerFileDependency(String name, String version, PathOrigin origin, String path) {
    FileDependency newDep = new FileDependency( version, origin, path, urlProvider);
    synchronized( packagingLock ) {
      if (registerDependency( name, newDep, fileDependencies )) {
        //invalidate packaged if there
        packagedDependency = null;
        return true;
      }
    }
    return false;
  }

  public boolean registerRawDependency(String name, String version, String contents) {
    SnippetDependency snip = new SnippetDependency( version, contents );
    return registerDependency( name, snip, rawDependencies );
  }

  protected <T extends Dependency> boolean registerDependency(String name, T dependency, Map<String, T> registry) {
    Dependency dep = registry.get(name);
    if (dep == null || dep.isOlderVersionThan( dependency )) {
      registry.put( name, dependency);
      return true;
    }
    return false;
  }

  /**
   * Get references to the dependencies with customized output.
   * @param format receives file path strings
   * @param isPackaged if to return a single compressed file
   * @return script or link tag with file references
   */
  public String getDependencies(StringFilter format, boolean isPackaged) {
    return isPackaged ?
        getPackagedDependency( format ):
        getUnpackagedDependencies( format );
  }

  public String getRawDependencies(boolean isPackaged) {
    StringBuilder sb = new StringBuilder();
    for (SnippetDependency dep : rawDependencies.values()) {
      sb.append( dep.getContents() );
      sb.append( '\n' );
    }
    return sb.toString();
  }

  /**
   * Get references to the dependencies.
   * @param isPackaged if to return a single compressed file
   * @return script or link tag with file references
   */
  public String getDependencies(boolean isPackaged) {
    return getDependencies( getDefaultStringFilter( type ), isPackaged );
  }

  public String getName() {
    return name;
  }

  public String getUnpackagedDependencies( StringFilter format ) {
    StringBuilder sb = new StringBuilder();
    sb.append( "\n" );
    // sb.append( "\t<!-- " + getName() + "-->\n" );
    for ( Dependency dep : fileDependencies.values() ) {
      sb.append( format.filter( dep.getDependencyInclude() ) );
    }
    return sb.toString();
  }

  protected String getPackagedDependency( StringFilter format ) {
    synchronized ( packagingLock ) {
      if ( packagedDependency == null ) {
        String packagedPath = name + "." + type.toString().toLowerCase();
        String baseDir = type.toString().toLowerCase();
        IRWAccess writer = factory.getPluginSystemWriter( baseDir );
        PathOrigin origin = new StaticSystemOrigin( baseDir );
        switch ( type ) {
          case CSS:
            packagedDependency =
                new CssMinifiedDependency( origin, packagedPath, writer, fileDependencies.values(), urlProvider );
            break;
          case JS:
            packagedDependency =
                new JsMinifiedDependency( origin, packagedPath, writer, fileDependencies.values(), urlProvider );
            break;
          default:
            throw new IllegalStateException( getClass().getSimpleName() + " does not have a recognized type: " + type );
        }
      }
      return format.filter( packagedDependency.getDependencyInclude() );
    }
  }

  public StringFilter getDefaultFilter() {
    return getDefaultStringFilter( this.type );
  }

  private static StringFilter getDefaultStringFilter( PackageType type ) {
    switch ( type ) {
      case CSS:
        return new StringFilter() {
          public String filter( String input ) {
            return String.format( "\t\t<link href=\"%s\" rel=\"stylesheet\" type=\"text/css\" />\n", input );
          }
        };
      case JS:
        return new StringFilter() {
          public String filter( String input ) {
            return String.format(
                "\t\t<script language=\"javascript\" type=\"text/javascript\" src=\"%s\"></script>\n", input );
          }
        };
      default:
        return new StringFilter() {
          public String filter( String input ) {
            return input + "\n";
          }
        };
    }
  }

}
