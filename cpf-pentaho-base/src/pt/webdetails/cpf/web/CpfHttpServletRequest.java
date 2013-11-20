/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pt.webdetails.cpf.web;


import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.security.Principal;
import java.util.*;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletContext;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import pt.webdetails.cpf.utils.CharsetHelper;


/**
 * TODO: turn this into an immutable static base mock
 * @author diogomariano
 */
public class CpfHttpServletRequest implements HttpServletRequest {

    public static final String DEFAULT_PROTOCOL = "http";
    public static final String DEFAULT_SERVER_ADDR = "127.0.0.1";
    public static final String DEFAULT_SERVER_NAME = "localhost";
    public static final int DEFAULT_SERVER_PORT = 8080;
    public static final String DEFAULT_REMOTE_ADDR = "127.0.0.1";
    public static final String DEFAULT_REMOTE_HOST = "localhost";
    
    private String protocol = DEFAULT_PROTOCOL;
    private String scheme = DEFAULT_PROTOCOL;
    private String serverName = DEFAULT_SERVER_NAME;
    private int serverPort = DEFAULT_SERVER_PORT;
    private int localPort = DEFAULT_SERVER_PORT;
    private boolean secure = false;
    private int remotePort = DEFAULT_SERVER_PORT;
    private String localName = DEFAULT_SERVER_NAME;
    private String localAddr = DEFAULT_SERVER_ADDR;
    private String remoteAddr = DEFAULT_REMOTE_ADDR;
    private String remoteHost = DEFAULT_REMOTE_HOST;
    
    
    private final ServletContext servletContext;
    private String method;
    private String pathInfo;
    private String requestURI;
    private String contextPath = "";
    private final Vector<Locale> locales = new Vector<Locale>();
    
    
    private String authType;
    private Cookie[] cookies;
    private final Hashtable<String, Object> headers = new Hashtable<String, Object>();
    private final Hashtable<String, Object> attributes = new Hashtable<String, Object>();
    
    private Map<String, String[]> parameters = null;//must be wrapped

    
    private final Set<String> userRoles = new HashSet<String>();

    
    private boolean active = true;
    private String servletPath;
    private Principal userPrincipal;
    private String queryString;
    
    private HttpSession session;
    
    private boolean requestedSessionIdValid = true;
    private boolean requestedSessionIdFromCookie = true;
    private boolean requestedSessionIdFromURL = false;
    private String characterEncoding = CharsetHelper.getEncoding();
    
    private byte[] content;
    private String contentType;
    
    public CpfHttpServletRequest() {
         this(null, "", "");
    }
    
     public CpfHttpServletRequest(String method, String requestURI) {
         this(null, method, requestURI);
     }

     public CpfHttpServletRequest(ServletContext servletContext) {
         this(servletContext, "", "");
     }

     public CpfHttpServletRequest(ServletContext servletContext, String method, String requestURI) {
         this.servletContext = (servletContext != null ? servletContext : new CpfServletContext());
         this.method = method;
         this.requestURI = requestURI;
         this.locales.add(Locale.ENGLISH);
         this.parameters = new HashMap<String, String[]>();
     }
     
     protected void setParameterMap(Map<String, String[]> parameters) {
       this.parameters = parameters;
     }
     
     public boolean isActive() {
         return this.active;
     }

     public void close() {
         this.active = false;
     }

     public void invalidate() {
         close();
         clearAttributes();
     }

     protected void checkActive() throws IllegalStateException {
         if (!this.active) {
             throw new IllegalStateException("Request is not active anymore");
         }
     }



    @Override
    public String getAuthType() {
        return this.authType;
    }
    
    public void setAuthType(String authType) {
        this.authType = authType;
    }

    @Override
    public Cookie[] getCookies() {
        return this.cookies;
    }
    
    public void setCookies(Cookie[] cookies) {
        this.cookies = cookies;
    }
    
    public void addHeader(String name, Object value) {
        HeaderValueHolder header = HeaderValueHolder.getByName(this.headers, name);
        if (header == null) {
            header = new HeaderValueHolder();
            this.headers.put(name, header);
        }
        if (value instanceof Collection) {
            header.addValues((Collection<?>) value);
        }
        else if (value.getClass().isArray()) {
            header.addValueArray(value);
        }
        else {
            header.addValue(value);
        }
    }


    @Override
    public long getDateHeader(String name) {
        HeaderValueHolder header = HeaderValueHolder.getByName(this.headers, name);
        Object value = (header != null ? header.getValue() : null);
        if (value instanceof Date) {
           return ((Date) value).getTime();
        }
        else if (value instanceof Number) {
           return ((Number) value).longValue();
        }
        else if (value != null) {
           throw new IllegalArgumentException("Value for header '" + name + "' is neither a Date nor a Number: " + value);
        }
        else {
           return -1L;
        }
    }

    @Override
    public String getHeader(String name) {
        HeaderValueHolder header = HeaderValueHolder.getByName(this.headers, name);
        return (header != null ? header.getValue().toString() : null);
    }

