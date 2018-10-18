package org.slim3plus.cache;

import org.slim3.memcache.Memcache;

import javax.inject.Singleton;
import java.util.Date;

/**
 * Created by katsume on 2018/04/03.
 */
@Singleton
public class MemcacheCache implements LowLevelCache {

    /**
     * キャッシュされるデータ値の最大サイズは、1 MB（2^20 バイト）から、キーのサイズと実装に依存するオーバーヘッド（約 73 バイト）を差し引いた値になります。
     * とあるので念のため100バイト引く
     */
    public static final int VALUE_LIMIT_BYTES = 1 * 1024 * 1024 - 100;

    public byte[] get(byte[] key) throws CacheNotFoundException {
        if (!Memcache.contains(key)) {
            throw new CacheNotFoundException();
        }
        return Memcache.get(key);
    }

    @Override
    public boolean put(byte[] key, byte[] value, Expiration expiration) {
        if (VALUE_LIMIT_BYTES < value.length) {
            return false;
        }
        if (expiration != null) {
            Memcache.put(key, value
                    , com.google.appengine.api.memcache.Expiration.onDate(new Date(expiration.getMilliSeconds())));
        } else {
            Memcache.put(key, value);
        }

        return true;
    }

    @Override
    public void clear(byte[] key) {
        Memcache.delete(key);
    }

}
