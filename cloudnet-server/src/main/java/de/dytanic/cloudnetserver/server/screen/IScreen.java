package de.dytanic.cloudnetserver.server.screen;

import de.dytanic.cloudnetserver.server.Commandable;

/**
 * Created by Tareko on 12.12.2016.
 */
public interface IScreen
			extends Commandable
{	
	String getServerId();
	Process getInstance();
}