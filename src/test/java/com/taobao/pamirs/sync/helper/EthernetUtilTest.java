// //////////////////////////////////////////////////////////////////////////
//
// Copyright (c) 2010, Taobao. All rights reserved.
//
// //////////////////////////////////////////////////////////////////////////

package com.taobao.pamirs.sync.helper;

import junit.framework.TestCase;

/**
 * 
 */
public class EthernetUtilTest extends TestCase
{
	public void testGetLocalNoneLoopAddress()
	{
		System.out.println("Local NonLoopAddress: " + EthernetUtil.getNonLoopAddress());
	}

	public void testGetLocalAddress()

	{
		System.out.println("Local All Address: " + EthernetUtil.getLocalAddress());
	}
}