    @Override
    public Enumeration<Object> getHeaders(String name) {
        HeaderValueHolder header = HeaderValueHolder.getByName(this.headers, name);
        return header != null ? header.getValues() : Collections.enumeration( Collections.emptyList() );
    }

    @Override
    public Enumeration<String> getHeaderNames() {
        return this.headers.keys();
    }

    @Override
    public int getIntHeader(String name) {
        HeaderValueHolder header = HeaderValueHolder.getByName(this.headers, name);
        Object value = (header != null ? header.getValue() : null);
        if (value instanceof Number) {
           return ((Number) value).intValue();
        }
        else if (value instanceof String) {
           return Integer.parseInt((String) value);
        }
        else if (value != null) {
           throw new NumberFormatException("Value for header '" + name + "' is not a Number: " + value);
        }
        else {
           return -1;
        }
    }

    @Override
    public String getMethod() {
        return this.method;
    }
    
    public void setMethod() {
//        this.method = method;
    }

    @Override
    public String getPathInfo() {
        return this.pathInfo;
    }
    
    public void setPathInfo(String pathInfo) {
        this.pathInfo = pathInfo;
    }

    @Override
    public String getPathTranslated() {
        return (this.pathInfo != null ? getRealPath(this.pathInfo) : null);
    }

    @Override
    public String getContextPath() {
        return this.contextPath;
    }
    
    public void setContextPath(String contextPath) {
        this.contextPath = contextPath;
    }

    @Override
    public String getQueryString() {
        return this.queryString;
    }
    
    public void setQueryString(String queryString) {
        this.queryString = queryString;
    }


    @Override
    public String getRemoteUser() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    public void setRemoteUser(String remoteUser) {
//        this.remoteUser = remoteUser;
    }

    public void addRole(String role) {
        addUserRole(role);
    }

    public void addUserRole(String role) {        
        this.userRoles.add(role);
    }
    
    @Override
    public boolean isUserInRole(String role) {
        return this.userRoles.contains(role);
    }

    @Override
    public Principal getUserPrincipal() {
        return this.userPrincipal;
    }
    
    public void setUserPrincipal(Principal userPrincipal) {
        this.userPrincipal = userPrincipal;
    }

    @Override
    public String getRequestedSessionId() {
        HttpSession session = getSession();
        return (session != null ? session.getId() : null);
    }

    @Override
    public String getRequestURI() {
        return this.requestURI;
    }
    
    public void setRequestURI(String requestURI) {
        this.requestURI = requestURI;
    }

    @Override
    public StringBuffer getRequestURL() {
        StringBuffer url = new StringBuffer(this.scheme);
        url.append("://").append(this.serverName).append(':').append(this.serverPort);
        url.append(getRequestURI());
        url.append('?');
        url.append( getQueryString() );
        return url;
    }
    
    @Override
    public String getServletPath() {
        return this.servletPath;
    }
    
    public void setServletPath(String servletPath){
        this.servletPath = servletPath;
    }

    @Override
    public HttpSession getSession(boolean create) {
        checkActive();
        // Reset session if invalidated.
        if (this.session instanceof CpfHttpSession && ((CpfHttpSession) this.session).isInvalid()) {
            this.session = null;
        }
        // Create new session if necessary.
        if (this.session == null && create) {
            this.session = new CpfHttpSession(this.servletContext);
        }
        return this.session;
    }


    @Override
    public HttpSession getSession() {
        return getSession(true);
    }
    
    public void setSession(HttpSession session) {
        this.session = session;
        if (session instanceof CpfHttpSession) {
            CpfHttpSession cpfSession = ((CpfHttpSession) session);
            cpfSession.access();
        }
    }
    
    @Override
    public boolean isRequestedSessionIdValid() {
        return this.requestedSessionIdValid;
    }
    
    public void setRequestedSessionIdValid(boolean requestedSessionIdValid) {
        this.requestedSessionIdValid = requestedSessionIdValid;
    }
    
    @Override
    public boolean isRequestedSessionIdFromCookie() {
        return this.requestedSessionIdFromCookie;
    }
    
    public void setRequestedSessionIdFromCookie(boolean requestedSessionIdFromCookie) {
        this.requestedSessionIdFromCookie = requestedSessionIdFromCookie;
    }

    @Override
    public boolean isRequestedSessionIdFromURL() {
        return this.requestedSessionIdFromURL;
    }
    
    public boolean isRequestedSessionIdFromUrl() {
        return isRequestedSessionIdFromURL();
    }

    
    public void setRequestedSessionIdFromURL(boolean requestedSessionIdFromURL) {
        this.requestedSessionIdFromURL = requestedSessionIdFromURL;
    }

    @Override
    public Object getAttribute(String name) {
        checkActive();
        return this.attributes.get(name);
    }

    @Override
    public Enumeration<String> getAttributeNames() {
        return this.attributes.keys();
    }

    @Override
    public String getCharacterEncoding() {
        return this.characterEncoding;
    }

