package de.dytanic.cloudnet.bukkitproxy.serverselectors;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import de.dytanic.cloudnet.api.CloudNetAPI;
import de.dytanic.cloudnet.bukkitproxy.api.CloudServer;
import de.dytanic.cloudnet.bukkitproxy.api.event.bukkit.ServerInfoUpdateEvent;
import de.dytanic.cloudnet.bukkitproxy.api.event.bukkit.ServerRemoveEvent;
import de.dytanic.cloudnet.bukkitproxy.packet.PacketOutHandleSign;
import de.dytanic.cloudnet.lib.Return;
import de.dytanic.cloudnet.network.ServerInfo;
import de.dytanic.cloudnet.servergroup.ServerGroupMode;
import de.dytanic.cloudnet.servergroup.ServerState;
import de.dytanic.cloudnet.sign.GroupsLayout;
import de.dytanic.cloudnet.sign.Sign;
import de.dytanic.cloudnet.sign.SignLayout;
import de.dytanic.cloudnet.sign.SignPosition;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.*;
import org.bukkit.plugin.Plugin;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Tareko on 04.06.2017.
 */
@Getter
public class ServerSelectorSigns implements ServerSelector {

    private java.util.Map<UUID, Sign> signs = new ConcurrentHashMap<>();
    private volatile int tick = 1;
    private Thread handledThread;
    private List<UUID> cached = new LinkedList<>();

    @Getter
    private static ServerSelectorSigns instance;

    @Setter
    private Collection<GroupsLayout> layouts = new ArrayList<>();

    private SignLayout getLayout(String group, String layout)
    {
        for(GroupsLayout groupsLayout : layouts)
        {
            if(groupsLayout.getGroupName().equalsIgnoreCase(group))
            {
                for(SignLayout signLayout : groupsLayout.getLayouts())
                {
                    if(signLayout.getName().equalsIgnoreCase(layout))
                    {
                        return signLayout;
                    }
                }
            }
        }
        return null;
    }

    private boolean containsGroup(String group)
    {
        for(GroupsLayout groupLayout : layouts)
        {
            if(groupLayout.getGroupName().equalsIgnoreCase(group)) return true;
        }
        return false;
    }

    public ServerSelectorSigns(Plugin plugin)
    {
        if (instance != null) return;

        instance = this;

        plugin.getServer().getPluginCommand("removesign").setPermission("cloudnet.command.removesign");
        plugin.getServer().getPluginCommand("removesign").setExecutor(this);
    }

    public Return<Boolean, UUID> contains(ServerInfo serverInfo)
    {
        for (Sign sign : signs.values())
        {
            if (sign.getServerInfo() != null && sign.getServerInfo().getName().equals(serverInfo.getName()))
            {
                return new Return<>(true, sign.getUniqueId());
            }
        }
        return new Return<>(false, null);
    }

    public Sign findNextSign(ServerInfo serverInfo)
    {
        for (Sign sign : signs.values())
        {
            if (sign.getServerInfo() == null && (sign.getTargetGroup().equals(serverInfo.getGroup()) || (
                    CloudNetAPI.getInstance().getCloudNetwork().getGroups().get(serverInfo.getGroup()).getParentGroup() != null
                            && CloudNetAPI.getInstance().getCloudNetwork().getGroups().get(serverInfo.getGroup()).getParentGroup().equals(sign.getTargetGroup())
            )))
            {
                Location location = getLocation(sign.getPosition());
                if (check(location))
                {
                    return sign;
                }
            }
        }
        return null;
    }

    public Sign getSignActive(ServerInfo serverInfo)
    {
        for (Sign sign : signs.values())
        {
            if (sign.getServerInfo() != null && sign.getServerInfo().getName().equals(serverInfo.getName()))
            {
                return sign;
            }
        }
        return null;
    }

    public void changeBlock(Location block, int typeId)
    {
        if (typeId != -1)
        {
            org.bukkit.material.Sign sign = (org.bukkit.material.Sign) block.getBlock().getState().getData();
            block.getBlock().getRelative(sign.getAttachedFace()).setTypeIdAndData(Material.STAINED_CLAY.getId(), (byte) typeId, true);
        }
    }

