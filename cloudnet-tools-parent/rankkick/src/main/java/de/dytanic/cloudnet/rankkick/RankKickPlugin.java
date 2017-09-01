package de.dytanic.cloudnet.rankkick;

import lombok.Getter;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Created by Tareko on 17.07.2017.
 */
public class RankKickPlugin
        extends Plugin {

    @Getter
    private static RankKickPlugin instance;

    @Getter
    private Configuration configuration;

    @Override
    public void onEnable()
    {
        instance = this;

        try
        {
            getDataFolder().mkdir();
            Path path = Paths.get("plugins/" + getDescription().getName() + "/config.yml");
            if (!Files.exists(path))
            {
                Files.createFile(path);
                Configuration configuration = new Configuration();
                configuration.set("kickMessage", "Your rank has been updated!\n Now your rank is %rank% with the time %time%!");
                ConfigurationProvider.getProvider(YamlConfiguration.class).save(configuration, new OutputStreamWriter(Files.newOutputStream(path), StandardCharsets.UTF_8));
            }
            this.configuration = ConfigurationProvider.getProvider(YamlConfiguration.class).load(Files.newInputStream(path), configuration);
        } catch (IOException e)
        {
            e.printStackTrace();
        }

        getProxy().getPluginManager().registerListener(this, new RankKickListener());
    }
}