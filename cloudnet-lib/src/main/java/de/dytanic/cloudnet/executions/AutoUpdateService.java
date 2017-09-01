package de.dytanic.cloudnet.executions;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import de.dytanic.cloudnet.Version;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * Created by Tareko on 25.06.2017.
 */
public class AutoUpdateService {

    public AutoUpdateService(String server, Version version)
    {
        try
        {
            System.out.println("Checking for updates...");
            URLConnection connection = new URL("http://dytanic.de/cloudnetproject/update.json").openConnection();
            connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");
            connection.connect();

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String input = bufferedReader.readLine();
            bufferedReader.close();
            JsonParser jsonParser = new JsonParser();
            JsonObject object = jsonParser.parse(input).getAsJsonObject();
            if (object.has("version"))
            {
                if (!version.getVersion().equalsIgnoreCase(object.get("version").getAsString()))
                {
                    System.out.println("Downloading CloudNet.zip...");
                    download("http://dytanic.de/cloudnetproject/CloudNet.zip", "CloudNet.zip");
                    System.out.println("Downloading complete!");

                    if (!System.getProperty("os.name").contains("Windows") && !System.getProperty("os.name").contains("windows"))
                    {
                        ZipFile zip = new ZipFile(new File("CloudNet.zip"));
                        ZipEntry entry = zip.getEntry(server + "/" + server + ".jar");
                        extractEntry(zip, entry);
                        new File(server + "/" + server + ".jar").renameTo(new File(server + ".jar"));

                        new File("CloudNet.zip").delete();

                        new File(server).delete();
                        System.out.println(server + " is updated... please restart your software!");
                    } else
                    {
                        System.out.println(server + " the archive is downloaded, please install the new update");
                    }
                    return;
                }
            }

            System.out.println("No update found");


        } catch (Exception ex)
        {
            System.out.println("Failed to Auto-update!");
        }
    }

    private void download(String url, String target) throws Exception
    {
        URLConnection connection = new URL(url).openConnection();
        connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");
        connection.connect();
        Files.copy(connection.getInputStream(), Paths.get(target));
    }

    private static final byte[] BUFFER = new byte[0xFFFF];

    private static void extractEntry(ZipFile zipFile, ZipEntry entry)
            throws IOException
    {
        File file = new File(entry.getName());

        if (entry.isDirectory())
            file.mkdirs();
        else
        {
            new File(file.getParent()).mkdirs();

            InputStream is = null;
            OutputStream os = null;

            try
            {
                is = zipFile.getInputStream(entry);
                os = new FileOutputStream(file);

                int len;
                while ((len = is.read(BUFFER)) != -1)
                    os.write(BUFFER, 0, len);
            } finally
            {
                if (os != null) os.close();
                if (is != null) is.close();
            }
        }
    }

}