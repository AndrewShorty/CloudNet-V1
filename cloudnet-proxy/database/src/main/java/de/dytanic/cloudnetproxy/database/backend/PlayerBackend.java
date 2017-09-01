package de.dytanic.cloudnetproxy.database.backend;

import de.dytanic.cloudnet.lib.document.Document;
import de.dytanic.cloudnet.lib.threading.Callback;
import de.dytanic.cloudnet.permission.PermissionEntity;
import de.dytanic.cloudnet.permission.PermissionPool;
import de.dytanic.cloudnet.player.PlayerMetaData;
import de.dytanic.cloudnet.player.PlayerWhereAmI;
import lombok.Getter;

import java.io.File;
import java.util.UUID;

/**
 * Created by Tareko on 01.06.2017.
 */
@Getter
public final class PlayerBackend {

    private final File playersFile = new File("database/players.json");
    private final Document document = new Document();

    public PlayerBackend() throws Exception
    {
        if(!playersFile.exists())
        {
            playersFile.createNewFile();
            new Document().saveAsConfig(playersFile);
        }
        document.loadToExistingDocument(playersFile);
    }

    public void registerPlayer(PlayerWhereAmI playerWhereAmI, PermissionPool permissionPool)
    {
        PlayerMetaData playerMetaData = new PlayerMetaData(playerWhereAmI.getUniqueId(), new Document(),
                playerWhereAmI.getName(),
                permissionPool.getNewPermissionEntity(playerWhereAmI));
        document.append(playerWhereAmI.getUniqueId().toString(), playerMetaData).saveAsConfig(playersFile);
    }

    public void updatePlayerMetaData(PlayerMetaData playerMetaData)
    {
        document.append(playerMetaData.getUniqueId().toString(), playerMetaData).saveAsConfig(playersFile);
    }

    public void updatePermissionEntity(UUID uuid, PermissionEntity permissionEntity)
    {
        PlayerMetaData playerMetaData = getPlayer(uuid);
        playerMetaData.setPermissionEntity(permissionEntity);
        document.append(uuid.toString(), playerMetaData).saveAsConfig(playersFile);

        System.out.println("The permissions was updated from " + uuid);

    }

    public void updateName(UUID uuid, String newName)
    {
        PlayerMetaData playerMetaData = getPlayer(uuid);
        playerMetaData.setName(newName);
        document.append(uuid.toString(), playerMetaData).saveAsConfig(playersFile);
    }

    public boolean containsPlayer(UUID uniqueId)
    {
        return document.contains(uniqueId.toString());
    }

    public PlayerMetaData getPlayer(UUID uniqueId)
    {
        return document.getObject(uniqueId.toString(), PlayerMetaData.class);
    }
}