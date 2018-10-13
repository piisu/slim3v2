package org.slim3plus.controller;

import org.slim3.controller.Controller;
import org.slim3.controller.Navigation;

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

}
