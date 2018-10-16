package org.slim3plus.cache;

import com.google.inject.AbstractModule;
import com.google.inject.matcher.Matchers;
import org.slim3plus.tq.Deferred;
import org.slim3plus.tq.DeferredInterceptor;

public final class CacheModule extends AbstractModule {

    private CacheModule() {}

    private static CacheModule module;

    @Override
    protected void configure() {
        MethodCacheInterceptor methodCacheInterceptor = new MethodCacheInterceptor();
        requestInjection(methodCacheInterceptor);
        bindInterceptor(Matchers.any(), Matchers.annotatedWith(Cacheable.class)
                , methodCacheInterceptor);
    }

    public static synchronized CacheModule get() {
        if (module == null) {
            module = new CacheModule();
        }
        return module;
    }
}
