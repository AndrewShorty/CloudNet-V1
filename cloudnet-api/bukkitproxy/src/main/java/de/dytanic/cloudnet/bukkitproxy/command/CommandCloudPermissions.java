package de.dytanic.cloudnet.bukkitproxy.command;

import de.dytanic.cloudnet.api.CloudNetAPI;
import de.dytanic.cloudnet.bukkitproxy.api.CloudProxy;
import de.dytanic.cloudnet.bukkitproxy.api.command.GlobalTabExecutor;
import de.dytanic.cloudnet.network.NetworkUtils;
import de.dytanic.cloudnet.permission.PermissionDefault;
import de.dytanic.cloudnet.permission.PermissionFallback;
import de.dytanic.cloudnet.permission.PermissionGroup;
import de.dytanic.cloudnet.permission.PermissionPool;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Tareko on 09.07.2017.
 */
public class CommandCloudPermissions
        extends Command implements GlobalTabExecutor {

    public CommandCloudPermissions()
    {
        super("permissions", "cloudnet.command.permissions", "perms", "cloudperms", "cperms", "perm");
    }

    @Override
    public void execute(CommandSender commandSender, String[] args)
    {
        PermissionPool permissionPool = CloudNetAPI.getInstance().getPermissionPool();
        switch (args.length)
        {

            case 1:
                if (args[0].equalsIgnoreCase("help"))
                {
                    commandSender.sendMessage(CloudProxy.getInstance().getProxyLayout().getPrefix() + "/cperms help");
                    commandSender.sendMessage(CloudProxy.getInstance().getProxyLayout().getPrefix() + "/cperms reload");
                    commandSender.sendMessage(CloudProxy.getInstance().getProxyLayout().getPrefix() + "/cperms group");
                    commandSender.sendMessage(CloudProxy.getInstance().getProxyLayout().getPrefix() + "/cperms group <group>");
                    commandSender.sendMessage(CloudProxy.getInstance().getProxyLayout().getPrefix() + "/cperms creategroup <name>");
                    commandSender.sendMessage(CloudProxy.getInstance().getProxyLayout().getPrefix() + "/cperms deletegroup <name>");
                    commandSender.sendMessage(CloudProxy.getInstance().getProxyLayout().getPrefix() + "/cperms group <group> add permission <permission>");
                    commandSender.sendMessage(CloudProxy.getInstance().getProxyLayout().getPrefix() + "/cperms group <group> add permission <permission> (servergroup)");
                    commandSender.sendMessage(CloudProxy.getInstance().getProxyLayout().getPrefix() + "/cperms group <group> delete permission <permission>");
                    commandSender.sendMessage(CloudProxy.getInstance().getProxyLayout().getPrefix() + "/cperms group <group> delete permission <permission> (servergroup)");
                    commandSender.sendMessage(CloudProxy.getInstance().getProxyLayout().getPrefix() + "/cperms group <group> add implementation <group>");
                    commandSender.sendMessage(CloudProxy.getInstance().getProxyLayout().getPrefix() + "/cperms group <group> delete implementation <group>");
                    commandSender.sendMessage(CloudProxy.getInstance().getProxyLayout().getPrefix() + "/cperms group <group> set display <display>");
                    commandSender.sendMessage(CloudProxy.getInstance().getProxyLayout().getPrefix() + "/cperms group <group> set prefix <prefix>");
                    commandSender.sendMessage(CloudProxy.getInstance().getProxyLayout().getPrefix() + "/cperms group <group> set suffix <suffix>");
                    commandSender.sendMessage(CloudProxy.getInstance().getProxyLayout().getPrefix() + "/cperms group <group> set tagid <tagid>");
                    commandSender.sendMessage(CloudProxy.getInstance().getProxyLayout().getPrefix() + "/cperms group <group> set permissiondefault <DEV, ADMINISTRATOR, TEAM_MEMBER, USER>");
                    commandSender.sendMessage(CloudProxy.getInstance().getProxyLayout().getPrefix() + "/cperms group <group> set default <true : false>");
                    commandSender.sendMessage(CloudProxy.getInstance().getProxyLayout().getPrefix() + "/cperms group <group> set fallback <fallback>");
                    commandSender.sendMessage(CloudProxy.getInstance().getProxyLayout().getPrefix() + "/cperms group <group> set joinpower <joinpower>");
                    return;
                }
                if (args[0].equalsIgnoreCase("group"))
                {
                    commandSender.sendMessage(" ");
                    for (PermissionGroup permissionGroup : permissionPool.getGroups().values())
                        commandSender.sendMessage(permissionGroup.getName() + "[" + permissionGroup.getJoinPower() + "] implements " + permissionGroup.getImplementGroups().toString());
                    commandSender.sendMessage(" ");
                    return;
                }
                if (args[0].equalsIgnoreCase("reload"))
                {
                    CloudNetAPI.getInstance().reloadPermissions();
                    commandSender.sendMessage(CloudProxy.getInstance().getProxyLayout().getPrefix() +
                            "The update was sent to the cnp-root successfully");
                    return;
                }
                break;
            case 2:
                if (args[0].equalsIgnoreCase("group"))
                {
                    PermissionGroup permissionGroup = permissionPool.getGroups().get(args[1]);
                    if (permissionGroup != null)
                    {
                        commandSender.sendMessage(" ");
                        commandSender.sendMessage("Name: " + permissionGroup.getName());
                        commandSender.sendMessage("JoinPower: " + permissionGroup.getJoinPower());
                        commandSender.sendMessage("Display: \"" + permissionGroup.getDisplay() + "\"");
                        commandSender.sendMessage("Prefix: \"" + permissionGroup.getPrefix() + "\"");
                        commandSender.sendMessage("Suffix: \"" + permissionGroup.getSuffix() + "\"");
                        commandSender.sendMessage("TagId: " + permissionGroup.getTagId());
                        commandSender.sendMessage("PermissionDefault: " + permissionGroup.getPermissionDefault());
                        commandSender.sendMessage("Implemenation: " + permissionGroup.getImplementGroups());
                        commandSender.sendMessage(" ");

                        commandSender.sendMessage("Permissions:");
                        for(java.util.Map.Entry<String, Boolean> permissions : permissionGroup.getPermissions().entrySet())
                        {
                            commandSender.sendMessage("- " + (permissions.getValue() ? "§e" : "§c") + permissions.getKey());
                        }
                        commandSender.sendMessage(" ");

                        commandSender.sendMessage("Permissions per Group:");
                        commandSender.sendMessage(" ");

                        for(java.util.Map.Entry<String, List<String>> perms : permissionGroup.getServerGroupPermissions().entrySet())
                        {
                            commandSender.sendMessage("Servergroup: " + perms.getKey());
                            for(String permission : perms.getValue())
                            {
                                commandSender.sendMessage("- §e" + permission);
                            }
                        }

                        commandSender.sendMessage(" ");
                        //TODO:
                    }
                    return;
                }
                if (args[0].equalsIgnoreCase("creategroup"))
                {
                    if (!permissionPool.getGroups().containsKey(args[1]))
                    {
                        PermissionGroup permissionGroup = new PermissionGroupNull(args[1]);
                        CloudNetAPI.getInstance().createPermissionGroup(permissionGroup);
                        commandSender.sendMessage(CloudProxy.getInstance().getProxyLayout().getPrefix() + "The group is created. Please setup the group with \"/cperms help\"");
                    } else
                    {
                        commandSender.sendMessage(CloudProxy.getInstance().getProxyLayout().getPrefix() + "The group is already exists!");
                    }
                    return;
                }
                if (args[0].equalsIgnoreCase("deletegroup"))
                {
                    if (permissionPool.getGroups().containsKey(args[1]))
                    {
                        PermissionGroup group = permissionPool.getGroups().get(args[1]);
                        CloudNetAPI.getInstance().deletePermissionGroup(group);
                        commandSender.sendMessage(CloudProxy.getInstance().getProxyLayout().getPrefix() + "The is group is now deleted.");
                    }
                    return;
                }
                break;
            case 5:
                if (args[0].equalsIgnoreCase("group"))
                {
                    if (permissionPool.getGroups().get(args[1]) != null)
                    {
                        PermissionGroup permissionGroup = permissionPool.getGroups().get(args[1]);
                        if (args[2].equalsIgnoreCase("set"))
                        {
                            if (args[3].equalsIgnoreCase("display"))
                            {
                                permissionGroup.setDisplay(args[4].replace("_", " "));
                                CloudNetAPI.getInstance().updatePermissionGroup(permissionGroup);
                                commandSender.sendMessage(CloudProxy.getInstance().getProxyLayout().getPrefix() + "The configuration from the group " + args[1] + " is updated.");
                                return;
                            }

                            if (args[3].equalsIgnoreCase("prefix"))
                            {
                                permissionGroup.setPrefix(args[4].replace("_", " "));
                                CloudNetAPI.getInstance().updatePermissionGroup(permissionGroup);
                                commandSender.sendMessage(CloudProxy.getInstance().getProxyLayout().getPrefix() + "The configuration from the group " + args[1] + " is updated.");
                                return;
                            }

                            if (args[3].equalsIgnoreCase("default"))
                            {
                                permissionGroup.setDefaultGroup(args[4].equalsIgnoreCase("true"));
                                CloudNetAPI.getInstance().updatePermissionGroup(permissionGroup);
                                commandSender.sendMessage(CloudProxy.getInstance().getProxyLayout().getPrefix() + "The configuration from the group " + args[1] + " is updated.");
                                return;
                            }

                            if (args[3].equalsIgnoreCase("fallback"))
                            {
                                if (CloudNetAPI.getInstance().getGroups().containsKey(args[4]))
                                {
                                    permissionGroup.setPermissionFallback(new PermissionFallback(true, args[4]));
                                    CloudNetAPI.getInstance().updatePermissionGroup(permissionGroup);
                                    commandSender.sendMessage(CloudProxy.getInstance().getProxyLayout().getPrefix() + "The configuration from the group " + args[1] + " is updated.");
                                }
                                return;
                            }

                            if (args[3].equalsIgnoreCase("suffix"))
                            {
                                permissionGroup.setSuffix(args[4].replace("_", " "));
                                CloudNetAPI.getInstance().updatePermissionGroup(permissionGroup);
                                commandSender.sendMessage(CloudProxy.getInstance().getProxyLayout().getPrefix() + "The configuration from the group " + args[1] + " is updated.");
                                return;
                            }

                            if (args[3].equalsIgnoreCase("tagid"))
                            {
                                if (NetworkUtils.checkIsNumber(args[4]))
                                {
                                    permissionGroup.setTagId(Integer.parseInt(args[4]));
                                    CloudNetAPI.getInstance().updatePermissionGroup(permissionGroup);
                                    commandSender.sendMessage(CloudProxy.getInstance().getProxyLayout().getPrefix() + "The configuration from the group " + args[1] + " is updated.");
                                    return;
                                }
                                return;
                            }

                            if (args[3].equalsIgnoreCase("joinpower"))
                            {
                                if (NetworkUtils.checkIsNumber(args[4]))
                                {
                                    permissionGroup.setJoinPower(Integer.parseInt(args[4]));
                                    CloudNetAPI.getInstance().updatePermissionGroup(permissionGroup);
                                    commandSender.sendMessage(CloudProxy.getInstance().getProxyLayout().getPrefix() + "The configuration from the group " + args[1] + " is updated.");
                                    return;
                                }
                                return;
                            }

                            if (args[3].equalsIgnoreCase("permissiondefault"))
                            {
                                if (PermissionDefault.valueOf(args[4]) != null)
                                {
                                    permissionGroup.setPermissionDefault(PermissionDefault.valueOf(args[4]));
                                    CloudNetAPI.getInstance().updatePermissionGroup(permissionGroup);
                                    commandSender.sendMessage(CloudProxy.getInstance().getProxyLayout().getPrefix() + "The configuration from the group " + args[1] + " is updated.");
                                    return;
                                }
                                return;
                            }


                            return;
                        }

                        if (args[2].equalsIgnoreCase("add"))
                        {
                            if (args[3].equalsIgnoreCase("implementation"))
                            {
                                if (permissionPool.getGroups().containsKey(args[4]))
                                {
                                    permissionGroup.getImplementGroups().add(args[4]);
                                    CloudNetAPI.getInstance().updatePermissionGroup(permissionGroup);
                                    commandSender.sendMessage(CloudProxy.getInstance().getProxyLayout().getPrefix() + "The configuration from the group " + args[1] + " is updated.");
                                    return;
                                }
                                return;
                            }
                            if (args[3].equalsIgnoreCase("permission"))
                            {
                                if (args[4].startsWith("-"))
                                {
                                    permissionGroup.getPermissions().put(args[4], false);
                                } else
                                {
                                    permissionGroup.getPermissions().put(args[4], true);
                                }
                                CloudNetAPI.getInstance().updatePermissionGroup(permissionGroup);
                                commandSender.sendMessage(CloudProxy.getInstance().getProxyLayout().getPrefix() + "The configuration from the group " + args[1] + " is updated.");
                                return;
                            }
                            return;
                        }

                        if (args[2].equalsIgnoreCase("delete"))
                        {
                            if (args[3].equalsIgnoreCase("implementation"))
                            {
                                permissionGroup.getImplementGroups().remove(args[4]);
                                CloudNetAPI.getInstance().updatePermissionGroup(permissionGroup);
                                commandSender.sendMessage(CloudProxy.getInstance().getProxyLayout().getPrefix() + args[1] + " is updated.");
                                return;
                            }
                            if (args[3].equalsIgnoreCase("permission"))
                            {
                                permissionGroup.getPermissions().remove(args[4]);
                                CloudNetAPI.getInstance().updatePermissionGroup(permissionGroup);
                                commandSender.sendMessage(CloudProxy.getInstance().getProxyLayout().getPrefix() + "The configuration from the group " + args[1] + " is updated.");
                                return;
                            }
                            return;
                        }

                    } else
                    {
                        commandSender.sendMessage("The group doesn't exists.");
                    }
                }
                break;
            case 6:
                if (args[0].equalsIgnoreCase("group"))
                {
                    if (permissionPool.getGroups().get(args[1]) != null)
                    {
                        PermissionGroup permissionGroup = permissionPool.getGroups().get(args[1]);
                        if (args[2].equalsIgnoreCase("add") && args[3].equalsIgnoreCase("permission"))
                        {
                            if (!permissionGroup.getServerGroupPermissions().containsKey(args[5]))
                            {
                                permissionGroup.getServerGroupPermissions().put(args[5], new ArrayList<>());
                            }

                            permissionGroup.getServerGroupPermissions().get(args[5]).add(args[4]);
                            CloudNetAPI.getInstance().updatePermissionGroup(permissionGroup);
                            commandSender.sendMessage("The configuration from the group " + args[1] + " is updated.");

                            return;
                        }

                        if (args[2].equalsIgnoreCase("delete") && args[3].equalsIgnoreCase("permission"))
                        {
                            if (!permissionGroup.getServerGroupPermissions().containsKey(args[5]))
                            {
                                permissionGroup.getServerGroupPermissions().put(args[5], new ArrayList<>());
                            }

                            permissionGroup.getServerGroupPermissions().get(args[5]).remove(args[4]);
                            CloudNetAPI.getInstance().updatePermissionGroup(permissionGroup);
                            commandSender.sendMessage("The configuration from the group " + args[1] + " is updated.");
                            return;
                        }
                    } else
                    {
                        commandSender.sendMessage("The group doesn't exists.");
                    }
                }
                break;
            default:
                commandSender.sendMessage("/cperms help");
                break;
        }
    }

    private class PermissionGroupNull
            extends PermissionGroup {

        public PermissionGroupNull(String name)
        {
            super(name, PermissionDefault.USER, "&7", "&f", "&7", 98, 0,
                    false, new HashMap<>(), new HashMap<>(), new HashMap<>(), new ArrayList<>(), new PermissionFallback(false,
                            CloudProxy.getInstance().getProxyLayout().getFallback()));
        }
    }
}