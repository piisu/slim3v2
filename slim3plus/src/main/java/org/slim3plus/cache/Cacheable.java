package org.slim3plus.cache;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Cacheable {
    Class<? extends LowLevelCache>[] value();

    String version() default "default";

    /***
     * キャッシュの有効期限(秒)
     * @return
     */
    int expiration() default -1;

}
