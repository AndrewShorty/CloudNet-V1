package de.dytanic.cloudnet.sign;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Created by Tareko on 26.05.2017.
 */
@AllArgsConstructor
@Getter
public class SignPosition {

    private String group;
    private String world;
    private double x;
    private double y;
    private double z;

    @Override
    public boolean equals(Object obj)
    {

        if(!(obj instanceof SignPosition)) return false;
        SignPosition signPosition = (SignPosition)obj;

        if(signPosition.x == x
                && signPosition.y == y &&
                signPosition.z == z && signPosition.world.equals(world) &&
                signPosition.group.equals(group))
        {
            return true;
        }

        return false;
    }
}
