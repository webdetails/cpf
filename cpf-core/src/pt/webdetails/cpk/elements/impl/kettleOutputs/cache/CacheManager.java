/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/. */

package pt.webdetails.cpk.elements.impl.kettleOutputs.cache;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import org.pentaho.di.core.Result;
import org.pentaho.di.core.row.RowMetaInterface;

/**
 *
 * @author Luis Paulo Silva<luis.silva@webdetails.pt>
 */
public class CacheManager implements ICacheManager{
    private static CacheManager _engine;
    private ConcurrentHashMap<CacheKey,Object> cache;
    private ConcurrentHashMap<CacheKey,CacheControlInfo> cacheControlMap;
            
    private CacheManager(){
        init();
    }
    
    public static CacheManager getInstance(){
        if(_engine == null){
            _engine = new CacheManager();
        }
        
        return _engine;
    }
    
    private void init(){
        cache = new ConcurrentHashMap<CacheKey, Object>();
        cacheControlMap = new ConcurrentHashMap<CacheKey, CacheControlInfo>();
    }

    @Override
    public void putObject(CacheKey aKey, Object anObjectToCache, long aTimeToLive) {
        cache.put(aKey, anObjectToCache);
        cacheControlMap.put(aKey, new CacheControlInfo(aTimeToLive));
    }

    @Override
    public Object getCachedObject(CacheKey aKey) {
        Object cachedObject = cache.get(aKey);
        CacheControlInfo cacheControl = cacheControlMap.get(aKey);
        Object result = null;
        
        if(cachedObject != null && cacheControl != null){
            if(!cacheControl.isValid()){
                result = null;
                remove(aKey);
            }else{
                result = cachedObject;
            }
        }
        
        return result;
    }

    @Override
    public void remove(CacheKey aKey) {
        cache.remove(aKey);
        cacheControlMap.remove(aKey);
    }

    @Override
    public void clearCache() {
        cache.clear();
        cacheControlMap.clear();
    }

    @Override
    public List<CacheKey> getKeys() {
        return Collections.list(cache.keys());
    }
    
    
    private class CacheControlInfo {
        private long timeToLive;
        private long timeStamp;
        
        public CacheControlInfo(long aTimeToLive){
            this.timeToLive = aTimeToLive;
            timeStamp = new Date().getTime();
        }
        
        public boolean isValid(){
            return (new Date().getTime() < timeStamp + timeToLive * 1000);
        }
    }
    

}
