package de.dytanic.cloudnet.api.network.packets;
import de.dytanic.cloudnet.api.CloudNetAPI;
import de.dytanic.cloudnet.lib.document.Document;
import de.dytanic.cloudnet.network.packet.Packet;
import de.dytanic.cloudnet.network.packet.PacketSender;

import java.util.UUID;

/**
 * Created by Tareko on 01.06.2017.
 */
public class PacketIORemovePlayerWhereAmI
                extends Packet {

    public PacketIORemovePlayerWhereAmI() {}

    public PacketIORemovePlayerWhereAmI(UUID uniqueId, int onlineCount, String proxy)
    {
        super(207, new Document().append("uuid", uniqueId.toString()).append("onlinecount", onlineCount).append("proxy", proxy));
    }

    @Override
    public void handleInput(Document document, PacketSender sender)
    {
        CloudNetAPI.getInstance().getCloudNetwork().getOnlinePlayers().remove(document.getString("uuid"));
        CloudNetAPI.getInstance().getCloudNetwork().setOnlineCount(document.getInt("onlinecount"));
    }
}