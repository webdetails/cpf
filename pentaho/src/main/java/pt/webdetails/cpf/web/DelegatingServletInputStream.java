/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2028-08-13
 ******************************************************************************/
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pt.webdetails.cpf.web;

import java.io.IOException;
import java.io.InputStream;
import javax.servlet.ServletInputStream;


public class DelegatingServletInputStream extends ServletInputStream  {
    private final InputStream sourceStream;
    
    /**
     * Create a new DelegatingServletInputStream.
     * @param sourceStream the sourceStream InputStream
     */
    
    public DelegatingServletInputStream(InputStream sourceStream) {
        this.sourceStream = sourceStream;
    }
    
    public InputStream getSourceStream() {
        return sourceStream;
    }
    
    
    public int read() throws IOException {
        return this.sourceStream.read();
    }

    
    public void close() throws IOException {
        super.close();
        this.sourceStream.close();
    }
}

