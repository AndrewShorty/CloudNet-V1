package de.dytanic.cloudnetserver.network.packets;

import de.dytanic.cloudnet.lib.document.Document;
import de.dytanic.cloudnet.network.packet.Packet;
import de.dytanic.cloudnet.network.packet.PacketSender;
import de.dytanic.cloudnet.player.PlayerWhereAmI;
import de.dytanic.cloudnetserver.CloudNetServer;

/**
 * Created by Tareko on 01.06.2017.
 */
public class PacketInUpdatePlayerWhereAmI extends Packet {

    public PacketInUpdatePlayerWhereAmI(){}

    @Override
    public void handleInput(Document document, PacketSender sender)
    {
        PlayerWhereAmI playerWhereAmI = document.getObject("pwai", PlayerWhereAmI.class);
        CloudNetServer.getInstance().getCloudNetwork().getOnlinePlayers().put(playerWhereAmI.getUniqueId().toString(), playerWhereAmI);
    }
}