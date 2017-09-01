package de.dytanic.cloudnetproxy.network.packets;

import de.dytanic.cloudnet.lib.document.Document;
import de.dytanic.cloudnet.network.packet.Packet;
import de.dytanic.cloudnet.network.packet.PacketSender;
import de.dytanic.cloudnet.servergroup.ServerGroupMode;
import de.dytanic.cloudnet.servergroup.ServerGroupProfile;
import de.dytanic.cloudnet.servergroup.ServerMap;
import de.dytanic.cloudnetproxy.CloudNetProxy;
import de.dytanic.cloudnetproxy.network.CloudNetProxyServer;
import de.dytanic.cloudnetproxy.network.NetworkInfo;
import de.dytanic.cloudnetproxy.network.components.CNS;
import de.dytanic.cloudnetproxy.network.components.MinecraftServer;

import java.util.UUID;

/**
 * Created by Tareko on 28.05.2017.
 */
public class PacketInAddServer
            extends Packet {

    public PacketInAddServer() {}

    @Override
    public void handleInput(Document value, PacketSender sender)
    {
        if(!(sender instanceof CNS)) return;
        CNS cns = (CNS)sender;

        String serverId = value.getString("serverId");
        MinecraftServer minecraftServer = new MinecraftServer(
                cns,
                cns.getGroups().get(value.getString("group")),
                UUID.fromString(value.getString("uniqueId")),
                new NetworkInfo(serverId, value.getString("host"), value.getInt("port")),
                new NetworkInfo(serverId, value.getString("connectioninfo").split(":")[0], Integer.parseInt(value.getString("connectioninfo").split(":")[1])),
                value.getDocument("properties"), ServerGroupMode.valueOf(value.getString("mode")), value.contains("map") ? value.getObject("map", ServerMap.class) : null,
                value.getObject("profile", ServerGroupProfile.class),
                value.getBoolean("hide"));
        cns.getServers().put(serverId, minecraftServer);
        System.out.println("[" + cns.getServerId() + "] add server [serverId=" +
                serverId + "/" + minecraftServer.getNetworkInfo().getHostName() + ":" + minecraftServer.getNetworkInfo().getPort() + "] into the network.");

        for(CloudNetProxyServer proxyServer : CloudNetProxy.getInstance().getProxyServer())
            proxyServer.sendAllPacket(
            new PacketOutUpdateNetwork(minecraftServer.getServerInfo(), PacketOutUpdateNetwork.UpdateType.UPDATE_SERVER));
        CloudNetProxy.getInstance().updateNetwork();
    }
}