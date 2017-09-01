package de.dytanic.cloudnet;

import de.dytanic.cloudnet.network.CNSInfo;
import de.dytanic.cloudnet.network.ServerInfo;
import de.dytanic.cloudnet.network.SimpleProxyInfo;
import de.dytanic.cloudnet.permission.PermissionPool;
import de.dytanic.cloudnet.player.PlayerWhereAmI;
import de.dytanic.cloudnet.servergroup.SimpleServerGroup;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;
import java.util.*;

/**
 * Created by Tareko on 28.05.2017.
 */
@Data
@AllArgsConstructor
public class CloudNetwork
                implements Serializable {

    private java.util.Map<String, SimpleServerGroup> groups;
    private java.util.Map<String, ServerInfo> servers;
    private int onlineCount;
    private java.util.Map<String, PlayerWhereAmI> onlinePlayers;
    private java.util.Map<String, SimpleProxyInfo> proxys;
    private ArrayList<CNSInfo> cloudNetServers;
    private PermissionPool permissionPool;
    private ServerLayout serverLayout;
    private List<String> ipwhitelist;

}