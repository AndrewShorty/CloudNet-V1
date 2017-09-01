package de.dytanic.cloudnetserver.setup;

import de.dytanic.cloudnet.lib.document.Document;
import de.dytanic.cloudnet.lib.threading.Runnabled;
import de.dytanic.cloudnet.servergroup.ServerGroup;
import de.dytanic.cloudnet.servergroup.ServerGroupMode;
import de.dytanic.cloudnet.servergroup.ServerGroupType;
import de.dytanic.cloudnetserver.CloudNetServer;
import de.dytanic.cloudnetserver.network.NetworkConnector;
import de.dytanic.cloudnetserver.network.packets.PacketOutServerData;
import de.dytanic.cloudnetserver.util.Utils;
import jline.console.ConsoleReader;
import org.apache.commons.io.FileUtils;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * Created by Tareko on 25.05.2017.
 */
public class SetupServerGroup
        implements Runnabled<ConsoleReader> {

    private final Document setup = new Document();
    private final byte[] BUFFER = new byte[0xFFFF];

    @Override
    public void run(ConsoleReader reader)
    {

        if (CloudNetServer.getInstance().getGroups().size() == 0 && CloudNetServer.getInstance().getArguments().contains("--groupSetupArguments"))
        {
            String value = CloudNetServer.getInstance().getArguments().get(CloudNetServer.getInstance().getArguments().indexOf("--groupSetupArguments") + 1);
            String[] args = value.split(";");
            if (args.length == 8)
            {
                this.setup
                        .append("name", args[0])
                        .append("memory", Integer.parseInt(args[1]))
                        .append("joinpower", Integer.parseInt(args[1]))
                        .append("maxplayers", Integer.parseInt(args[2]))
                        .append("priority", Integer.parseInt(args[3]))
                        .append("startup", Integer.parseInt(args[4]))
                        .append("groupmode", args[5])
                        .append("grouptype", args[6]);

                if (args[7].equalsIgnoreCase("true"))
                {
                    new File("database/templates/" + this.setup.getString("name") + "/plugins").mkdir();
                    try
                    {
                        System.out.println("Downloading ViaVersion...");
                        URLConnection connection = new URL("https://ci.viaversion.com/job/ViaVersion/lastSuccessfulBuild/artifact/jar/target/ViaVersion-1.1.2-SNAPSHOT.jar").openConnection();
                        connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");
                        connection.connect();
                        FileUtils.copyToFile(connection.getInputStream(), new File("database/templates/" + this.setup.getString("name") + "/plugins/ViaVersion.jar"));
                        System.out.println("Downloading Complete!");
                    } catch (Exception ex)
                    {
                        ex.printStackTrace();
                    }
                }
                new File("database/templates/" + this.setup.getString("name")).mkdir();
            }
        } else
        {
            System.out.println("Group-Setup:");

            {
                System.out.println(CloudNetServer.getInstance().getMessages().getProperty("groupSetupName"));
                this.setup.append("name", input(reader));
            }

            {
                System.out.println(CloudNetServer.getInstance().getMessages().getProperty("groupSetupMemory"));
                boolean a = false;
                while (!a)
                {
                    String input = input(reader);
                    if (Utils.checkAsNumber(input))
                    {
                        this.setup.append("memory", Integer.parseInt(input));
                        a = true;
                    } else
                    {
                        System.out.println("Invalid input");
                    }
                }
            }

            {
                System.out.println(CloudNetServer.getInstance().getMessages().getProperty("groupSetupJoinPower"));
                boolean a = false;
                while (!a)
                {
                    String input = input(reader);
                    if (Utils.checkAsNumber(input))
                    {
                        this.setup.append("joinpower", Integer.parseInt(input));
                        a = true;
                    } else
                    {
                        System.out.println("Invalid input");
                    }
                }
            }

            {
                System.out.println(CloudNetServer.getInstance().getMessages().getProperty("groupSetupMaxPlayers"));
                boolean a = false;
                while (!a)
                {
                    String input = input(reader);
                    if (Utils.checkAsNumber(input))
                    {
                        this.setup.append("maxplayers", Integer.parseInt(input));
                        a = true;
                    } else
                    {
                        System.out.println("Invalid input");
                    }
                }
            }

            {
                System.out.println(CloudNetServer.getInstance().getMessages().getProperty("groupSetupPriority"));
                boolean a = false;
                while (!a)
                {
                    String input = input(reader);
                    if (Utils.checkAsNumber(input))
                    {
                        this.setup.append("priority", Integer.parseInt(input));
                        a = true;
                    } else
                    {
                        System.out.println("Invalid input");
                    }
                }
            }

            {
                System.out.println(CloudNetServer.getInstance().getMessages().getProperty("groupSetupPriorityGroup"));
                boolean a = false;
                while (!a)
                {
                    String input = input(reader);
                    if (Utils.checkAsNumber(input))
                    {
                        this.setup.append("grouppriority", Integer.parseInt(input));
                        a = true;
                    } else
                    {
                        System.out.println("Invalid input");
                    }
                }
            }

            {
                System.out.println(CloudNetServer.getInstance().getMessages().getProperty("groupSetupStartup"));
                boolean a = false;
                while (!a)
                {
                    String input = input(reader);
                    if (Utils.checkAsNumber(input))
                    {
                        this.setup.append("startup", Integer.parseInt(input));
                        a = true;
                    } else
                    {
                        System.out.println("Invalid input");
                    }
                }
            }

            {
                System.out.println(CloudNetServer.getInstance().getMessages().getProperty("groupSetupGroupMode"));
                boolean a = false;
                while (!a)
                {
                    String input = input(reader);
                    switch (input.toUpperCase())
                    {
                        case "DYNAMIC":
                            this.setup.append("groupmode", "DYNAMIC");
                            a = true;
                            break;
                        case "STATIC":
                            this.setup.append("groupmode", "STATIC");
                            a = true;
                            break;
                        case "LOBBY":
                            this.setup.append("groupmode", "LOBBY");
                            a = true;
                            break;
                        default:
                        {
                            System.out.println("Invalid input");
                        }
                        break;
                    }
                }
            }

            {
                System.out.println(CloudNetServer.getInstance().getMessages().getProperty("groupSetupServerType"));
                boolean a = false;
                while (!a)
                {
                    String input = input(reader);
                    switch (input.toUpperCase())
                    {
                        case "BUKKIT":
                            this.setup.append("grouptype", "BUKKIT");
                            a = true;
                            break;
                        case "CAULDRON":
                            this.setup.append("grouptype", ServerGroupType.CAULDRON.name());
                            a = true;
                            break;
                        default:
                        {
                            System.out.println("Invalid input");
                        }
                        break;
                    }
                }

            }

            new File("database/templates/" + this.setup.getString("name")).mkdir();
            new File("database/templates/" + this.setup.getString("name") + "/global").mkdir();
            new File("database/templates/" + this.setup.getString("name") + "/maps").mkdir();
            new File("database/templates/" + this.setup.getString("name") + "/profiles").mkdir();

            System.out.println(CloudNetServer.getInstance().getMessages().getProperty("groupSetupViaVersion"));
            String input = input(reader);

            if (input.equalsIgnoreCase("yes"))
            {
                new File("database/templates/" + this.setup.getString("name") + "/global/plugins").mkdir();
                try
                {
                    System.out.println("Downloading ViaVersion...");
                    URLConnection connection = new URL("https://ci.viaversion.com/job/ViaVersion/lastSuccessfulBuild/artifact/jar/target/ViaVersion-1.1.2-SNAPSHOT.jar").openConnection();
                    connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");
                    connection.connect();
                    FileUtils.copyToFile(connection.getInputStream(), new File("database/templates/" + this.setup.getString("name") + "/global/plugins/ViaVersion.jar"));
                    System.out.println("Downloading Complete!");
                } catch (Exception ex)
                {
                    ex.printStackTrace();
                }
            }

        }

        ServerGroup group =
                new ServerGroup(
                        setup.getString("name"), setup.getInt("memory"),
                        setup.getInt("memory"), new HashMap<String, Object>(),
                        setup.getInt("joinpower"), true,
                        setup.getInt("startup"), setup.getInt("priority"), setup.getInt("grouppriority"), "127.0.0.1",
                        setup.getInt("maxplayers"), 180, 100, 100, ServerGroupType.valueOf(setup.getString("grouptype")),
                        ServerGroupMode.valueOf(setup.getString("groupmode")), null);

        CloudNetServer.getInstance().getConfig().addNewGroup(group);

        if (NetworkConnector.getInstance() != null)
        {
            NetworkConnector.getInstance().sendPacket(new PacketOutServerData());
        }

        if (group.getServerType().equals(ServerGroupType.CAULDRON))
        {
            try
            {
                System.out.println("Downloading cauldron.zip...");
                File file = new File("database/templates/" + group.getName() + "/cauldron.zip");
                URLConnection connection = new URL("https://yivesmirror.com/files/cauldron/cauldron-1.7.10-2.1403.1.54.zip").openConnection();
                connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");
                connection.connect();
                FileUtils.copyToFile(connection.getInputStream(), file);
                System.out.println("Downloading Complete!");

                ZipFile zip = new ZipFile(file);
                Enumeration<? extends ZipEntry> entryEnumeration = zip.entries();
                while (entryEnumeration.hasMoreElements())
                {
                    ZipEntry entry = entryEnumeration.nextElement();

                    if (!entry.isDirectory())
                    {
                        System.out.println("Creating " + entry.getName() + "...");
                        extractEntry(zip, entry, "database/templates/" + group.getName() + "/global");
                    }
                }

                zip.close();
                file.delete();

                new File("database/templates/" + group.getName() + "/global/cauldron-1.7.10-2.1403.1.54-server.jar")
                        .renameTo(new File("database/templates/" + group.getName() + "/global/cauldron.jar"));

                System.out.println("Use a cauldron.jar for your minecraft server template " + group.getName() + " please add a eula.txt into the template or into the global folder");
            } catch (Exception e)
            {
                e.printStackTrace();
            }
        }

        System.out.println(" ");
        System.out.println(CloudNetServer.getInstance().getMessages().getProperty("groupSetupCompleteFirst").replace("%group%", group.getName()));
        System.out.println(CloudNetServer.getInstance().getMessages().getProperty("groupSetupCompleteSecond"));
        System.out.println(CloudNetServer.getInstance().getMessages().getProperty("groupSetupCompleteThird"));
        System.out.println(" ");


        if (CloudNetServer.getInstance().getArguments().contains("--ssl") && group.getServerType().equals(ServerGroupType.CAULDRON))
            return;
        CloudNetServer
                .getInstance().getScheduler()
                .runTaskSync(new Runnable() {
                    @Override
                    public void run()
                    {
                        CloudNetServer.getInstance().startServer(group, new Document(), false, true);
                    }
                });
    }

    private String input(ConsoleReader reader)
    {
        try
        {
            return reader.readLine();
        } catch (IOException ex)
        {
            return "";
        }
    }

    private void extractEntry(ZipFile zipFile, ZipEntry entry, String destDir)
            throws IOException
    {
        File file = new File(destDir, entry.getName());

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