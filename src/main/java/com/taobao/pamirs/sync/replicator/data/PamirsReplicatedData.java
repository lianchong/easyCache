package com.taobao.pamirs.sync.replicator.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sf.ehcache.Cache;
import net.sf.ehcache.Element;

public class PamirsReplicatedData<K extends Serializable, V extends Serializable> implements
        java.util.Map<K, V>
{
	private static final long	serialVersionUID	= 1L;
	private Cache	          cache;

	public PamirsReplicatedData()
	{
		super();
	}

	public PamirsReplicatedData(final Map<K, V> data, final Cache cache)
	{
		this.cache = cache;
		for (final K key : data.keySet())
		{
			final Element element = new Element(key, data.get(key));
			cache.put(element);
		}
	}

	@Override
	public V put(final K key, final V value)
	{
		put(key, value, true);
		return value;
	}

	public V put(final K key, final V value, final boolean notifyAllCacheReplicators)
	{
		cache.remove(key);
		cache.put(new Element(key, value), !notifyAllCacheReplicators);
		return value;
	}

	public void putQuiet(final K key, final V value)
	{
		removeQuiet(key);
		cache.putQuiet(new Element(key, value));
	}

	public void removeQuiet(final K key)
	{
		cache.removeQuiet(key);
	}

	/**
	 * @param key
	 * @return
	 *         true if the element was removed, false if it was not found in
	 *         the cache
	 */
	@Override
	public V remove(final Object key)
	{
		cache.remove(key);

		return null;
	}

	@Override
	public V get(final Object key)
	{
		if (cache.get(key) != null)
		{
			return (V) cache.get(key).getValue();
		} else
		{
			return null;
		}
	}

	public Cache getCache()
	{
		return cache;
	}

	@Override
	public void clear()
	{
		cache.removeAll();
	}

	@Override
	public boolean containsKey(final Object key)
	{
		return cache.getQuiet(key) != null;
	}

	@Override
	public boolean containsValue(final Object value)
	{
		return cache.isValueInCache(value);
	}

	@Override
	public Set<java.util.Map.Entry<K, V>> entrySet()
	{
		final Set<java.util.Map.Entry<K, V>> entries = new HashSet<java.util.Map.Entry<K, V>>();
		for (final Object key : cache.getKeys())
		{
			final java.util.Map.Entry<K, V> entry = new Entry<K, V>()
			{

				@Override
				public K getKey()
				{
					return (K) key;
				}

				@Override
				public V getValue()
				{
					return (V) cache.get(key).getValue();
				}

				@Override
				public V setValue(final V value)
				{
					return null;
				}
			};

			entries.add(entry);
		}

		return entries;
	}

	@Override
	public boolean isEmpty()
	{
		return cache.getSize() == 0;
	}

	@Override
	public Set<K> keySet()
	{
		return new HashSet<K>(cache.getKeys());
	}

	@Override
	public void putAll(final Map<? extends K, ? extends V> m)
	{
		for (final K key : m.keySet())
		{
			cache.put(new Element(key, m.get(key)));
		}
	}

	public void putIfAbsent(final K key, final V value)
	{
		cache.putIfAbsent(new Element(key, value));
	}

	@Override
	public int size()
	{
		return cache.getSize();
	}

	@Override
	public Collection<V> values()
	{
		final List<V> vl = new ArrayList<V>();
		for (final Object key : cache.getKeys())
		{
			final V v = (V) cache.get(key).getValue();
			vl.add(v);
		}

		return vl;
	}

}
