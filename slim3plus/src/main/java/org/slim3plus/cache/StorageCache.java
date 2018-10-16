package org.slim3plus.cache;

import com.google.appengine.api.NamespaceManager;
import com.google.appengine.api.taskqueue.TaskHandle;
import com.google.appengine.tools.cloudstorage.GcsFileOptions;
import com.google.appengine.tools.cloudstorage.GcsFilename;
import org.slim3plus.service.StorageService;
import org.slim3plus.tq.Deferred;
import org.slim3plus.tq.DeferredInterceptor;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.IOException;
import java.util.Base64;
import java.util.Date;

/**
 * Created by katsume on 2018/04/03.
 */
@Singleton
public class StorageCache implements LowLevelCache {

    @Inject
    StorageService storageService;

    public byte[] get(byte[] key) throws CacheNotFoundException {
        try {
            GcsFilename filename = getFilename(key);
            return storageService.readBytes(filename);
        } catch (IOException e) {
            e.printStackTrace();
            throw new CacheNotFoundException(e);
        }
    }

    @Override
    public boolean put(byte[] key, byte[] value, Expiration expiration) {
        GcsFilename filename = getFilename(key);
        try {
            storageService.writeBytes(filename, value, new GcsFileOptions.Builder()
                    .mimeType("application/octet-stream").build());
            if (expiration != null) {
                try {
                    DeferredInterceptor.schedule(new Date(expiration.getMilliSeconds()), () -> {
                        return clearDeferred(key);
                    });
                }
                catch(Exception ex) {
                    ex.printStackTrace();
                }
            }
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Deferred
    public TaskHandle clearDeferred(byte[] key) {
        return null;
    }

    @Override
    public void clear(byte[] key) {
        try {
            storageService.delete(getFilename(key));
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    /**
     * "ns/{namespace}/.cache/{Base64(key)}" 形式のファイル名を作成する
     * namespaceがnullの場合は、.となる。
     * @param key
     * @return
     */
    private GcsFilename getFilename(byte[] key) {
        String namespace = NamespaceManager.get();
        if (namespace == null) {
            namespace = ".";
        }
        String objectName = "ns/" + namespace + "/.cache/" + Base64.getEncoder().encodeToString(key);
        return new GcsFilename(storageService.getDefaultBucketName(), objectName);
    }

}
