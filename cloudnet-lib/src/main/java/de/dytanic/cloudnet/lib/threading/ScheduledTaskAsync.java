package de.dytanic.cloudnet.lib.threading;

/**
 * Created by Tareko on 24.05.2017.
 */
public class ScheduledTaskAsync
        extends ScheduledTask{

    public ScheduledTaskAsync(long taskId, Runnable runnable, int delay, int repeatDelay)
    {
        super(taskId, runnable, delay, repeatDelay);
    }

    @Override
    protected boolean isAsync()
    {
        return true;
    }
}