package de.dytanic.cloudnet.player;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

/**
 *
 */
@AllArgsConstructor
@Getter
public class PlayerWhereAmI {

    private UUID uniqueId;
    private String ip;
    private String name;
    private String proxy;


    @Setter private String server;
    @Setter private PlayerMetaData playerMetaData;

}