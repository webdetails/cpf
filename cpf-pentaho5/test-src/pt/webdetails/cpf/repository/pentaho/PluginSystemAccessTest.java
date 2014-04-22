/*!
* Copyright 2002 - 2013 Webdetails, a Pentaho company.  All rights reserved.
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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import junit.framework.TestCase;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.junit.Test;
import org.pentaho.platform.plugin.services.pluginmgr.PluginClassLoader;

import pt.webdetails.cpf.PluginSettings;
import pt.webdetails.cpf.repository.api.IBasicFile;
import pt.webdetails.cpf.repository.api.IBasicFileFilter;
import pt.webdetails.cpf.repository.api.IRWAccess;
import pt.webdetails.cpf.repository.api.IReadAccess;
import pt.webdetails.cpf.repository.util.RepositoryHelper;
import pt.webdetails.cpf.utils.CharsetHelper;

public class PluginSystemAccessTest extends TestCase {
  private final String userDir = System.getProperty("user.dir");

  @Test
  public void testSimpleRead() throws IOException {
      IReadAccess bogusReader = createPluginSystemAccess(null);
  
      assertTrue("basic fileExists", bogusReader.fileExists("plugin.xml"));
      
      assertFalse("basic !fileExists", bogusReader.fileExists("notThere.txt"));
      
      assertTrue("dir fileExists", bogusReader.fileExists("resources/stuff"));
  
      InputStream baahStream = bogusReader.getFileInputStream("resources/stuff/stuffedBogus.txt");
      String baah = IOUtils.toString(bogusReader.getFileInputStream("resources/stuff/stuffedBogus.txt"), CharsetHelper.getEncoding());
      baahStream.close();
      assertEquals("File contents read.", "Baah!", baah);
  
      InputStream notThereStream = bogusReader.getFileInputStream("resources/notThere/bogus.txt");
      assertNull("Stream to inexistent file", notThereStream);
  
      bogusReader = createPluginSystemAccess("");
      assertTrue("empty basePath", bogusReader.fileExists("plugin.xml"));
    }
  
  @Test
  public void testPluginBasePathRead() throws IOException {
      IReadAccess reader = createPluginSystemAccess("resources/stuff");
      assertTrue("fileExists", reader.fileExists("stuffedBogus.txt"));

      IReadAccess readerSlash = createPluginSystemAccess("resources/stuff/");
      assertTrue("fileExists trail slash", readerSlash.fileExists("stuffedBogus.txt"));

      IReadAccess readerBackTrack = createPluginSystemAccess("resources/stuff/");
      assertTrue("backtrack", readerBackTrack.fileExists("../bogus.txt"));

      assertFalse("too much backtrack", readerBackTrack.fileExists("../../../../../../../../../justDontThrowUp"));
  }

  @Test
  public void testReadWrite() throws IOException {
      IRWAccess rw = createPluginSystemAccess("resources");
      assertTrue(rw.fileExists("bogus.txt"));
      assertFalse(rw.fileExists("stuff/bogus.txt"));
      assertTrue(rw.copyFile("bogus.txt", "stuff/bogus.txt"));
      assertTrue(rw.deleteFile("stuff/bogus.txt"));
      // save, read, delete
      final String contents = "badum";
      final String newFile = "stuff/bogus.txt";
      assertTrue(rw.saveFile(newFile, IOUtils.toInputStream(contents)));
      assertEquals(contents, IOUtils.toString(rw.getFileInputStream(newFile)));
      assertTrue(rw.deleteFile(newFile));
      assertFalse(rw.fileExists(newFile));
  }

  @Test
  public void testPluginSettings() throws IOException {
      IRWAccess rw = createPluginSystemAccess(null);
      BogusSettings settings = new BogusSettings(rw);
      assertEquals("basic read", "Baah", settings.getBogusString(null));
      assertNull("not there", settings.getNotThere(null));

      String originalVal = settings.getOtherBogusString("default");
      String newVal = "a new value";
      assertTrue(settings.setOtherBogusString(newVal));
      assertEquals(newVal, settings.getOtherBogusString(null));
      assertTrue(settings.setOtherBogusString(originalVal));
      assertEquals(originalVal, settings.getOtherBogusString(null));
  }

  @Test
  public void testBasicFile() throws IOException {
      IReadAccess reader = createPluginSystemAccess("resources");
      IBasicFile file = reader.fetchFile("stuff/stuffedBogus.txt");
      assertEquals("txt", file.getExtension());
      assertEquals("stuffedBogus.txt", file.getName());
      assertEquals("stuff/stuffedBogus.txt",file.getPath());
      StringBuilder builder = new StringBuilder(getFullPluginDir("bogusPlugin"));
      RepositoryHelper.appendPath(builder, "resources");
      RepositoryHelper.appendPath(builder, file.getPath());
      final String fullPath = builder.toString();
      assertEquals(fullPath, file.getFullPath());
      assertEquals(userDir + "/test-resources/repo/system/bogusPlugin/resources/stuff/stuffedBogus.txt", fullPath);
  }

  @Test
  public void testExtension() {
      IReadAccess reader = createPluginSystemAccess("resources");
      IBasicFile dotFile = reader.fetchFile(".hidden");
      assertFalse("hidden".equals(dotFile.getExtension()));
      assertTrue(StringUtils.isEmpty(dotFile.getExtension()));
  }

  @Test
  public void testSimpleExtension() {
      IReadAccess reader = createPluginSystemAccess("resources");
      IBasicFile txt = reader.fetchFile("bogus.txt");
      assertEquals("txt", txt.getExtension());
  }

  @Test
  public void testListFiles() throws IOException {
      IReadAccess reader = createPluginSystemAccess("resources");
      IBasicFileFilter txtFilter = new IBasicFileFilter() {
        public boolean accept(IBasicFile file) {
          return file.getExtension().equals("txt");
        }
      };
      List<IBasicFile> txtFiles = reader.listFiles("", txtFilter, -1, false);
      assertEquals("listFiles result", 4, txtFiles.size());
      boolean bogusFound = false, stuffFound = false, moreFound = false;
      // no contract on order
      for (IBasicFile txtFile : txtFiles) {
        if (txtFile.getName().equals("stuffedBogus.txt")) {
          stuffFound = true;
          assertEquals("stuff/stuffedBogus.txt", txtFile.getPath());
          assertEquals(getFullPluginDir("bogusPlugin/resources/stuff/stuffedBogus.txt"), txtFile.getFullPath());
        }
        if (txtFile.getName().equals("moreStuffedBogus.txt")) {
          moreFound = true;
          assertEquals("stuff/moreStuff/moreStuffedBogus.txt", txtFile.getPath());
        }
        if (txtFile.getName().equals("bogus.txt")) {
          bogusFound = true;
        }
      }
      assertTrue(stuffFound && moreFound && bogusFound);

      txtFiles = reader.listFiles(null, txtFilter, 1);
      assertEquals(1, txtFiles.size());
      assertEquals("bogus.txt", txtFiles.get(0).getName());
      bogusFound = false;
  }

  class BogusSettings extends PluginSettings {
      public BogusSettings(IRWAccess writeAccess) {
          super(writeAccess);
      }

      public String getBogusString(String defVal) {
        return getStringSetting("bogus/bogus-string", defVal);
      }

      public String getOtherBogusString(String defVal) {
        return getStringSetting("bogus/mister/bogus", defVal);
      }
      public boolean setOtherBogusString(String newVal) {
        return writeSetting("bogus/mister/bogus", newVal);
      }

      public String getNotThere(String defVal) {
        return getStringSetting("bogus/not/there", defVal);
      }
  }


  private SystemPluginResourceAccess createPluginSystemAccess(String basePath) {
      return new SystemPluginResourceAccess(getPluginClassLoader(), basePath);
  }

  private PluginClassLoader getPluginClassLoader() {
      return getPluginClassLoader("bogusPlugin");
  }

  private String getFullPluginDir(String pluginDir) {
    return userDir + "/test-resources/repo/system/" + pluginDir;
  }

  private PluginClassLoader getPluginClassLoader(String pluginDir) {
      String systemPath = getFullPluginDir(pluginDir);
      return new PluginClassLoader(new File(systemPath), this.getClass().getClassLoader());
  }

}
