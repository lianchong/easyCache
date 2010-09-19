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
		System.out.println("按长度5分: " + ArrayUtil.toString(stringArray));
		stringArray = ObjectUtil.Splitter.split(13, longString);
		System.out.println("按长度13分: " + ArrayUtil.toString(stringArray));
	}
}
