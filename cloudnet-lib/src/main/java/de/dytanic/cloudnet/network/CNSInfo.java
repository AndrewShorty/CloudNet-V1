package de.dytanic.cloudnet.network;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Created by Tareko on 29.06.2017.
 */
@Getter
@AllArgsConstructor
public class CNSInfo {

    private String serverId;
    private String hostName;
    private int availableProcessors;
    private int memory;

}