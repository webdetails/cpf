package pt.webdetails.cpf.test;

import junit.framework.TestCase;
import pt.webdetails.cpf.repository.BaseRepositoryAccess.FileAccess;
import pt.webdetails.cpf.repository.VfsRepositoryAccess;

public class VfsRepositoryTest extends TestCase {
	
	private VfsRepositoryAccess repository;
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		this.repository = new VfsRepositoryAccess("res:repository", "res:settings");
	}
	public void testBasicRepo() {
		try {
			String content = repository.getResourceAsString("testsolutionfile", FileAccess.READ);
			assertEquals("testcontent",  content);
			String settingContent = repository.getSettingsResourceAsString("testsettingsfile");
			assertEquals("testsetting",  settingContent);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail();
		}
		
	}

}
