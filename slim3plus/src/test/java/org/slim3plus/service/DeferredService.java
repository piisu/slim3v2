package org.slim3plus.service;


import com.google.appengine.api.taskqueue.TaskHandle;
import org.slim3plus.tq.Deferred;

public class DeferredService {


    @Deferred
    public TaskHandle testDeferred() {
        return null;
    }

    public void test() {
        System.out.printf("test");
    }


}
