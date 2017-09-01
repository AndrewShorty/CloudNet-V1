package de.dytanic.cloudnetserver.setup;

import de.dytanic.cloudnet.RunnabledCall;
import jline.console.ConsoleReader;

/**
 * Created by Tareko on 22.06.2017.
 */
public class SetupLanguage
        implements RunnabledCall<ConsoleReader, String> {

    @Override
    public String call(ConsoleReader value)
    {
        String input = null;
        try
        {
            System.out.println("Select a language \"german\", \"english\", \"french\"");

            while (true)
            {
                input = value.readLine();

                switch (input.toLowerCase())
                {
                    case "german":
                        System.out.println("Du hast die Sprache \"GERMAN\" ausgewählt");
                        return "german";
                    case "french":
                        System.out.println("Vous avez sélectionné la langue \"FRANÇAIS\"");
                        return "french";
                    case "english":
                        System.out.println("I chose the language \"ENGLISH\"");
                        return "english";
                    default:
                        System.out.println("Invalid language");
                        continue;
                }
            }

        }catch (Exception ex)
        {
            ex.printStackTrace();
        }

        return input;

    }
}
