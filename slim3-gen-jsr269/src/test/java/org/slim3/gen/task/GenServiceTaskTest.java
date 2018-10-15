/*
 * Copyright 2004-2010 the Seasar Foundation and the Others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package org.slim3.gen.task;

import org.junit.Test;
import org.slim3.gen.desc.DaoDesc;
import org.slim3.gen.desc.ServiceDesc;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * @author sue445
 *
 */
public class GenServiceTaskTest {

    /**
     * @throws Exception
     */
    @Test
    public void testCreateService() throws Exception{
        GenServiceTask task = new GenServiceTask();
        task.setServiceDefinition("HogeService");
        task.setPackageName("slim3.service");

        ServiceDesc serviceDesc = task.createServiceDesc();
        assertThat(serviceDesc.getSimpleName(), is("HogeService"));
        assertThat(serviceDesc.getPackageName(), is("slim3.service"));
    }


}
