package com.taobao.pamirs.cache;

import org.springframework.test.AbstractDependencyInjectionSpringContextTests;

import com.taobao.pamirs.cache.store.ExtendedChannel;

public class CacheTest extends AbstractDependencyInjectionSpringContextTests
{

	private ExtendedChannel	extendedChannel;

	public ExtendedChannel getExtendedChannel()
	{
		return extendedChannel;
	}

	public void setExtendedChannel(final ExtendedChannel extendedChannel)
	{
		this.extendedChannel = extendedChannel;
	}

	@Override
	protected String getConfigPath()
	{
		return "/pamirs-cache-config.xml";
	}

	public void test()
	{

	}

}
