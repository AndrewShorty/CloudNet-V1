package de.dytanic.cloudnetproxy.network.packets;

import de.dytanic.cloudnet.lib.document.Document;
import de.dytanic.cloudnet.network.packet.Packet;
import de.dytanic.cloudnet.network.packet.PacketSender;
import de.dytanic.cloudnet.sign.Sign;
import de.dytanic.cloudnetproxy.CloudNetProxy;

/**
 * Created by Tareko on 06.06.2017.
 */
public class PacketInHandleSign
        extends Packet {

    @Override
    public void handleInput(Document data, PacketSender packetSender)
    {
        if (data.contains("serverselectors") && data.contains("handle"))
        {
            HandleType handleType = HandleType.valueOf(data.getString("handle"));
            if (handleType.equals(HandleType.ADD))
            {
                Sign sign = data.getObject("serverselectors", Sign.class);

                CloudNetProxy.getInstance().getSignBackend().getSigns().append(sign.getUniqueId().toString(), sign);
                CloudNetProxy.getInstance().getSignBackend().saveAndReloadSignsJson();

                CloudNetProxy.getInstance().sendAllLobbys(new PacketOutUpdateSignsSystem(CloudNetProxy.getInstance().getSignBackend().signs(),
                        CloudNetProxy.getInstance().getSignGroupLayoutsBackend().load()
                ));
            } else
            {
                Sign sign = data.getObject("serverselectors", Sign.class);

                CloudNetProxy.getInstance().getSignBackend().getSigns().remove(sign.getUniqueId().toString());
                CloudNetProxy.getInstance().getSignBackend().saveAndReloadSignsJson();

                CloudNetProxy.getInstance().sendAllLobbys(new PacketOutUpdateSignsSystem(CloudNetProxy.getInstance().getSignBackend().signs(),
                        CloudNetProxy.getInstance().getSignGroupLayoutsBackend().load()
                ));
            }

        }
    }

    public enum HandleType {
        ADD,
        REMOVE;
    }
}