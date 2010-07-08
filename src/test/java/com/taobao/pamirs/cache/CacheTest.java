package com.taobao.pamirs.cache;

import org.springframework.test.AbstractDependencyInjectionSpringContextTests;

import com.taobao.pamirs.cache.event.MethodSyncServerBean;

public class CacheTest extends AbstractDependencyInjectionSpringContextTests
{

	private MethodSyncServerBean	methodSyncServerBean;

	public MethodSyncServerBean getMethodSyncServerBean()
	{
		return methodSyncServerBean;
	}

	public void setMethodSyncServerBean(final MethodSyncServerBean methodSyncServerBean)
	{
		this.methodSyncServerBean = methodSyncServerBean;
	}

	@Override
	protected String getConfigPath()
	{
		return "/pamirs-cache-config.xml";
	}

	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	public void testCache()
	{
		System.err.println(methodSyncServerBean.getServerInstance());
	}
}
