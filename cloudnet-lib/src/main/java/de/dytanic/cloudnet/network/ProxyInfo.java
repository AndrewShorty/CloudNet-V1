package de.dytanic.cloudnet.network;

import de.dytanic.cloudnet.player.PlayerWhereAmI;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

/**
 * Created by Tareko on 25.05.2017.
 */
@AllArgsConstructor
@Getter
public class ProxyInfo {

    private String name;
    private String host;
    private int port;
    private String cloudId;
    private UUID uniqueId;
    private boolean online;
    private HashMap<String, PlayerWhereAmI> players;
    private int memory;
    private int onlineCount;
    private String fallback;

    public SimpleProxyInfo toSimple()
    {
        return new SimpleProxyInfo(online, name, uniqueId, host, port, memory, onlineCount);
    }

}