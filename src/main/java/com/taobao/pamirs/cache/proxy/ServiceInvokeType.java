package com.taobao.pamirs.cache.proxy;

public enum ServiceInvokeType
{
	LOCAL, HSF, HTTP, WEBSERVICE;
	@Override
	public String toString()
	{
		return name();
	}
}
