package com.taobao.pamirs.cache.replicator.data;

public class CacheManagerStatData
{
	private String	status;
	private String	uuid;
	private String	name;
	private String	registeredCaches;
	private String	hosts;

	public String getHosts()
	{
		return hosts;
	}

	public void setHosts(final String hosts)
	{
		this.hosts = hosts;
	}

	public String getUuid()
	{
		return uuid;
	}

	public void setUuid(final String uuid)
	{
		this.uuid = uuid;
	}

	public String getName()
	{
		return name;
	}

	public void setName(final String name)
	{
		this.name = name;
	}

	public String getRegisteredCaches()
	{
		return registeredCaches;
	}

	public void setRegisteredCaches(final String registeredCaches)
	{
		this.registeredCaches = registeredCaches;
	}

	public String getStatus()
	{
		return status;
	}

	public void setStatus(final String status)
	{
		this.status = status;
	}

}
