/*!
 * Copyright 2002 - 2015 Webdetails, a Pentaho company. All rights reserved.
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

package pt.webdetails.cpf.repository.pentaho.repository;


import junit.framework.Assert;
import junit.framework.TestCase;
import org.junit.Test;
import org.mockito.Mockito;
import org.pentaho.platform.api.engine.IPentahoAclEntry;
import org.pentaho.platform.api.engine.IPentahoSession;
import org.pentaho.platform.api.repository.ISolutionRepository;
import org.pentaho.platform.api.repository.ISolutionRepositoryService;
import pt.webdetails.cpf.repository.pentaho.PentahoLegacySolutionAccess;

import java.io.IOException;

public class PentahoLegacySolutionAccessTest extends TestCase {

  protected ISolutionRepository mockRepository;
  ISolutionRepositoryService mockRepositoryService;

  @Test
  public void testCreateFolderBaseDoesNotExist() throws IOException {
    IPentahoSession session = Mockito.mock( IPentahoSession.class );
    mockRepositoryService = Mockito.mock( ISolutionRepositoryService.class );
    mockRepository = Mockito.mock( ISolutionRepository.class );
    Mockito.when( mockRepository.resourceExists( "basePath", IPentahoAclEntry.PERM_EXECUTE ) ).thenReturn( false ); //Folder does not exist, return false
    Mockito.when( mockRepositoryService.createFolder( session, "", "", "basePath", "" ) ).thenReturn( true ); //Create base first
    Mockito.when( mockRepositoryService.createFolder( session, "", "basePath/", "folderToCreate", "" ) ).thenReturn(
        true );
    PentahoLegacySolutionAccessForTests access = new PentahoLegacySolutionAccessForTests( "basePath", session, false,
            mockRepository, mockRepositoryService );

    Assert.assertTrue( access.createFolder( "folderToCreate", false ) );
  }


  @Test
  public void testCreateFolderBaseExists() throws IOException {
    IPentahoSession session = Mockito.mock( IPentahoSession.class );
    mockRepositoryService = Mockito.mock( ISolutionRepositoryService.class );
    mockRepository = Mockito.mock( ISolutionRepository.class );
    Mockito.when( mockRepository.resourceExists( "basePath", IPentahoAclEntry.PERM_EXECUTE ) ).thenReturn( true ); //Folder exists
    Mockito.when( mockRepositoryService.createFolder( session, "", "basePath/", "folderToCreate", "" ) ).thenReturn( true );
    PentahoLegacySolutionAccessForTests access = new PentahoLegacySolutionAccessForTests( "basePath", session, false,
            mockRepository, mockRepositoryService );

    Assert.assertTrue( access.createFolder( "folderToCreate", false ) );
  }

  @Test
  public void testCreateFolderFails() throws IOException {
    IPentahoSession session = Mockito.mock( IPentahoSession.class );
    mockRepositoryService = Mockito.mock( ISolutionRepositoryService.class );
    mockRepository = Mockito.mock( ISolutionRepository.class );
    Mockito.when( mockRepository.resourceExists( "basePath", IPentahoAclEntry.PERM_EXECUTE  ) ).thenReturn( true ); //Folder exists
    Mockito.when( mockRepositoryService.createFolder( session, "", "basePath/", "folderToCreate", "" ) ).thenReturn( false );
    PentahoLegacySolutionAccessForTests access = new PentahoLegacySolutionAccessForTests( "basePath", session, false,
            mockRepository, mockRepositoryService );

    Assert.assertFalse( access.createFolder( "folderToCreate", false ) );
  }

  @Test
  public void testCreateFolderException() throws IOException {
    IPentahoSession session = Mockito.mock( IPentahoSession.class );
    mockRepositoryService = Mockito.mock( ISolutionRepositoryService.class );
    mockRepository = Mockito.mock( ISolutionRepository.class );
    Mockito.when( mockRepository.resourceExists( "basePath", IPentahoAclEntry.PERM_EXECUTE  ) ).thenReturn( true ); //Folder exists
    Mockito.when( mockRepositoryService.createFolder( session, "", "basePath/", "folderToCreate", "" ) ).
            thenThrow( new IOException() );
    PentahoLegacySolutionAccessForTests access = new PentahoLegacySolutionAccessForTests( "basePath", session, false,
            mockRepository, mockRepositoryService );

    Assert.assertFalse( access.createFolder( "folderToCreate", false ) );
  }

  @Test
  public void testIsSystemPath() {
    IPentahoSession session = Mockito.mock( IPentahoSession.class );
    mockRepositoryService = Mockito.mock( ISolutionRepositoryService.class );
    mockRepository = Mockito.mock( ISolutionRepository.class );
    PentahoLegacySolutionAccessForTests access = new PentahoLegacySolutionAccessForTests( "basePath", session, false,
        mockRepository, mockRepositoryService );

    String systemPath = "system/path/file.cdfde";
    String systemPath2 = "system/tmptest/path/file.cdfde";
    String systemBacktrackPath = "system/path/../file.cdfde";
    String systemBacktrackPath2 = "system/tmp/../file.cdfde";
    String notSystemPath = "system/tmp/path/file.cdfde";

    Assert.assertTrue( access.isSystemPath( systemPath ) );
    Assert.assertTrue( access.isSystemPath( systemPath2 ) );
    Assert.assertTrue( access.isSystemPath( systemBacktrackPath ) );
    Assert.assertTrue( access.isSystemPath( systemBacktrackPath2 ) );
    Assert.assertFalse( access.isSystemPath( notSystemPath ) );

  }


  class PentahoLegacySolutionAccessForTests extends PentahoLegacySolutionAccess {
    public PentahoLegacySolutionAccessForTests( String path, IPentahoSession session, boolean baseDirHidden,
                                                ISolutionRepository solutionRepository,
                                                ISolutionRepositoryService repositoryService ) {
      super( path, session, baseDirHidden );
      this.repository = solutionRepository;
      this.repositoryService = repositoryService;

    }

    @Override
    protected ISolutionRepositoryService getRepositoryService() {
      return mockRepositoryService;
    }

    @Override
    protected ISolutionRepository getRepository() {
      return mockRepository;
    }


    protected ISolutionRepository initSolutionRepository( IPentahoSession session ) {
      return null;
    }
    protected ISolutionRepositoryService initRepositoryService( IPentahoSession session ) {
      return null;
    }
  }

}
