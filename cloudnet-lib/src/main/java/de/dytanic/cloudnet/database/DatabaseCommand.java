package de.dytanic.cloudnet.database;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Created by Tareko on 01.07.2017.
 */
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum DatabaseCommand {

    INSERT("INSERT"),
    DELETE("DELETE"),
    GET("GET"),
    CONTAINS("CONTAINS"),
    SET("SET");

    private String name;

}