    public void handleNext(ServerInfo serverInfo)
    {

        Sign sign = findNextSign(serverInfo);

        if (sign == null) return;

        handleUpdate(sign, serverInfo);
    }

    public void handleUpdate(Sign sign, ServerInfo serverInfo)
    {
        try
        {
            Location location = getLocation(sign.getPosition());
            if (check(location))
            {
                if (serverInfo.isOnline() && serverInfo.getServerState().equals(ServerState.LOBBY) && !serverInfo.getMotd().contains("INGAME") && !serverInfo.getMotd().contains("INGAME")
                        && !serverInfo.isHided())
                {
                    if (CloudNetAPI.getInstance().getCloudNetwork().getGroups().containsKey(sign.getTargetGroup()) &&
                            CloudNetAPI.getInstance().getCloudNetwork().getGroups().get(sign.getTargetGroup()).isMaintenance())
                    {
                        sign.setServerInfo(null);
                        SignLayout signLayout = containsGroup(sign.getTargetGroup()) ? getLayout(sign.getTargetGroup(), "maintenance") : getLayout("default", "maintenance");
                        String[] value = signLayout.getSignLayout().clone();
                        updateOfflineAndMaintenance(value, sign);
                        updatePacketAsync(value, location);
                    } else
                    {
                        SignLayout signLayout;
                        if (serverInfo.getOnlineCount() >= serverInfo.getMaxPlayers())
                        {
                            signLayout = containsGroup(sign.getTargetGroup()) ? getLayout(sign.getTargetGroup(), "full") : getLayout("default", "full");
                        } else
                        {
                            signLayout = containsGroup(sign.getTargetGroup()) ? getLayout(sign.getTargetGroup(), "online") : getLayout("default", "online");
                        }
                        String[] value = signLayout.getSignLayout().clone();
                        sign.setServerInfo(serverInfo);
                        updateArray(value, sign.getServerInfo());
                        updatePacketAsync(value, location);
                    }
                } else
                {
                    sign.setServerInfo(null);
                    SignLayout signLayout;

                    if (CloudNetAPI.getInstance().getCloudNetwork().getGroups().containsKey(sign.getTargetGroup()) &&
                            CloudNetAPI.getInstance().getCloudNetwork().getGroups().get(sign.getTargetGroup()).isMaintenance())
                    {
                        signLayout = containsGroup(sign.getTargetGroup()) ? getLayout(sign.getTargetGroup(), "maintenance") : getLayout("default", "maintenance");
                    } else
                    {
                        signLayout = containsGroup(sign.getTargetGroup()) ? getLayout(sign.getTargetGroup(), "loading" + tick) : getLayout("default", "loading" + tick);
                    }
                    String[] value = signLayout.getSignLayout().clone();
                    updateOfflineAndMaintenance(value, sign);
                    updatePacketAsync(value, location);
                }
            } else
            {
                sign.setServerInfo(null);
            }
        } catch (Throwable ex)
        {
        }
    }

    public Location getLocation(SignPosition signPosition)
    {
        return new Location(Bukkit.getWorld(signPosition.getWorld()), signPosition.getX(), signPosition.getY(), signPosition.getZ());
    }

    public void updateArray(String[] value, ServerInfo serverInfo)
    {
        short i = 0;
        for (String x : value)
        {
            value[i] = ChatColor.translateAlternateColorCodes('&', x
                    .replace("%server%", serverInfo.getName())
                    .replace("%map%", serverInfo.getServerMap().getName())
                    .replace("%onlineplayers%", serverInfo.getOnlineCount() + "")
                    .replace("%maxplayers%", serverInfo.getMaxPlayers() + "")
                    .replace("%motd%", ChatColor.translateAlternateColorCodes('&', serverInfo.getMotd()))
                    .replace("%state%", serverInfo.getServerState().name() + "")
                    .replace("%cloudid%", serverInfo.getCloudId() + "")
                    .replace("%profile%", (serverInfo.getProfile() != null ? serverInfo.getProfile().getName() : "default") + "")
                    .replace("%extra%", (serverInfo.getExtra() != null ? serverInfo.getExtra() : "null"))
                    .replace("%group%", serverInfo.getGroup()));
            i++;
        }
    }

