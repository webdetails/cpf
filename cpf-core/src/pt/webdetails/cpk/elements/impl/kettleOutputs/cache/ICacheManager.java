/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/. */

package pt.webdetails.cpk.elements.impl.kettleOutputs.cache;

import java.util.List;

/**
 *
 * @author Luís Paulo Silva<luis.silva@webdetails.pt>
 */
public interface ICacheManager {
    void putObject(CacheKey aKey, Object anObjectToCache, long aTimeToLive);
    Object getCachedObject(CacheKey aKey);
    void remove(CacheKey aKey);
    void clearCache();
    List<CacheKey> getKeys();
}
