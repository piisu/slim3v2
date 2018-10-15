package org.slim3plus.controller;

import com.google.appengine.api.taskqueue.TaskHandle;
import org.slim3.controller.Controller;
import org.slim3.controller.Navigation;
import org.slim3plus.tq.Deferred;

import javax.inject.Inject;
import javax.inject.Named;

public class InjectionTestController extends Controller {

    @Override
    public Navigation run() throws Exception {
        return null;
    }

    @Inject
    @Named("greeting")
    public String greeting;


    @Deferred
    public TaskHandle testDeferred() {
        return null;
    }

    public void test() {
        System.out.printf("test");
    }

}
