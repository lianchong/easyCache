package com.taobao.pamirs.sync.proxy;

public enum ServiceInvokeType
{
	LOCAL, HSF, HTTP, WEBSERVICE;
	@Override
	public String toString()
	{
		return name();
	}
}
