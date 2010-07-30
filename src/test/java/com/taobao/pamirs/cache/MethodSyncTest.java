package com.taobao.pamirs.cache;

import org.springframework.test.AbstractDependencyInjectionSpringContextTests;

import com.taobao.pamirs.cache.annotation.Sync;

public class MethodSyncTest extends AbstractDependencyInjectionSpringContextTests
{

	@Override
	protected String getConfigPath()
	{
		return "/pamirs-cache-config-test.xml";
	}

	@Sync
	public void fun()
	{

	}

	public void testUnicast() throws Exception
	{

	}

}
