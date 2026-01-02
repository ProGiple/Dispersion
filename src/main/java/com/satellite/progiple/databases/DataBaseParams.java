package com.satellite.progiple.databases;

import com.satellite.progiple.filles.handlers.EnvStorage;
import com.satellite.progiple.utils.Utilities;

public record DataBaseParams(String driverClass,
                             String url,
                             String user,
                             String password,
                             int maxRetries,
                             int poolSize,
                             int queryTimeout,
                             int validationTimeout,
                             int asyncThreadPoolSize) {

    @Override
    public String toString() {
        return Utilities.toString(this);
    }

    public static DataBaseParams toParams(String additivePath, EnvStorage env) {
        return new DataBaseParams(
                env.get(additivePath + "driver"),
                env.get(additivePath + "url"),
                env.get(additivePath + "user"),
                env.get(additivePath + "password"),
                Integer.parseInt(env.get(additivePath + "maxRetries")),
                Integer.parseInt(env.get(additivePath + "poolSize")),
                Integer.parseInt(env.get(additivePath + "queryTimeout")),
                Integer.parseInt(env.get(additivePath + "validationTimeout")),
                Integer.parseInt(env.get(additivePath + "asyncThreadPoolSize"))
        );
    }
}
