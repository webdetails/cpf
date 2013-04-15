package pt.webdetails.cpf.test;

import java.io.UnsupportedEncodingException;
import java.util.logging.Level;
import java.util.logging.Logger;
import junit.framework.TestCase;
import pt.webdetails.cpf.repository.BaseRepositoryAccess.FileAccess;
import pt.webdetails.cpf.repository.VfsRepositoryAccess;
import pt.webdetails.cpf.repository.BaseRepositoryAccess.SaveFileStatus;

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
        public void testFolderCreation(){
            try{
                boolean create = repository.createFolder("testFolderCreation");
                assertTrue(create);
            }catch(Exception e){
            e.printStackTrace();
            fail();
        }    
            
        }
        public void testResourceExists(){
            try{
                boolean exists = repository.resourceExists("testFolderCreation");
                boolean notExists = repository.resourceExists("IAmNotAFolder!");
                assertTrue(exists&&!notExists);
            }catch(Exception e){
            e.printStackTrace();
            fail();
        }
        }
        public void testPublishFile(){
        try {
            boolean publishCreate = SaveFileStatus.OK==repository.publishFile("fileNameCreate", "fileContent", true);
             assertTrue(publishCreate);
            boolean publish = SaveFileStatus.OK==repository.publishFile("fileName", "fileContent", true);
            assertTrue(publish);
            boolean publishOverwriteFalse = SaveFileStatus.OK==repository.publishFile("fileName", "contentOverwrite", false);
            assertTrue(!publishOverwriteFalse);
            repository.publishFile("fileToOverwrite", "I shall be overwritten", false);
            boolean publishOverwriteTrue = SaveFileStatus.OK==repository.publishFile("fileToOverwrite", "Overwrite Sucessfull!", true);
            assertTrue(publishOverwriteTrue);
            
            //removing the files to create always on test
            repository.removeFileIfExists("fileNameCreate");
            repository.removeFileIfExists("fileToOverwrite");
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
        }
        public void testCopyFile(){//XXX Failing
        try {
            
            repository.publishFile("from", "My contents shall be copied to the other file", false);
            repository.publishFile("to", "no matter, these contents will be deleted", false);
            
            boolean copy = SaveFileStatus.OK==repository.copySolutionFile("from", "to");
            
            assertTrue(copy);
            
            repository.removeFileIfExists("from");
            repository.removeFileIfExists("to");
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
        }
        
        
        
        
        
        
        
        
        
        public void testFileRemoval(){
            try{
                boolean removal = repository.removeFileIfExists("fileName");
                assertTrue(removal);
            }catch(Exception e){
            e.printStackTrace();
            fail();
        }
        }
        

}
