package com.satellite.progiple.databases.reflection;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ConfDataBase {
    Class<?> parentClass() default Void.class;
    String paramsField() default "dbparams";
}
