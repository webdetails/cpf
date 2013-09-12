package pt.webdetails.cpf.packager;


import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.junit.Test;

import pt.webdetails.cpf.packager.MinificationPackage.CssUrlReplacer;
import pt.webdetails.cpf.repository.api.IBasicFile;

import junit.framework.TestCase;

public class MinificationPackageTest extends TestCase {

   private static final String fancyBoxIECss =
        ".fancybox-ie6 #fancybox-close { background: transparent; filter: progid:DXImageTransform.Microsoft.AlphaImageLoader(src='fancybox/fancy_shadow_n.png', sizingMethod='scale'); }\n"
      + ".fancybox-ie6 #fancybox-title-float-right { background: transparent; filter: progid:DXImageTransform.Microsoft.AlphaImageLoader(src='fancybox/fancy_shadow_n.png', sizingMethod='scale'); }\n"
      + ".fancybox-ie #fancybox-bg-n { filter: progid:DXImageTransform.Microsoft.AlphaImageLoader(src='fancybox/fancy_shadow_n.png', sizingMethod='scale'); }\n";

   private static final String fancyBoxUrlCss = 
         ".someRule { background-image: url('fancybox.png');} \n"
       + ".someRule { background: transparent url('fancybox.png') -40px 0px;} \n"
       + ".someRule { background: url('fancybox.png') -40px -90px no-repeat;} \n"
       + ".someRule { background: url('fancybox.png') -55px -90px no-repeat;} \n";

  @Test
  public void testCssUrlReplacer() {
    CssUrlReplacer replacer = new CssUrlReplacer("resources/css");
    String updatedUrls = replacer.processContents(
            fancyBoxUrlCss,
            getBogusFile("resources/js/fancybox/fancy.css", fancyBoxUrlCss, "fancy.css", null, null));
    String expectedUpdatedUrls = StringUtils.replaceEach(fancyBoxUrlCss,
        new String[] {"url('fancybox.png')"},
        new String[] {"url('../js/fancybox/fancybox.png')"});
    assertEquals("urls, parallel path", expectedUpdatedUrls, updatedUrls);

    replacer = new CssUrlReplacer("resources/js/minified/");
    updatedUrls = replacer.processContents(
        fancyBoxUrlCss,
        getBogusFile("resources/js/fancybox/fancy.css", fancyBoxUrlCss, "fancy.css", null, null));
    expectedUpdatedUrls = StringUtils.replaceEach(fancyBoxUrlCss,
        new String[] {"url('fancybox.png')"},
        new String[] {"url('../fancybox/fancybox.png')"});
    assertEquals("urls, semi-path", expectedUpdatedUrls, updatedUrls);

    replacer = new CssUrlReplacer("js/");
    updatedUrls = replacer.processContents(
        fancyBoxUrlCss,
        getBogusFile("js/fancybox/fancy.css", fancyBoxUrlCss, "fancy.css", null, null));
    expectedUpdatedUrls = StringUtils.replaceEach(fancyBoxUrlCss,
        new String[] {"url('fancybox.png')"},
        new String[] {"url('fancybox/fancybox.png')"});
    assertEquals("urls, alog same path", expectedUpdatedUrls, updatedUrls);
  }

//  @Test
//  public void testOutOfScopeUrlReplacer() {
//    CssUrlReplacer replacer = new CssUrlReplacer("../side/minified/");
//    String updatedUrls = replacer.processContents(
//        fancyBoxUrlCss,
//        getBogusFile("resources/js/fancybox/fancy.css", fancyBoxUrlCss, "fancy.css", null, null));
//    String expectedUpdatedUrls = StringUtils.replaceEach(fancyBoxUrlCss,
//        new String[] {"url('fancybox.png')"},
//        new String[] {"url('../../../resources/js/fancybox/fancybox.png')"});
//    assertEquals("urls, out of scope", expectedUpdatedUrls, updatedUrls);
//  }

  @Test
  public void testUrlReplacer() {
    String content = fancyBoxIECss + fancyBoxUrlCss;
    CssUrlReplacer replacer = new CssUrlReplacer("resources/css");
    String updatedUrls = replacer.processContents(
        content,
            getBogusFile("resources/js/fancybox/fancy.css", fancyBoxUrlCss, "fancy.css", null, null));
    String expectedUpdatedUrls = StringUtils.replaceEach(content,
        new String[] {"url('fancybox.png')",
                      "(src='fancybox/fancy_shadow_n.png'"},
        new String[] {"url('../js/fancybox/fancybox.png')",
                      "(src='../js/fancybox/../fancybox/fancy_shadow_n.png'"});//  just don't think too much about it
    assertEquals("fancy ie", expectedUpdatedUrls, updatedUrls);
  }
  @Test
  public void testCssUrlCdfTemplateReplacer() {
    String content =  ".classy-class { background: #007788 url('images/x3.gif') top left repeat-x;}";
    CssUrlReplacer replacer = new CssUrlReplacer("js");
    String updatedUrls = replacer.processContents(
            content,
            getBogusFile("resources/style/template.css", content, "template.css", null, null));
    // in current version: ../js/../resources/style/images/x3.gif
    String expectedUpdatedUrls =
        ".classy-class { background: #007788 url('../resources/style/images/x3.gif') top left repeat-x;}";
    assertEquals("urls, parallel path", expectedUpdatedUrls, updatedUrls);
  }
  

  private IBasicFile getBogusFile(final String filePath, final String contents,
      final String name, final String extension, final String fullPath)
  {
    return new IBasicFile() {
      
      @Override
      public String getPath() {
        return filePath;
      }
      
      @Override
      public String getName() {
        return name;
      }
      
      @Override
      public String getFullPath() {
        return fullPath;
      }
      
      @Override
      public String getExtension() {
        return extension;
      }
      
      @Override
      public InputStream getContents() throws IOException {
        return IOUtils.toInputStream(contents);
      }
    };
  }
  
}
