package com.taobao.pamirs.cache.event;

import java.io.Serializable;

import com.taobao.pamirs.cache.store.Server;

public class MethodSyncServerBean implements Serializable
{
	private static final long	serialVersionUID	= 1L;
	private final Server	  server;

	public MethodSyncServerBean()
	{
		server = new Server("t.xml");
		try
		{
			server.start();
			System.out.println("Hey!" + server.info());
		} catch (final Exception e)
		{
			e.printStackTrace();
		}
	}

	public Server getServerInstance()
	{
		return server;
	}
}
