package de.dytanic.cloudnet.servergroup;

import de.dytanic.cloudnet.lib.interfaces.Nameable;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.List;

/**
 * Created by Tareko on 21.05.2017.
 */
@Getter
@Setter
public class ServerGroup
        implements Nameable{

    private String name;
    private int memory;
    private int dynamicMemory;
    private int joinPower;
    private boolean maintenance;
    private int startup;
    private java.util.Map<String, Object> properties;

    private int priority;
    private int groupPriority;
    private int priorityStopTime;
    private int onlineCountForPriority;
    private int priorityForGroupOnlineCount;

    private String hostName;
    private int maxPlayers;
    private ServerGroupType serverType;
    private ServerGroupMode groupMode;
    private boolean createServerEnabled;

    private String parentGroup;

    public ServerGroup(String name, int memory, int dynamicMemory, java.util.Map<String, Object> properties, int joinPower, boolean maintenance, int startup,
                       int priority, int groupPriority, String hostName, int maxPlayers, int priorityStopTime, int onlineCountForPriority, int priorityForGroupOnlineCount,
                       ServerGroupType serverType, ServerGroupMode groupMode, ServerGroup parentGroup)
    {
        this.name = name;
        this.memory = memory;
        this.dynamicMemory = dynamicMemory;
        this.joinPower = joinPower;
        this.maintenance = maintenance;
        this.startup = startup;
        this.priority = priority;
        this.groupPriority = groupPriority;
        this.hostName = hostName;
        this.maxPlayers = maxPlayers;
        this.serverType = serverType;
        this.groupMode = groupMode;
        this.createServerEnabled = false;

        this.properties = properties;

        this.priorityStopTime = priorityStopTime;
        this.onlineCountForPriority = onlineCountForPriority;
        this.priorityForGroupOnlineCount = priorityForGroupOnlineCount;

        this.parentGroup = parentGroup != null ? parentGroup.getName() : null;

        if(parentGroup != null)
        {
            this.joinPower = parentGroup.getJoinPower();
            this.serverType = parentGroup.getServerType();
            this.maintenance = parentGroup.isMaintenance();
        }
    }

    public void handleExtends(ServerGroup parentGroup)
    {
        if(parentGroup == null &&
           this.parentGroup == null &&
           !this.parentGroup.equals(parentGroup.getName())) return;

        this.joinPower = parentGroup.getJoinPower();
        this.serverType = parentGroup.getServerType();
        this.maintenance = parentGroup.isMaintenance();

    }

    public SimpleServerGroup toSimple()
    {
        return new SimpleServerGroup(name, joinPower, groupMode, maintenance, parentGroup, createServerEnabled);
    }
}
