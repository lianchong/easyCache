package com.taobao.pamirs.cache.listener;

import java.io.Serializable;
import java.util.Map;
import java.util.Vector;

import org.jgroups.Address;
import org.jgroups.View;
import org.jgroups.blocks.ReplicatedHashMap.Notification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ReplicatedHashMapListener<K extends Serializable, V extends Serializable> implements
        Notification<K, V>
{
	Logger	logger	= LoggerFactory.getLogger(ReplicatedHashMapListener.class);

	public void entrySet(final K key, final V value)
	{
	}

	public void entryRemoved(final K key)
	{
	}

	public void viewChange(final View view, final Vector<Address> new_mbrs, final Vector<Address> old_mbrs)
	{
		logger.info("新的集群成员加入: " + new_mbrs.toString());
	}

	public void contentsSet(final Map<K, V> new_entries)
	{
		logger.info("集群批量数据插入: " + new_entries.size() + "条");
	}

	public void contentsCleared()
	{
		logger.info("集群数据清空!");
	}

}
