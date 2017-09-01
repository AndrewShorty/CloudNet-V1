package de.dytanic.cloudnetserver.server.screen;

import lombok.Getter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

/**
 * Screen-Service
 * @author Tareko
 *
 */
@Getter
public final class ScreenService
{

	private IScreen screen;
	private Thread handled;
    
    public void joinNewScreen(IScreen screen)
    {
    	closeScreen();
			this.screen = screen;
			this.handled = new Thread(() -> {

				try
				{
					InputStreamReader in = new InputStreamReader(screen.getInstance().getInputStream(), StandardCharsets.UTF_8);
					BufferedReader reader = new BufferedReader(in);
					String input;
					while ((input = reader.readLine()) != null)
					{
						{
							System.out.println("[" + screen.getServerId() + "] " + input);
						}

					}

					this.handled = null;
					this.screen = null;

				} catch (IOException e)
				{
					closeScreen();
				}

			});
			handled.start();
    }
    
    public ScreenService closeScreen()
    {
    	if(this.handled != null)
    	{
    		this.handled.stop();
			this.handled = null;
    	}
    	if(this.screen != null) this.screen = null;
		return this;
    }
    
    public ScreenService checkAndRemove(IScreen screen)
    {
    	if(this.screen != null && this.screen.getServerId().equals(screen.getServerId()))
    	return closeScreen();
    	else
    	return this;
    }

    public void command(String command)
    {
    	if(screen != null) screen.runCommand(command);
    }
    
    public void shutdown()
    {
    	closeScreen();
    }
    
}