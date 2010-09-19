package com.taobao.pamirs.sync.spi;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;
import net.sf.ehcache.statistics.LiveCacheStatistics;

import org.jgroups.Address;
import org.jgroups.blocks.GroupRequest;
import org.jgroups.blocks.RpcDispatcher;
import org.jgroups.stack.IpAddress;
import org.jgroups.util.RspList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.taobao.pamirs.commons.dao.Query;
import com.taobao.pamirs.commons.dao.Result;
import com.taobao.pamirs.sync.aspect.CacheAspect;
import com.taobao.pamirs.sync.replicator.data.CacheElementData;
import com.taobao.pamirs.sync.replicator.data.CacheStatData;
import com.taobao.pamirs.sync.replicator.data.ExtendedChannel;

@Component
public class CacheStatFacade
{
	@Autowired
	private CacheManager	cacheManager;
	@Autowired
	private ExtendedChannel	serviceInvokeChannel;

	public List<CacheStatData> getCacheStat()
	{
		final List<CacheStatData> dataList = new ArrayList<CacheStatData>();
		for (final String cacheName : cacheManager.getCacheNames())
		{
			final Cache cache = cacheManager.getCache(cacheName);
			final LiveCacheStatistics stat = cache.getLiveCacheStatistics();
			final CacheStatData data = new CacheStatData();
			data.setCacheName(cache.getName());
			data.setSize(cache.getSize());
			data.setCacheHitCount(stat.getCacheHitCount());
			data.setCacheMissCount(stat.getCacheMissCount());
			data.setMaxGetTimeMillis(stat.getMaxGetTimeMillis());
			data.setPutCount(stat.getPutCount());
			data.setRemoveCount(stat.getRemovedCount());
			data.setInMemorySize(stat.getInMemorySize());
			data.setPolicy(cache.getMemoryStoreEvictionPolicy().getName());
			data.setRegister(CacheAspect.getCacheRegistry().get(cacheName));
			dataList.add(data);
		}

		return dataList;
	}

	public Result<CacheElementData> getKeys(final Query<CacheElementData> query)
	{
		final List<CacheElementData> dataList = new ArrayList<CacheElementData>();
		for (final String cacheName : cacheManager.getCacheNames())
		{
			final Cache cache = cacheManager.getCache(cacheName);
			final List keys = cache.getKeys();

			for (final Object key : keys)
			{
				final Element element = cache.getQuiet(key);
				final CacheElementData data = new CacheElementData();
				data.setCacheName(cacheName);
				data.setKey(String.valueOf(key));
				data.setHitCount(element.getHitCount());
				data.setSize(element.getSerializedSize());
				data.setValue(String.valueOf(element.getValue()));
				data.setUpdateTime(new Date(element.getLatestOfCreationAndUpdateTime()));
				dataList.add(data);
			}
		}

		final Result<CacheElementData> result = new Result<CacheElementData>();
		result.setDataList(dataList);
		result.setTotal(dataList.size());
		return result;
	}

	public Result<CacheElementData> getKeysByCache(final Query<String> query)
	{
		final List<CacheElementData> dataList = new ArrayList<CacheElementData>();
		final String cacheName = query.getQueryObject();
		final Cache cache = cacheManager.getCache(cacheName);
		final List keys = cache.getKeys();

		for (final Object key : keys)
		{
			final Element element = cache.getQuiet(key);
			final CacheElementData data = new CacheElementData();
			data.setCacheName(cacheName);
			data.setKey(String.valueOf(key));
			data.setHitCount(element.getHitCount());
			data.setSize(element.getSerializedSize());
			data.setValue(String.valueOf(element.getValue()));
			data.setUpdateTime(new Date(element.getLatestOfCreationAndUpdateTime()));
			dataList.add(data);
		}

		final Result<CacheElementData> result = new Result<CacheElementData>();
		result.setDataList(dataList);
		result.setTotal(dataList.size());
		return result;
	}

	public Result<CacheElementData> getRemoteKeysByHost(final String hostName, final String cacheName)
	{
		Result<CacheElementData> result = new Result<CacheElementData>();
		try
		{
			final RpcDispatcher dispatcher = new RpcDispatcher(serviceInvokeChannel, null, null, this);
			serviceInvokeChannel.connect("SERVICE_INVOKE_CHANNEL");
			final Query<String> query = new Query<String>();
			query.setQueryObject(cacheName);
			final RspList response = dispatcher.callRemoteMethods(filterMachine(hostName), "getKeysByCache",
			        new Object[]
			{ query }, new Class[]
			{ Query.class }, GroupRequest.GET_ALL, 0);

			result = (Result) response.getFirst();
			dispatcher.stop();
		} catch (final Exception e)
		{
			e.printStackTrace();
		}

		return result;
	}

	public Result<CacheElementData> getRemoteCacheNamesByHost(final String hostName)
	{
		Result<CacheElementData> result = new Result<CacheElementData>();
		try
		{
			final RpcDispatcher dispatcher = new RpcDispatcher(serviceInvokeChannel, null, null, this);
			serviceInvokeChannel.connect("SERVICE_INVOKE_CHANNEL");
			final RspList response = dispatcher.callRemoteMethods(filterMachine(hostName), "getCacheNames",
			        new Object[]
			{}, new Class[]
			{}, GroupRequest.GET_ALL, 0);

			result = (Result<CacheElementData>) response.getFirst();
			dispatcher.stop();
		} catch (final Exception e)
		{
			e.printStackTrace();
		}

		return result;
	}

	public Result<CacheElementData> getCacheNames()
	{
		final List<CacheElementData> caches = new ArrayList<CacheElementData>();
		for (final String cacheName : cacheManager.getCacheNames())
		{
			final CacheElementData data = new CacheElementData();
			data.setKey(cacheName);
			caches.add(data);
		}

		final Result<CacheElementData> result = new Result<CacheElementData>();
		result.setDataList(caches);

		return result;
	}

	/*
	 * 留下指定的成员
	 */
	private Vector<Address> filterMachine(final String address)
	{
		final Vector<Address> specAddress = new Vector<Address>();
		for (final Address addr : getClusterMembers())
		{
			final IpAddress ip = (IpAddress) addr;
			final String[] addresses = address.split(",");
			boolean flag = false;
			for (final String target : addresses)
			{
				if (target.trim().equals("") == false && ip.getIpAddress().toString().indexOf(target) >= 0)
				{
					flag = true;
					break;
				}
			}
			if (flag)
			{
				specAddress.add(addr);
			}
		}
		return specAddress;
	}

	public List<IpAddress> getClusterMembers()
	{
		final List<IpAddress> hosts = new ArrayList<IpAddress>();
		for (final Address addr : serviceInvokeChannel.getView().getMembers())
		{
			hosts.add((IpAddress) addr);
		}
		return hosts;
	}
}
