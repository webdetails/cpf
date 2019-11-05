package pt.webdetails.cpf.packager.dependencies;

import junit.framework.TestCase;
import pt.webdetails.cpf.packager.origin.PathOrigin;
import pt.webdetails.cpf.repository.api.IReadAccess;

import java.io.IOException;
import java.io.InputStream;

import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class FileDependencyTest extends TestCase {

    public void testGetFileInputStreamNullContentFactory() throws IOException {
        FileDependencyForTest fdft = new FileDependencyForTest( "1.0", null, "/home/admin/t.js", true );
        assertNull( fdft.getFileInputStream() );
    }


    public void testGetFileInputStream() throws IOException {
        PathOrigin pathOrigin = mock (PathOrigin.class);
        IReadAccess readAccess = mock (IReadAccess.class);
        InputStream is = mock (InputStream.class);
        when( pathOrigin.getReader(anyObject()) ).thenReturn(readAccess);

        when (readAccess.getFileInputStream("/home/admin/t.js")).thenReturn(is);
        FileDependencyForTest fdft = new FileDependencyForTest( "1.0", pathOrigin, "/home/admin/t.js", false );
        assertEquals("Resource is correctly read from repository", is, fdft.getFileInputStream());

        fdft = new FileDependencyForTest( "1.0", pathOrigin, "/home/admins/t.js", false );
        assertNull("Different path fails to load resource", fdft.getFileInputStream());

    }


}
