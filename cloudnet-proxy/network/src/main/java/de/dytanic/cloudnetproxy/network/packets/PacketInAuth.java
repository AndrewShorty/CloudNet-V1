package de.dytanic.cloudnetproxy.network.packets;

import com.google.gson.reflect.TypeToken;
import de.dytanic.cloudnet.lib.document.Document;
import de.dytanic.cloudnet.network.CNSInfo;
import de.dytanic.cloudnet.network.packet.Packet;
import de.dytanic.cloudnet.network.packet.PacketSender;
import de.dytanic.cloudnetproxy.CloudNetProxy;
import de.dytanic.cloudnetproxy.network.CloudNetProxyClient;
import de.dytanic.cloudnetproxy.network.CloudNetProxyClientSimple;
import de.dytanic.cloudnetproxy.network.components.CNS;
import de.dytanic.cloudnetproxy.network.components.MinecraftServer;
import de.dytanic.cloudnetproxy.network.components.ProxyServer;
import io.netty.channel.Channel;
import java.util.UUID;

/**
 * Created by Tareko on 02.06.2017.
 */
public class PacketInAuth
            extends Packet {

    public PacketInAuth() {}

    public PacketInAuth(Type type, UUID uuid, String serverId)
    {
        super(new Document().append("type", type.name()).append("uniqueid", uuid.toString()).append("serverid", serverId));
    }

    @Override
    public void handleInput(Document document, PacketSender sender)
    {
            if(sender instanceof CloudNetProxyClientSimple)
            {
                CloudNetProxyClientSimple proxyClientSimple = (CloudNetProxyClientSimple)sender;

                Type type = Type.valueOf(document.getString("type"));
                if(type.equals(Type.CNS))
                {
                    if(document.contains("cnsId") && document.contains("servicekey"))
                    {
                        if(CloudNetProxy.getInstance().getCnsSystems().containsKey(document.getString("cnsId"))
                            && CloudNetProxy.getInstance().getCnsSystems().get(document.getString("cnsId")).getChannel() == null && document.getString("servicekey").equals(CloudNetProxy.getInstance().getServiceKey()))
                        {
                            CNS cns = CloudNetProxy.getInstance().getCnsSystems().get(document.getString("cnsId"));
                            Channel channel = proxyClientSimple.getChannel();
                            if(channel != null)
                            {
                                cns.setChannel(channel);
                                channel.pipeline().remove("client");
                                channel.pipeline().addLast(new CloudNetProxyClient(cns, channel));
                                proxyClientSimple.getCloudNetProxyServer().getCnsGroup().add(channel);
                            }
                        }
                    }
                    else
                    {
                        return;
                    }

                }else
                if(type.equals(Type.MINECRAFT))
                {
                    MinecraftServer minecraftServer = CloudNetProxy.getInstance().getServer(document.getString("serverid"));
                    if(minecraftServer != null &&
                            minecraftServer.getChannel() == null &&
                            minecraftServer.getUniqueId().toString().equals(document.getString("uniqueid")))
                    {
                        Channel channel = proxyClientSimple.getChannel();
                        if(channel != null)
                        {
                            minecraftServer.setChannel(channel);
                            channel.pipeline().remove("client");
                            channel.pipeline().addLast(new CloudNetProxyClient(minecraftServer, channel));
                            proxyClientSimple.getCloudNetProxyServer().getServerGroup().add(channel);
                        }
                    }
                }
                else
                {
                    ProxyServer minecraftServer = CloudNetProxy.getInstance().getProxy(document.getString("serverid"));
                    if(minecraftServer != null &&
                            minecraftServer.getChannel() == null)
                    {
                        if(minecraftServer.getUniqueId().toString().equals(document.getString("uniqueid")))
                        {
                            Channel channel = proxyClientSimple.getChannel();
                            if(channel != null)
                            {
                                minecraftServer.setChannel(channel);
                                channel.pipeline().remove("client");
                                channel.pipeline().addLast(new CloudNetProxyClient(minecraftServer, channel));
                                proxyClientSimple.getCloudNetProxyServer().getProxyGroup().add(channel);
                            }
                        }
                    }
                }

            }
    }

    public enum Type
    {
        PROXY,
        CNS,
        MINECRAFT
    }
}
