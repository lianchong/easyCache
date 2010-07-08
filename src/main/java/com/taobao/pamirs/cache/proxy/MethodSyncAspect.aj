package com.taobao.pamirs.cache.proxy;

public aspect MethodSyncAspect
{
	
	pointcut methodIntercept(): call(@com.taobao.pamirs.cache.Sync * *(..));
	
	
}
