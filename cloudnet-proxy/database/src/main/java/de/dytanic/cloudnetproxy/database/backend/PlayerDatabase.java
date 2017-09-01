package de.dytanic.cloudnetproxy.database.backend;

import de.dytanic.cloudnet.database.Database;
import de.dytanic.cloudnet.database.DatabaseDocument;
import de.dytanic.cloudnet.lib.document.Document;
import de.dytanic.cloudnet.permission.PermissionEntity;
import de.dytanic.cloudnet.permission.PermissionPool;
import de.dytanic.cloudnet.player.PlayerMetaData;
import de.dytanic.cloudnet.player.PlayerWhereAmI;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

/**
 * Created by Tareko on 01.07.2017.
 */
@Getter
@AllArgsConstructor
public class PlayerDatabase {

    private Database database;

    public PlayerDatabase registerPlayer(PlayerWhereAmI playerWhereAmI, PermissionPool permissionPool)
    {
        PlayerMetaData playerMetaData = new PlayerMetaData(playerWhereAmI.getUniqueId(), new Document(),
                playerWhereAmI.getName(),
                permissionPool.getNewPermissionEntity(playerWhereAmI));

        database.insert(new DatabaseDocument(playerWhereAmI.getUniqueId().toString()).append("playermetadata", playerMetaData));
        return this;
    }

    public PlayerDatabase updatePlayerMetaData(PlayerMetaData playerMetaData)
    {
        Document document = database.getDocument(playerMetaData.getUniqueId().toString());
        document.append("playermetadata", playerMetaData);
        database.insert(document);
        return this;
    }

    public PlayerDatabase updateName(UUID uuid, String name)
    {
        PlayerMetaData playerMetaData = getPlayer(uuid);
        playerMetaData.setName(name);
        updatePlayerMetaData(playerMetaData);
        return this;
    }

    public boolean containsPlayer(UUID uuid)
    {
        return database.containsDoc(uuid.toString());
    }

    public PlayerDatabase updatePermissionEntity(UUID uuid, PermissionEntity permissionEntity)
    {
        Document document = database.getDocument(uuid.toString());
        PlayerMetaData playerMetaData = document.getObject("playermetadata", PlayerMetaData.class);
        playerMetaData.setPermissionEntity(permissionEntity);
        document.append("playermetadata", playerMetaData);
        database.insert(document);

        System.out.println("Updating permissions from Player " + uuid.toString());

        return this;
    }

    public PlayerMetaData getPlayer(UUID uniqueId)
    {
        Document document = database.getDocument(uniqueId.toString());
        return document.getObject("playermetadata", PlayerMetaData.class);
    }
}