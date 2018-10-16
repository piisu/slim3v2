package org.slim3plus.cache;

import com.google.appengine.api.memcache.MemcacheSerialization;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.lang3.SerializationUtils;
import org.slim3plus.service.LowLevelCacheService;

import javax.inject.Inject;
import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Method;

public class MethodCacheInterceptor implements MethodInterceptor {

    @Inject
   LowLevelCacheService lowLevelCacheService;

    /**
     * @param cacheClearContext 　このコンテキストでよばれたメソッドのキャッシュはクリアされる。また、このコンテキストでよばれたメソッドはnullを返す
     */
    public static void clear(final Runnable cacheClearContext) {
        clearContext.set(Boolean.TRUE);
        try {
            cacheClearContext.run();
        } finally {
            clearContext.set(Boolean.FALSE);
        }
    }

    private static ThreadLocal<Boolean> clearContext = new ThreadLocal<Boolean>();

    private static boolean isClearContext() {
        return clearContext.get() == Boolean.TRUE;
    }

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        final Cacheable cacheable = invocation.getMethod()
                .getAnnotation(Cacheable.class);

        final byte[] key = createKey(invocation.getMethod(), invocation.getArguments(), cacheable.version());
        if (isClearContext()) {
            lowLevelCacheService.clear(cacheable.value(), key);
            return null;
        }

        try {
            return SerializationUtils.deserialize(
                    lowLevelCacheService.get(cacheable.value(), key));
        }
        catch(CacheNotFoundException ex) {
            //nothing
        }

        final Object result = invocation.proceed();
        byte[] value = SerializationUtils.serialize((Serializable) result);
        Expiration exp = cacheable.expiration() < 0 ? null: Expiration.byDeltaSeconds(cacheable.expiration());
        lowLevelCacheService.put(cacheable.value(), key, value, exp);

        return result;
    }

    /**
     * 250 バイト未満となるキーを生成する
     *
     * @param method    メソッド
     * @param arguments 引数
     * @param version   バージョン
     * @return
     * @throws IOException
     */
    public byte[] createKey(Method method, Object[] arguments, String version) throws IOException {
        return createKey(method.getDeclaringClass(), method.getName()
                , method.getParameterTypes(), arguments, version);
    }

    /**
     * 250 バイト未満となるキーを生成する
     * @param clazz
     * @param methodName
     * @param parameterTypes
     * @param arguments
     * @param version
     * @return
     * @throws IOException
     */
    public byte[] createKey(Class clazz, String methodName, Class[] parameterTypes, Object[] arguments, String version) throws IOException {
        String[] parameterTypeNames = new String[parameterTypes.length];
        for (int i = 0; i < parameterTypes.length; i++) {
            parameterTypeNames[i] = parameterTypes[i].getName();
        }
        return MemcacheSerialization.makePbKey(new Serializable[]{
                clazz.getName(),
                methodName,
                parameterTypeNames,
                arguments,
                version
        });
    }
}