    public String[] updateOfflineAndMaintenance(String[] value, Sign sign)
    {
        for (short i = 0; i < value.length; i++)
        {
            value[i] = ChatColor.translateAlternateColorCodes('&',
                    value[i].replace("%group%", sign.getTargetGroup()).replace("%from%", sign.getPosition().getGroup()));
        }
        return value;
    }

    /*==============================================================================================*/
    //Event Handlers

    @EventHandler
    public void handleServerInfoUpdate(ServerInfoUpdateEvent e)
    {
        ServerInfo serverInfo = e.getServerInfo();

        Return<Boolean, UUID> c = contains(serverInfo);
        if (c.getFirst())
        {
            Sign sign = signs.get(c.getSecond());
            handleUpdate(sign, serverInfo);

        } else
        {
            Sign sign = findNextSign(serverInfo);

            if (sign == null) return;

            handleUpdate(sign, serverInfo);
        }
    }

    @EventHandler
    public void handleServerRemoveUpdate(ServerRemoveEvent e)
    {
        ServerInfo serverInfo = e.getServerInfo();

        Return<Boolean, UUID> result = contains(serverInfo);

        if (result.getFirst())
        {
            Sign sign = signs.get(result.getSecond());

            sign.setServerInfo(null);

            Location location = getLocation(sign.getPosition());

            if (check(location))
            {
                SignLayout signLayout = containsGroup(sign.getTargetGroup()) ? getLayout(sign.getTargetGroup(), "loading" + tick) : getLayout("default", "loading" + tick);
                String[] value = signLayout.getSignLayout().clone();
                short v = 0;
                for (String x : value)
                {
                    value[v] = ChatColor.translateAlternateColorCodes('&', x);
                    v++;
                }
                updatePacketSynchronized(sign, value, signLayout.getClayBlockId(), location, true);
            }
        }
    }

    @EventHandler
    public void handleAsyncPre(AsyncPlayerPreLoginEvent e)
    {
        this.cached.add(e.getUniqueId());

        Bukkit.getScheduler().runTaskLater(CloudServer.getInstance().getPlugin(), new Runnable() {
            @Override
            public void run()
            {
                cached.remove(e.getUniqueId());
            }
        }, 10L);

    }

    @EventHandler
    public void handleQuit(PlayerQuitEvent e)
    {
        this.cached.remove(e.getPlayer().getUniqueId());
    }

    @EventHandler
    public void handlePlayerInteract(PlayerInteractEvent e)
    {
        if (e.getAction() == Action.RIGHT_CLICK_BLOCK)
        {
            if (e.getClickedBlock().getType() == Material.WALL_SIGN || e.getClickedBlock().getType() == Material.SIGN_POST)
            {
                Block block = e.getClickedBlock();
                for (Sign sign : signs.values())
                {
                    Location location = getLocation(sign.getPosition());
                    if (block.getLocation().equals(location))
                    {
                        if (sign.getServerInfo() != null)
                        {
                            e.getPlayer().sendMessage(net.md_5.bungee.api.ChatColor.translateAlternateColorCodes('&', CloudNetAPI.getInstance().getCloudNetwork()
                                    .getServerLayout().getSignSendToServerMessage().replace("%server%", sign.getServerInfo().getName())));
                            ByteArrayDataOutput output = ByteStreams.newDataOutput();
                            output.writeUTF("Connect");
                            output.writeUTF(sign.getServerInfo().getName());
                            e.getPlayer().sendPluginMessage(CloudServer.getInstance().getPlugin(),
                                    "BungeeCord", output.toByteArray());
                        }
                        return;
                    }
                }
            }
        }
    }

