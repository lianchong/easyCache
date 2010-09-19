package com.taobao.pamirs.sync;

import org.springframework.test.AbstractDependencyInjectionSpringContextTests;

import com.taobao.pamirs.sync.annotation.Sync;
import com.taobao.pamirs.sync.annotation.Sync.InstanceType;

public class MethodSyncTest extends AbstractDependencyInjectionSpringContextTests
{

	@Override
	protected String getConfigPath()
	{
		return "/pamirs-cache-config-test.xml";
	}

	@Sync(BeanType = InstanceType.NORMAL_INSTANCE)
	public void fun()
	{

	}

	public void testUnicast() throws Exception
	{
		fun();
	}

}
