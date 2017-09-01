package de.dytanic.cloudnet.permission;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Tareko on 01.06.2017.
 */
@AllArgsConstructor
@Getter
public enum PermissionDefault {

    ADMINISTRATOR(Arrays.asList("*")),
    DEVELOPER(Arrays.asList("cloudnet.maintenance.join", "cloudnet.joinfull", "cloudnet.notify")),
    TEAM_MEMBER(Arrays.asList("cloudnet.maintenance.join", "cloudnet.joinfull")),
    DEV(Arrays.asList("cloudnet.maintenance.join", "cloudnet.joinfull", "cloudnet.notify")),
    USER(new LinkedList<>());

    private List<String> permissions;

}
