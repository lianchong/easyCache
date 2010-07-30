package com.taobao.pamirs.cache.ehcache;

import java.util.HashMap;
import java.util.Map;

import net.sf.ehcache.Cache;
import net.sf.ehcache.Element;
import net.sf.ehcache.Statistics;

import org.springframework.test.AbstractDependencyInjectionSpringContextTests;

import com.taobao.pamirs.cache.helper.EthernetUtil;
import com.taobao.pamirs.commons.utils.BeanUtils;

public class EhCacheTest extends
        AbstractDependencyInjectionSpringContextTests
{

	public EhCacheTest()
	{
		setAutowireMode(AUTOWIRE_BY_NAME);
	}

	Cache	pamirsCache;

	public Cache getPamirsCache()
	{
		return pamirsCache;
	}

	public void setPamirsCache(final Cache pamirsCache)
	{
		this.pamirsCache = pamirsCache;
	}

	@Override
	protected String getConfigPath()
	{
		return "/pamirs-cache-config-test.xml";
	}

	public void testPut() throws Exception
	{
		for (int i = 0; i < 10; ++i)
		{
			final Map m = new HashMap();
			m.put("[" + EthernetUtil.getNonLoopAddress() + "]: " + i, "[value] " + i);
			final Element element = new Element("[" + EthernetUtil.getNonLoopAddress() + "]: " + i,
			        "[value] "
			                + i);
			pamirsCache.setStatisticsAccuracy(Statistics.STATISTICS_ACCURACY_BEST_EFFORT);
			pamirsCache.put(element);
			pamirsCache.get(element.getKey());

			System.out.println(pamirsCache.getKeys());
			Thread.sleep(1000);
		}

		System.out.println("取样数据0: "
		        + BeanUtils.describe(pamirsCache.getStatistics()));
		System.out.println("取样数据2: "
		        + BeanUtils.describe(pamirsCache.getLiveCacheStatistics().getPutCount()));
		System.out.println("取样数据3: "
		        + BeanUtils.describe(pamirsCache.isStatisticsEnabled()));

	}

	@com.taobao.pamirs.cache.annotation.Cached
	Map<String, String>	map;

	public void testInterceptMap()
	{
		map = new HashMap<String, String>();
		map.put("key", "m");
		System.out.println(map.getClass());
	}
}
