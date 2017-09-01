package de.dytanic.cloudnet.bukkitproxy.command;

import de.dytanic.cloudnet.api.CloudNetAPI;
import de.dytanic.cloudnet.bukkitproxy.api.CloudServer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.lang.management.ManagementFactory;

/**
 * Created by Tareko on 07.07.2017.
 */
public class CommandResource
        implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] strings)
    {
        long used = ManagementFactory.getMemoryMXBean().getHeapMemoryUsage().getUsed() / 1048576L;
        long max = Runtime.getRuntime().maxMemory() / 1048576L;

        sender.sendMessage(" ");
        sender.sendMessage("§cServer§8: §3" + CloudNetAPI.getInstance().getServerId() + ":" + CloudNetAPI.getInstance().getUniqueId());
        sender.sendMessage("§cState§8: §3" + CloudServer.getInstance().getServerState());
        sender.sendMessage("§cMap§8: §3" + CloudServer.getInstance().getMap().getName());
        sender.sendMessage("§cProfile§8: §3" + CloudServer.getInstance().getProfile().getName());
        sender.sendMessage("§cMemory§8: §3" + used + "§8/§3" + max + "MB");
        sender.sendMessage("§cCPU-Load§8: §3" + ManagementFactory.getOperatingSystemMXBean().getSystemLoadAverage());
        sender.sendMessage(" ");
        return false;
    }
}