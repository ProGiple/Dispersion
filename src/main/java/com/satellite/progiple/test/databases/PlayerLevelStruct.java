package com.satellite.progiple.test.databases;

import com.satellite.progiple.databases.ITableStructure;
import com.satellite.progiple.databases.reflection.TableProcessing;
import lombok.Getter;

@TableProcessing @Getter
public class PlayerLevelStruct implements ITableStructure {
    private String playerName;
}
