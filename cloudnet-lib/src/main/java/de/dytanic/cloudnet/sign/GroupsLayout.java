package de.dytanic.cloudnet.sign;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Collection;

/**
 * Created by Tareko on 27.07.2017.
 */
@Getter
@AllArgsConstructor
public class GroupsLayout {

    private String groupName;
    private Collection<SignLayout> layouts;

}