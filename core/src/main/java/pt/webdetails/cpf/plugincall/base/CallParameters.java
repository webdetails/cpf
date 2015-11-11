package pt.webdetails.cpf.plugincall.base;

import java.util.HashMap;
import java.util.Map;


/**
 * Helper for {@link IPluginCall} parameters
 */
public class CallParameters {
  private Map<String, String[]> params = new HashMap<String, String[]>();

  public void put( String name, String value ) {
    params.put( name, new String[] { value } );
  }

  public void setParameter( String name, String[] value ) {
    params.put( name, value );
  }

  public void put( String name, boolean value ) {
    put( name, Boolean.toString( value ) );
  }

  public void put( String name, int value ) {
    put( name, Integer.toString( value ) );
  }

  public void put( String name, double value ) {
    put( name, Double.toString( value ) );
  }

  public String[] getValues( String name ) {
    return params.get( name );
  }

  public String getValue( String name ) {
    String[] value = params.get( name );
    if ( value != null && value.length > 0 ) {
      return value[ 0 ];
    }
    return null;
  }

  public Map<String, String[]> getParameters() {
    return params;
  }
}
