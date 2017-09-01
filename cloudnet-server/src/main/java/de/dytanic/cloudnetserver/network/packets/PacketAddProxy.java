package de.dytanic.cloudnetserver.network.packets;

import de.dytanic.cloudnet.lib.document.Document;
import de.dytanic.cloudnet.network.packet.Packet;
import de.dytanic.cloudnet.network.packet.PacketSender;
import de.dytanic.cloudnetserver.CloudNetServer;
import de.dytanic.cloudnetserver.server.ProxyServer;

/**
 * Created by Tareko on 28.05.2017.
 */
public class PacketAddProxy
        extends Packet {

    public PacketAddProxy(ProxyServer minecraftServer)
    {
        super(304, new Document()
                .append("serverId", minecraftServer.getServerId())
                .append("uniqueId", minecraftServer.getUniqueId().toString())
                .append("connectioninfo", CloudNetServer.getInstance().getHostName() + ":" + (minecraftServer.getPort() - 1))
                .append("host", CloudNetServer.getInstance().getHostName())
                .append("port", minecraftServer.getPort())
                .append("fallback", minecraftServer.getConfig().getFallback())
                .append("memory", minecraftServer.getConfig().getMemory())
        );
    }

    @Override
    public void handleInput(Document data, PacketSender packetSender) {}
}