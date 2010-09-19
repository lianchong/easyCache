// //////////////////////////////////////////////////////////////////////////
//
// Copyright (c) 2010, Taobao. All rights reserved.
//
// //////////////////////////////////////////////////////////////////////////

package com.taobao.pamirs.sync.helper;

import junit.framework.TestCase;

import com.alibaba.common.lang.ArrayUtil;

/**
 * 
 */
public class ObjectUtilTest extends TestCase
{

	public void testStringSplitter()
	{
		final String longString = "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa";
		String[] stringArray = ObjectUtil.Splitter.split(5, longString);
		System.out.println("������5��: " + ArrayUtil.toString(stringArray));
		stringArray = ObjectUtil.Splitter.split(13, longString);
		System.out.println("������13��: " + ArrayUtil.toString(stringArray));
	}
}
