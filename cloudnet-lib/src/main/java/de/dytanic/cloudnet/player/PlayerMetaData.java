package de.dytanic.cloudnet.player;

import de.dytanic.cloudnet.lib.document.Document;
import de.dytanic.cloudnet.permission.PermissionEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import java.util.UUID;

/**
 * Created by Tareko on 01.06.2017.
 */
@AllArgsConstructor
@Getter
public class PlayerMetaData {

    private UUID uniqueId;
    private Document meta;

    @Setter private String name;
    @Setter private PermissionEntity permissionEntity;
}