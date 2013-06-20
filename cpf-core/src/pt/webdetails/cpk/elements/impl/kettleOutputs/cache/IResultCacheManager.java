/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/. */

package pt.webdetails.cpk.elements.impl.kettleOutputs.cache;

import java.util.List;
import org.pentaho.di.core.Result;
import org.pentaho.di.core.row.RowMetaInterface;

/**
 *
 * @author Luís Paulo Silva<luis.silva@webdetails.pt>
 */
public interface IResultCacheManager {
    void putResult(ResultCacheKey aKey, Result aResult, List<Object[]> aRowList, RowMetaInterface aRowMeta, long aTimeToLive);
    ResultCache getResultCache(ResultCacheKey aKey);
    void remove(ResultCacheKey aKey);
    void clearCache();
    List<ResultCacheKey> getKeys();
}
