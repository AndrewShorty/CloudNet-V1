package de.dytanic.cloudnetserver.network.packets;

import de.dytanic.cloudnet.lib.document.Document;
import de.dytanic.cloudnet.network.packet.Packet;
import de.dytanic.cloudnet.network.packet.PacketSender;
import de.dytanic.cloudnetserver.server.MinecraftServer;

/**
 * Created by Tareko on 28.05.2017.
 */
public class PacketAddServer
        extends Packet {

    public PacketAddServer(MinecraftServer minecraftServer, int bind)
    {
        super(302, new Document()
                .append("serverId", minecraftServer.getServerId())
                .append("uniqueId", minecraftServer.getUniqueId().toString())
                .append("group", minecraftServer.getGroup().getName())
                .append("mode", minecraftServer.getGroup().getGroupMode())
                .append("connectioninfo", minecraftServer.getGroup().getHostName() + ":" + bind)
                .append("host", minecraftServer.getGroup().getHostName())
                .append("port", minecraftServer.getPort())
                .append("properties", minecraftServer.getProperties())
                .append("profile", minecraftServer.getProfile())
                .append("hide", minecraftServer.isHide())
        );
        if(minecraftServer.getServerMap() != null)
        {
            this.getData().append("map", minecraftServer.getServerMap());
        }
    }

    @Override
    public void handleInput(Document data, PacketSender packetSender) {}
}