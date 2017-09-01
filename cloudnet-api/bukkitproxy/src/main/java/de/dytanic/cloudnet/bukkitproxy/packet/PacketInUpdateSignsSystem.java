package de.dytanic.cloudnet.bukkitproxy.packet;

import com.google.gson.reflect.TypeToken;
import de.dytanic.cloudnet.api.CloudNetAPI;
import de.dytanic.cloudnet.bukkitproxy.api.CloudServer;
import de.dytanic.cloudnet.bukkitproxy.serverselectors.ServerSelectorSigns;
import de.dytanic.cloudnet.lib.Return;
import de.dytanic.cloudnet.lib.document.Document;
import de.dytanic.cloudnet.network.ServerInfo;
import de.dytanic.cloudnet.network.packet.Packet;
import de.dytanic.cloudnet.network.packet.PacketSender;
import de.dytanic.cloudnet.sign.GroupsLayout;
import de.dytanic.cloudnet.sign.Sign;
import de.dytanic.cloudnet.sign.SignLayout;

import java.util.Collection;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Tareko on 06.06.2017.
 */
public class PacketInUpdateSignsSystem
        extends Packet {

    @Override
    public void handleInput(Document data, PacketSender packetSender)
    {



        java.util.Map<UUID, Sign> signs = data.getObject("signs", new TypeToken<java.util.Map<UUID, Sign>>() {}.getType());
        Collection<GroupsLayout> layouts = data.getObject("layouts", new TypeToken<Collection<GroupsLayout>>(){}.getType());

        if (ServerSelectorSigns.getInstance() != null)
        {
            ServerSelectorSigns.getInstance().setLayouts(layouts);

            for (Sign sign : ServerSelectorSigns.getInstance().getSigns().values())
            {
                if (!signs.containsKey(sign.getUniqueId()))
                {
                    ServerInfo serverInfo = sign.getServerInfo();
                    sign.setServerInfo(null);
                    ServerSelectorSigns.getInstance().getSigns().remove(sign.getUniqueId());

                    if (serverInfo != null)
                        ServerSelectorSigns.getInstance().handleNext(serverInfo);
                }
            }

            for (Sign sign : signs.values())
            {
                if (!ServerSelectorSigns.getInstance().getSigns().containsKey(sign.getUniqueId())
                        && sign.getPosition().getGroup().equals(CloudServer.getInstance().getGroup()))
                {
                    ServerSelectorSigns.getInstance().getSigns().put(sign.getUniqueId(), sign);
                }
            }

        }

        if(ServerSelectorSigns.getInstance().getHandledThread() == null)
        {
            for (ServerInfo serverInfo : CloudNetAPI.getInstance().getCloudNetwork().getServers().values())
            {
                Return<Boolean, UUID> c = ServerSelectorSigns.getInstance().contains(serverInfo);
                if (c.getFirst())
                {
                    Sign sign = signs.get(c.getSecond());
                    ServerSelectorSigns.getInstance().handleUpdate(sign, serverInfo);

                } else
                {
                    Sign sign = ServerSelectorSigns.getInstance().findNextSign(serverInfo);

                    if (sign != null)
                    {
                        ServerSelectorSigns.getInstance().handleUpdate(sign, serverInfo);
                    }
                }
            }
            ServerSelectorSigns.getInstance().appendHandledThread();
            ServerSelectorSigns.getInstance().getHandledThread().start();
        }

    }
}