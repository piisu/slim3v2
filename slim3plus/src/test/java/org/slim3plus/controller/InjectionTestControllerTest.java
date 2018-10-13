package org.slim3plus.controller;

import org.slim3plus.tester.PlusControllerTestCase;
import org.junit.Test;
import org.slim3plus.util.InjectorFactory;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

public class InjectionTestControllerTest extends PlusControllerTestCase {

    @Test
    public void run() throws Exception {
        tester.start("/injectionTest");
        InjectionTestController controller = tester.getController();
        assertThat(controller, is(notNullValue()));
        assertThat(tester.isRedirect(), is(false));
        assertThat(tester.getDestinationPath(), is(nullValue()));

        assertThat(InjectorFactory.getInjector(), is(notNullValue()));
        assertThat(controller.greeting, is(notNullValue()));
        assertThat(controller.greeting, is("Hello"));

    }
}
