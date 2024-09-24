/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pt.webdetails.cpf.web;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *
 * @author diogomariano
 */
class CpfRequestDispatcher implements RequestDispatcher {
    private final String url;

    private final Log logger = LogFactory.getLog(getClass());
    
    public CpfRequestDispatcher(String url) {
       this.url = url;
    }


    public void forward(ServletRequest request, ServletResponse response) {
       if (response.isCommitted()) {
           throw new IllegalStateException("Cannot perform forward - response is already committed");
       }
       if (!(response instanceof CpfHttpServletResponse)) {
          throw new IllegalArgumentException("CpfRequestDispatcher requires CpfHttpServletResponse");
       }
       ((CpfHttpServletResponse) response).setForwardedUrl(this.url);
       if (logger.isDebugEnabled()) {
           logger.debug("CpfRequestDispatcher: forwarding to URL [" + this.url + "]");
       }
    }

    public void include(ServletRequest request, ServletResponse response) {
       if (!(response instanceof CpfHttpServletResponse)) {
          throw new IllegalArgumentException("CpfRequestDispatcher requires CpfHttpServletResponse");
       }
       ((CpfHttpServletResponse) response).setIncludedUrl(this.url);
       if (logger.isDebugEnabled()) {
           logger.debug("CpfRequestDispatcher: including URL [" + this.url + "]");
       }
    }

}
