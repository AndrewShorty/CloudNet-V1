package de.dytanic.cloudnet.api.network.packets;

import de.dytanic.cloudnet.api.CloudNetAPI;
import de.dytanic.cloudnet.lib.document.Document;
import de.dytanic.cloudnet.network.packet.Packet;
import de.dytanic.cloudnet.network.packet.PacketSender;
import de.dytanic.cloudnet.player.PlayerWhereAmI;

/**
 * Created by Tareko on 01.06.2017.
 */
public class PacketIOUpdatePlayerWhereAmI
            extends Packet {

    public PacketIOUpdatePlayerWhereAmI() {}

    public PacketIOUpdatePlayerWhereAmI(PlayerWhereAmI playerWhereAmI, int onlinecount, String proxy, boolean value)
    {
        super(206, new Document()
                .append("pwai", playerWhereAmI)
                .append("onlinecount", onlinecount)
                .append("proxy", proxy));

        if(value)
        {
           getData().append("update_group", true);
        }
    }

    public PacketIOUpdatePlayerWhereAmI(PlayerWhereAmI playerWhereAmI)
    {
        super(206, new Document()
                .append("pwai", playerWhereAmI).append("proxy", playerWhereAmI.getProxy()).append("update_group", true));
    }

    public PacketIOUpdatePlayerWhereAmI(PlayerWhereAmI playerWhereAmI, boolean updatePlayerMeta)
    {
        super(206, new Document()
                .append("pwai", playerWhereAmI).append("proxy", playerWhereAmI.getProxy()));
        if(updatePlayerMeta)
        {
            this.data.append("update_playermeta", true);
        }
    }

    @Override
    public void handleInput(Document document, PacketSender sender)
    {
        PlayerWhereAmI playerWhereAmI = document.getObject("pwai", PlayerWhereAmI.class);
        if(document.contains("onlinecount"))
        {
            CloudNetAPI.getInstance().getCloudNetwork().setOnlineCount(document.getInt("onlinecount"));
        }
        CloudNetAPI.getInstance().getCloudNetwork().getOnlinePlayers().put(playerWhereAmI.getUniqueId().toString(), playerWhereAmI);
    }
}