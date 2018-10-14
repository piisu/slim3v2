package org.slim3plus;

import com.google.inject.AbstractModule;
import com.google.inject.name.Names;
import org.slim3plus.tq.DeferredModule;

public class AppModule extends AbstractModule {
    @Override
    protected void configure() {

        install(DeferredModule.get());

        bind(String.class).annotatedWith(Names.named("greeting")).toInstance("Hello");
    }
}