    @Override
    public void setCharacterEncoding(String characterEncoding) throws UnsupportedEncodingException {
        this.characterEncoding = characterEncoding;
    }
    
    public void setContent(byte[] content) {
        this.content = content;
    }


    @Override
    public int getContentLength() {
        return (this.content != null ? this.content.length : -1);
    }

    @Override
    public String getContentType() {
        return this.contentType;
    }
    
    public void setContentType(String contentType) {
        this.contentType = contentType;
    }


    @Override
    public ServletInputStream getInputStream() throws IOException {
        if (this.content != null) {
            return new DelegatingServletInputStream(new ByteArrayInputStream(this.content));
        }
        else {
            return null;
        }
    }

    @Override
    public String getParameter(String name) {
        String[] arr = (String[]) this.parameters.get(name);
        return (arr != null && arr.length > 0 ? arr[0] : null);
    }
    
    public void setParameter(String name, String value) {
        setParameter(name, new String[] {value});
    }
    
    public void setParameter(String name, String[] values) {
        this.parameters.put(name, values);
    }
//    
//    public void addParameter(String name, String value) {
//        addParameter(name, new String[] {value});
//    }
//    
//    public void addParameter(String name, String[] values) {
//        String[] oldArr = (String[]) this.parameters.get(name);
//        if (oldArr != null) {
//            String[] newArr = new String[oldArr.length + values.length];
//            System.arraycopy(oldArr, 0, newArr, 0, oldArr.length);
//            System.arraycopy(values, 0, newArr, oldArr.length, values.length);
//            this.parameters.put(name, newArr);
//        }
//        else {
//            this.parameters.put(name, values);
//        }
//    }
//    
//    public void removeParameter(String name) {
//        this.parameters.remove(name);
//    }

    @Override
    public Enumeration<String> getParameterNames() {
        return Collections.enumeration(this.parameters.keySet());
    }

    @Override
    public String[] getParameterValues(String name) {
        return this.parameters.get(name);
    }

    @Override
    public Map<String, String[]> getParameterMap() {
        return Collections.unmodifiableMap(this.parameters);
    }

    @Override
    public String getProtocol() {
        return this.protocol;
    }
    
    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    @Override
    public String getScheme() {
        return this.scheme;
    }
    
    public void setScheme(String scheme) {
        this.scheme = scheme;
    }

    @Override
    public String getServerName() {
        return this.serverName;
    }
    
    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    @Override
    public int getServerPort() {
        return this.serverPort;
    }
    
    public void setServerPort(int serverPort) {
        this.serverPort = serverPort;
    }

    @Override
    public BufferedReader getReader() throws IOException {
        if (this.content != null) {
            InputStream sourceStream = new ByteArrayInputStream(this.content);
            Reader sourceReader = (this.characterEncoding != null) ?
                    new InputStreamReader(sourceStream, this.characterEncoding) : new InputStreamReader(sourceStream);
            return new BufferedReader(sourceReader);
        }
        else {
            return null;
        }
    }

    @Override
    public String getRemoteAddr() {
        return this.remoteAddr;
    }
    
    public void setRemoteAddr(String remoteAddr) {
        this.remoteAddr = remoteAddr;
    }

    @Override
    public String getRemoteHost() {
        return this.remoteHost;
    }
    
    public void setRemoteHost(String remoteHost) {
        this.remoteHost = remoteHost;
    }

    @Override
    public void setAttribute(String name, Object value) {
        checkActive();
        if (value != null) {
            this.attributes.put(name, value);
        }
        else {
            this.attributes.remove(name);
        }
    }

    @Override
    public void removeAttribute(String name) {
        checkActive();
        this.attributes.remove(name);
    }
    
    public void clearAttributes() {
        this.attributes.clear();
    }
    
    public void addPreferredLocale(Locale locale) {
        this.locales.add(0, locale);
    }

    @Override
    public Locale getLocale() {
        return (Locale) this.locales.get(0);
    }

    @Override
    public Enumeration<Locale> getLocales() {
        return this.locales.elements();
    }

    @Override
    public boolean isSecure() {
        return this.secure;
    }
    
    public void setSecure(boolean secure) {
        this.secure = secure;
    }

    @Override
    public RequestDispatcher getRequestDispatcher(String path) {
        return new CpfRequestDispatcher(path);
    }

    @Override
    public String getRealPath(String path) {
        return this.servletContext.getRealPath(path);
    }

    @Override
    public int getRemotePort() {
        return this.remotePort;
    }
    
    public void setRemotePort(int remotePort) {
        this.remotePort = remotePort;
    }


    @Override
    public String getLocalName() {
        return this.localName;
    }
    
    public void setLocalName(String localName) {
        this.localName = localName;
    }

    @Override
    public String getLocalAddr() {
        return this.localAddr;
    }
    
    public void setLocalAddr(String localAddr) {
        this.localAddr = localAddr;
    }


    @Override
    public int getLocalPort() {
        return this.localPort;
    }
    
    public void setLocalPort(int localPort) {
        this.localPort = localPort;
    }
}
