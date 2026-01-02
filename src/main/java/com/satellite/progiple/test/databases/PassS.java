package com.satellite.progiple.test.databases;

import com.satellite.progiple.databases.ITableStructure;
import com.satellite.progiple.databases.reflection.AutoIncrement;
import com.satellite.progiple.databases.reflection.PrimaryKey;
import com.satellite.progiple.databases.reflection.TableProcessing;

@TableProcessing
public class PassS implements ITableStructure {
    @PrimaryKey @AutoIncrement
    int id;
    String hash;
}
