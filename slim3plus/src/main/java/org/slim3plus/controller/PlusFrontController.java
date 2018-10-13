package org.slim3plus.controller;

import org.slim3.controller.Controller;
import org.slim3.controller.FrontController;
import org.slim3plus.util.InjectorFactory;

import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import java.lang.reflect.Modifier;

public class PlusFrontController extends FrontController {

    @Override
    protected Controller createController(String path) throws IllegalStateException {
        String className = toControllerClassName(path);
        if (className == null) {
            return null;
        }
        Class<?> clazz = null;
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        try {
            clazz = Class.forName(className, true, loader);
        } catch (Throwable t) {
            return null;
        }
        if (!Controller.class.isAssignableFrom(clazz)) {
            return null;
        }
        if (Modifier.isAbstract(clazz.getModifiers())) {
            return null;
        }
        return (Controller) InjectorFactory.getInjector().getInstance(clazz);
    }

    @Override
    public void init(FilterConfig config) throws ServletException {
        super.init(config);
    }

}
