package com.taobao.pamirs.sync.event;

import java.util.Properties;

import net.sf.ehcache.event.CacheEventListener;
import net.sf.ehcache.event.CacheEventListenerFactory;

public class PamirsCacheEventFactory extends CacheEventListenerFactory
{

	@Override
	public CacheEventListener createCacheEventListener(final Properties arg0)
	{
		return new PamirsCacheEvent();
	}

}
