package de.dytanic.cloudnet.bukkitproxy.packet;

import de.dytanic.cloudnet.lib.document.Document;
import de.dytanic.cloudnet.network.ProxyInfo;
import de.dytanic.cloudnet.network.packet.Packet;
import de.dytanic.cloudnet.network.packet.PacketSender;

/**
 * Created by Tareko on 28.05.2017.
 */
public class PacketOutUpdateProxyInfo
                extends Packet {

    public PacketOutUpdateProxyInfo(ProxyInfo proxyInfo)
    {
        super(204, new Document().append("proxyinfo", proxyInfo));
    }

    @Override
    public void handleInput(Document data, PacketSender packetSender) {}
}