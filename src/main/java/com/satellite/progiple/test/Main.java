package com.satellite.progiple.test;

import com.satellite.progiple.registration.DiRegister;
import com.satellite.progiple.test.databases.DataBaseTest;

public class Main {
    public static final String PATH = "C:/Users/VyachePC/IdeaProjects/Dispersion";

    public static void main(String[] args) {
        DiRegister.initialize(Main.class);
        System.out.println(Example.script.execute(0));
    }
}