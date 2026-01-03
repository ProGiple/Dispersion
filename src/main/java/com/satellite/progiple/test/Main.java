package com.satellite.progiple.test;

import com.satellite.progiple.encryption.DiCode;
import com.satellite.progiple.registration.DiRegister;

public class Main {
    public static final String PATH = "C:/Users/VyachePC/IdeaProjects/Dispersion";

    public static void main(String[] args) {
        DiRegister.initialize(Main.class);

        String seed = DiCode.gen();
        System.out.println(seed);
        DiCode diCode = new DiCode(seed);
        String form = diCode.encode("Hello World");

        System.out.println(form);
    }
}