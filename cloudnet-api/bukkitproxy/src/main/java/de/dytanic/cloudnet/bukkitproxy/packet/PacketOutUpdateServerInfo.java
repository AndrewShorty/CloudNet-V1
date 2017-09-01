package de.dytanic.cloudnet.bukkitproxy.packet;

import de.dytanic.cloudnet.lib.document.Document;
import de.dytanic.cloudnet.network.ServerInfo;
import de.dytanic.cloudnet.network.packet.Packet;
import de.dytanic.cloudnet.network.packet.PacketSender;

/**
 * Created by Tareko on 28.05.2017.
 */
public class PacketOutUpdateServerInfo
                                    extends Packet {

    //203
    public PacketOutUpdateServerInfo(ServerInfo serverInfo)
    {
        super(203, new Document().append("serverinfo", serverInfo));
    }

    @Override
    public void handleInput(Document data, PacketSender packetSender) {}
}