package pt.webdetails.cpf.exceptions;

public class InitializationException extends Exception {

  private static final long serialVersionUID = 1089220229330479839L;

  public InitializationException( final String s, final Exception cause ) {
    super( s, cause );
  }
}
