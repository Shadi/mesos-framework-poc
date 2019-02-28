package com.github.mesosdemo;

import com.google.inject.Guice;
import com.google.inject.Injector;

public class Framework {

    public static void main(String[] args) {
        Injector injector = Guice.createInjector(new FrameworkModule());
        MainRunner mainRunner = injector.getInstance(MainRunner.class);
        mainRunner.run("localhost:5050");
    }
}
