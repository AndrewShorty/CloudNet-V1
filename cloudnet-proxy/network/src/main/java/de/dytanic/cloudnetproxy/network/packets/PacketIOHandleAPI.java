package de.dytanic.cloudnetproxy.network.packets;

import de.dytanic.cloudnet.lib.document.Document;
import de.dytanic.cloudnet.lib.threading.Callback;
import de.dytanic.cloudnet.network.packet.Packet;
import de.dytanic.cloudnet.network.packet.PacketSender;
import de.dytanic.cloudnet.player.PlayerMetaData;
import de.dytanic.cloudnet.player.PlayerWhereAmI;
import de.dytanic.cloudnet.servergroup.ServerGroup;
import de.dytanic.cloudnetproxy.CloudNetProxy;
import de.dytanic.cloudnetproxy.network.components.CNS;

import java.util.UUID;

/**
 * Created by Tareko on 18.06.2017.
 */
public class PacketIOHandleAPI
            extends Packet {

    public PacketIOHandleAPI() {}

    private PacketIOHandleAPI(Document document, UUID uuid) {
        super(214,
            new Document()
                    .append("data", document).append("uuid", uuid));
    }

    @Override
    public void handleInput(Document data, PacketSender packetSender)
    {

        String query = data.getString("query");
        Document document = data.getDocument("data");
        UUID uuid = UUID.fromString(data.getString("uuid"));
        switch (query.toLowerCase())
        {
            case "get_offlineplayerwhereami_by_name":

                CloudNetProxy.getInstance().handlePlayer(document.getString("name"), value ->
                {
                    PlayerWhereAmI playerWhereAmI = new PlayerWhereAmI(value.getUniqueId(), null, document.getString("name"), null, null, value);
                    packetSender.sendPacket(new PacketIOHandleAPI(new Document().append("playerwhereami", playerWhereAmI), uuid));
                }, value -> packetSender.sendPacket(new PacketIOHandleAPI(new Document().append("playerwhereami", (Object) null), uuid)));

                break;
            case "get_uuid":

                CloudNetProxy.getInstance().handlePlayer(document.getString("name"), value ->
                                packetSender.sendPacket(new PacketIOHandleAPI(new Document().append("uuid", value.getUniqueId()), uuid)),

                        value -> packetSender.sendPacket(new PacketIOHandleAPI(new Document().append("uuid", (Object) null), uuid)));
                break;
            case "get_serverGroup":
            {
                if(document.contains("groupname"))
                {
                    for(CNS cns : CloudNetProxy.getInstance().getCnsSystems().values())
                    {
                        for(ServerGroup groups : cns.getGroups().values())
                        {
                            if(groups.getName().equalsIgnoreCase(document.getString("groupname")))
                            {
                                packetSender.sendPacket(new PacketIOHandleAPI(new Document().append("group", groups), uuid));
                                return;
                            }
                        }
                    }
                }
            }
                break;
        }
    }
}