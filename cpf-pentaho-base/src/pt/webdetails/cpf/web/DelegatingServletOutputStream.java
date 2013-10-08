/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pt.webdetails.cpf.web;

import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.ServletOutputStream;

/**
 *
 * @author diogomariano
 */


public class DelegatingServletOutputStream extends ServletOutputStream {
    private final OutputStream targetStream;
    /**
     * Create a new DelegatingServletOutputStream.
     * @param targetStream the target OutputStream
     */
    
    public DelegatingServletOutputStream(OutputStream targetStream) {
        this.targetStream = targetStream;
    }
    
    public OutputStream getTargetStream() {
        return targetStream;
    }
    
    public void write(int b) throws IOException {
        this.targetStream.write(b);
    }
    
    public void flush() throws IOException {
        super.flush();
        this.targetStream.flush();
    }
    
    public void close() throws IOException {
        super.close();
        this.targetStream.close();
    }
}

