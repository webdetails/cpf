/*!
 * Copyright 2018 Webdetails, a Hitachi Vantara company. All rights reserved.
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
package org.pentaho.ctools.cpf.repository.factory;

import java.io.File;
import java.nio.file.FileSystems;
import java.nio.file.FileSystem;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.ctools.cpf.repository.bundle.ReadAccessProxy;
import org.pentaho.ctools.cpf.repository.bundle.UserContentAccess;
import org.pentaho.ctools.cpf.repository.utils.FileSystemRWAccess;
import org.pentaho.ctools.cpf.repository.utils.OverlayRWAccess;
import org.pentaho.ctools.cpf.repository.utils.OverlayUserContentAccess;
import pt.webdetails.cpf.api.IContentAccessFactoryExtended;
import pt.webdetails.cpf.api.IUserContentAccessExtended;
import pt.webdetails.cpf.repository.api.IReadAccess;
import pt.webdetails.cpf.repository.api.IRWAccess;


/**
 * The {@code ContentAccessFactory} class creates access providers for user content {@code IUserContentAccessExtended}
 * and plugin content ({@code IReadAccess} or {@code IRWAccess}) for OSGi environments.
 *
 * User content is backed by a supplied instance of {@code IUserContentAccessExtended} and plugin content is backed
 * by the filesystem. Plugin content has separate namespaces (folders) for plugin-system (/system/&lt;plugin-id&gt;/)
 * and plugin-repos (/repos/), with the plugin-system segregated by plugin-id.
 *
 * Both user content and plugin-system content can be augmented by supplying instances of {@code IReadAccess} with
 * appropriate properties defined: (isUserContent = true) for user content and (pluginId) for plugin content. These are
 * combined with the base providers, supplying read-only content for paths not found in the base providers.
 *
 * @see IContentAccessFactoryExtended
 * @see IUserContentAccessExtended
 * @see IReadAccess
 * @see IRWAccess
 */
public final class ContentAccessFactory implements IContentAccessFactoryExtended {
  private static final Log logger = LogFactory.getLog( ContentAccessFactory.class );
  private static final String SERVICE_PROPERTY_PLUGIN_ID = "pluginId";
  private static final String SERVICE_PROPERTY_IS_USER_CONTENT = "isUserContent";
  private static final String PLUGIN_REPOS_NAMESPACE = "repos";
  private static final String PLUGIN_SYSTEM_NAMESPACE = "system";
  private Map<String, List<IReadAccess>> pluginReadAccessMap = new HashMap<>();
  private List<IReadAccess> userContentReadAccesses = new ArrayList<>();
  private IUserContentAccessExtended userContentAccess = null;
  private String baseStoragePath;
  private final String parentPluginId;
  private FileSystem storageFilesystem = FileSystems.getDefault();

  /**
   * @param parentPluginId is the ID of the CTools plugin using this instance.
   *
   */
  public ContentAccessFactory( String parentPluginId ) {
    this.baseStoragePath = System.getProperty( "java.io.tmpdir" );
    this.parentPluginId = parentPluginId;
  }

  /**
   * @param baseStoragePath is the base path for plugin namespaces on the backing filesystem.
   * @param parentPluginId is the ID of the CTools plugin using this instance.
   */
  public ContentAccessFactory( String baseStoragePath, String parentPluginId ) {
    this.baseStoragePath = baseStoragePath;
    this.parentPluginId = parentPluginId;
  }

  /**
   * Add new read-only content to the plugin system namespace.
   * @param pluginId identifier of the plugin
   * @param readAccess instance of read access to the plugin resources
   */
  public void addReadOnlyPluginSystemAccess( String pluginId, IReadAccess readAccess ) {
    List<IReadAccess> pluginList = getPluginSystemAccessList( pluginId );
    pluginList.add( readAccess );
  }

  private List<IReadAccess> getPluginSystemAccessList( String pluginId ) {
    List<IReadAccess> pluginList;

    if ( !this.pluginReadAccessMap.containsKey( pluginId ) ) {
      pluginList = new ArrayList<>();
      this.pluginReadAccessMap.put( pluginId, pluginList );
    } else {
      pluginList = this.pluginReadAccessMap.get( pluginId );
    }

    return pluginList;
  }

  /**
   * Remove read-only content from the plugin system namespace.
   * @param pluginId identifier of the plugin
   * @param readAccess instance of read access to the plugin resources
   */
  public void removeReadOnlyPluginSystemAccess( String pluginId, IReadAccess readAccess ) {
    List<IReadAccess> pluginList = this.pluginReadAccessMap.get( pluginId );
    if ( pluginList != null ) {
      pluginList.remove( readAccess );
    }
  }

  /**
   * Add read-only user content or plugin-system resource access providers.
   *
   * User content must have a boolean "isUserContent" property defined and set to true.
   * Plugin-system resources must have a string "pluginId" property defined.
   * @param readAccess is the read-only content provider to be added.
   * @param serviceProperties is the map of service properties for the supplied content provider.
   */
  public void addReadAccess( IReadAccess readAccess, Map serviceProperties ) {
    if ( !serviceProperties.isEmpty() ) {
      Object id = serviceProperties.get( SERVICE_PROPERTY_PLUGIN_ID );
      if ( id != null && id instanceof String ) {
        addReadOnlyPluginSystemAccess( (String) id, readAccess );
      } else {
        Object isUserContent = serviceProperties.get( SERVICE_PROPERTY_IS_USER_CONTENT );
        if ( isUserContent != null && isUserContent instanceof Boolean && ((Boolean) isUserContent).booleanValue() ) {
          userContentReadAccesses.add( readAccess );
        }
      }
    }
  }

