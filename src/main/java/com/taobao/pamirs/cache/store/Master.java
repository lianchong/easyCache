package com.taobao.pamirs.cache.store;

public interface Master
{
	Object submit(Task task, long timeout) throws Exception;
}
