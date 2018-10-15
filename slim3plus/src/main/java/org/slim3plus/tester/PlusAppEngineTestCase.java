package org.slim3plus.tester;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import org.slim3.controller.ControllerConstants;
import org.slim3.tester.AppEngineTestCase;
import org.slim3.util.ClassUtil;
import org.slim3.util.StringUtil;

public class PlusAppEngineTestCase extends AppEngineTestCase {

    @Override
    protected <T> T getInstance(Class<T> clazz) {
        final String rootPackageName = getRootPackageName(clazz);
        Injector injector = Guice.createInjector(createAppModule(rootPackageName));
        return injector.getInstance(clazz);
    }

    protected String getRootPackageName(Class<?> testClass) {
        String className = testClass.getName();
        int pos = className.lastIndexOf(".service.");
        if (pos < 0) {
            pos = className.lastIndexOf(".model.");
            if (pos < 0) {
                pos = className.lastIndexOf('.');
            }
        }
        return className.substring(0, pos);
    }

    private Module createAppModule(String rootPackageName) {
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


}
