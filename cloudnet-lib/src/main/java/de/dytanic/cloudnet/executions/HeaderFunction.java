package de.dytanic.cloudnet.executions;

import lombok.Getter;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.function.Consumer;
import java.util.stream.Stream;

/**
 * Created by Tareko on 10.07.2017.
 */
@Getter
public class HeaderFunction {

    private boolean executed = false;

    public HeaderFunction()
    {
        File file = new File("HEADER.txt");
        if(file.exists())
        {
            executed = true;
            try(InputStreamReader inputStreamReader = new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader)){

                String input;
                while ((input = bufferedReader.readLine()) != null)
                {
                    System.out.println(input);
                }

            } catch (FileNotFoundException e)
            {
                e.printStackTrace();
            } catch (IOException e)
            {
                e.printStackTrace();
            }
            System.out.println();
        }
    }
}