package de.dytanic.cloudnetproxy.network.packets;

import de.dytanic.cloudnet.lib.document.Document;
import de.dytanic.cloudnet.network.packet.Packet;
import de.dytanic.cloudnet.network.packet.PacketSender;
import de.dytanic.cloudnetproxy.network.components.MinecraftServer;

/**
 * Created by Tareko on 25.05.2017.
 */
public class PacketIOKeepAlive
                    extends Packet {

    public PacketIOKeepAlive()
    {
        super(0, new Document());
    }

    @Override
    public void handleInput(Document run, PacketSender sender)
    {
        if(sender instanceof MinecraftServer)
        {
            ((MinecraftServer)sender).setUpdatePacketTime(System.currentTimeMillis());
        }
    }
}