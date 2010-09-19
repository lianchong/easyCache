// //////////////////////////////////////////////////////////////////////////
//
// Copyright (c) 2010, Taobao. All rights reserved.
//
// //////////////////////////////////////////////////////////////////////////

package com.taobao.pamirs.sync.config;

import java.util.Arrays;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import com.taobao.config.client.Publisher;
import com.taobao.config.client.PublisherRegistrar;
import com.taobao.config.client.PublisherRegistration;
import com.taobao.config.client.Subscriber;
import com.taobao.config.client.SubscriberRegistrar;
import com.taobao.config.client.SubscriberRegistration;
import com.taobao.pamirs.sync.helper.EthernetUtil;
import com.taobao.pamirs.sync.replicator.data.ExtendedChannel;

/**
 * 
 */
public class MembershipConfiger
{
	static String	        publication	= "com.taobao.pamirs.sync";
	private ExtendedChannel	serviceInvokeChannel;

	public void setServiceInvokeChannel(final ExtendedChannel serviceInvokeChannel)
	{

		this.serviceInvokeChannel = serviceInvokeChannel;
	}

	public void publish()
	{
		final String publisherName = "Sync Client"; // Identity of
		// publisher
		final PublisherRegistration<String> registration // Registration
		                                                 // of publisher
		= new PublisherRegistration<String>(publisherName, publication);
		final Publisher<String> publisher = PublisherRegistrar.register(registration);
		// Do register
		publisher.publishAll(Arrays.asList(EthernetUtil.getNonLoopAddress().split(","))); // Do
		                                                                                  // publish
		// now
	}

	public MembershipConfiger()
	{

	}

	@PostConstruct
	public void init()
	{
		// publish local ip to the club
		publish();

		// subscirbe latest membership
		subscribe();
	}

	public void subscribe()
	{
		final String subscriberName = "sync client"; // Identity of subscriber
		final SubscriberRegistration registration = new SubscriberRegistration(subscriberName, publication);
		// Registration of subscriber
		final Subscriber subscriber = SubscriberRegistrar.register(registration);
		// Do register
		final MembershipChangeEvent event = new MembershipChangeEvent();
		event.setServiceInvokeChannel(serviceInvokeChannel);
		subscriber.setDataObserver(event); // Get
		                                   // notified
		// upon
		// new publication
	}

	@PreDestroy
	public void off()
	{

	}
}
