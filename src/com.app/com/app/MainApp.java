package com.app;

import module com.greeting;

public class MainApp {
    public static void main(String[] args) {
        HelloService service = new HelloService();

        System.out.println(service.sayHello("nara"));
    }
}