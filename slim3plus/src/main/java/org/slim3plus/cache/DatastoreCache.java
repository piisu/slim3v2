package org.slim3plus.cache;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.taskqueue.TaskHandle;
import org.slim3.datastore.Datastore;
import org.slim3.datastore.EntityNotFoundRuntimeException;
import org.slim3plus.model.CacheStore;
import org.slim3plus.tq.Deferred;
import org.slim3plus.tq.DeferredInterceptor;

import javax.inject.Singleton;
import java.util.Base64;
import java.util.Date;

/**
 * Created by katsume on 2018/04/03.
 */
@Singleton
public class DatastoreCache implements LowLevelCache {

    /**
     * Blob has 1MB limit.<br>
     * see https://cloud.google.com/appengine/docs/standard/java/javadoc/com/google/appengine/api/datastore/Blob
     */
    public static final  int VALUE_LIMIT_BYTES = 1 * 1024 * 1024;

    public byte[] get(byte[] key) throws CacheNotFoundException {
        try {
            final Key dsKey = CacheStore.createKey(getKeyName(key));
            return Datastore.getWithoutTx(CacheStore.class, dsKey).getResult();
        }
        catch(EntityNotFoundRuntimeException ex) {
            throw new CacheNotFoundException(ex);
        }
    }

    @Override
    public boolean put(byte[] key, byte[] value, Expiration expiration) {
        if (VALUE_LIMIT_BYTES < value.length) {
            return false;
        }
        final Key dsKey = CacheStore.createKey(getKeyName(key));
        final CacheStore cache = new CacheStore();
        cache.setKey(dsKey);
        cache.setResult(value);
        Datastore.putWithoutTx(cache);

        if (expiration != null) {
            try {
                DeferredInterceptor.schedule(new Date(expiration.getMilliSeconds()), () -> {return clearDeferred(key);});
            }
            catch(Exception ex) {
                ex.printStackTrace();
            }
        }

        return true;
    }

    @Deferred
    public TaskHandle clearDeferred(byte[] key) {
        return null;
    }

    @Override
    public void clear(byte[] key) {
        final Key dsKey = CacheStore.createKey(getKeyName(key));
        Datastore.deleteWithoutTx(dsKey);
    }

    private String getKeyName(byte[] key) {
        return Base64.getEncoder().encodeToString(key);
    }
}
