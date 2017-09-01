package de.dytanic.cloudnetserver.util;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Created by Tareko on 25.05.2017.
 */
@AllArgsConstructor
@Getter
public class ProxyConfig {

    private int startPort;
    private String fallback;
    private int startup;
    private int memory;
    private boolean staticProxyMode;

}