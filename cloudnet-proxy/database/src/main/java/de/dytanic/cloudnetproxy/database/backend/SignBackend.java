package de.dytanic.cloudnetproxy.database.backend;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import de.dytanic.cloudnet.lib.document.Document;
import de.dytanic.cloudnet.network.NetworkUtils;
import de.dytanic.cloudnet.sign.Sign;
import de.dytanic.cloudnet.sign.SignLayout;
import lombok.Getter;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.UUID;

/**
 * Created by Tareko on 03.06.2017.
 */
@Getter
public class SignBackend {

    private final File signsFile = new File("database/signs.json");
    private final File layoutsFile = new File("layouts.json");

    /*
    private Document layouts;
    */
    private Document signs;

    public SignBackend() throws Exception
    {

        /*
        if (!layoutsFile.exists())
        {
            /*
            Document layouts = new Document()
                    .append("default", new Document()
                                        .append("layouts", Arrays.asList(
                                                new SignLayout("online", new String[]{
                                                        "%server%",
                                                        "[&e%state%&0]",
                                                        "&b %onlineplayers% &8/ &b%maxplayers%",
                                                        "%motd%",
                                                }, -1),
                                                new SignLayout("full", new String[]{
                                                        "%server%",
                                                        "%state%",
                                                        "&b %onlineplayers% &8/ &b%maxplayers%",
                                                        "%motd%"
                                                }, -1),
                                                new SignLayout("maintenance", new String[]{
                                                        "",
                                                        "maintenance",
                                                        "mode",
                                                        ""
                                                }, -1),
                                                new SignLayout("loading1", new String[]{
                                                        "",
                                                        "Server",
                                                        "loading.",
                                                        ""
                                                }, -1),
                                                new SignLayout("loading2", new String[]{
                                                        "",
                                                        "Server",
                                                        "loading..",
                                                        ""
                                                }, -1),
                                                new SignLayout("loading3", new String[]{
                                                        "",
                                                        "Server",
                                                        "loading...",
                                                        ""
                                                }, -1),
                                                new SignLayout("loading4", new String[]{
                                                        "",
                                                        "Server",
                                                        "loading",
                                                        ""
                                                }, -1)
                                        )));
            */
        /*
            Document document = new Document()
                .append("layouts", Arrays.asList(
                        new SignLayout("online", new String[]{
                                "%server%",
                                "[&e%state%&0]",
                                "&b %onlineplayers% &8/ &b%maxplayers%",
                                "%motd%",
                        }, -1),
                        new SignLayout("full", new String[]{
                                "%server%",
                                "%state%",
                                "&b %onlineplayers% &8/ &b%maxplayers%",
                                "%motd%"
                        }, -1),
                        new SignLayout("maintenance", new String[]{
                                "",
                                "maintenance",
                                "mode",
                                ""
                        }, -1),
                        new SignLayout("loading1", new String[]{
                                "",
                                "Server",
                                "loading.",
                                ""
                        }, -1),
                        new SignLayout("loading2", new String[]{
                                "",
                                "Server",
                                "loading..",
                                ""
                        }, -1),
                        new SignLayout("loading3", new String[]{
                                "",
                                "Server",
                                "loading...",
                                ""
                        }, -1),
                        new SignLayout("loading4", new String[]{
                                "",
                                "Server",
                                "loading",
                                ""
                        }, -1)
                ));
            document.saveAsConfig(layoutsFile);
            */

        if (!signsFile.exists())
        {
            new Document().saveAsConfig(signsFile);
        }

        /*
        layouts = Document.loadDocument(layoutsFile);
        */
        signs = Document.loadDocument(signsFile);
    }

    /*
    public java.util.Map<String, SignLayout> layouts()
    {
        HashMap<String, SignLayout> layoutHashMap = new HashMap<>();

        JsonArray jsonElements = layouts.getArray("layouts");

        for(JsonElement jsonElement : jsonElements)
        {
            SignLayout signLayout = NetworkUtils.GSON.fromJson(jsonElement, SignLayout.class);

            layoutHashMap.put(signLayout.getName(), signLayout);
        }

        return layoutHashMap;
    }
    */

    public java.util.Map<UUID, Sign> signs()
    {
        HashMap<UUID, Sign> signs = new HashMap<>();

        for(String key : this.signs.keys())
        {
            signs.put(UUID.fromString(key), this.signs.getObject(key, Sign.class));
        }

        return signs;

    }

    /*
    public void loadLayouts()
    {
        this.layouts = layouts.loadToExistingDocument(layoutsFile);
    }
    */

    public void saveAndReloadSignsJson()
    {
        this.signs.saveAsConfig(signsFile);
        this.signs.loadToExistingDocument(signsFile);
    }
}