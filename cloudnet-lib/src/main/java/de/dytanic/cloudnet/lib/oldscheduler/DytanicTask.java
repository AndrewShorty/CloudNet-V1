package de.dytanic.cloudnet.lib.oldscheduler;

import lombok.Getter;

public class DytanicTask extends Thread{
	
	@Getter private Runnable task;
	@Getter private int taskId;
	@Getter private long delay;
	@Getter private long repeat;
	@Getter private DytanicScheduler scheduler;
	
	protected DytanicTask(Runnable run, int id, long delay, long repeat, DytanicScheduler scheduler) {
		this.task = run;
		this.taskId = id;
		this.delay = delay;
		this.repeat = repeat;
		this.scheduler = scheduler;
		start();
	}
	
	@Override
	public void run()
	{
		try
		{
			Thread.sleep(delay);
		} catch (InterruptedException e)
		{
			e.printStackTrace();
		}
		
		while(repeat != -1)
		{
			task.run();
			try
			{
				Thread.sleep(repeat);
			} catch (InterruptedException e)
			{
				e.printStackTrace();
			}
		}
		
		if(repeat == -1)
		{
			task.run();
		}
		
		scheduler.tasks.remove(taskId);
		scheduler.cancelTask(taskId);
	}

}
