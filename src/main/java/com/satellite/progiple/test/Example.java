package com.satellite.progiple.test;

import com.satellite.progiple.filles.handlers.EnvStorage;
import com.satellite.progiple.filles.handlers.Script;
import com.satellite.progiple.filles.reflection.ScriptData;
import com.satellite.progiple.registration.Autowired;
import com.satellite.progiple.registration.Event;
import com.satellite.progiple.registration.EventType;

public class Example {
    @ScriptData(path = Main.PATH + "/target/test/scripts/script.jd", packageName = "com.satellite.progiple")
    public static Script script;

    @Autowired
    public static EnvStorage env;

    @Event(EventType.ON_START)
    public static void _join() {
        System.out.println("_join");
    }

    @Event(EventType.REG_FILES)
    public static void _regwefFiles() {
        System.out.println("_regFiles");
    }

    @Event(EventType.REG_TABLES)
    public static void efesfe() {
        System.out.println("_regTables");
    }

    @Event(EventType.AFTER_INIT)
    public static void rtytr() {
        System.out.println("_init");
    }

    @Event(EventType.REG_SCRIPTS)
    public static void eeerer() {
        System.out.println("_scripts");
    }
}
