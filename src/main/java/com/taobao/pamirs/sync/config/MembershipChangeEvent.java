// //////////////////////////////////////////////////////////////////////////
//
// Copyright (c) 2010, Taobao. All rights reserved.
//
// //////////////////////////////////////////////////////////////////////////

package com.taobao.pamirs.sync.config;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.jgroups.protocols.TCPPING;
import org.jgroups.stack.IpAddress;
import org.springframework.beans.factory.annotation.Autowired;

import com.taobao.config.client.SubscriberDataObserver;
import com.taobao.pamirs.sync.replicator.data.ExtendedChannel;

/**
 * 成员变化事件
 */
public class MembershipChangeEvent implements SubscriberDataObserver
{
	@Autowired
	private ExtendedChannel	serviceInvokeChannel;

	public void setServiceInvokeChannel(final ExtendedChannel serviceInvokeChannel)
	{
		this.serviceInvokeChannel = serviceInvokeChannel;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.taobao.config.client.SubscriberDataObserver#handleData(java.lang.
	 * String, java.util.List)
	 */
	@Override
	synchronized public void handleData(final String dataId, final List<Object> data)
	{
		TCPPING ping = (TCPPING) serviceInvokeChannel.getProtocolStack().findProtocol(TCPPING.class);
		ping.getInitialHosts().clear();
		for (final Object ip : new ArrayList<Object>(new HashSet<Object>(data)))
		{
			try
			{
				ping.getInitialHosts().add(new IpAddress((String) ip, 7800));
			} catch (final UnknownHostException e)
			{
				e.printStackTrace();
			}
		}

		ping = (TCPPING) serviceInvokeChannel.getProtocolStack().findProtocol(TCPPING.class);
		System.out.println("current hosts: " + ping.getInitialHosts());
	}
}
