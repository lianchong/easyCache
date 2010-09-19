// //////////////////////////////////////////////////////////////////////////
//
// Copyright (c) 2010, Taobao. All rights reserved.
//
// //////////////////////////////////////////////////////////////////////////

package com.taobao.pamirs.sync.service;

import java.util.Arrays;
import java.util.List;

import org.springframework.test.AbstractDependencyInjectionSpringContextTests;

import com.taobao.pamirs.sync.annotation.Sync;
import com.taobao.pamirs.sync.replicator.service.RpcManager;

/**
 * 
 */
public class RpcManagerTest extends
        AbstractDependencyInjectionSpringContextTests
{
	@Override
	protected String getConfigPath()
	{
		return "/pamirs-cache-config-test.xml";
	}

	private RpcManager	rpcManager;

	public RpcManager getRpcManager()
	{
		return rpcManager;
	}

	public void setRpcManager(final RpcManager rpcManager)
	{
		this.rpcManager = rpcManager;
	}

	@Sync(BeanName = "rpcManager", SkipLocal = false)
	private void reload(final List<String> v)
	{
		try
		{
			Thread.sleep(10000);
		} catch (final InterruptedException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(v);

	}

	public void testMethodIn()
	{
		reload(Arrays.asList("m", "1111"));
	}

}
