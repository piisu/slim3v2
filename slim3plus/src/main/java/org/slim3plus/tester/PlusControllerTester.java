package org.slim3plus.tester;

import org.slim3.tester.ControllerTester;
import org.slim3plus.controller.PlusFrontController;

public class PlusControllerTester extends ControllerTester {
    public PlusControllerTester(Class<?> testClass) throws NullPointerException {
        super(testClass);
        this.frontController = new PlusFrontController();
    }
}