  /**
   * Remove read-only user content or plugin-system resource access providers.
   *
   * User content must have a boolean "isUserContent" property defined and set to true.
   * Plugin-system resources must have a string "pluginId" property defined.
   * @param readAccess is the read-only content provider to be removed.
   * @param serviceProperties is the map of service properties for the supplied content provider.
   */
  public void removeReadAccess( IReadAccess readAccess, Map serviceProperties ) {
    if ( !serviceProperties.isEmpty() ) {
      Object id = serviceProperties.get( SERVICE_PROPERTY_PLUGIN_ID );
      if ( id != null && id instanceof String ) {
        removeReadOnlyPluginSystemAccess( (String) id, readAccess );
      } else {
        Object isUserContent = serviceProperties.get( SERVICE_PROPERTY_IS_USER_CONTENT );
        if ( isUserContent != null && isUserContent instanceof Boolean && ((Boolean) isUserContent).booleanValue() ) {
          userContentReadAccesses.remove( readAccess );
        }
      }
    }
  }

  public void setUserContentAccess( IUserContentAccessExtended userContentAccess ) {
    this.userContentAccess = userContentAccess;
  }

  public void removeUserContentAccess( IUserContentAccessExtended userContentAccess ) {
    this.userContentAccess = null;
  }

  public FileSystem getPluginStorageFilesystem() {
    return storageFilesystem;
  }

  public void setPluginStorageFilesystem( FileSystem storageFilesystem ) {
    this.storageFilesystem = storageFilesystem;
  }

  public String getBaseStoragePath() {
    return baseStoragePath;
  }

  public void setBaseStoragePath( String baseStoragePath ) {
    this.baseStoragePath = baseStoragePath;
  }

  @Override
  public IUserContentAccessExtended getUserContentAccess( String basePath ) {
    if ( userContentAccess == null ) {
      if ( userContentReadAccesses.isEmpty() ) {
        return null;
      } else {
        return new UserContentAccess( new ReadAccessProxy( userContentReadAccesses, basePath ) );
      }
    } else {
      return new OverlayUserContentAccess( basePath, userContentAccess, userContentReadAccesses );
    }
  }

  @Override
  public IReadAccess getPluginRepositoryReader( String basePath ) {
    logger.info( "[PluginRepos]  RO FileSystemOverlay for: " + basePath );
    return getPluginRepositoryOverlay( basePath );
  }

  @Override
  public IRWAccess getPluginRepositoryWriter( String basePath ) {
    logger.info( "[PluginRepos]  RW FileSystemOverlay for: " + basePath );
    return getPluginRepositoryOverlay( basePath );
  }

  @Override
  public IReadAccess getPluginSystemReader( String basePath ) {
    logger.info( "[PluginSystem] RO FileSystemOverlay for <" + parentPluginId + ">: " + basePath );
    return getOtherPluginSystemReader( parentPluginId, basePath );
  }

  @Override
  public IRWAccess getPluginSystemWriter( String basePath ) {
    logger.info( "[PluginSystem] RW FileSystemOverlay for <" + parentPluginId + ">: " + basePath );
    return getOtherPluginSystemWriter( parentPluginId, basePath );
  }

  @Override
  public IReadAccess getOtherPluginSystemReader( String pluginId, String basePath ) {
    logger.info( "[PluginOtherSystem]  RO FileSystemOverlay for <" + pluginId + ">: " + basePath );
    return getPluginSystemOverlay( pluginId, basePath );
  }

  @Override
  public IRWAccess getOtherPluginSystemWriter( String pluginId, String basePath ) {
    logger.info( "[PluginOtherSystem]  RW FileSystemOverlay for <" + pluginId + ">: " + basePath );
    return getPluginSystemOverlay( pluginId, basePath );
  }

  private IRWAccess getPluginRepositoryOverlay( String basePath ) {
    // implemented as a filesystem folder on foundry, as it is a storage area common to all users
    String storagePath = createStoragePath( PLUGIN_REPOS_NAMESPACE );
    return new FileSystemRWAccess( FileSystems.getDefault(), storagePath, basePath );
  }

  private IRWAccess getPluginSystemOverlay( String pluginId, String basePath ) {
    // combine read-write via filesystem storage with bundle supplied read-only assets
    String storagePath = createStoragePath( PLUGIN_SYSTEM_NAMESPACE, pluginId );
    IRWAccess fileSystemWriter = new FileSystemRWAccess( storageFilesystem, storagePath, null );
    return new OverlayRWAccess( basePath, fileSystemWriter, getPluginSystemAccessList( pluginId ) );
  }

  private String createStoragePath( String namespace ) {
    return createStoragePath( namespace, null );
  }

  private String createStoragePath( String namespace, String id ) {
    // TODO: validate that basePath does not travel across the namespace boundary
    Path storagePath =  id != null ? storageFilesystem.getPath( baseStoragePath, namespace, id ) : storageFilesystem.getPath( baseStoragePath, namespace );
    File storage = storagePath.toFile();
    if ( storage.exists() && !storage.isDirectory() ) {
      throw new IllegalStateException( "Expected path to be a directory: " + storagePath.toString() );
    }
    if ( !storage.exists() ) {
      storage.mkdirs();
    }
    return storagePath.toString();
  }

}
