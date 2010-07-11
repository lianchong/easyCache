package com.taobao.pamirs.cache;

import org.springframework.test.AbstractDependencyInjectionSpringContextTests;

import com.taobao.pamirs.cache.event.PamirsCacheEvent;

public class MethodSyncTest extends AbstractDependencyInjectionSpringContextTests
{

	@Override
	protected String getConfigPath()
	{
		return "/pamirs-cache-config.xml";
	}

	@Sync
	public void fun()
	{

	}

	public void testUnicast() throws Exception
	{

		for (int i = 0; i < 50; ++i)
		{
			new PamirsCacheEvent().test(i);
			Thread.sleep(500);
		}

	}

}
