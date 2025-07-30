/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2029-07-20
 ******************************************************************************/
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pt.webdetails.cpf.web;

import java.io.IOException;
import java.io.OutputStream;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.WriteListener;

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

    @Override
    public boolean isReady() {
        return false;
    }

    @Override
    public void setWriteListener( WriteListener writeListener ) {

    }
}

