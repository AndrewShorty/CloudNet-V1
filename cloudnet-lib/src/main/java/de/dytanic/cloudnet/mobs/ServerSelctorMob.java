package de.dytanic.cloudnet.mobs;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

/**
 * Created by Tareko on 09.07.2017.
 */
@Getter
@AllArgsConstructor
public class ServerSelctorMob {

    protected UUID uniqueId;
    protected String display;
    protected String type;
    protected String targetGroup;

}