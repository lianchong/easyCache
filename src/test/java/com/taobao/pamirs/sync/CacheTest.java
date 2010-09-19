package com.taobao.pamirs.sync;

import java.util.HashMap;
import java.util.Map;

import org.springframework.test.AbstractDependencyInjectionSpringContextTests;

import com.taobao.pamirs.sync.annotation.Cached;

public class CacheTest extends AbstractDependencyInjectionSpringContextTests
{

	@Override
	protected String getConfigPath()
	{
		return "/pamirs-cache-config-test.xml";
	}

	@Cached(CacheName = "Test Cache")
	private Map<String, String>	map;

	public void testAnnotatedCache()
	{
		map = new HashMap<String, String>();
		map.put("wa", "woo!");
	}

}
