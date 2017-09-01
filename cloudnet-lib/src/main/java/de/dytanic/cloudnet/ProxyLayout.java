package de.dytanic.cloudnet;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * The dynamic proxy layout
 */
@AllArgsConstructor
@Getter
@Setter
public final class ProxyLayout
{
    private String prefix;
    private boolean disableLayoutFunction;
    private boolean disableMaintenanceFunction;
    private TabList tabList;
    private List<String> playerWhitelist;

    private Motd defaultMotd;
    private Motd maintenanceMotd;
    private boolean maintenance;
    private int maxOnlineCount;
    private boolean autoSlot;
    private boolean notifySystem;

    private String maintenanceDesign;
    private String fallback;
    private String maintenanceMessage;
    private String hubCommandMessage;
    private String alreadyOnHubMessage;
    private String serverFullMessage;
    private String fallbackNotFoundMessage;

    private CreateServerCommandProperties createServerCommandProperties;

    @Getter
    @AllArgsConstructor
    public static class CreateServerCommandProperties
    {
        private boolean enabled;
        private String listMessage;
        private String executeMessage;
    }


}