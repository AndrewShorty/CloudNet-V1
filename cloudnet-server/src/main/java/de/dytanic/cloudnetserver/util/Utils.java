package de.dytanic.cloudnetserver.util;

import java.text.SimpleDateFormat;
import java.util.Random;

/**
 * Created by Tareko on 25.05.2017.
 */
public final class Utils {

    public static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
    public static final Random RANDOM = new Random();

    public static boolean checkAsNumber(String input)
    {
        try{
            Short.parseShort(input);
            return true;
        }catch (Exception e)
        {
            return false;
        }
    }

}
