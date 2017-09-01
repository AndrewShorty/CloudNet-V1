package de.dytanic.cloudnet;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

/**
 * Created by Tareko on 28.05.2017.
 */
@AllArgsConstructor
@Data
public class Motd {

    private String firstLine;
    private String secondLine;

}
