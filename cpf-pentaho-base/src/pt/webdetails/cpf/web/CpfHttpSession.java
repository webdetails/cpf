/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pt.webdetails.cpf.web;

/**
 *
 * @author diogomariano
 */
import java.io.Serializable;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionBindingListener;
import javax.servlet.http.HttpSessionContext;

import org.springframework.util.Assert;

public class CpfHttpSession implements HttpSession {

    public static final String SESSION_COOKIE_NAME = "JSESSION";
    private static int nextId = 1;

    private final String id = Integer.toString(nextId++);
    private final long creationTime = System.currentTimeMillis();
    private int maxInactiveInterval = 120 * 60;

    private long lastAccessedTime = System.currentTimeMillis();
    private final ServletContext servletContext;
    private final Hashtable attributes = new Hashtable();
    private boolean invalid = false;
    private boolean isNew = true;

    public CpfHttpSession() {
       this(null);
    }

    public CpfHttpSession(ServletContext servletContext) {
       this.servletContext = (servletContext != null ? servletContext : new CpfServletContext());
    }


    public long getCreationTime() {
       return this.creationTime;
    }

    public String getId() {
       return this.id;
    }

    public void access() {
       this.lastAccessedTime = System.currentTimeMillis();
       this.isNew = false;
    }

    public long getLastAccessedTime() {
       return this.lastAccessedTime;
    }

    public ServletContext getServletContext() {
       return this.servletContext;
    }

    public void setMaxInactiveInterval(int interval) {
       this.maxInactiveInterval = interval;
    }

    public int getMaxInactiveInterval() {
       return this.maxInactiveInterval;
    }

    public HttpSessionContext getSessionContext() {
       throw new UnsupportedOperationException("getSessionContext");
    }

    public Object getAttribute(String name) {
       return this.attributes.get(name);
    }

    public Object getValue(String name) {
       return getAttribute(name);
    }

    public Enumeration getAttributeNames() {
       return this.attributes.keys();
    }

    public String[] getValueNames() {
       return (String[]) this.attributes.keySet().toArray(new String[this.attributes.size()]);
    }

    public void setAttribute(String name, Object value) {
       if (value != null) {
          this.attributes.put(name, value);
          if (value instanceof HttpSessionBindingListener) {
              ((HttpSessionBindingListener) value).valueBound(new HttpSessionBindingEvent(this, name, value));
          }
       }
       else {
          removeAttribute(name);
       }
   }

   public void putValue(String name, Object value) {
       setAttribute(name, value);
   }

   public void removeAttribute(String name) {
       Object value = this.attributes.remove(name);
       if (value instanceof HttpSessionBindingListener) {
           ((HttpSessionBindingListener) value).valueUnbound(new HttpSessionBindingEvent(this, name, value));
       }
   }

   public void removeValue(String name) {
       removeAttribute(name);
   }

    public void clearAttributes() {
       for (Iterator it = this.attributes.entrySet().iterator(); it.hasNext();) {
           Map.Entry entry = (Map.Entry) it.next();
           String name = (String) entry.getKey();
           Object value = entry.getValue();
           it.remove();
           if (value instanceof HttpSessionBindingListener) {
               ((HttpSessionBindingListener) value).valueUnbound(new HttpSessionBindingEvent(this, name, value));
           }
       }
    }

    public void invalidate() {
       this.invalid = true;
       clearAttributes();
    }

    public boolean isInvalid() {
       return this.invalid;
    }

    public void setNew(boolean value) {
       this.isNew = value;
    }

    public boolean isNew() {
       return this.isNew;
    }

    public Serializable serializeState() {
       HashMap state = new HashMap();
       for (Iterator it = this.attributes.entrySet().iterator(); it.hasNext();) {
           Map.Entry entry = (Map.Entry) it.next();
           String name = (String) entry.getKey();
           Object value = entry.getValue();
           it.remove();
           if (value instanceof Serializable) {
               state.put(name, value);
           }
           else {
               // Not serializable... Servlet containers usually automatically
               // unbind the attribute in this case.
               if (value instanceof HttpSessionBindingListener) {
                   ((HttpSessionBindingListener) value).valueUnbound(new HttpSessionBindingEvent(this, name, value));
               }
           }
       }
       return state;
    }

    public void deserializeState(Serializable state) {
       this.attributes.putAll((Map) state);
    }
}
