package de.dytanic.cloudnetproxy;

import de.dytanic.cloudnet.lib.threading.Runnabled;
import lombok.Getter;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by Tareko on 23.05.2017.
 */
public final class CloudNetServiceShutdown<V>
            extends Thread{

    private V key;

    @Getter
    private Queue<Runnabled<V>> tasks = new ConcurrentLinkedQueue<>();

    public CloudNetServiceShutdown(V key)
    {
        this.key = key;
    }

    @Override
    public void run()
    {
        for(Runnabled<V> runnabled : tasks)
        {
            runnabled.run(key);
        }
    }
}
