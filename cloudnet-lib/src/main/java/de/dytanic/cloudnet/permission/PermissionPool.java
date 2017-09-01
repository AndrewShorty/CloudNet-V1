package de.dytanic.cloudnet.permission;

import de.dytanic.cloudnet.player.PlayerWhereAmI;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;

/**
 * Created by Tareko on 01.06.2017.
 */
@Getter
public class PermissionPool {

    @Setter private boolean available = true;

    private java.util.Map<String, PermissionGroup> groups = new HashMap<>();

    public PermissionGroup getDefaultGroup()
    {
        for(PermissionGroup group : groups.values())
        {
            if(group.isDefaultGroup())
            {
                return group;
            }
        }
        return null;
    }

    public PermissionEntity getNewPermissionEntity(PlayerWhereAmI playerWhereAmI)
    {
        return new PermissionEntity(playerWhereAmI.getUniqueId(), new HashMap<>(), getDefaultGroup().getName(), 0L);
    }

}