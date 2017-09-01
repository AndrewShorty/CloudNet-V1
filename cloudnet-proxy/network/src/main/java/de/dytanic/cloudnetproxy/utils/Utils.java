package de.dytanic.cloudnetproxy.utils;

import de.dytanic.cloudnet.lib.Acceptable;

import java.text.SimpleDateFormat;
import java.util.Collection;
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

    public static <T> void addAll(Collection<T> key, Collection<T> value)
    {
        for(T k : value)
        {
            key.add(k);
        }
    }

    public static <T, V> void addAll(java.util.Map<T, V> key, java.util.Map<T, V> value)
    {
        for(T key_ : value.keySet())
        {
            key.put(key_, value.get(key_));
        }
    }

    public static <T, V> void addAll(java.util.Map<T, V> key, java.util.Map<T, V> value, Acceptable<V> handle)
    {
        for(T key_ : value.keySet())
        {
            if(handle.isAccepted(value.get(key_)))
            key.put(key_, value.get(key_));
        }
    }
}