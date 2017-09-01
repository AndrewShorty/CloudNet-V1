package de.dytanic.cloudnetserver.server.handlers;

import de.dytanic.cloudnet.lib.document.Document;
import de.dytanic.cloudnet.servergroup.ServerGroup;
import de.dytanic.cloudnet.servergroup.ServerGroupMode;
import de.dytanic.cloudnetserver.CloudNetServer;
import de.dytanic.cloudnetserver.server.MinecraftServer;

import java.util.List;

/**
 * Created by Tareko on 25.06.2017.
 */
public final class GroupPriorityStartup
            implements Runnable {

    @Override
    public void run()
    {
        for(ServerGroup group : CloudNetServer.getInstance().getGroups().values())
        {

            if ((group.getMemory() + CloudNetServer.getInstance().getUsedMemory()) > CloudNetServer.getInstance().getMaxMemory())
            {
                continue;
            }

            if(group.getGroupPriority() == 0 || group.getGroupMode() == ServerGroupMode.STATIC
                    || group.isMaintenance()) continue;

            int groupOnline = 0;
            for(MinecraftServer minecraftServer : CloudNetServer.getInstance().getServers().values())
            {
                if(minecraftServer.getGroup().getName().equalsIgnoreCase(group.getName()))
                {
                    if(CloudNetServer.getInstance().getCloudNetwork().getServers().containsKey(minecraftServer.getServerId()))
                    {
                        groupOnline = groupOnline + CloudNetServer.getInstance().getCloudNetwork().getServers().get(minecraftServer.getServerId()).getOnlineCount();
                    }
                }
            }

            if(group.getGroupPriority() == 0 || group.getGroupMode() == ServerGroupMode.STATIC
                    || group.isMaintenance()) continue;

            double priority = (group.getGroupPriority() / ((double) group.getPriorityForGroupOnlineCount())) * (groupOnline == 0 ? 1.0D : (groupOnline));
            List<String> servers = CloudNetServer.getInstance().getServersByName(group.getName());

            if(servers.size() < (priority <= 1 ? 1 : priority))
            {
                CloudNetServer.getInstance().startServer(group, new Document(), servers.size() != 0, false);
            }

        }
    }
}
