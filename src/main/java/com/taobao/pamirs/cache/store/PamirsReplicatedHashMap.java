package com.taobao.pamirs.cache.store;

import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import net.sf.ehcache.Cache;
import net.sf.ehcache.Element;

public class PamirsReplicatedHashMap<K extends Serializable, V extends Serializable> extends ConcurrentHashMap<K, V> {
	private static final long serialVersionUID = 1L;
	private Cache cache;
	public PamirsReplicatedHashMap() {
		super();
	}
	
	public PamirsReplicatedHashMap(Map<K, V> data, Cache cache)
	{
		super(data);
		this.cache = cache;
	}
	
	public V put(K key, V value)
	{
		return put(key, value, true);
	}
	
	public V put(K key, V value, boolean notifyAllCacheReplicators)
	{
		V v = super.put(key, value);
		if (notifyAllCacheReplicators)
		{
			cache.put(new Element(key, value));
		}
		return v; 
	}
	
	public V remove(Object key)
	{
		V v = super.remove(key);
		cache.remove(key);
		return v;
	}
	
	public Cache getCache()
	{
		return cache;
	}
	
}
