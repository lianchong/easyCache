package com.taobao.pamirs.cache.event;

import java.lang.reflect.InvocationTargetException;

import net.sf.ehcache.CacheException;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;
import net.sf.ehcache.event.CacheEventListener;

import org.apache.commons.lang.reflect.MethodUtils;

public class PamirsCacheEvent implements CacheEventListener
{
	@Override
	public Object clone()
	{
		return null;
	}

	@Override
	public void dispose()
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void notifyElementEvicted(final Ehcache cache, final Element element)
	{
		System.out.println("PAMIRS_CACHE_EVENT: EVICTION" + cache + element);
	}

	@Override
	public void notifyElementExpired(final Ehcache cache, final Element element)
	{
		System.out.println("PAMIRS_CACHE_EVENT: EXPIRATION" + cache + element);
	}

	@Override
	public void notifyElementPut(final Ehcache cache, final Element element) throws CacheException
	{
		// System.out.println("PAMIRS_CACHE_EVENT: PUT" + cache + element);
		try
		{
			final Object[] oa = (Object[]) element.getValue();
			final String className = (String) oa[0];
			final String methodName = (String) oa[1];
			final Object params = oa[2];
			final Object result = MethodUtils.invokeMethod(Class.forName(className)
			        .newInstance(),
			        methodName, params);
			System.out.println(result);
		} catch (final NoSuchMethodException e)
		{
			e.printStackTrace();
		} catch (final IllegalAccessException e)
		{
			e.printStackTrace();
		} catch (final InvocationTargetException e)
		{
			e.printStackTrace();
		} catch (final ClassNotFoundException e)
		{
			e.printStackTrace();
		} catch (final InstantiationException e)
		{
			e.printStackTrace();
		}
	}

	@Override
	public void notifyElementRemoved(final Ehcache cache, final Element element) throws CacheException
	{
		// System.out.println("PAMIRS_CACHE_EVENT: REMOVE" + cache + element);
	}

	@Override
	public void notifyElementUpdated(final Ehcache cache, final Element element) throws CacheException
	{
		// System.out.println("PAMIRS_CACHE_EVENT: UPDATE" + cache + element);
	}

	@Override
	public void notifyRemoveAll(final Ehcache cache)
	{
		// TODO Auto-generated method stub

	}

}
