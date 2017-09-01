package de.dytanic.cloudnet.network;

import de.dytanic.cloudnet.lib.document.Document;
import de.dytanic.cloudnet.servergroup.ServerGroupProfile;
import de.dytanic.cloudnet.servergroup.ServerMap;
import de.dytanic.cloudnet.servergroup.ServerState;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;
import java.util.UUID;

/**
 * Created by Tareko on 24.05.2017.
 */
@Getter
@AllArgsConstructor
public class ServerInfo {

    private String name;
    private String group;
    private String host;
    private int port;
    private String cloudId;
    private UUID uniqueId;
    private boolean online;
    private List<String> players;
    private int memory;
    private ServerMap serverMap;
    private ServerGroupProfile profile;
    private long startUp;
    private String motd;
    private int onlineCount;
    private int maxPlayers;
    private String extra;
    private ServerState serverState;
    private Document properties;
    private boolean hided;

    public boolean isIngame()
    {
        return serverState == ServerState.INGAME || (motd.equalsIgnoreCase("INGAME") || motd.equalsIgnoreCase("RUNNING"));
    }

}