package com.satellite.progiple.test.databases;

import com.satellite.progiple.databases.ITableStructure;
import com.satellite.progiple.databases.reflection.AutoIncrement;
import com.satellite.progiple.databases.reflection.PrimaryKey;
import com.satellite.progiple.databases.reflection.TableProcessing;
import lombok.Getter;

import java.sql.Timestamp;

@TableProcessing @Getter
public class PlayerStruct implements ITableStructure {
    @PrimaryKey @AutoIncrement
    private int id;
    private String name;
    private Timestamp registrationDate;
}
