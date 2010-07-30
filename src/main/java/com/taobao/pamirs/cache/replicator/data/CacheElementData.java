package com.taobao.pamirs.cache.replicator.data;

import java.util.Date;

public class CacheElementData
{
	private String	key;
	private String	value;
	private long	hitCount;
	private long	size;
	private String	cacheName;
	private Date	updateTime;

	public Date getUpdateTime()
	{
		return updateTime;
	}

	public void setUpdateTime(final Date updateTime)
	{
		this.updateTime = updateTime;
	}

	public String getKey()
	{
		return key;
	}

	public void setKey(final String key)
	{
		this.key = key;
	}

	public String getValue()
	{
		return value;
	}

	public void setValue(final String value)
	{
		this.value = value;
	}

	public long getHitCount()
	{
		return hitCount;
	}

	public void setHitCount(final long hitCount)
	{
		this.hitCount = hitCount;
	}

	public long getSize()
	{
		return size;
	}

	public void setSize(final long size)
	{
		this.size = size;
	}

	public String getCacheName()
	{
		return cacheName;
	}

	public void setCacheName(final String cacheName)
	{
		this.cacheName = cacheName;
	}

}
