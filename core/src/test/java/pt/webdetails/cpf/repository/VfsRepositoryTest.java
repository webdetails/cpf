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

package pt.webdetails.cpf.repository;

import org.apache.commons.vfs.FileObject;
import org.apache.commons.vfs.Selectors;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import pt.webdetails.cpf.repository.IRepositoryAccess.FileAccess;
import pt.webdetails.cpf.repository.IRepositoryAccess.SaveFileStatus;
import pt.webdetails.cpf.repository.vfs.VfsRepositoryAccess;

import java.io.File;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class VfsRepositoryTest {

  private VfsRepositoryAccessForTests repository;
  private final String userDir = System.getProperty( "user.dir" );

  @Before
  public void setUp() {
    File baseDir = new File( userDir + "/src/test/resources/repository" );
    baseDir.mkdir();

    this.repository = new VfsRepositoryAccessForTests( userDir + "/src/test/resources/repository",
      userDir + "/src/test/resources/settings" );
  }

  @After
  public void tearDown() {
    repository.removeUnsafe( "." );
  }

  @Test
  public void testBasicRepo() throws Exception {
    repository.publishFile( "testsolutionfile", "testcontent", true );
    String content = repository.getResourceAsString( "testsolutionfile", FileAccess.READ );
    assertEquals( "testcontent", content );
    String settingContent = repository.getSettingsResourceAsString( "testsettingsfile" );
    assertEquals( "testsetting", settingContent );
  }

  @Test
  public void testFolderCreation() throws Exception {
    boolean create = repository.createFolder( "testFolderCreation" ); //creates a folder
    assertTrue( create );
    boolean folderCeption =
        repository.createFolder( "folder/within/a/folder/with/folders/inside/other/folders" ); //folders within folders
    assertTrue( folderCeption );
    boolean doNothing = repository.createFolder( "testFolderCreation" ); //do nothing because folder exists
    assertTrue( doNothing );
  }

  @Test
  public void testResourceExists() throws Exception {
    repository.createFolder( "testFolderCreation" );

    // A resource that exists
    assertTrue( repository.resourceExists( "testFolderCreation" ) );

    // A resource that does not exist
    assertFalse( repository.resourceExists( "IAmNotAFolder!" ) );
  }

  @Test
  public void testPublishFile() throws Exception {
    boolean publishCreate =
        SaveFileStatus.OK == repository.publishFile( "fileNameCreate", "fileContent", true ); //creates a file
    assertTrue( publishCreate );
    repository.publishFile( "fileName", "fileContent", true );
    boolean publishOverwriteFalse =
        SaveFileStatus.FAIL == repository.publishFile( "fileName", "contentOverwrite", false ); //overwrite = false
    assertTrue( publishOverwriteFalse );
    repository
      .publishFile( "fileToOverwrite", "I shall be overwritten", false ); //creates a file which will be overwritten
    boolean publishOverwriteTrue = SaveFileStatus.OK == repository
        .publishFile( "fileToOverwrite", "Overwrite Sucessfull!", true ); //overwrites a file
    assertTrue( publishOverwriteTrue );
  }

  @Test
  public void testCopyFile() throws Exception {
    repository.publishFile( "from", "My contents shall be copied to the other file", false );
    repository.publishFile( "to", "no matter, these contents will be deleted", false );

    boolean copy = SaveFileStatus.OK == repository.copySolutionFile( "from", "to" );

    assertTrue( copy ); //was copied
    assertEquals( "My contents shall be copied to the other file",
        repository.getResourceAsString( "to" ) ); //the content was correctly copied
    boolean createsFileTo =
        SaveFileStatus.OK == repository.copySolutionFile( "from", "createdOnCopy" ); //creates the destination file
    assertTrue( createsFileTo );
    boolean cantFindFile =
        SaveFileStatus.FAIL == repository.copySolutionFile( "nonExistingFile", "to" ); //won't find nonExistingFile
    assertTrue( cantFindFile );
  }

  @Test
  public void testFileRemoval() throws Exception {
    repository.publishFile( "fileToDelete", "", true );
    repository.createFolder( "folderToDelete" );
    boolean fileRemoval = repository.removeFileIfExists( "fileToDelete" ); //will remove file
    assertTrue( fileRemoval );
    boolean folderRemoval = repository.removeFileIfExists( "folderToDelete" ); //will remove folder
    assertTrue( folderRemoval );
    repository.publishFile( "cantDeleteMe/imSafeHere", "", true );
    boolean cantRemoveFolderWithFiles =
        !repository.removeFileIfExists( "cantDeleteMe" ); //won't remove a folder with files
    assertTrue( cantRemoveFolderWithFiles );
  }

  @Test
  public void testGetRepositoryFile() throws Exception {
    repository.publishFile( "repoFolder/repoFile", "repo file content", true );

    IRepositoryFile repoFile = repository.getRepositoryFile( "repoFolder/repoFile", FileAccess.READ );
    IRepositoryFile repoFolder = repository.getRepositoryFile( "repoFolder", FileAccess.READ );
    IRepositoryFile nonExistent = repository.getRepositoryFile( "wrongName", FileAccess.READ );
    assertTrue( repoFile.exists() ); //file exists
    assertFalse( repoFile.isDirectory() ); //is not directory
    assertFalse( repoFile.isRoot() ); //not root
    assertTrue( repoFolder.exists() ); //folder exists
    assertTrue( repoFolder.isDirectory() ); //is directory
    assertFalse( repoFolder.isRoot() ); //not root
    assertTrue( repoFolder.listFiles().length > 0 ); //folder has children

    assertFalse( nonExistent.exists() );
  }

  @Test
  public void testGetSettingsFile() {
    assertTrue( repository.getSettingsFile( "testFile", FileAccess.READ ).exists() ); //this one exists
    assertFalse( repository.getSettingsFile( "random", FileAccess.READ ).exists() ); //this one doesn't
  }

  @Test
  public void testGetSettingsFileTree() {
    //create dir testFolder and inside, at least 2 files, one with name.extension and another with name.anotherExtension
    IRepositoryFile[] files0 = repository.getSettingsFileTree( "testFolder", "extension", FileAccess.READ );
    IRepositoryFile[] files1 = repository.getSettingsFileTree( "testFolder", "anotherExtension", FileAccess.READ );
    IRepositoryFile[] files2 = repository.getSettingsFileTree( "testFolder", "notAnExtension", FileAccess.READ );
    IRepositoryFile[] files3 = repository.getSettingsFileTree( "notEvenAFolder", "extension", FileAccess.READ );

    assertTrue( files0.length > 0 );
    assertTrue( files1.length > 0 );
    assertFalse( files2.length > 0 );
    assertFalse( files3.length > 0 );

  }

  @Test
  public void testGetPluginFiles() {
    IRepositoryFile[] files = repository.getPluginFiles( "pluginDir", FileAccess.READ );
    assertTrue( files.length > 0 );

    IRepositoryFile[] files1 = repository.getPluginFiles( "wrongDir", FileAccess.READ );
    assertFalse( files1.length > 0 );
  }

  @Test
  public void testFileUnsafeRemoval() throws Exception {
    repository.publishFile( "folderToDelete/anotherFolder/fileToDelete", "", true );
    repository.publishFile( "folderToDelete/plusOne/file", "", true );
    repository.publishFile( "folderToDelete/plusTwo/child/fileToDelete", "", true );
    repository.publishFile( "folderToDelete/anotherFolder/folderAsInFolder/anotherFile", "", true );
    assertTrue( repository.resourceExists( "folderToDelete" ) ); //folder exists
    assertTrue( repository.resourceExists( "folderToDelete/plusOne" ) ); //just testing for one child
    assertTrue( repository.removeUnsafe( "folderToDelete" ) >= 0 ); //removal
    assertFalse( repository.resourceExists( "folderToDelete" ) ); //folder and children no longer exist
  }

  @Test
  public void testGetSolutionPath() throws Exception {
    repository.createFolder( "one" );
    repository.createFolder( "one/two" );

    String solPath = repository.getSolutionPath( "" );
    String solPath1 = repository.getSolutionPath( "one" );
    String solPath2 = repository.getSolutionPath( "one/two" );
    assertNotNull( solPath );
    assertNotNull( solPath1 );
    assertNotNull( solPath2 );

    repository.removeUnsafe( "one" );
  }

  static class VfsRepositoryAccessForTests extends VfsRepositoryAccess {

    public VfsRepositoryAccessForTests( String repo, String settings ) {
      super( repo, settings );
    }

    /**
     * Please do note this will remove the folder and all subfolders and files Used for testing purposes only
     *
     * @param file
     * @return
     */
    public int removeUnsafe( String file ) {
      try {
        if ( ".".equals( file ) ) {
          return repo.delete( Selectors.EXCLUDE_SELF );
        }
        FileObject f = resolveFile( repo, file );
        if ( f.exists() ) {
          return f.delete( Selectors.SELECT_ALL );
        }
        return -1;
      } catch ( Exception e ) {
        throw new RuntimeException( "Cannot delete file: " + file, e );
      }
    }
  }
}
