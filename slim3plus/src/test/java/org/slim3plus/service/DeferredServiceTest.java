package org.slim3plus.service;

import org.slim3.tester.AppEngineTestCase;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

public class DeferredServiceTest extends AppEngineTestCase {

    private DeferredService service = new DeferredService();

    @Test
    public void test() throws Exception {
        assertThat(service, is(notNullValue()));
    }
}
