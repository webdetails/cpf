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


package pt.webdetails.cpf.repository;

import org.junit.Test;
import pt.webdetails.cpf.repository.util.RepositoryHelper;

import static org.junit.Assert.assertEquals;

public class RepositoryHelperTest {

  @Test
  public void testRelativizeWinAbsPaths() {
    String filePath = "C:\\TargetPlatforms\\pentaho5\\server\\biserver-ee\\pentaho-solutions\\system\\saiku-reporting\\resources\\templates\\cobalt_4_left_aligned_grid.png";
    String basePath = "C:\\TargetPlatforms\\pentaho5\\server\\biserver-ee\\pentaho-solutions\\system\\saiku-reporting";
    String expected = "resources/templates/cobalt_4_left_aligned_grid.png";
    String relativized = RepositoryHelper.relativizePath( basePath, filePath, false );
    assertEquals( expected, relativized );
  }
}
