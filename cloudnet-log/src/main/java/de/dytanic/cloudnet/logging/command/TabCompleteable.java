package de.dytanic.cloudnet.logging.command;

import java.util.List;

/**
 * Created by Tareko on 23.05.2017.
 */
public interface TabCompleteable {

    List<String> onTab(long argsLength, String lastWord);

}