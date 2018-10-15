package org.slim3plus.tester;

import org.slim3.controller.ControllerConstants;
import org.slim3.tester.AppEngineTestCase;

public class PlusAppEngineTestCase extends AppEngineTestCase {

    @Override
    protected <T> T getInstance(Class<T> clazz) {
        final String rootPackageName = rootPackageName(clazz);
        return super.getInstance(clazz);
    }

    protected String rootPackageName(Class<?> testClass) {
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
}
