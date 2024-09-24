/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2028-08-13
 ******************************************************************************/

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
