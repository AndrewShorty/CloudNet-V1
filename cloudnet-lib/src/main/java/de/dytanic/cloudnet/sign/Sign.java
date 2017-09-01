package de.dytanic.cloudnet.sign;

import de.dytanic.cloudnet.network.ServerInfo;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

/**
 * Created by Tareko on 03.06.2017.
 */
@AllArgsConstructor
@Getter
public class Sign {

    private UUID uniqueId;
    private String targetGroup;
    private SignPosition position;

    @Setter
    private volatile ServerInfo serverInfo;
}