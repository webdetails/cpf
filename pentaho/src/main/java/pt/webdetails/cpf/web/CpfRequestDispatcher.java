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

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;

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
