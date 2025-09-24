package org.slim3plus.util;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import org.slim3.controller.ControllerConstants;
import org.slim3.controller.router.Router;
import org.slim3.util.*;

import jakarta.servlet.ServletContext;

public class InjectorFactory {
    /**
     * The key of {@link Router}.
     */
    public static final String INJECTOR_KEY = "slim3.injector";

    /**
     * Returns a injector.
     *
     * @return a injector
     */
    public static synchronized Injector getInjector() {
               ServletContext servletContext = getServletContext();
        Injector injector = (Injector) servletContext.getAttribute(INJECTOR_KEY);
        if (injector == null) {
            injector = createInjector(servletContext);
            servletContext.setAttribute(INJECTOR_KEY, injector);
            Cleaner.add(new Cleanable() {
                public void clean() {
                    getServletContext().removeAttribute(INJECTOR_KEY);
                }
            });
        }
        return injector;
    }

    private static ServletContext getServletContext() {
        ServletContext servletContext = ServletContextLocator.get();
        if (servletContext == null) {
            throw new IllegalStateException("The servletContext is not found.");
        }
        return servletContext;
    }

    private static Injector createInjector(ServletContext servletContext) {
        return Guice.createInjector(createAppModule(servletContext));
    }

    private static Module createAppModule(ServletContext servletContext) {
        String rootPackageName =
                servletContext
                        .getInitParameter(ControllerConstants.ROOT_PACKAGE_KEY);
        return createAppModule(rootPackageName);
    }

    private static Module createAppModule(String rootPackageName) {
        if (StringUtil.isEmpty(rootPackageName)) {
            throw new IllegalStateException("The context-param("
                    + ControllerConstants.ROOT_PACKAGE_KEY
                    + ") is not found in web.xml.");
        }
        try {
            String className =
                    rootPackageName +  ".AppModule";
            Class<?> clazz = Class.forName(className);
            return ClassUtil.newInstance(clazz);
        } catch (ClassNotFoundException e) {
            return null;
        }
    }

    private InjectorFactory() {
    }


}
