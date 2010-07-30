package com.taobao.pamirs.cache.spi;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.distribution.CacheManagerPeerProvider;
import net.sf.ehcache.distribution.CachePeer;
import net.sf.ehcache.distribution.jgroups.JGroupManager;

import org.jgroups.Address;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.taobao.pamirs.cache.replicator.data.CacheManagerStatData;

@Component
public class CacheManagerStatFacade
{
	@Autowired
	private CacheManager	cacheManager;

	public CacheManagerStatData getStatus()
	{
		final CacheManagerStatData data = new CacheManagerStatData();
		data.setStatus(cacheManager.getStatus().toString());
		data.setName(cacheManager.getName());
		data.setRegisteredCaches(join(Arrays.asList(cacheManager.getCacheNames()), ","));
		data.setUuid(cacheManager.getClusterUUID());
		final StringBuilder builder = new StringBuilder();
		for (final String cacheName : cacheManager.getCacheNames())
		{
			final Cache cache = cacheManager.getCache(cacheName);
			final List<CachePeer> peerList = acquireCachePeers(cache);
			for (final CachePeer peer : peerList)
			{
				final JGroupManager jgm = (JGroupManager) peer;
				final List<Address> members = jgm.getBusMembership();
				for (final Address member : members)
				{
					builder.append(member.toString() + ";");
				}
			}
			break;
		}

		data.setHosts(builder.toString());
		return data;

	}

	public static <T>
	        String join(final Collection<T> objs, final String delimiter)
	{
		if ((objs == null) || objs.isEmpty())
		{
			return "";
		}
		final Iterator<T> iter = objs.iterator();
		// remove the following two lines, if you expect the Collection will
		// behave well
		if (!iter.hasNext())
		{
			return "";
		}
		final StringBuffer buffer = new StringBuffer(String.valueOf(iter.next()));
		while (iter.hasNext())
		{
			buffer.append(delimiter).append(String.valueOf(iter.next()));
		}
		return buffer.toString();
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
