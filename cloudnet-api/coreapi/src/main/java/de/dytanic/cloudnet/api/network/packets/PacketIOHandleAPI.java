package de.dytanic.cloudnet.api.network.packets;

import de.dytanic.cloudnet.lib.document.Document;
import de.dytanic.cloudnet.lib.threading.Callback;
import de.dytanic.cloudnet.network.packet.Packet;
import de.dytanic.cloudnet.network.packet.PacketSender;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Tareko on 18.06.2017.
 */
public class PacketIOHandleAPI
                    extends Packet {

    private static final java.util.Map<UUID, Callback<Document>> QUERYS_OPEN = new ConcurrentHashMap<>();

    public PacketIOHandleAPI() {}

    public PacketIOHandleAPI(String qry, Document meta, Callback<Document> results, UUID random)
    {
        super(214, new Document().append("query", qry).append("uuid", random.toString()).append("data", meta));
        QUERYS_OPEN.put(random, results);
    }

    @Override
    public void handleInput(Document data, PacketSender packetSender)
    {
        UUID uuid = UUID.fromString(data.getString("uuid"));
        if(QUERYS_OPEN.containsKey(uuid))
        {
            Callback<Document> callback = QUERYS_OPEN.get(uuid);
            callback.call(data.getDocument("data"));
        }
    }
}