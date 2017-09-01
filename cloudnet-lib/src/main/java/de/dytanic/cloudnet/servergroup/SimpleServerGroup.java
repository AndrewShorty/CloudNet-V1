package de.dytanic.cloudnet.servergroup;

import de.dytanic.cloudnet.lib.interfaces.Nameable;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Created by Tareko on 01.06.2017.
 */
@AllArgsConstructor
@Getter
public class SimpleServerGroup
        implements Nameable{

    private String name;
    private int joinPower;
    private ServerGroupMode mode;
    private boolean maintenance;
    private String parentGroup;
    private boolean createServerEnabled;

}
