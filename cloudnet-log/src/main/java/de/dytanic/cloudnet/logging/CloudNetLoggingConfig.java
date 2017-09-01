package de.dytanic.cloudnet.logging;

import lombok.Getter;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

/**
 * Created by Tareko on 15.07.2017.
 */
@Getter
public class CloudNetLoggingConfig {

    private Properties properties;
    private File file = new File("dodaylightconsole.properties");

    public CloudNetLoggingConfig() throws Exception
    {
        properties = new Properties();

        if(!file.exists())
        {
            file.createNewFile();
            Properties properties = new Properties();
            properties.setProperty("dodaylightconsole", "true");
            properties.setProperty("state", "NIGHT");
            try(FileOutputStream outputStream = new FileOutputStream(file))
            {
                properties.save(outputStream, "States: NIGHT, DAY, OFF");
            }
        }

        load();

    }

    public CloudNetLoggingConfig load()
    {
        try(InputStreamReader reader = new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))
        {

            properties.load(reader);

        } catch (FileNotFoundException e)
        {
            e.printStackTrace();
        } catch (IOException e)
        {
            e.printStackTrace();
        }
        return this;
    }

}