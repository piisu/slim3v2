package org.slim3plus.service;

import org.junit.Test;
import org.slim3plus.tester.PlusAppEngineTestCase;

import java.util.Date;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

public class TestServiceTest extends PlusAppEngineTestCase {

    private TestService service =  getInstance(TestService.class);

    @Test
    public void test() throws Exception {
        assertThat(service, is(notNullValue()));

        Date date = service.getDate();
        Thread.sleep(1);
        assertThat(date, is(not(service.getDate())));


        Date date2 = service.getCachedDate();
        Thread.sleep(1);
        assertThat(date2, is(not(service.getCachedDate())));
    }
}
