package de.dytanic.cloudnetserver.server;

import de.dytanic.cloudnet.lib.document.Document;
import de.dytanic.cloudnet.lib.interfaces.Executeable;
import de.dytanic.cloudnet.servergroup.ServerGroup;
import de.dytanic.cloudnet.servergroup.ServerGroupProfile;
import de.dytanic.cloudnet.servergroup.ServerMap;
import de.dytanic.cloudnetserver.server.screen.IScreen;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by Tareko on 24.05.2017.
 */
@Getter
@AllArgsConstructor
public abstract class MinecraftServer
                implements Executeable, IScreen{

    protected AtomicBoolean running = new AtomicBoolean(false);

    protected String serverId;
    protected UUID uniqueId;
    protected int port;
    protected ServerGroup group;
    protected volatile Process instance;
    protected Document metaData;
    protected Document properties;
    protected boolean priorityStop;
    protected ServerMap serverMap;
    protected int memory;
    protected boolean betaStatic;
    protected boolean hide;
    protected ServerGroupProfile profile;

    public boolean isAlive()
    {
        return instance != null && instance.isAlive();
    }

    @Override
    public String toString()
    {
        return "[" + serverId + "/uuid=" + uniqueId + "/group=" + group.getName() + "/address=" + group.getHostName() + ":" + port + "]";
    }

    public abstract void copy();
}