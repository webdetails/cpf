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


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Collection;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;

import pt.webdetails.cpf.utils.CharsetHelper;

/**
 * 
 */
public class CpfHttpServletResponse implements HttpServletResponse {

    private static final String CHARSET_PREFIX = "charset=";

    //---------------------------------------------------------------------
    // ServletResponse properties
    //---------------------------------------------------------------------

    private boolean outputStreamAccessAllowed = true;
    private boolean writerAccessAllowed = true;
    private String characterEncoding = CharsetHelper.getEncoding();
    private final ByteArrayOutputStream content;
    private final DelegatingServletOutputStream servletOutputStream;
    
    private PrintWriter writer;
    private int contentLength = 0;
    private String contentType;
    private int bufferSize = 4096;
    private boolean committed;
    private Locale locale = Locale.getDefault();

    //---------------------------------------------------------------------
    // HttpServletResponse properties
    //---------------------------------------------------------------------

    private final List<Cookie> cookies = new ArrayList<Cookie>();
    /**
     * The key is the lowercase header name; the value is a {@link HeaderValueHolder} object.
     */
    
    private final Hashtable<String, Object> headers = new Hashtable<String, Object>();
    private int status = HttpServletResponse.SC_OK;
    private String errorMessage;
    private String redirectedUrl;
    private String forwardedUrl;
    private String includedUrl;

    public CpfHttpServletResponse( ByteArrayOutputStream outputStream ) {
      this.content = outputStream;
      this.servletOutputStream = new DelegatingServletOutputStream(this.content);
    }

    public CpfHttpServletResponse() {
      this( new ByteArrayOutputStream() );
    }

    //---------------------------------------------------------------------
    // ServletResponse interface
    //---------------------------------------------------------------------
    
    /**
     * Set whether {@link #getOutputStream()} access is allowed.
     */
    
    public void setOutputStreamAccessAllowed(boolean outputStreamAccessAllowed) {
        this.outputStreamAccessAllowed = outputStreamAccessAllowed;
    }
    
    /**
     * Return whether {@link #getOutputStream()} access is allowed.
     */
    
    public boolean isOutputStreamAccessAllowed() {
        return this.outputStreamAccessAllowed;
    }
    
    /**
     * Set whether {@link #getWriter()} access is allowed.
     * <p>Default is <code>true</code>.
     */
    
    public void setWriterAccessAllowed(boolean writerAccessAllowed) {
        this.writerAccessAllowed = writerAccessAllowed;
    }
    
    /**
     * Return whether {@link #getOutputStream()} access is allowed.
     */
    
    public boolean isWriterAccessAllowed() {
        return this.writerAccessAllowed;
    }
    
    public void setCharacterEncoding(String characterEncoding) {
        this.characterEncoding = characterEncoding;
    }
    
    public String getCharacterEncoding() {
        return this.characterEncoding;
    }

    @Override
    public ServletOutputStream getOutputStream() {
        if (!this.outputStreamAccessAllowed) {
            throw new IllegalStateException("OutputStream access not allowed");
        }
        return this.servletOutputStream;
    }
    
    public PrintWriter getWriter() throws UnsupportedEncodingException {
        if (!this.writerAccessAllowed) {
            throw new IllegalStateException("Writer access not allowed");
        }
        if (this.writer == null) {
            Writer targetWriter = (this.characterEncoding != null ?
                    new OutputStreamWriter(this.content, this.characterEncoding) : new OutputStreamWriter(this.content));
            this.writer = new PrintWriter(targetWriter);
        }
        return this.writer;
    }

    public byte[] getContentAsByteArray() {
        flushBuffer();
        return this.content.toByteArray();
    }

    public String getContentAsString() throws UnsupportedEncodingException {
        flushBuffer();
        return (this.characterEncoding != null) ? this.content.toString(this.characterEncoding) : this.content.toString();
    }
    
    public void setContentLength(int contentLength) {
        this.contentLength = contentLength;
    }

    @Override
    public void setContentLengthLong( long l ) {
        this.contentLength= ( int ) l;
    }

    public int getContentLength() {
        return this.contentLength;
    }
    
    public void setContentType(String contentType) {
        this.contentType = contentType;
        if (contentType != null) {
            int charsetIndex = contentType.toLowerCase().indexOf(CHARSET_PREFIX);
            if (charsetIndex != -1) {
                String encoding = contentType.substring(charsetIndex + CHARSET_PREFIX.length());
                setCharacterEncoding(encoding);
            }
        }
    }
    
    public String getContentType() {
        return this.contentType;
    }
    
    public void setBufferSize(int bufferSize) {
        this.bufferSize = bufferSize;
    }
    
    public int getBufferSize() {
        return this.bufferSize;
    }
    
    public void flushBuffer() {
        if (this.writer != null) {
            this.writer.flush();
        }
        if ( this.servletOutputStream != null ) {
          try {
            this.servletOutputStream.flush();
          } catch ( IOException ex ) {
            throw new IllegalStateException( "Could not flush OutputStream: " + ex.getMessage() );
          }
        }
        this.committed = true;
    }

