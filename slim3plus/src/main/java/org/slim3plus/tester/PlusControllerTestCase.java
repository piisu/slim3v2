package org.slim3plus.tester;

import org.junit.After;
import org.junit.Before;

public class PlusControllerTestCase {
    /**
     * The tester for Slim3 Controller.
     */
    protected PlusControllerTester tester =
            new PlusControllerTester(getClass());

    /**
     * Sets up this test.
     *
     * @throws Exception
     *             if an exception occurred
     */
    @Before
    public void setUp() throws Exception {
        tester.setUp();
    }

    /**
     * Tears down this test
     *
     * @throws Exception
     *             if an exception occurred
     */
    @After
    public void tearDown() throws Exception {
        tester.tearDown();
    }
}
