package com.taobao.pamirs.cache;

import org.springframework.test.AbstractDependencyInjectionSpringContextTests;

import com.taobao.pamirs.cache.store.Server;

public class MethodSyncTest extends AbstractDependencyInjectionSpringContextTests
{
	@Override
	protected String getConfigPath()
	{
		return "/pamirs-cache-config.xml";
	}

	@Sync
	private void fun()
	{

	}

	public void testUnicast() throws Exception
	{

		for (int i = 0; i < 100; ++i)
		{
			Server.main(null);
		}
	}

}
