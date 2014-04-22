package pt.webdetails.cpf.messaging;

import java.io.OutputStream;

import javax.servlet.ServletOutputStream;

import pt.webdetails.cpf.web.CpfHttpServletResponse;
import pt.webdetails.cpf.web.DelegatingServletOutputStream;

/**
 * InterPluginCall response, only cares about OutputStream
 * 
 */
public class MockHttpServletResponse extends CpfHttpServletResponse {
  private OutputStream outputStream;
  private ServletOutputStream servletOutputStream;

  public MockHttpServletResponse(OutputStream output) {
    outputStream = output;
    servletOutputStream = new DelegatingServletOutputStream( outputStream );
  }

  @Override
  public ServletOutputStream getOutputStream() {
    return servletOutputStream;
  }

}
