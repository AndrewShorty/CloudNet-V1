package de.dytanic.cloudnetproxy.network.packets;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;
import de.dytanic.cloudnet.lib.document.Document;
import de.dytanic.cloudnet.network.CNSInfo;
import de.dytanic.cloudnet.network.NetworkUtils;
import de.dytanic.cloudnet.network.packet.Packet;
import de.dytanic.cloudnet.network.packet.PacketSender;
import de.dytanic.cloudnet.player.PlayerWhereAmI;
import de.dytanic.cloudnet.servergroup.ServerGroup;
import de.dytanic.cloudnet.servergroup.SimpleServerGroup;
import de.dytanic.cloudnetproxy.CloudNetProxy;
import de.dytanic.cloudnetproxy.network.CloudNetProxyServer;
import de.dytanic.cloudnetproxy.network.components.CNS;

import java.util.HashMap;

/**
 * Created by Tareko on 27.05.2017.
 */
public class PacketInServerData
        extends Packet {

    public PacketInServerData()
    {
    }

    @Override
    public void handleInput(Document document, PacketSender sender)
    {
        if (!(sender instanceof CNS)) return;
        CNS cns = ((CNS) sender);

        JsonArray jsonElements = document.getArray("groups");

        cns.setMaxMemory(document.getInt("maxmemory"));
        cns.getGroups().clear();

        for (JsonElement jsonElement : jsonElements)
        {
            ServerGroup group = NetworkUtils.GSON.fromJson(jsonElement, ServerGroup.class);
            cns.getGroups().put(group.getName(), group);
        }

        System.out.println(sender.getName() + " has " + cns.getMaxMemory() + "MB and the following groups:");
        System.out.println(cns.getGroups().keySet());

        java.util.Map<String, SimpleServerGroup> groups = new HashMap<>();
        for (CNS cns_ : CloudNetProxy.getInstance().getCnsSystems().values())
        {
            for (ServerGroup group : cns_.getGroups().values())
            {
                groups.put(group.getName(), group.toSimple());
            }
        }

        cns.setCnsInfo(document.getObject("info", new TypeToken<CNSInfo>(){}.getType()));

        CloudNetProxy.getInstance().updateNetwork(false);

        PacketOutUpdateNetwork packetOutUpdateNetwork = new PacketOutUpdateNetwork(groups, PacketOutUpdateNetwork.UpdateType.UPDATE_GROUPS);
        for (CloudNetProxyServer proxyServer : CloudNetProxy.getInstance().getProxyServer())
            proxyServer.sendAllPacket(packetOutUpdateNetwork);
    }
}