package de.dytanic.cloudnetserver.network.packets;

import de.dytanic.cloudnet.lib.document.Document;
import de.dytanic.cloudnet.network.CNSInfo;
import de.dytanic.cloudnet.network.packet.Packet;
import de.dytanic.cloudnet.network.packet.PacketSender;
import de.dytanic.cloudnetserver.CloudNetServer;

/**
 * Created by Tareko on 25.05.2017.
 */
public class PacketOutServerData
                extends Packet {

    public PacketOutServerData()
    {
        super(301,
                new Document()
                .append("groups", CloudNetServer.getInstance().getGroups().values())
                .append("maxmemory", CloudNetServer.getInstance().getMaxMemory())
                .append("info", new CNSInfo(
                        CloudNetServer.getInstance().getCloudId(),
                        CloudNetServer.getInstance().getConfig().getHostName(),
                        Runtime.getRuntime().availableProcessors(),
                        CloudNetServer.getInstance().getMaxMemory()))
        );
    }

    @Override
    public void handleInput(Document data, PacketSender packetSender) {}
}