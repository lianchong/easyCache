// //////////////////////////////////////////////////////////////////////////
//
// Copyright (c) 2010, Taobao. All rights reserved.
//
// //////////////////////////////////////////////////////////////////////////

package com.taobao.pamirs.sync.config;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import junit.framework.TestCase;
import net.sf.ehcache.config.CacheConfiguration;
import net.sf.ehcache.config.Configuration;

import com.taobao.config.client.Publisher;
import com.taobao.config.client.PublisherRegistrar;
import com.taobao.config.client.PublisherRegistration;
import com.taobao.config.client.Subscriber;
import com.taobao.config.client.SubscriberDataObserver;
import com.taobao.config.client.SubscriberRegistrar;
import com.taobao.config.client.SubscriberRegistration;
import com.taobao.pamirs.sync.helper.EthernetUtil;

/**
 * 
 */
public class ConfigurationTest extends TestCase
{
	public void testMultiplecastConfig()
	{
		final Configuration config = new EhCacheConfigurator().multiple_suck("MyCache");
		assertNotNull(config);
	}

	public void testTcpConfig()
	{
		final Configuration config = new EhCacheConfigurator().suck(EthernetUtil.getNonLoopAddress(),
		        "MyCache");
		assertNotNull(config);
	}

	public void testGenerateCacheConfig()
	{
		final CacheConfiguration config = new EhCacheConfigurator().generateDefinedCacheConfig("MyCache");
		assertNotNull(config);
	}

	static String	publication	= "com.taobao.pamirs.sync";

	public void publish()
	{
		final String publisherName = "App1"; // Identity of
		                                     // publisher
		final PublisherRegistration<String> registration // Registration
		                                                 // of publisher
		= new PublisherRegistration<String>(publisherName, publication);
		final Publisher<String> publisher = PublisherRegistrar.register(registration);
		// Do register
		publisher.publish(EthernetUtil.getNonLoopAddress()); // Do publish
		// now
	}

	public void subscribe()
	{
		final String subscriberName = "sync client"; // Identity of subscriber
		final SubscriberRegistration registration = new SubscriberRegistration(subscriberName, publication);
		// Registration of subscriber
		final Subscriber subscriber = SubscriberRegistrar.register(registration);
		// Do register
		subscriber.setDataObserver(new NewIssueNotifier()); // Get notified upon
		                                                    // new publication
	}

	class PlayboyMagzine implements Serializable
	{

		public PlayboyMagzine(final String issue)
		{
		}

		private static final long	serialVersionUID	= 1L;
	}

	class NewIssueNotifier implements SubscriberDataObserver
	{

		public void handleData(final String dataId, final List<Object> data)
		{
			System.out.println("New issue(s) arrived: " + data.get(data.size() - 1));
		}
	}

	public void testConfigServerClient() throws InterruptedException
	{
		publish();
		subscribe();
		for (int i = 0; i < 10; ++i)
		{
			publish();
			Thread.sleep(1000);
		}

		final List<String> l = Arrays.asList("1", "1", "2");
		System.out.println(new ArrayList<Object>(new HashSet<Object>(l)));
	}

}
