package org.slim3plus.model.mr;

import org.slim3.tester.AppEngineTestCase;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

public class MapReduceTest extends AppEngineTestCase {

    private MapReduce model = new MapReduce();

    @Test
    public void test() throws Exception {
        assertThat(model, is(notNullValue()));
    }
}
