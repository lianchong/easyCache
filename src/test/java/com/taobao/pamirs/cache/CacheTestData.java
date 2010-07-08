package com.taobao.pamirs.cache;

import java.util.HashMap;
import java.util.Map;

import com.taobao.pamirs.cache.Cached;

public class CacheTestData
{

	@Cached
	Map	map	= new HashMap();

	public CacheTestData()
	{
		map.put("name", "1");
	}

	public Map getMap()
	{
		return map;
	}
}
