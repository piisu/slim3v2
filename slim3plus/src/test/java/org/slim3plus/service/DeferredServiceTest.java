package org.slim3plus.service;

import org.slim3.tester.AppEngineTestCase;
import org.junit.Test;
import org.slim3plus.tester.PlusAppEngineTestCase;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

public class DeferredServiceTest extends PlusAppEngineTestCase {

    private DeferredService service =  getInstance(DeferredService.class);

    @Test
    public void test() throws Exception {
        assertThat(service, is(notNullValue()));
        System.out.println(service);
        System.out.println(service.testDeferred());
    }
}
