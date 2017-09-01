package de.dytanic.cloudnet.logging.command;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Created by Tareko on 13.07.2017.
 */
@Getter
@AllArgsConstructor
public abstract class CommandArgument {

    private String name;

    public abstract void execute(Command command);

}