package com.taobao.pamirs.cache.spi;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;
import net.sf.ehcache.statistics.LiveCacheStatistics;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.taobao.pamirs.cache.aspect.CacheAspect;
import com.taobao.pamirs.cache.replicator.data.CacheElementData;
import com.taobao.pamirs.cache.replicator.data.CacheStatData;
import com.taobao.pamirs.commons.dao.Query;
import com.taobao.pamirs.commons.dao.Result;

@Component
public class CacheStatFacade
{
	@Autowired
	private CacheManager	cacheManager;

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
				final Element element = cache.get(key);
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
}
