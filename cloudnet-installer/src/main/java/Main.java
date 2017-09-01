import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * Created by Tareko on 22.06.2017.
 */
public final class Main {

    public static final String HEADER = "\n" +
            "      " + "   ___  _                    _      __        _   \n" +
            "      " + "  / __\\| |  ___   _   _   __| |  /\\ \\ \\  ___ | |_ \n" +
            "      " + " / /   | | / _ \\ | | | | / _` | /  \\/ / / _ \\| __|\n" +
            "      " + "/ /___ | || (_) || |_| || (_| |/ /\\  / |  __/| |_ \n" +
            "      " + "\\____/ |_| \\___/  \\__,_| \\__,_|\\_\\ \\/   \\___| \\__|\n" +
            "      " + "                                                  \n" +
            "      " + "Software: CloudNet-Installer    | You don't have permission\n" +
            "      " + "CopyRight (c) Tarek H. 2017     | to decompile this\n" +
            "      " + "Version: 1.0-Pre                | resource!\n" +
            "      " + "Java version: " + System.getProperty("java.version") + "\n"
            ;

    public static void main(String[] args) throws Exception
    {
        Process process = null;

        System.out.println(HEADER);

        System.out.println("What do you want? [\"download\", \"install\", \"update\"]");

        try(BufferedReader reader = new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8)))
        {
            while (true)
            {
                switch (reader.readLine().toLowerCase())
                {
                    case "download":
                    {
                        System.out.println("Downloading CloudNet.zip...");
                        download("http://dytanic.de/cloudnet/CloudNet.zip", "CloudNet.zip");
                        System.out.println("Downloading complete!");

                        if(process != null)
                        {
                            process.destroy();
                        }

                        System.exit(0);
                    }
                        break;
                    case "install":
                    {
                        /*
                        System.out.println("Installing CloudNet-Server...");
                        new File("CloudNet-Server").mkdir();
                        download("http://dytanic.de/cloudnet/CloudNet-Server.jar", "CloudNet-Server/CloudNet-Server.jar");
                        System.out.println("CloudNet-Server is installed.");
                        addingScripts("CloudNet-Server");


                        System.out.println("Installing CloudNet-Proxy...");
                        new File("CloudNet-Proxy").mkdir();
                        download("http://dytanic.de/cloudnet/CloudNet-Proxy.jar", "CloudNet-Proxy/CloudNet-Proxy.jar");
                        System.out.println("CloudNet-Proxy is installed.");
                        addingScripts("CloudNet-Proxy");
                        System.exit(0);
                        */

                        System.out.println("Downloading CloudNet.zip...");
                        download("http://dytanic.de/cloudnet/CloudNet.zip", "CloudNet.zip");
                        System.out.println("Downloading complete!");

                        ZipFile zip = new ZipFile(new File("CloudNet.zip"));
                        Enumeration<? extends ZipEntry> entryEnumeration = zip.entries();
                        while (entryEnumeration.hasMoreElements())
                        {
                            ZipEntry entry = entryEnumeration.nextElement();

                            if(!entry.isDirectory())
                            {
                                System.out.println("Creating " + entry.getName() + "...");
                                extractEntry(zip, entry);
                            }
                        }
                        System.exit(0);
                    }
                        break;
                    case "update":
                    {

                        System.out.println("Downloading CloudNet.zip...");
                        download("http://dytanic.de/cloudnet/CloudNet.zip", "CloudNet.zip");
                        System.out.println("Downloading complete!");

                        System.out.println("Installing CloudNet-Server...");
                        ZipFile zip = new ZipFile(new File("CloudNet.zip"));
                        ZipEntry entry = zip.getEntry("CloudNet-Server/CloudNet-Server.jar");
                        extractEntry(zip, entry);
                        addingScripts("CloudNet-Server");
                        System.out.println("CloudNet-Server is installed.");
                        //addingScripts("CloudNet-Server");


                        System.out.println("Installing CloudNet-Proxy...");
                        ZipEntry entry_ = zip.getEntry("CloudNet-Proxy/CloudNet-Proxy.jar");
                        extractEntry(zip, entry_);
                        addingScripts("CloudNet-Server");
                        System.out.println("CloudNet-Proxy is installed.");
                        //addingScripts("CloudNet-Proxy");

                        System.exit(0);
                    }
                        break;
                    default:
                        continue;
                }
            }

        }
    }

    private static void addingScripts(String server)
    {
        {
            File file = new File(server + "/start.sh");
            try(FileWriter writer = new FileWriter(file))
            {

                writer.write("screen -S " + (server.equals("CloudNet-Server") ? "CNS" : "CNP") + " java " +
                        "-Xmx64M -XX:+UseG1GC " +
                        "-XX:MaxGCPauseMillis=50 " +
                        "-Xmn2M -XX:MaxPermSize=256M -jar " + server + ".jar\n");
                writer.flush();

            } catch (IOException e)
            {
                e.printStackTrace();
            }
            file.setExecutable(true);
            file.setWritable(true);
            file.setReadable(true);
        }

        File file = new File(server + "/start.bat");
        try(FileWriter writer = new FileWriter(file))
        {

            writer.write("java " +
                    "-Xmx64M -XX:+UseG1GC " +
                    "-XX:MaxGCPauseMillis=50 " +
                    "-Xmn2M -XX:MaxPermSize=256M -jar " + server + ".jar\n");
            writer.flush();

        } catch (IOException e)
        {
            e.printStackTrace();
        }
        file.setExecutable(true);
        file.setWritable(true);
        file.setReadable(true);
    }

    private static void download(String url, String target) throws Exception
    {
        if(new File(target).exists())
        {
            new File(target).delete();
        }
        URLConnection connection = new URL(url).openConnection();
        connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");
        connection.connect();
        Files.copy(connection.getInputStream(), Paths.get(target));
    }

    private static final byte[] BUFFER = new byte[ 0xFFFF ];

    private static void extractEntry(ZipFile zipFile, ZipEntry entry)
            throws IOException
    {
        File file = new File( entry.getName() );

        if ( entry.isDirectory() )
            file.mkdirs();
        else
        {
            if(file.getParent() != null)
            {
                new File( file.getParent() ).mkdirs();
            }

            InputStream  is = null;
            OutputStream os = null;

            try
            {
                is = zipFile.getInputStream( entry );
                os = new FileOutputStream( file );

                int len;
                while ((len = is.read(BUFFER)) != -1)
                    os.write( BUFFER, 0, len );
            }
            finally
            {
                if ( os != null ) os.close();
                if ( is != null ) is.close();
            }
        }
    }

}