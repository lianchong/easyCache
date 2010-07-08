package com.taobao.pamirs.cache.event;

import net.sf.ehcache.CacheException;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;
import net.sf.ehcache.event.CacheEventListener;

public class PamirsCacheEvent implements CacheEventListener
{
	@Override
	public Object clone()
	{
		return null;
	}

	public void dispose()
	{
		// TODO Auto-generated method stub

	}

	public void notifyElementEvicted(final Ehcache arg0, final Element arg1)
	{
		// TODO Auto-generated method stub

	}

	public void notifyElementExpired(final Ehcache arg0, final Element arg1)
	{
		// TODO Auto-generated method stub

	}

	public void notifyElementPut(final Ehcache arg0, final Element arg1) throws CacheException
	{
		// TODO Auto-generated method stub

	}

	public void notifyElementRemoved(final Ehcache arg0, final Element arg1) throws CacheException
	{
		// TODO Auto-generated method stub

	}

	public void notifyElementUpdated(final Ehcache arg0, final Element arg1) throws CacheException
	{
		// TODO Auto-generated method stub

	}

	public void notifyRemoveAll(final Ehcache arg0)
	{
		// TODO Auto-generated method stub

	}

}
