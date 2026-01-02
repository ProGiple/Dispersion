package com.satellite.progiple.databases;

import com.satellite.progiple.utils.Utilities;
import lombok.SneakyThrows;

import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class AsyncExecutor {
    private final ExecutorService executor;
    private final ConnectionPool connectionPool;

    public AsyncExecutor(String driverClass,
                         String url,
                         String user,
                         String password,
                         int maxRetries,
                         int poolSize,
                         int queryTimeout,
                         int validationTimeout,
                         int asyncThreadPoolSize) {
        this.executor = Executors.newFixedThreadPool(asyncThreadPoolSize);
        this.connectionPool = new ConnectionPool(driverClass,
                url,
                user,
                password,
                maxRetries,
                poolSize,
                queryTimeout,
                validationTimeout);
    }

    public AsyncExecutor(DataBaseParams dataBaseParams) {
        this(dataBaseParams.driverClass(),
                dataBaseParams.url(),
                dataBaseParams.user(),
                dataBaseParams.password(),
                dataBaseParams.maxRetries(),
                dataBaseParams.poolSize(),
                dataBaseParams.queryTimeout(),
                dataBaseParams.validationTimeout(),
                dataBaseParams.asyncThreadPoolSize());
    }

    public void executeAsync(String query, Object... params) {
        executor.submit(() -> {
            try {
                connectionPool.executeUpdate(query, 1, params);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public void executeSync(String query, Object... params) {
        try {
            connectionPool.executeUpdate(query, 1, params);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public <T> List<T> executeQuery(String query, ResultSetHandler<T> handler, Object... params) {
        try {
            return connectionPool.executeQuery(query, handler, params);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @SneakyThrows
    public void shutdown() {
        executor.shutdown();
        if (!executor.awaitTermination(60, TimeUnit.SECONDS)) {
            executor.shutdownNow();
        }
        connectionPool.closeAll();
    }

    @Override
    public String toString() {
        return Utilities.toString(this);
    }
}
