package com.taobao.pamirs.sync.replicator.data;

import java.io.Serializable;

import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;

public class PamirsCache
{
	private final Ehcache	cache;

	public PamirsCache(final Ehcache cache)
	{
		this.cache = cache;
	}

	public Element get(final Serializable key)
	{
		return cache.get(key);
	}

	public void put(final Element element)
	{
		cache.put(element);
	}

	public Element generateElement(final Serializable key, final Object value)
	{
		return new Element(key, value);
	}

	public Serializable getValue(final Element element)
	{
		return (Serializable) element.getObjectValue();
	}

	public void setTtl(final Element element, final int ttl)
	{
		element.setTimeToLive(ttl);
	}

}
