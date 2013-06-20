/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/. */

package pt.webdetails.cpk.elements.impl.kettleOutputs.cache;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import org.pentaho.di.core.Result;
import org.pentaho.di.core.row.RowMetaInterface;

/**
 *
 * @author Luis Paulo Silva<luis.silva@webdetails.pt>
 */
public class ResultCacheManager implements IResultCacheManager{
    private static ResultCacheManager _engine;
    private ConcurrentHashMap<ResultCacheKey,ResultCache> cache;
    
    private ResultCacheManager(){
        init();
    }
    
    public static ResultCacheManager getInstance(){
        if(_engine == null){
            _engine = new ResultCacheManager();
        }
        
        return _engine;
    }
    
    private void init(){
        cache = new ConcurrentHashMap<ResultCacheKey, ResultCache>();
    }

    @Override
    public void putResult(ResultCacheKey aKey, Result aResult, List<Object[]> aRowList, RowMetaInterface aRowMeta, long aTimeToLive) {
        if(!cache.containsKey(aKey)){
            cache.put(aKey, new ResultCache(aResult, aRowList, aRowMeta, aTimeToLive));
        }else if(!cache.get(aKey).isValid()){
            cache.put(aKey, new ResultCache(aResult, aRowList, aRowMeta, aTimeToLive));
        }
    }

    @Override
    public ResultCache getResultCache(ResultCacheKey aKey) {
        ResultCache resultCache = cache.get(aKey);
        
        if(resultCache != null && !resultCache.isValid()){
            resultCache = null;
            remove(aKey);
        }
        
        return resultCache;
    }

    @Override
    public void remove(ResultCacheKey aKey) {
        cache.remove(aKey);
    }

    @Override
    public void clearCache() {
        cache.clear();
    }

    @Override
    public List<ResultCacheKey> getKeys() {
        return Collections.list(cache.keys());
    }
    

}
