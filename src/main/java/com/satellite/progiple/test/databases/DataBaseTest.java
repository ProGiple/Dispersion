package com.satellite.progiple.test.databases;

import com.satellite.progiple.databases.AsyncExecutor;
import com.satellite.progiple.databases.DataBaseParams;
import com.satellite.progiple.databases.Table;
import com.satellite.progiple.databases.reflection.ConfDataBase;
import com.satellite.progiple.databases.reflection.ConfTableBase;
import com.satellite.progiple.databases.reflection.TableBase;
import com.satellite.progiple.filles.handlers.EnvStorage;
import com.satellite.progiple.filles.reflection.EnvData;
import com.satellite.progiple.registration.Event;
import com.satellite.progiple.registration.EventType;
import com.satellite.progiple.test.Main;
import lombok.experimental.UtilityClass;

@UtilityClass
public class DataBaseTest {
    @EnvData(Main.PATH + "/target/test/database.env") private EnvStorage env;
    private String tableName;
    private boolean tableAsync;
    public DataBaseParams dbparams;

    @ConfDataBase private AsyncExecutor database;
    @ConfTableBase public Table<PlayerStruct> playersTable;
    @TableBase(name = "psw") public Table<PassS> passTable;

    @Event(EventType.REG_FILES)
    private void _regFiles() {
        tableAsync = "true".equalsIgnoreCase(env.get("tableAsync"));
        tableName = env.get("tableName");
        dbparams = DataBaseParams.toParams("db.", env);
    }
}