    @EventHandler
    public void onSignChange(SignChangeEvent e)
    {
        if (!CloudServer.getInstance().getServerGroupMode().equals(ServerGroupMode.LOBBY))
        {
            return;
        }
        if (e.getLine(0).equalsIgnoreCase("[cloudnet]"))
        {
            if (!e.getPlayer().hasPermission("cloudnet.signs"))
            {
                e.setCancelled(true);
                return;
            }

            if (!CloudNetAPI.getInstance().getCloudNetwork().getGroups().containsKey(e.getLine(1)))
            {
                e.getPlayer().sendMessage("The group doesn't exists.");
                e.setCancelled(true);
                return;
            }

            Location location = e.getBlock().getLocation();

            Sign sign = new Sign(
                    UUID.randomUUID(),
                    e.getLine(1),
                    new SignPosition(CloudServer.getInstance().getGroup(), location.getWorld().getName(), location.getBlockX(), location.getBlockY(), location.getBlockZ()),
                    null
            );

            e.getPlayer().sendMessage("The signs is created. please wait a moment.");

            CloudNetAPI.getInstance()
                    .getCnpConnector()
                    .sendPacket(
                            new PacketOutHandleSign(
                                    PacketOutHandleSign
                                            .HandleType
                                            .ADD,
                                    sign));

        }
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings)
    {
        Player player = (Player) commandSender;
        Block block = player.getTargetBlock((HashSet<Byte>) null, 15);

        if (block.getState() instanceof org.bukkit.block.Sign)
        {

            for (Sign sign : signs.values())
            {
                Location location = getLocation(sign.getPosition());
                if (block.getLocation().equals(location))
                {
                    CloudNetAPI.getInstance()
                            .getCnpConnector().sendPacket(new PacketOutHandleSign(PacketOutHandleSign.HandleType.REMOVE, sign));
                    commandSender.sendMessage("The sign is removed");
                    return false;
                }
            }
        }

        commandSender.sendMessage("Cannot find available signs. ");
        return false;
    }

    public List<String> freeServers(String group)
    {
        List<String> servers = CloudNetAPI.getInstance().getServers(group);

        for (Sign sign : signs.values())
        {
            if (sign.getServerInfo() != null && servers.contains(sign.getServerInfo().getName()))
            {
                servers.remove(sign.getServerInfo().getName());
            }
        }

        List<String> x = new ArrayList<>();

        ServerInfo serverInfo;
        for (short i = 0; i < servers.size(); i++)
        {
            serverInfo = CloudNetAPI.getInstance().getServerInfo(servers.get(i));
            if(serverInfo != null)
            {
                if (!serverInfo.isOnline() || !serverInfo.getServerState().equals(ServerState.LOBBY) || serverInfo.getMotd().contains("INGAME") || serverInfo.getMotd().contains("RUNNING") ||
                        serverInfo.isHided())
                {
                    x.add(serverInfo.getName());
                }
            }
            else
            {
                x.add(servers.get(i));
            }
        }

        for (String b : x)
        {
            servers.remove(b);
        }

        return servers;
    }

    public void appendHandledThread()
    {
        if (handledThread == null) handledThread = new HandleThread();
    }

