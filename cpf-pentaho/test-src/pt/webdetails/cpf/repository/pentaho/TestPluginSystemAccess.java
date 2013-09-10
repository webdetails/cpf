package pt.webdetails.cpf.repository.pentaho;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import junit.framework.TestCase;

import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.pentaho.platform.plugin.services.pluginmgr.PluginClassLoader;

import pt.webdetails.cpf.PluginSettings;
import pt.webdetails.cpf.repository.api.IRWAccess;
import pt.webdetails.cpf.repository.api.IReadAccess;
import pt.webdetails.cpf.utils.CharsetHelper;

public class TestPluginSystemAccess extends TestCase {
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
      assertEquals("basic read","Baah",settings.getBogusString(null));
      assertNull("not there", settings.getNotThere(null));
      
      String originalVal = settings.getOtherBogusString("default");
      String newVal = "a new value";
      assertTrue(settings.setOtherBogusString(newVal));
      assertEquals(newVal, settings.getOtherBogusString(null));
      assertTrue(settings.setOtherBogusString(originalVal));
      assertEquals(originalVal, settings.getOtherBogusString(null));
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

  private PluginClassLoader getPluginClassLoader(String pluginDir) {
      String systemPath = userDir + "/test-resources/repo/system/" + pluginDir;
      return new PluginClassLoader(new File(systemPath), this.getClass().getClassLoader());
  }

}
