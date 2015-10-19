package pt.webdetails.cpf.repository;

import org.junit.Test;

import pt.webdetails.cpf.repository.util.RepositoryHelper;

import junit.framework.TestCase;

public class RepositoryHelperTest extends TestCase {

  @Test
  public void testRelativizeWinAbsPaths() {
    String filePath = "C:\\TargetPlatforms\\pentaho5\\server\\biserver-ee\\pentaho-solutions\\system\\saiku-reporting\\resources\\templates\\cobalt_4_left_aligned_grid.png";
    String basePath = "C:\\TargetPlatforms\\pentaho5\\server\\biserver-ee\\pentaho-solutions\\system\\saiku-reporting";
    String expected = "resources/templates/cobalt_4_left_aligned_grid.png";
    String relativized = RepositoryHelper.relativizePath( basePath, filePath, false );
    assertEquals( expected, relativized );
  }

}
