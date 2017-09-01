package de.dytanic.cloudnetserver.server.handlers;
import de.dytanic.cloudnet.lib.threading.ScheduledTask;
import de.dytanic.cloudnet.servergroup.ServerGroup;
import de.dytanic.cloudnetserver.CloudNetServer;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

/**
 * Created by Tareko on 25.05.2017.
 */
@Getter
public class ServerPriority
                implements Runnable {

    private final String serverId;
    private final UUID uuid;

    private int tick;

    @Setter
    private ScheduledTask scheduledTask;

    public ServerPriority(String serverId, UUID uuid, ServerGroup group)
    {
        this.serverId = serverId;
        this.uuid = uuid;
        this.tick = group.getPriorityStopTime() + 11;
    }

    @Override
    public void run()
    {
        if(CloudNetServer.getInstance().getCloudNetwork().getServers().containsKey(serverId)
                && CloudNetServer.getInstance().getCloudNetwork().getServers().get(serverId).getOnlineCount() != 0)
        {
            return;
        }

        if(CloudNetServer.getInstance().getServers().containsKey(serverId)
                &&
           CloudNetServer.getInstance().getServers().get(serverId).getUniqueId().equals(uuid))
        {
            if(tick > 0)
            {
                tick--;
            }
            else
            {
                    CloudNetServer.getInstance().getServers().get(serverId).shutdown();
                    if(scheduledTask != null)
                    {
                        scheduledTask.cancel();
                    }
            }
        } else
            if(scheduledTask != null)
            {
                scheduledTask.cancel();
            }
    }
}
