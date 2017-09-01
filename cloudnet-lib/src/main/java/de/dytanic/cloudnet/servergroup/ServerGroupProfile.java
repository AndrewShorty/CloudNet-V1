package de.dytanic.cloudnet.servergroup;

import de.dytanic.cloudnet.lib.interfaces.Nameable;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Created by Tareko on 03.07.2017.
 */
@Getter
@AllArgsConstructor
public class ServerGroupProfile
                    implements Nameable{

    private String name;

}