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

package pt.webdetails.cpf.persistence;

import org.json.JSONException;
import org.json.JSONObject;

import pt.webdetails.cpf.messaging.JsonSerializable;

/**
 * @author pdpi
 */
public interface Persistable extends JsonSerializable {

  public void setKey( String key );

  public String getKey();

  public void fromJSON( JSONObject json ) throws JSONException;
}
