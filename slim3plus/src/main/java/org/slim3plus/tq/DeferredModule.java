package org.slim3plus.tq;

import com.google.inject.AbstractModule;
import com.google.inject.matcher.Matchers;

public final class DeferredModule extends AbstractModule {

    private DeferredModule() {}

    private static DeferredModule module;

    @Override
    protected void configure() {
        bindInterceptor(Matchers.any(),
                Matchers.annotatedWith(Deferred.class),
                new DeferredInterceptor());
    }

    public static synchronized DeferredModule get() {
        if (module == null) {
            module = new DeferredModule();
        }
        return module;
    }
}
