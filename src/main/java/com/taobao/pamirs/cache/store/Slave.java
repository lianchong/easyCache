package com.taobao.pamirs.cache.store;

public interface Slave
{
	Object handle(Task task);
}
