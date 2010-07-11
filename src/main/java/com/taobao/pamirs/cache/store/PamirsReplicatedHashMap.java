package com.taobao.pamirs.cache.store;

import java.io.Serializable;

import org.jgroups.Channel;
import org.jgroups.blocks.ReplicatedHashMap;

public class PamirsReplicatedHashMap<K extends Serializable, V extends Serializable> extends
        ReplicatedHashMap<K, V>
{

	public PamirsReplicatedHashMap(final Channel channel)
	{
		super(channel);
	}

	public V putLocal(final K key, final V value)
	{
		return map.put(key, value);
	}

	public V removeLocal(final K key)
	{
		return map.remove(key);
	}
}
