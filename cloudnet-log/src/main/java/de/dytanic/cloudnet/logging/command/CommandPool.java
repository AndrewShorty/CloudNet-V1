package de.dytanic.cloudnet.logging.command;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import jline.console.completer.Completer;

public final class CommandPool
        implements Completer
{

    private final java.util.Map<String, Command> commands = new ConcurrentHashMap<>();
    private final CommandSenderCloudNet administrator = new CommandSenderCloudNet();

    private CommandPool() {}

    private static CommandPool commandPool = new CommandPool();

    public static CommandPool newCommandPoolHandler()
    {
        return commandPool;
    }

    public CommandPool clearCommands()
    {
        commands.clear();
        return this;
    }

    public CommandPool registerCommand(Command command)
    {
        if (command == null) return this;

        this.commands.put(command.getName().toLowerCase(), command);

        if (command.getAliases().length != 0)
        {
            for (String aliases : command.getAliases())
            {
                commands.put(aliases.toLowerCase(), command);
            }
        }

        return this;
    }

    public Set<String> getCommands()
    {
        return commands.keySet();
    }

    public Command getCommand(String name)
    {
        return commands.get(name.toLowerCase());
    }

    public boolean dispatchCommand(CommandSender sender, String command)
    {
        String[] a = command.split(" ");
        if(this.commands.containsKey(a[0].toLowerCase()))
        {
            String b = command.replace((command.contains(" ") ? command.split(" ")[0] + " " : command), "");
            try
            {
                if(b.equals(""))
                {
                    this.commands.get(a[0].toLowerCase()).onExecuteCommand(sender, new String[0]);
                }else{
                    String[] c = b.split(" ");
                    this.commands.get(a[0].toLowerCase()).onExecuteCommand(sender, c);
                }
            }catch
                    (Exception ex)
            {
                ex.printStackTrace();
            }
            return true;
        }
        else
        {
            return false;
        }
    }

    public boolean dispatchCommand(String command)
    {
        String[] a = command.split(" ");
        if(this.commands.containsKey(a[0].toLowerCase()))
        {
            String b = command.replace((command.contains(" ") ? command.split(" ")[0] + " " : command), "");
            try
            {
                if(b.equals(""))
                {
                    this.commands.get(a[0].toLowerCase()).onExecuteCommand(administrator, new String[0]);
                }else{
                    String[] c = b.split(" ");
                    this.commands.get(a[0].toLowerCase()).onExecuteCommand(administrator, c);
                }

                for(String argument : a)
                {
                    for(CommandArgument argumenents : this.commands.get(a[0]).getCommandArguments())
                    {
                        if(argumenents.getName().equalsIgnoreCase(argument)) argumenents.execute(this.commands.get(a[0]));
                    }
                }

            }catch
                    (Exception ex)
            {
                ex.printStackTrace();
            }
            return true;
        }
        else
        {
            return false;
        }
    }

    @Override
    public int complete(String buffer, int cursor, List<CharSequence> candidates)
    {
        String[] input = buffer.split(" ");

        if(input.length == 0) return cursor;

        Command command = getCommand(input[0].toLowerCase());
        if(command != null && command instanceof TabCompleteable)
        {
            List<String> tabCompletions = ((TabCompleteable)command).onTab((input.length - 1), input[input.length - 1]);

            for(String t : tabCompletions)
            {
                candidates.add(t);
            }

            final int lastSpace = buffer.lastIndexOf( ' ' );
            if ( lastSpace == -1 )
            {
                return cursor - buffer.length();
            } else
            {
                return cursor - ( buffer.length() - lastSpace - 1 );
            }
        }
        return cursor;
    }
}
