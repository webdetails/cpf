/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pt.webdetails.cpf.persistence;

import org.json.JSONObject;
import pt.webdetails.cpf.JsonSerializable;

/**
 *
 * @author pdpi
 */
public interface Persistable extends JsonSerializable{

    public JSONObject getKey();
    public String getPersistenceClass();
}
