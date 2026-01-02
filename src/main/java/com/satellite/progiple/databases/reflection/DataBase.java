package com.satellite.progiple.databases.reflection;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface DataBase {
    String driverClass() default "com.mysql.cj.jdbc.Driver";
    String url() default "jdbc:mysql://localhost:3306/";
    String user() default "root";
    String password() default "";
    int maxRetries() default 5;
    int poolSize() default 10;
    int queryTimeout() default 30;
    int validationTimeout() default 2;
    int asyncThreadPoolSize() default 10;
}
