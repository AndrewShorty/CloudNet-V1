package de.dytanic.cloudnet;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Created by Tareko on 19.06.2017.
 */
@AllArgsConstructor
@Getter
public class ServerLayout {

    private String noPermissionToEnterServerMessage;
    private String signSendToServerMessage;

    private boolean signFullServerHide;

}