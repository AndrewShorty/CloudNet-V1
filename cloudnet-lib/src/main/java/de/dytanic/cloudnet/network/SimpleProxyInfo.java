package de.dytanic.cloudnet.network;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

/**
 * Created by Tareko on 02.07.2017.
 */
@Getter
@AllArgsConstructor
public class SimpleProxyInfo {

    private boolean online;
    private String name;
    private UUID uniqueId;
    private String hostName;
    private int port;
    private int memory;
    private int onlineCount;

}