package com.taobao.pamirs.cache.replicator.data;

import java.util.List;

public class CacheStatData
{
	private List<String>	keys;
	private String	     cacheName;
	private int	         size;
	private long	     objectCount;
	private long	     cacheHitCount;
	private long	     cacheMissCount;
	private long	     maxGetTimeMillis;
	private long	     putCount;
	private long	     removeCount;
	private long	     inMemorySize;
	private String	     policy;
	private String	     register;

	public String getRegister()
	{
		return register;
	}

	public void setRegister(final String register)
	{
		this.register = register;
	}

	public String getPolicy()
	{
		return policy;
	}

	public void setPolicy(final String policy)
	{
		this.policy = policy;
	}

	public List<String> getKeys()
	{
		return keys;
	}

	public void setKeys(final List<String> keys)
	{
		this.keys = keys;
	}

	public String getCacheName()
	{
		return cacheName;
	}

	public void setCacheName(final String cacheName)
	{
		this.cacheName = cacheName;
	}

	public int getSize()
	{
		return size;
	}

	public void setSize(final int size)
	{
		this.size = size;
	}

	public long getObjectCount()
	{
		return objectCount;
	}

	public void setObjectCount(final long objectCount)
	{
		this.objectCount = objectCount;
	}

	public long getCacheHitCount()
	{
		return cacheHitCount;
	}

	public void setCacheHitCount(final long cacheHitCount)
	{
		this.cacheHitCount = cacheHitCount;
	}

	public long getCacheMissCount()
	{
		return cacheMissCount;
	}

	public void setCacheMissCount(final long cacheMissCount)
	{
		this.cacheMissCount = cacheMissCount;
	}

	public long getMaxGetTimeMillis()
	{
		return maxGetTimeMillis;
	}

	public void setMaxGetTimeMillis(final long maxGetTimeMillis)
	{
		this.maxGetTimeMillis = maxGetTimeMillis;
	}

	public long getPutCount()
	{
		return putCount;
	}

	public void setPutCount(final long putCount)
	{
		this.putCount = putCount;
	}

	public long getRemoveCount()
	{
		return removeCount;
	}

	public void setRemoveCount(final long removeCount)
	{
		this.removeCount = removeCount;
	}

	public long getInMemorySize()
	{
		return inMemorySize;
	}

	public void setInMemorySize(final long inMemorySize)
	{
		this.inMemorySize = inMemorySize;
	}

}
