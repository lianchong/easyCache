package com.taobao.pamirs.cache.store;

import java.io.Serializable;

public abstract interface Task extends Serializable
{
	public abstract Object execute();
}