    public void resetBuffer() {
        if (this.committed) {
            throw new IllegalStateException("Cannot reset buffer - response is already committed");
        }
        this.content.reset();
    }
    
    public void setCommitted(boolean committed) {
        this.committed = committed;
    }
    
    public boolean isCommitted() {
        return this.committed;
    }
    
    public void reset() {
        resetBuffer();
        this.characterEncoding = null;
        this.contentLength = 0;
        this.contentType = null;
        this.locale = null;
        this.cookies.clear();
        this.headers.clear();
        this.status = HttpServletResponse.SC_OK;
        this.errorMessage = null;
    }
    
    public void setLocale(Locale locale) {
        this.locale = locale;
    }
    
    public Locale getLocale() {
        return this.locale;
    }
    
    //---------------------------------------------------------------------
    // HttpServletResponse interface
    //---------------------------------------------------------------------
    
    public void addCookie(Cookie cookie) {
        this.cookies.add(cookie);
    }
    
    public Cookie[] getCookies() {
        return (Cookie[]) this.cookies.toArray(new Cookie[this.cookies.size()]);
    }
    
    public Cookie getCookie(String name) {
      for ( Cookie cookie : cookies ) {
            if (name.equals(cookie.getName())) {
                return cookie;
            }
        }
        return null;
    }
    
    public boolean containsHeader(String name) {
        return (HeaderValueHolder.getByName(this.headers, name) != null);
    }

    public Set<String> getHeaderNames() {
        return this.headers.keySet();
    }

    public String getHeader(String name) {
        HeaderValueHolder header = HeaderValueHolder.getByName(this.headers, name);
        return (header != null ? header.getValue().toString() : null);
    }

    public Collection<String> getHeaders(String name) {
        HeaderValueHolder header = HeaderValueHolder.getByName(this.headers, name);
        return header != null ? ( Collection<String> ) header.getValues() : ( Collection<String> ) Collections.enumeration(  Collections.emptyList() );
    }

    public String encodeURL(String url) {
        return url;
    }
    
    public String encodeRedirectURL(String url) {
        return url;
    }
    
    public String encodeUrl(String url) {
        return url;
    }
    
    public String encodeRedirectUrl(String url) {
        return url;
    }
    
    public void sendError(int status, String errorMessage) throws IOException {
        if (this.committed) {
            throw new IllegalStateException("Cannot set error status - response is already committed");
        }
        this.status = status;
        this.errorMessage = errorMessage;
        this.committed = true;
    }
    
    public void sendError(int status) throws IOException {
        if (this.committed) {
            throw new IllegalStateException("Cannot set error status - response is already committed");
        }
        
        this.status = status;
        this.committed = true;
    }
    
    public void sendRedirect(String url) throws IOException {
        if (this.committed) {
            throw new IllegalStateException("Cannot send redirect - response is already committed");
        }
        
        this.redirectedUrl = url;
        this.committed = true;
    }
    
    public String getRedirectedUrl() {
        return this.redirectedUrl;
    }
    
    public void setDateHeader(String name, long value) {
        setHeaderValue(name, new Long(value));
    }
    
    public void addDateHeader(String name, long value) {
        addHeaderValue(name, new Long(value));
    }
    
    public void setHeader(String name, String value) {
        setHeaderValue(name, value);
    }
    
    public void addHeader(String name, String value) {
        addHeaderValue(name, value);
    }
    
    public void setIntHeader(String name, int value) {
        setHeaderValue(name, new Integer(value));
    }
    
    public void addIntHeader(String name, int value) {
        addHeaderValue(name, new Integer(value));
    }
    
    private void setHeaderValue(String name, Object value) {
        doAddHeaderValue(name, value, true);
    }
    
    private void addHeaderValue(String name, Object value) {
        doAddHeaderValue(name, value, false);
    }
    
    private void doAddHeaderValue(String name, Object value, boolean replace) {
        HeaderValueHolder header = HeaderValueHolder.getByName(this.headers, name);
        if (header == null) {
            header = new HeaderValueHolder();
            this.headers.put(name, header);
        }
        if (replace) {
            header.setValue(value);
        }
        else {
            header.addValue(value);
        }
    }
    
    public void setStatus(int status) {
        this.status = status;
    }
    
    public void setStatus(int status, String errorMessage) {
        this.status = status;
        this.errorMessage = errorMessage;
    }
    
    public int getStatus() {
        return this.status;
    }
    
    public String getErrorMessage() {
        return this.errorMessage;
    }
    
    public void setForwardedUrl(String forwardedUrl) {
        this.forwardedUrl = forwardedUrl;
    }
    
    public String getForwardedUrl() {
        return this.forwardedUrl;
    }
    
    public void setIncludedUrl(String includedUrl) {
        this.includedUrl = includedUrl;
    }

    public String getIncludedUrl() {
        return this.includedUrl;
    }

}
