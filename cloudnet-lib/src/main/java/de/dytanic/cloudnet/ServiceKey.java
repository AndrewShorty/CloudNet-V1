package de.dytanic.cloudnet;
import java.util.Random;

/**
 * Created by Tareko on 19.06.2017.
 */
public final class ServiceKey {

    public String newKey()
    {
        Random random = new Random();
        char[] values = new char[]{
                'A','B','C','D','E','F','G','H','I'
                ,'J', '1', '2', '3', '4', '5',  '6',
                '7', '8', '9', '0','K','L','M', 'N',
                'O','P', 'Q', 'R','S','T','U','V','W',
                'X','Y','Z'};

        StringBuilder builder = new StringBuilder();
        for(short i = 1;  i < 20; i++)
        {
            //ABCD-EFGH-IJKL-MNOP
            if(i == 5 || i == 10 || i == 15)
            {
                builder.append("-");
            }
            else
            {
                builder.append(values[random.nextInt(values.length)]);
            }
        }
        return builder.substring(0);
    }

}