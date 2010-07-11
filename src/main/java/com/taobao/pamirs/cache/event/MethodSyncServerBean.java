package com.taobao.pamirs.cache.event;

import java.io.Serializable;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.jgroups.Channel;
import org.jgroups.ChannelException;

import com.taobao.pamirs.cache.store.Server;

public class MethodSyncServerBean implements Serializable
{
	private static final long	serialVersionUID	= 1L;
	private Server	          server;
	private Channel	          channel;

	public Channel getChannel()
	{
		return channel;
	}

	public void setChannel(final Channel channel) throws ChannelException
	{
		this.channel = channel;
		server = new Server(channel);
	}

	public MethodSyncServerBean()
	{

	}

	public Server getServerInstance()
	{
		return server;
	}

	@PostConstruct
	public void start()
	{
		try
		{
			server.start();
		} catch (final Exception e)
		{
			e.printStackTrace();
		}
	}

	@PreDestroy
	public void stop()
	{
		server.stop();
	}

	public Server getServer()
	{
		return server;
	}

}
