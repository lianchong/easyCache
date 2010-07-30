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
		logger.info("�µļ�Ⱥ��Ա����: " + new_mbrs.toString());
	}

	public void contentsSet(final Map<K, V> new_entries)
	{
		logger.info("��Ⱥ�������ݲ���: " + new_entries.size() + "��");
	}

	public void contentsCleared()
	{
		logger.info("��Ⱥ�������!");
	}

}