    private class HandleThread
            extends Thread {

        private boolean value = false; //7

        @Override
        public void run()
        {
            while (!isInterrupted())
            {
                if (layouts.size() != 0)
                {
                    if (signs.size() != 0)
                    {
                        Bukkit.getScheduler().runTask(CloudServer.getInstance().getPlugin(), new Runnable() {
                            @Override
                            public void run()
                            {
                                try
                                {

                                    for (Sign sign : signs.values())
                                    {
                                        Location location = getLocation(sign.getPosition());
                                        if (!check(location))
                                        {
                                            sign.setServerInfo(null);
                                            continue;
                                        }

                                        if (CloudNetAPI.getInstance().getCloudNetwork().getGroups().containsKey(sign.getTargetGroup()) &&
                                                CloudNetAPI.getInstance().getCloudNetwork().getGroups().get(sign.getTargetGroup()).isMaintenance())
                                        {
                                            if(value)
                                            {
                                                sign.setServerInfo(null);
                                                SignLayout signLayou = containsGroup(sign.getTargetGroup()) ? getLayout(sign.getTargetGroup(), "maintenance") : getLayout("default", "maintenance");
                                                String[] value = signLayou.getSignLayout().clone();
                                                updatePacketSynchronized(sign, updateOfflineAndMaintenance(value, sign), signLayou.getClayBlockId(), location, true);
                                            }
                                            continue;
                                        }

                                        if (sign.getServerInfo() == null)
                                        {
                                            List<String> s = freeServers(sign.getTargetGroup());
                                            if (s.size() == 0)
                                            {
                                                SignLayout signLayout = containsGroup(sign.getTargetGroup()) ? getLayout(sign.getTargetGroup(), "loading" + tick) : getLayout("default", "loading" + tick);
                                                String[] value_ = signLayout.getSignLayout().clone();
                                                updatePacketSynchronized(sign, updateOfflineAndMaintenance(value_, sign), signLayout.getClayBlockId(), location, true);
                                            } else
                                            {
                                                ServerInfo serverInfo = CloudNetAPI.getInstance().getServerInfo(s.get(0));
                                                sign.setServerInfo(serverInfo);
                                                SignLayout signLayou;
                                                if (serverInfo.getOnlineCount() >= serverInfo.getMaxPlayers())
                                                {
                                                    signLayou = containsGroup(sign.getTargetGroup()) ? getLayout(sign.getTargetGroup(), "full") : getLayout("default", "full");
                                                } else
                                                {
                                                    signLayou = containsGroup(sign.getTargetGroup()) ? getLayout(sign.getTargetGroup(), "online") : getLayout("default", "online");
                                                }
                                                String[] value = signLayou.getSignLayout().clone();
                                                updateArray(value, serverInfo);
                                                updatePacketSynchronized(sign, value, signLayou.getClayBlockId(), location, true);
                                            }
                                            continue;
                                        } else
                                        {
                                            if(value)
                                            {
                                                SignLayout signLayou;
                                                if (sign.getServerInfo().getOnlineCount() >= sign.getServerInfo().getMaxPlayers())
                                                {
                                                    signLayou = containsGroup(sign.getTargetGroup()) ? getLayout(sign.getTargetGroup(), "full") : getLayout("default", "full");
                                                } else
                                                {
                                                    signLayou = containsGroup(sign.getTargetGroup()) ? getLayout(sign.getTargetGroup(), "online") : getLayout("default", "online");
                                                }
                                                String[] value = signLayou.getSignLayout().clone();
                                                updateArray(value, sign.getServerInfo());
                                                updatePacketSynchronized(sign, value, signLayou.getClayBlockId(), location, true);
                                                continue;
                                            }
                                        }
                                    }
                                }catch (Exception ex){

                                }
                            }
                        });
                    }
                }
                tick++;

                if (tick == 5)
                {
                    tick = 1;
                }

                value = !value;

                try
                {
                    Thread.sleep(500);
                } catch (InterruptedException e)
                {
                }

            }
        }
    }

    private boolean check(Location location)
    {
        try
        {
            return location != null && location.getWorld() != null && location.getBlock().getState() instanceof org.bukkit.block.Sign;
        } catch (Exception ex)
        {
            return false;
        }
    }

    private void updatePacketAsync(String[] value, Location location)
    {
        for (Player all : Bukkit.getOnlinePlayers())
        {
            if (all.getLocation().distance(location) <= 16 && all.isValid() && !cached.contains(all.getUniqueId()))
            {
                all.sendSignChange(location, value);
            }
        }
    }

    private void updatePacketSynchronized(Sign cloudSign, String[] value, int clayblockId, Location location, boolean sync)
    {
        if (sync)
        {
            Bukkit.getScheduler().runTask(CloudServer.getInstance().getPlugin(), new Runnable() {
                @Override
                public void run()
                {
                    if (check(location))
                    {
                        org.bukkit.block.Sign sign = (org.bukkit.block.Sign) location.getBlock().getState();
                        sign.setLine(0, value[0]);
                        sign.setLine(1, value[1]);
                        sign.setLine(2, value[2]);
                        sign.setLine(3, value[3]);
                        sign.update();
                        changeBlock(location, clayblockId);
                    } else
                    {
                        if (cloudSign.getServerInfo() != null)
                        {

                            cloudSign.setServerInfo(null);
                        }
                    }
                }
            });
        } else
        {
            if (check(location))
            {
                org.bukkit.block.Sign sign = (org.bukkit.block.Sign) location.getBlock().getState();
                sign.setLine(0, value[0]);
                sign.setLine(1, value[1]);
                sign.setLine(2, value[2]);
                sign.setLine(3, value[3]);
                sign.update();
                changeBlock(location, clayblockId);
            } else
            {
                cloudSign.setServerInfo(null);
            }
        }
    }

}