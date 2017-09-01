package de.dytanic.cloudnetserver.network.packets;
import de.dytanic.cloudnet.lib.document.Document;
import de.dytanic.cloudnet.network.packet.Packet;
import de.dytanic.cloudnet.network.packet.PacketSender;
import de.dytanic.cloudnetserver.CloudNetServer;

/**
 * Created by Tareko on 01.06.2017.
 */
public class PacketInRemovePlayerWhereAmI
                extends Packet {

    public PacketInRemovePlayerWhereAmI()
    {
        super(3);
    }

    @Override
    public void handleInput(Document document, PacketSender sender)
    {
        CloudNetServer.getInstance().getCloudNetwork().getOnlinePlayers().remove(document.getString("uuid"));
        CloudNetServer.getInstance().getCloudNetwork().setOnlineCount(document.getInt("onlinecount"));
    }
}