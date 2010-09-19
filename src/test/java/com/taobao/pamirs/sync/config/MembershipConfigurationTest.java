// //////////////////////////////////////////////////////////////////////////
//
// Copyright (c) 2010, Taobao. All rights reserved.
//
// //////////////////////////////////////////////////////////////////////////

package com.taobao.pamirs.sync.config;

import org.springframework.test.AbstractDependencyInjectionSpringContextTests;

/**
 * 
 */
public class MembershipConfigurationTest extends AbstractDependencyInjectionSpringContextTests
{
	@Override
	protected String getConfigPath()
	{
		setAutowireMode(AUTOWIRE_BY_NAME);
		return "/pamirs-cache-config-test.xml";
	}

	private MembershipConfiger	membershipConfiger;

	public MembershipConfiger getMembershipConfiger()
	{
		return membershipConfiger;
	}

	public void setMembershipConfiger(final MembershipConfiger membershipConfiger)
	{
		this.membershipConfiger = membershipConfiger;
	}

	public void testConfiger() throws InterruptedException
	{
		Thread.sleep(100000);
	}

}
