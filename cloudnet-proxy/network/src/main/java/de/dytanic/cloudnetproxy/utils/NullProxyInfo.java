package de.dytanic.cloudnetproxy.utils;

import de.dytanic.cloudnet.network.ProxyInfo;

import java.util.HashMap;
import java.util.UUID;

/**
 * Created by Tareko on 28.05.2017.
 */
public class NullProxyInfo
            extends ProxyInfo{

    public NullProxyInfo(String name, String host, int port, String cloudId, UUID uniqueId, int memory, String fallback)
    {
        super(name, host, port, cloudId, uniqueId, false, new HashMap<>(), memory, 0, fallback);
    }
}
