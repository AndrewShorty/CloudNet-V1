package de.dytanic.cloudnetproxy.database.backend;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import de.dytanic.cloudnet.lib.document.Document;
import de.dytanic.cloudnet.network.NetworkUtils;
import de.dytanic.cloudnet.permission.PermissionDefault;
import de.dytanic.cloudnet.permission.PermissionFallback;
import de.dytanic.cloudnet.permission.PermissionGroup;
import de.dytanic.cloudnetproxy.database.yaml.Configuration;
import de.dytanic.cloudnetproxy.database.yaml.ConfigurationProvider;
import de.dytanic.cloudnetproxy.database.yaml.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * Created by Tareko on 01.06.2017.
 */
public class PermissionBackend {

    private final File permissionFile = new File("permissions.yml");

    public PermissionBackend() throws Exception {

        if(!permissionFile.exists())
        {
            permissionFile.createNewFile();

            Configuration c = provider.load(permissionFile);

            {
                HashMap<String, Boolean> optionsAdmin = new HashMap<>();
                optionsAdmin.put("test", true);

                HashMap<String, List<String>> x = new HashMap<>();
                x.put("Lobby", Arrays.asList("minecraft.command.gamemode", "minecraft.command.help"));

                Configuration admin = new Configuration();
                admin.set("permissionDefault", PermissionDefault.ADMINISTRATOR.name());
                admin.set("prefix", "&4Admin &7| ");
                admin.set("suffix", "&f");
                admin.set("display", "&4");
                admin.set("tagId", 10);
                admin.set("joinpower", 100);
                admin.set("defaultGroup", false);
                admin.set("permissions", Arrays.asList("cloudnet.command.alert"));
                admin.set("serverPermissions", x);
                admin.set("implements", Arrays.asList("Member"));
                admin.set("options", optionsAdmin);
                admin.set("alternatefallback", new Configuration().set("enabled", false).set("fallback", "Lobby"));

                c.set("Admin", admin);
            }

            {
                HashMap<String, Boolean> optionsMember = new HashMap<>();
                optionsMember.put("test", true);

                HashMap<String, List<String>> x = new HashMap<>();
                x.put("Lobby", Arrays.asList("bukkit.command.tps", "bukkit.command.help"));

                Configuration member = new Configuration();
                member.set("permissionDefault", PermissionDefault.USER.name());
                member.set("prefix", "&eMember &7| ");
                member.set("suffix", "&f");
                member.set("display", "&e");
                member.set("tagId", 11);
                member.set("joinpower", 0);
                member.set("defaultGroup", true);
                member.set("permissions", Arrays.asList("cloudnet.command.test"));
                member.set("serverPermissions", x);
                member.set("implements", Arrays.asList());
                member.set("options", optionsMember);
                member.set("alternatefallback", new Configuration().set("enabled", false).set("fallback", "Lobby"));

                c.set("Member", member);
            }

            provider.save(c, permissionFile);
        }
    }

    public PermissionBackend update(PermissionGroup permissionGroup)
    {
        try
        {
            Configuration c = ConfigurationProvider.getProvider(YamlConfiguration.class).load(permissionFile);

            List<String> permissions = new ArrayList<>();

            for(java.util.Map.Entry<String, Boolean> permission : permissionGroup.getPermissions().entrySet())
            {
                permissions.add((permission.getValue() ? "" : "-") + permission.getKey());
            }

            Configuration member = new Configuration();
            member.set("permissionDefault", permissionGroup.getPermissionDefault().name());
            member.set("prefix", permissionGroup.getPrefix());
            member.set("suffix", permissionGroup.getSuffix());
            member.set("display", permissionGroup.getDisplay());
            member.set("tagId", permissionGroup.getTagId());
            member.set("joinpower", permissionGroup.getJoinPower());
            member.set("defaultGroup", permissionGroup.isDefaultGroup());
            member.set("permissions", permissions);
            member.set("serverPermissions", permissionGroup.getServerGroupPermissions());
            member.set("implements", permissionGroup.getImplementGroups());
            member.set("options", permissionGroup.getOptions());
            member.set("alternatefallback", new Configuration().set("enabled", permissionGroup.getPermissionFallback().isEnabled()).set("fallback",
                    permissionGroup.getPermissionFallback().getFallback()));

            c.set(permissionGroup.getName(), member);
            ConfigurationProvider.getProvider(YamlConfiguration.class).save(c, permissionFile);
        }catch (Exception ex){}
        return this;
    }

    public PermissionBackend delete(PermissionGroup permissionGroup)
    {
        try
        {
            Configuration configuration = ConfigurationProvider.getProvider(YamlConfiguration.class).load(permissionFile);
            configuration.self.remove(permissionGroup.getName());
            ConfigurationProvider.getProvider(YamlConfiguration.class).save(configuration, permissionFile);
        } catch (IOException e)
        {
            e.printStackTrace();
        }
        return this;
    }

    @Deprecated
    public HashMap<String, PermissionGroup> _loadPermissions()
    {
        HashMap<String, PermissionGroup> groups = new HashMap<>();
        Document document = Document.loadDocument(permissionFile);

        JsonArray jsonArray = document.getArray("groups");
        for(JsonElement element : jsonArray)
        {
            PermissionGroup group = NetworkUtils.GSON.fromJson(element, PermissionGroup.class);
            groups.put(group.getName(), group);
        }
        return groups;
    }

    private final ConfigurationProvider provider = ConfigurationProvider.getProvider(YamlConfiguration.class);

    public HashMap<String, PermissionGroup> loadPermissions()
    {
        HashMap<String, PermissionGroup> groups = new HashMap<>();
        try
        {
            Configuration c = provider.load(new File("permissions.yml"));
            for (String key : c.getKeys())
            {
                Configuration configuration = c.getSection(key);

                List<String> se = configuration.getStringList("permissions");
                HashMap<String, Boolean> permissions__ = new HashMap<>();
                for(String s : se)
                {
                    if(s.startsWith("-"))
                    {
                        permissions__.put(s.replaceFirst("-", ""), false);
                    }else{
                        permissions__.put(s, true);
                    }
                }

                java.util.Map<String, List<String>> permissions = new HashMap<>();
                Configuration sec2 = configuration.getSection("serverPermissions");
                for(String groups2 : sec2.getKeys())
                {
                    permissions.put(groups2, sec2.getStringList(groups2));
                }

                Configuration sec3 = configuration.getSection("alternatefallback");
                PermissionGroup group = new PermissionGroup(key, PermissionDefault.valueOf(configuration.getString("permissionDefault")), configuration.getString("prefix"),
                        configuration.getString("suffix"), configuration.getString("display"), configuration.getInt("tagId"), configuration.getInt("joinpower"),
                        configuration.getBoolean("defaultGroup"), permissions__, permissions, configuration.getSection("options").self, configuration.getStringList("implements"),
                        new PermissionFallback(sec3.getBoolean("enabled"), sec3.getString("fallback")));

                groups.put(group.getName(), group);
            }

        }catch (Exception ex)
        {
            ex.printStackTrace();
        }

        for(PermissionGroup group : groups.values())
        {
            for(String impl : group.getImplementGroups())
            {
                if(groups.containsKey(impl))
                {
                    group.getPermissions().putAll(groups.get(impl).getPermissions());
                }
            }
        }

        return groups;
    }
}