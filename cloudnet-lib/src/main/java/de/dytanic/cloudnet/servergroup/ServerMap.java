package de.dytanic.cloudnet.servergroup;

import de.dytanic.cloudnet.lib.interfaces.Nameable;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Created by Tareko on 30.06.2017.
 */
@Getter
@AllArgsConstructor
public class ServerMap
            implements Nameable{

    private String name;
    private int maxPlayers;

}
