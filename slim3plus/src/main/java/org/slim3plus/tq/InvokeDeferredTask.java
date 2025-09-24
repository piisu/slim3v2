package org.slim3plus.tq;

import com.google.appengine.api.taskqueue.DeferredTask;
import com.google.appengine.api.taskqueue.DeferredTaskContext;
import com.google.inject.Guice;
import com.google.inject.Injector;
import org.slim3.util.ServletContextLocator;
import org.slim3plus.util.InjectorFactory;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.Arrays;

public class InvokeDeferredTask implements DeferredTask {

    private static final long serialVersionUID = 1L;

    private Class<?> clazz;

    private String methodName;

    private Class<?>[] parameterTypes;

    private Object[] params;

    public InvokeDeferredTask(Method method, Object[] params) {
        this.clazz = method.getDeclaringClass();
        this.methodName = method.getName();
        this.parameterTypes = method.getParameterTypes();
        this.params = params;
    }

    public InvokeDeferredTask(Class<?> clazz, String methodName,
                              Class<?>[] parameterTypes, Object[] params) {
        super();
        this.clazz = clazz;
        this.methodName = methodName;
        this.parameterTypes = parameterTypes;
        this.params = params;
    }

    @Override
    public void run() {
        Injector injector;
        try {
            final Object obj = InjectorFactory.getInjector().getInstance(clazz);
            final Method method = clazz.getMethod(methodName, parameterTypes);
            final Object ret = method.invoke(obj, params);
        } catch (Exception e) {
            throw new RuntimeException(this.toString(), e);
        }
    }


    @Override
    public String toString() {
        return "InvokeDeferredTask [clazz=" + clazz + ", methodName="
                + methodName + ", parameterTypes="
                + Arrays.toString(parameterTypes) + ", params="
                + Arrays.toString(params) + "]";
    }
}
