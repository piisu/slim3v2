package org.slim3plus.cache;

import com.google.appengine.api.memcache.MemcacheSerialization;

/**
 * 低レベルキャッシュインターフェース<br/>
 * <br/>
 * キャッシュキー は250バイト以下でなければいけません <br/>
 * MemcacheSerialization#makePbKeyの使用を推奨
 *
 * @see MemcacheSerialization#makePbKey(Object)
 */
public interface LowLevelCache {
    /**
     * キャッシュをgetする
     *
     * @param key キャッシュキー。250バイト以下でなければいけません
     * @return キャッシュされていた値
     * @throws CacheNotFoundException キャッシュが存在しない場合は例外を返す
     */
    byte[] get(byte[] key) throws CacheNotFoundException;

    /**
     * キャッシュをputする　<br/>
     * キャッシュ先の容量制限等でputできない場合があります。<br/>
     * putに失敗した場合はfalseが返ります。
     *
     * @param key        キャッシュキー。250バイト以下でなければいけません
     * @param value      キャシュする値
     * @param expiration 有効期限
     * @return putできた場合はtrue、できなかった場合はfalse
     */
    boolean put(byte[] key, byte[] value, Expiration expiration);

    /**
     * キャッシュを削除する
     *
     * @param key キャッシュキー。250バイト以下でなければいけません
     */
    void clear(byte[] key);
}
