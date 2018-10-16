package org.slim3plus.service;


import com.google.inject.Injector;
import org.slim3plus.cache.CacheNotFoundException;
import org.slim3plus.cache.Expiration;
import org.slim3plus.cache.LowLevelCache;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Low-Level Cache Service
 */
@Singleton
public class LowLevelCacheService {

    @Inject
    Injector injector;

    public byte[] get(Class<? extends LowLevelCache>[] cacheClasses, byte[] key) throws CacheNotFoundException {
        for (Class<? extends LowLevelCache> clazz :cacheClasses) {
            try {
                return injector.getInstance(clazz).get(key);
            } catch(CacheNotFoundException ex) {
            }
        }
        throw new CacheNotFoundException();
    }

    public void put(Class<? extends LowLevelCache>[] cacheClasses, byte[] key, byte[] value, Expiration expiration) {
        for (Class<? extends LowLevelCache> clazz :cacheClasses) {
            injector.getInstance(clazz).put(key, value, expiration);
        }
    }

    public void clear(Class<? extends LowLevelCache>[] cacheClasses, byte[] key) {
        for (Class<? extends LowLevelCache> clazz :cacheClasses) {
            injector.getInstance(clazz).clear(key);
        }
    }
}
