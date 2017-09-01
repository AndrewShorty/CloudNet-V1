package de.dytanic.cloudnet.lib.oldscheduler;

import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

public class DytanicScheduler {
	
	protected final ConcurrentHashMap<Integer, DytanicTask> tasks;
	protected final Random random;
	
	public DytanicScheduler() {
		tasks = new ConcurrentHashMap<>();
		random = new Random();
	}
	
	public int scheduleAsync(Runnable run)
	{
		int taskid = random.nextInt(Integer.MAX_VALUE);
		tasks.put(taskid, new DytanicTask(run, taskid, 0, -1, this));
		return taskid;
	}
	
	public int scheduleAsyncDelay(Runnable run, long delay)
	{
		int taskid = random.nextInt(Integer.MAX_VALUE);
		tasks.put(taskid, new DytanicTask(run, taskid, delay, -1, this));
		return taskid;
	}
	
	public int scheduleAsyncWhile(Runnable run, long delay, long repeat)
	{
		int taskid = random.nextInt(Integer.MAX_VALUE);
		tasks.put(taskid, new DytanicTask(run, taskid, delay, repeat, this));
		return taskid;
	}
	
	public int getCount()
	{
	    return tasks.size();
	}
	
	@SuppressWarnings("deprecation")
	public void cancelTask(int id)
	{
		if(tasks.containsKey(id))
		{
			tasks.get(id).stop();
			tasks.remove(id);
		}
	}
	
	public void cancelAllTasks()
	{
		for(int task : tasks.keySet())
		{
			cancelTask(task);
		}
	}
	
}
