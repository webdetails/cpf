/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/. */

package pt.webdetails.cpk.elements.impl.kettleOutputs.cache;

import java.util.Map;
import java.util.Objects;
import java.util.SortedMap;

/**
 *
 * @author Luis Paulo Silva<luis.silva@webdetails.pt>
 */
public class ResultCacheKey {
    
    private String path, stepName;
    private SortedMap<String,String> parameters;
    
    public ResultCacheKey(String aPath, SortedMap<String,String> aParamsList, String aStepName){
        setPath(aPath);
        setStepName(aStepName);
        setParameters(aParamsList);
        
    }

    public String getPath() {
        return path;
    }

    private void setPath(String path) {
        this.path = path;
    }

    public String getStepName() {
        return stepName;
    }

    private void setStepName(String stepName) {
        this.stepName = stepName;
    }

    public SortedMap<String,String> getParameters() {
        return parameters;
    }

    private void setParameters(SortedMap<String,String> parameters) {
        this.parameters = parameters;
    }
    
    public boolean equals(ResultCacheKey key){
        boolean equals = false;
        
            if(key == null){
                //Does nothing, just makes sure that if a key is null the result is false
            }else if(key.getPath().equals(getPath()) && key.getParameters().equals(getParameters()) && key.getStepName().equals(getStepName())){
                equals = true;
            }
        
        return equals;
    }
    
    @Override
    public boolean equals(Object obj){
        ResultCacheKey key = null;
        if(obj instanceof ResultCacheKey){
            key = (ResultCacheKey)obj;
        }
        return equals(key);
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 83 * hash + Objects.hashCode(this.path);
        hash = 83 * hash + Objects.hashCode(this.stepName);
        hash = 83 * hash + Objects.hashCode(this.parameters);
        return hash;
    }
    
    @Override
    public String toString(){
        return getPath()+" - "+getParametersMapToString()+" - "+getStepName();
    }
    
    private String getParametersMapToString(){
        String params = new String();
        
        for(Map.Entry<String,String> entry : parameters.entrySet()){
            params += "["+entry.getKey()+" = "+entry.getValue()+"],";
        }
        
        return params.isEmpty() ? "[NO PARAMETERS]" : params.substring(0, params.length()-1);
    }
}
