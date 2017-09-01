package de.dytanic.cloudnetproxy.database.backend;

import com.google.gson.reflect.TypeToken;
import de.dytanic.cloudnet.lib.document.Document;
import de.dytanic.cloudnet.sign.GroupsLayout;
import de.dytanic.cloudnet.sign.SignLayout;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;

/**
 * Created by Tareko on 27.07.2017.
 */
public class SignGroupLayoutsBackend {

    public SignGroupLayoutsBackend()
    {
        if (!new File("signLayouts.json").exists())
        {
            new Document()
                    .append("layouts", Arrays.asList(
                            new GroupsLayout("default", Arrays.asList(
                                    new SignLayout("online", new String[]{
                                            "%server%",
                                            "&e%state%",
                                            "%motd%",
                                            "%onlineplayers%/%maxplayers%",
                                    }, -1),
                                    new SignLayout("full", new String[]{
                                            "%server%",
                                            "&6PREMIUM",
                                            "%motd%",
                                            "%onlineplayers%/%maxplayers%",
                                    }, -1),
                                    new SignLayout("maintenance", new String[]{
                                            "",
                                            "§cmaintenance",
                                            "§4mode",
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
                            )),
                            new GroupsLayout("Lobby", Arrays.asList(
                                    new SignLayout("online", new String[]{
                                            "%server%",
                                            "&e%state%",
                                            "&cLobby Server",
                                            "%onlineplayers%/%maxplayers%",
                                    }, -1),
                                    new SignLayout("full", new String[]{
                                            "%server%",
                                            "&6PREMIUM",
                                            "&cLobby Server",
                                            "%onlineplayers%/%maxplayers%",
                                    }, -1),
                                    new SignLayout("maintenance", new String[]{
                                            "",
                                            "§cmaintenance",
                                            "§4mode",
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
                            )))).saveAsConfig("signLayouts.json");
        }
    }

    public Collection<GroupsLayout> load()
    {
        return Document.loadDocument(new File("signLayouts.json")).getObject("layouts", new TypeToken<Collection<GroupsLayout>>(){}.getType());
    }

}