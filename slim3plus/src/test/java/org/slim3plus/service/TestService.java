package org.slim3plus.service;


import com.google.appengine.api.taskqueue.TaskHandle;
import org.slim3.memcache.Memcache;
import org.slim3plus.cache.Cacheable;
import org.slim3plus.cache.MemcacheCache;
import org.slim3plus.tq.Deferred;

import java.util.Date;

public class TestService {




    @Deferred
    public TaskHandle testDeferred() {
        return null;
    }

    public void test() {
        System.out.printf("test");
    }


    @Cacheable(MemcacheCache.class)
    public Date getCachedDate() {
        return getDate();
    }

    public Date getDate() {
        return new Date();
    }

}
