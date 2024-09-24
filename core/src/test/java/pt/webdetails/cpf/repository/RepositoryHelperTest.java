/*!
 * Copyright 2013-2021 Webdetails, a Hitachi Vantara company.  All rights reserved.
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
