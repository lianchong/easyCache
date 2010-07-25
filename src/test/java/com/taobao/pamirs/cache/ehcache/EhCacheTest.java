package com.taobao.pamirs.cache.ehcache;

import java.util.HashMap;
import java.util.Map;

import net.sf.ehcache.Cache;
import net.sf.ehcache.Element;

import org.springframework.test.AbstractDependencyInjectionSpringContextTests;

import com.taobao.pamirs.cache.Cached;
import com.taobao.pamirs.cache.protocol.EthernetUtil;

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
		for (int i = 0; i < 0; ++i)
		{
			final Map m = new HashMap();
			m.put("[" + EthernetUtil.getNonLoopAddress() + "]: " + i, "[value] " + i);
			final Element element = new Element("[" + EthernetUtil.getNonLoopAddress() + "]: " + i,
			        "[value] "
			                + i);
			pamirsCache.put(element);
			System.out.println(pamirsCache.getKeys());
			Thread.sleep(1000);
		}
	}
	@Cached
	Map<String, String> map;
	
	public void testInterceptMap()
	{
		map = new HashMap<String, String>();
		map.put("key", "m");
		System.out.println(map.getClass());
	}

}
