package de.dytanic.cloudnetproxy.utils;

import de.dytanic.cloudnet.lib.document.Document;
import de.dytanic.cloudnet.network.ServerInfo;
import de.dytanic.cloudnet.servergroup.ServerGroupProfile;
import de.dytanic.cloudnet.servergroup.ServerMap;
import de.dytanic.cloudnet.servergroup.ServerState;

import java.util.ArrayList;
import java.util.UUID;

/**
 * Created by Tareko on 27.05.2017.
 */
public class NullServerInfo
            extends ServerInfo{

    public NullServerInfo(String serverId, String group, String host, int port, String cloudId, UUID uuid, ServerMap map, ServerGroupProfile profile, boolean hide)
    {
        super(serverId,group, host, port, cloudId, uuid, false, new ArrayList<>(), 512, map, profile,
                4, "Null", 0, 0, "null", ServerState.OFFLINE, new Document(), hide);
    }
}
