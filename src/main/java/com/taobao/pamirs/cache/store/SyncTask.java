package com.taobao.pamirs.cache.store;

public class SyncTask implements Task
{

	/**
     * 
     */
	private static final long	serialVersionUID	= 1L;

	public Object execute()
	{
		System.out.println("Executing Tasks!");
		return "hiello";
	}

}
