package pt.webdetails.cpf.test;


import java.io.IOException;
import junit.framework.TestCase;
import org.apache.commons.vfs.FileObject;
import org.apache.commons.vfs.Selectors;
import pt.webdetails.cpf.repository.BaseRepositoryAccess.FileAccess;
import pt.webdetails.cpf.repository.VfsRepositoryAccess;
import pt.webdetails.cpf.repository.BaseRepositoryAccess.SaveFileStatus;
import pt.webdetails.cpf.repository.IRepositoryFile;



public class VfsRepositoryTest extends TestCase {
	
	private VfsRepositoryAccessForTests repository;
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		this.repository = new VfsRepositoryAccessForTests("res:repository", "res:settings");
	}
        
        
	public void testBasicRepo() {
		try {
                        repository.publishFile("testsolutionfile", "testcontent", true);
			String content = repository.getResourceAsString("testsolutionfile", FileAccess.READ);
			assertEquals("testcontent",  content);
			String settingContent = repository.getSettingsResourceAsString("testsettingsfile");
			assertEquals("testsetting",  settingContent);
                        repository.removeUnsafe(".");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail();
		}
		
	}
        public void testFolderCreation(){
            try{
                boolean create = repository.createFolder("testFolderCreation");//creates a folder
                assertTrue(create);
                boolean folderCeption = repository.createFolder("folder/within/a/folder/with/folders/inside/other/folders");//folders within folders
                assertTrue(folderCeption);
                boolean doNothing = repository.createFolder("testFolderCreation");//do nothing because folder exists
                assertTrue(doNothing);
                
                //cleanup after the test
                repository.removeUnsafe(".");
            }catch(Exception e){
            e.printStackTrace();
            fail();
        }    
            
        }
        public void testResourceExists(){
            try{
                repository.createFolder("testFolderCreation");
                boolean exists = repository.resourceExists("testFolderCreation");
                boolean notExists = repository.resourceExists("IAmNotAFolder!");
                assertTrue(exists&&!notExists);
                
                //cleanup after the test
                repository.removeUnsafe(".");
            }catch(Exception e){
            e.printStackTrace();
            fail();
        }
        }
        public void testPublishFile(){
        try {
            boolean publishCreate = SaveFileStatus.OK==repository.publishFile("fileNameCreate", "fileContent", true);//creates a file
             assertTrue(publishCreate);
            repository.publishFile("fileName", "fileContent", true);
            boolean publishOverwriteFalse = SaveFileStatus.FAIL==repository.publishFile("fileName", "contentOverwrite", false);//overwrite = false
            assertTrue(publishOverwriteFalse);
            repository.publishFile("fileToOverwrite", "I shall be overwritten", false);//creates a file wich will be overwriten 
            boolean publishOverwriteTrue = SaveFileStatus.OK==repository.publishFile("fileToOverwrite", "Overwrite Sucessfull!", true);//overwrites a file
            assertTrue(publishOverwriteTrue);
             
            //cleanup after the test
            repository.removeUnsafe(".");
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
        }
        public void testCopyFile(){
        try {
            
            repository.publishFile("from", "My contents shall be copied to the other file", false);
            repository.publishFile("to", "no matter, these contents will be deleted", false);
            
            boolean copy = SaveFileStatus.OK==repository.copySolutionFile("from", "to");
            
            assertTrue(copy);//was copied
            assertEquals("My contents shall be copied to the other file",repository.getResourceAsString("to"));//the content was correctly copied
            boolean createsFileTo = SaveFileStatus.OK==repository.copySolutionFile("from", "createdOnCopy");//creates the destination file
            assertTrue(createsFileTo);
            boolean cantFindFile = SaveFileStatus.FAIL==repository.copySolutionFile("nonExistingFile", "to");//wont find nonExistingFile
            assertTrue(cantFindFile);
         
            //cleanup after the test
            repository.removeUnsafe(".");
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
        }  
        public void testFileRemoval(){
            try{
                repository.publishFile("fileToDelete", "", true);
                repository.createFolder("folderToDelete");
                boolean fileRemoval = repository.removeFileIfExists("fileToDelete");//will remove file
                assertTrue(fileRemoval);
                boolean folderRemoval = repository.removeFileIfExists("folderToDelete");//will remove folder
                assertTrue(folderRemoval);
                repository.publishFile("cantDeleteMe/imSafeHere","",true);
                boolean cantRemoveFolderWithFiles = !repository.removeFileIfExists("cantDeleteMe");//wont remove a folder with files
                assertTrue(cantRemoveFolderWithFiles);
                  
                //cleanup after the test
                repository.removeUnsafe(".");
            }catch(Exception e){
            e.printStackTrace();
            fail();
        }
        }
        public void testGetRepositoryFile(){
            try{
               repository.publishFile("repoFolder/repoFile", "repo file content", true); 
               
               IRepositoryFile repoFile = repository.getRepositoryFile("repoFolder/repoFile", FileAccess.READ);
               IRepositoryFile repoFolder = repository.getRepositoryFile("repoFolder", FileAccess.READ);
               IRepositoryFile nonExistent = repository.getRepositoryFile("wrongName", FileAccess.READ);
               assertTrue(repoFile.exists());//file exists
               assertFalse(repoFile.isDirectory());//is not directory
               assertFalse(repoFile.isRoot());//not root
               assertTrue(repoFolder.exists());//folder exists
               assertTrue(repoFolder.isDirectory());//is directory
               assertFalse(repoFolder.isRoot());//not root
               assertTrue(repoFolder.listFiles().length>0);//folder has children
               
               assertFalse(nonExistent.exists());
               
               
               
            //cleanup after the test
            repository.removeUnsafe(".");  
            }catch(Exception e){
            e.printStackTrace();
            fail();
        }
        }
        public void testGetSettingsFile(){
            try{
               
             assertTrue(repository.getSettingsFile("testFile", FileAccess.READ).exists());//this one exists
             assertFalse(repository.getSettingsFile("random", FileAccess.READ).exists());//this one doesnt
             

            }catch(Exception e){
            e.printStackTrace();
            fail();
        }
        }
        public void testGetSettingsFileTree(){
            try{
                //create dir testFolder and inside, at leat 2 files, one with name.extension and another with name.anotherExtension
               IRepositoryFile[] files0 = repository.getSettingsFileTree("testFolder", "extension", FileAccess.READ);
               IRepositoryFile[] files1 = repository.getSettingsFileTree("testFolder", "anotherExtension", FileAccess.READ);
               IRepositoryFile[] files2 = repository.getSettingsFileTree("testFolder", "notAnExtension", FileAccess.READ);
               IRepositoryFile[] files3 = repository.getSettingsFileTree("notEvenAFolder", "extension", FileAccess.READ);
               
               assertTrue(files0.length>0);
               assertTrue(files1.length>0);
               assertFalse(files2.length>0);
               assertFalse(files3.length>0);
               
            }catch(Exception e){
            e.printStackTrace();
            fail();
        }
        }
        public void testGetPluginFiles(){
            try{
              //created manually
               IRepositoryFile[] files = repository.getPluginFiles("pluginDir", FileAccess.READ);
               IRepositoryFile[] files1 = repository.getPluginFiles("wrongDir", FileAccess.READ);
               
               
               assertTrue(files.length>0);
               assertFalse(files1.length>0);
               
               
               
            }catch(Exception e){
            e.printStackTrace();
            fail();
        }
        } 
        public void testFileUnsafeRemoval(){
            try{
                repository.publishFile("folderToDelete/anotherFolder/fileToDelete", "", true);
                repository.publishFile("folderToDelete/plusOne/file", "", true);
                repository.publishFile("folderToDelete/plusTwo/child/fileToDelete", "", true);
                repository.publishFile("folderToDelete/anotherFolder/folderAsInFolder/anotherFile", "", true);
                assertTrue(repository.resourceExists("folderToDelete"));//folder exists
                assertTrue(repository.resourceExists("folderToDelete/plusOne"));//just testing for one child
                assertTrue(repository.removeUnsafe("folderToDelete")>=0);//removal
                assertFalse(repository.resourceExists("folderToDelete"));//folder and children no longer exist
            }catch(Exception e){
            e.printStackTrace();
            fail();
        }
        }      
        
        public void testGetSolutionPath() throws IOException {
        repository.createFolder("one");
        repository.createFolder("one/two");
        
        String solPath = repository.getSolutionPath("");
        String solPath1 = repository.getSolutionPath("one");
        String solPath2 = repository.getSolutionPath("one/two");
        
        assertNotNull(solPath);
        assertNotNull(solPath1);
        assertNotNull(solPath2);
        
        repository.removeUnsafe("one");
        
        }
        
}



 class  VfsRepositoryAccessForTests extends VfsRepositoryAccess {
      
     
     
     public VfsRepositoryAccessForTests(String repo,String settings){
         super(repo,settings);
     }
     
          /**
           * Please do note this will remove the folder and all subfolders and files
           * Used for testing purposes only
           * @param file
           * @return 
           */
          public int removeUnsafe(String file){
              try {
                  if(file.equals("."))
                      return repo.delete(Selectors.EXCLUDE_SELF);
			FileObject f = resolveFile(repo, file);
			if (f.exists()) {
				return f.delete(Selectors.SELECT_ALL);
			}
			return -1;
		} catch(Exception e) {
			throw new RuntimeException("Cannot delete file: " + file, e);
		}
          }
}