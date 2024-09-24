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

package pt.webdetails.cpf.repository.pentaho;

import org.junit.Test;
import org.pentaho.platform.api.repository2.unified.IUnifiedRepository;
import org.pentaho.platform.api.repository2.unified.RepositoryFilePermission;
import pt.webdetails.cpf.repository.api.FileAccess;
import pt.webdetails.cpf.repository.pentaho.unified.UserContentRepositoryAccess;

import java.util.EnumSet;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UserContentRepositoryAccessTest {
  UserContentRepositoryAccess repositoryAccess;

  @Test
  public void testHasAccess() {
    //setup test
    IUnifiedRepository unifiedRepository = mock(IUnifiedRepository.class);
    repositoryAccess = new UserContentRepositoryForTest(unifiedRepository);

    when(unifiedRepository.hasAccess("/home/admin", EnumSet.of( RepositoryFilePermission.READ))).thenReturn(true);
    when(unifiedRepository.hasAccess("/home/admin", EnumSet.of( RepositoryFilePermission.WRITE))).thenReturn(true);
    when(unifiedRepository.hasAccess("/home/pat", EnumSet.of( RepositoryFilePermission.READ))).thenReturn(true);

    //unix
    assertTrue("Access allowed to /home/admin on unix", repositoryAccess.hasAccess("/home/admin", FileAccess.READ));
    assertTrue("Access allowed to home/admin on unix", repositoryAccess.hasAccess("home/admin", FileAccess.READ));
    assertTrue("Access denied to /home/admin on unix", repositoryAccess.hasAccess("/home/admin", FileAccess.WRITE));
    assertFalse("Access denied to /home/admin on unix", repositoryAccess.hasAccess("/home/admin", FileAccess.DELETE));
    assertTrue("Access allowed to /home/pat on unix", repositoryAccess.hasAccess("/home/pat", FileAccess.EXECUTE));

    //windows
    assertTrue("Access allowed to /home/admin on windows", repositoryAccess.hasAccess("\\home\\admin", FileAccess.READ));
    assertTrue("Access allowed to /home/admin on windows", repositoryAccess.hasAccess("home\\admin", FileAccess.READ));
    assertFalse("Access defined to home/suzy on windows", repositoryAccess.hasAccess("home\\suzy", FileAccess.READ));
  }

  static class UserContentRepositoryForTest extends UserContentRepositoryAccess {
    IUnifiedRepository unifiedRepository;

    public UserContentRepositoryForTest(IUnifiedRepository repository) {
      super(null);
      unifiedRepository = repository;
    }

    @Override
    protected IUnifiedRepository initRepository(){
      return unifiedRepository;
    }
  }
}
