package com.taobao.pamirs.cache;

import org.springframework.test.AbstractDependencyInjectionSpringContextTests;

public class CacheTest extends AbstractDependencyInjectionSpringContextTests
{

	@Override
	protected String getConfigPath()
	{
		return "/pamirs-cache-config-test.xml";
	}

	public void test()
	{

	}

}
