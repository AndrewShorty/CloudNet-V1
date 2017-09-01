package de.dytanic.cloudnetserver.server.handlers;

import de.dytanic.cloudnet.lib.document.Document;
import de.dytanic.cloudnet.servergroup.ServerGroup;
import de.dytanic.cloudnet.servergroup.ServerGroupMode;
import de.dytanic.cloudnetserver.CloudNetServer;

import java.util.List;

/**
 * Created by Tareko on 28.05.2017.
 */
public final class GlobalPriorityStartup
            implements Runnable {

    @Override
    public void run()
    {
       double onlineCount = CloudNetServer.getInstance().getCloudNetwork().getOnlineCount();
       for(ServerGroup group : CloudNetServer.getInstance().getGroups().values())
       {
           if ((group.getMemory() + CloudNetServer.getInstance().getUsedMemory()) > CloudNetServer.getInstance().getMaxMemory())
           {
               continue;
           }
            if(group.getPriority() == 0 || group.getGroupMode() == ServerGroupMode.STATIC
                    || group.isMaintenance()) continue;

           double priortiy = (group.getPriority() / ((double) group.getOnlineCountForPriority())) * (onlineCount == 0 ? 1.0D : (onlineCount));
           List<String> servers = CloudNetServer.getInstance().getServersByName(group.getName());

           if(servers.size() < (priortiy <= 1 ? 1 : priortiy))
           {
               CloudNetServer.getInstance().startServer(group, new Document(), servers.size() != 0, false);
           }

       }
    }
}
