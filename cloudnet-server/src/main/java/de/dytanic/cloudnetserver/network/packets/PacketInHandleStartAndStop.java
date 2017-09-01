package de.dytanic.cloudnetserver.network.packets;

import de.dytanic.cloudnet.lib.document.Document;
import de.dytanic.cloudnet.network.packet.Packet;
import de.dytanic.cloudnet.network.packet.PacketSender;
import de.dytanic.cloudnetserver.CloudNetServer;
import de.dytanic.cloudnetserver.server.MinecraftServer;

/**
 * Created by Tareko on 02.06.2017.
 */
public class PacketInHandleStartAndStop
            extends Packet {

    public PacketInHandleStartAndStop() {}

    public PacketInHandleStartAndStop(Document handler, HandleType handleType)
    {
        super(new Document().append("data", handler).append("type", handleType));
    }

    @Override
    public void handleInput(Document document, PacketSender sender)
    {
        switch (document.getString("type").toUpperCase())
        {
            case "START_SERVER":
            {

                Document doc = document.getDocument("data");
                if(doc.contains("group"))
                {
                    if(CloudNetServer.getInstance().getGroups().containsKey(doc.getString("group")))
                    {

                        if(!doc.contains("static"))
                        {
                            CloudNetServer.getInstance().startServer(CloudNetServer.getInstance().getGroups().get(doc.getString("group")),
                                    (doc.contains("properties") ? doc.getDocument("properties") : new Document()),
                                    (doc.contains("prioritystop") ? doc.getBoolean("prioritystop") : true), doc.contains("hide"));
                        }
                        else
                        if(doc.contains("custom"))
                        {
                            Document dst = doc.getDocument("custom");
                            CloudNetServer.getInstance().startCustomServer(CloudNetServer.getInstance().getGroups().get(doc.getString("group")),
                                    dst.getString("serverid"),
                                    (doc.contains("properties") ? doc.getDocument("properties") : new Document()),
                                    (doc.contains("prioritystop") ? doc.getBoolean("prioritystop") : true), dst.getInt("maxplayers"),
                                    dst.getInt("memory"),
                                    doc.contains("hide"));
                        }
                        else
                        {
                            Document dst = doc.getDocument("static");
                            CloudNetServer.getInstance().startStaticServer(CloudNetServer.getInstance().getGroups().get(doc.getString("group")),
                                    dst.getString("serverid"),
                                    (doc.contains("properties") ? doc.getDocument("properties") : new Document()),
                                    (doc.contains("prioritystop") ? doc.getBoolean("prioritystop") : true), dst.getInt("memory"));
                        }
                    }
                }
            }
                break;
            case "STOP_SERVER":
            {
                Document doc = document.getDocument("data");
                if(doc.contains("serverid"))
                {
                    if(CloudNetServer.getInstance().getServers().containsKey(doc.getString("serverid")))
                    {
                        MinecraftServer minecraftServer = CloudNetServer.getInstance().getServers().get(doc.getString("serverid"));
                        CloudNetServer.getInstance().getStopServerScheduler().runTaskSync(new Runnable() {
                            @Override
                            public void run()
                            {
                                if(minecraftServer != null)
                                {
                                    minecraftServer.shutdown();
                                }
                            }
                        });
                    }
                }
            }
                break;
            default:
                break;
        }
    }

    public enum HandleType
    {
        START_SERVER,
        WRITE_COMMAND,
        STOP_SERVER;
    }
}