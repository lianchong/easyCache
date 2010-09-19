// //////////////////////////////////////////////////////////////////////////
//
// Copyright (c) 2010, Taobao. All rights reserved.
//
// //////////////////////////////////////////////////////////////////////////

package com.taobao.pamirs.sync.spi;

import org.springframework.test.AbstractDependencyInjectionSpringContextTests;

import com.taobao.pamirs.commons.dao.Result;
import com.taobao.pamirs.sync.helper.EthernetUtil;
import com.taobao.pamirs.sync.replicator.data.CacheElementData;
import com.taobao.pamirs.sync.replicator.data.ExtendedChannel;

public class CacheStatFacadeTest extends
        AbstractDependencyInjectionSpringContextTests
{

	@Override
	protected String getConfigPath()
	{
		return "/pamirs-cache-config-test.xml";
	}

	public CacheStatFacadeTest()
	{
		setAutowireMode(AUTOWIRE_BY_NAME);
	}

	ExtendedChannel	serviceInvokeChannel;
	CacheStatFacade	cacheStatFacade;

	public CacheStatFacade getCacheStatFacade()
	{
		return cacheStatFacade;
	}

	public void setCacheStatFacade(final CacheStatFacade cacheStatFacade)
	{
		this.cacheStatFacade = cacheStatFacade;
	}

	public ExtendedChannel getServiceInvokeChannel()
	{
		return serviceInvokeChannel;
	}

	public void setServiceInvokeChannel(final ExtendedChannel serviceInvokeChannel)
	{
		this.serviceInvokeChannel = serviceInvokeChannel;
	}

	public void testGetClusterMembers()
	{
		System.out.println(cacheStatFacade.getClusterMembers());
	}

	public void testGetRemoteKeys()
	{
		final Result<CacheElementData> result = cacheStatFacade.getRemoteKeysByHost(
		        EthernetUtil.getNonLoopAddress(),
		        "PAMIRS_CACHE");
		assertNotNull(result);
		assertNotNull(result.getDataList());
		System.out.println(result.getDataList());
	}

	public void testGetRemoteCaches()
	{
		final Result<CacheElementData> result = cacheStatFacade.getRemoteCacheNamesByHost(EthernetUtil
		        .getNonLoopAddress());
		assertNotNull(result);
		assertNotNull(result.getDataList());
		System.out.println(result.getDataList());
	}

}
