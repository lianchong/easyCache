package com.taobao.pamirs.cache.ehcache;

import java.util.List;
import java.util.UUID;

import net.sf.ehcache.Cache;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;
import net.sf.ehcache.distribution.CacheManagerPeerProvider;

import org.springframework.test.AbstractDependencyInjectionSpringContextTests;

import com.taobao.pamirs.cache.helper.EthernetUtil;

public class ServiceInvokeTest extends
        AbstractDependencyInjectionSpringContextTests
{
	Cache	serviceInvokeTunnel;

	public Cache getServiceInvokeTunnel()
	{
		return serviceInvokeTunnel;
	}

	public void setServiceInvokeTunnel(final Cache serviceInvokeTunnel)
	{
		this.serviceInvokeTunnel = serviceInvokeTunnel;
	}

	public ServiceInvokeTest()
	{
		setAutowireMode(AUTOWIRE_BY_NAME);
	}

	@Override
	protected String getConfigPath()
	{
		return "/pamirs-cache-config-test.xml";
	}

	public void testSitEvent() throws Exception
	{
		for (int i = 0; i < 1; ++i)
		{
			serviceInvokeTunnel.put(new Element(UUID.randomUUID().toString(), new Object[]
			{ "java.lang.String", "valueOf", EthernetUtil.getNonLoopAddress() }));
			Thread.sleep(800);
		}
	}

	protected List acquireCachePeers(final Ehcache cache)
	{

		long timeForClusterToForm = 0;
		final CacheManagerPeerProvider cacheManagerPeerProvider = cache.getCacheManager()
		        .getCacheManagerPeerProvider("JGroups");
		if (cacheManagerPeerProvider != null)
		{
			timeForClusterToForm = cacheManagerPeerProvider.getTimeForClusterToForm();
		}

		List cachePeers = null;
		for (int i = 0; i <= timeForClusterToForm; i = i + 1000)
		{
			cachePeers = listRemoteCachePeers(cache);
			/*
			 * if (cachePeers == null) { break; } if (cachePeers.size() > 0) {
			 * break; }
			 */
			try
			{
				Thread.sleep(1000);
			} catch (final InterruptedException e)
			{
			}
		}

		return cachePeers;
	}

	protected List listRemoteCachePeers(final Ehcache cache)
	{
		final CacheManagerPeerProvider provider = cache.getCacheManager().getCacheManagerPeerProvider(
		        "JGroups");
		if (provider == null)
		{
			return null;
		} else
		{
			return provider.listRemoteCachePeers(cache);
		}

	}

}
