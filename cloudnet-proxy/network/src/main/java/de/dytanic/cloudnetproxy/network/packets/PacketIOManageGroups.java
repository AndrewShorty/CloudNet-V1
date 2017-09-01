package de.dytanic.cloudnetproxy.network.packets;

import de.dytanic.cloudnet.lib.document.Document;
import de.dytanic.cloudnet.network.packet.Packet;
import de.dytanic.cloudnet.network.packet.PacketSender;
import de.dytanic.cloudnetproxy.CloudNetProxy;
import de.dytanic.cloudnetproxy.network.components.CNS;

/**
 * Created by Tareko on 07.07.2017.
 */
public class PacketIOManageGroups
        extends Packet {

    public PacketIOManageGroups()
    {
    }

    private PacketIOManageGroups(Document data)
    {
        super(216, data);
    }

    @Override
    public void handleInput(Document data, PacketSender packetSender)
    {
        String cnsInfo = data.getString("cns");

        if(cnsInfo != null)
        {
            CNS cns = CloudNetProxy.getInstance().getCnsSystems().get(cnsInfo);
            cns.sendPacket(new PacketIOManageGroups(data));
        }
    }
}