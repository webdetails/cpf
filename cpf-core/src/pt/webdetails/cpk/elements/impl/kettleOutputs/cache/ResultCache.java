/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/. */

package pt.webdetails.cpk.elements.impl.kettleOutputs.cache;

import java.util.Date;
import java.util.List;
import org.pentaho.di.core.Result;
import org.pentaho.di.core.row.RowMetaInterface;

/**
 *
 * @author Luis Paulo Silva<luis.silva@webdetails.pt>
 */
public class ResultCache {
    private long timeToLive; // Value in seconds
    private long cacheTimestamp; // Value in seconds (Unix timestamp value)
    private Result result;
    private List<Object[]> rows;
    private RowMetaInterface rowMeta;
    
    public ResultCache(Result aResult, List<Object[]> aRowList, RowMetaInterface aRowMeta, long aTimeToLive){
        init();
        setResult(aResult);
        setTimeToLive(aTimeToLive);
        setRows(aRowList);
        setRowMeta(aRowMeta);
    }

    private void setRows(List<Object[]> rows) {
        this.rows = rows;
    }

    private void setRowMeta(RowMetaInterface rowMeta) {
        this.rowMeta = rowMeta;
    }

    public List<Object[]> getRows() {
        return rows;
    }

    public RowMetaInterface getRowMeta() {
        return rowMeta;
    }
    
    private void init(){
        setCacheTimestamp(new Date().getTime()/1000); //The time returned comes in milliseconds and we need it in seconds
    }

    public long getTimeToLive() {
        return timeToLive;
    }

    private void setTimeToLive(long timeout) {
        this.timeToLive = timeout;
    }

    public long getCacheTimestamp() {
        return cacheTimestamp;
    }

    private void setCacheTimestamp(long cacheTimestamp) {
        this.cacheTimestamp = cacheTimestamp;
    }

    public Result getResult() {
        return result;
    }

    private void setResult(Result result) {
        this.result = result;
    }
    
    public boolean isValid(){
        boolean is = false;
        long currentTime = new Date().getTime()/1000;
        
        if(currentTime < cacheTimestamp+timeToLive){
            is = true;
        }
        
        return is;
    }
    
    

}
