/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/. */

package pt.webdetails.cpf.persistence;

import org.json.JSONException;
import org.json.JSONObject;
import pt.webdetails.cpf.JsonSerializable;

/**
 *
 * @author pdpi
 */
public interface Persistable extends JsonSerializable{

    public void setKey(String  key);
    public String getKey();
    public void fromJSON(JSONObject json) throws JSONException;
}