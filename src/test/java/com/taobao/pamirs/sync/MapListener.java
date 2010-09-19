package com.taobao.pamirs.sync;

import java.io.Serializable;
import java.util.Map;
import java.util.Vector;

import org.jgroups.Address;
import org.jgroups.View;
import org.jgroups.blocks.ReplicatedHashMap.Notification;

public class MapListener<K extends Serializable, V extends Serializable> implements Notification<K, V>
{

	public void contentsCleared()
	{
		System.out.println("cleared");
	}

	public void contentsSet(final Map<K, V> arg0)
	{
		System.out.println("set all: " + arg0);

	}

	public void entryRemoved(final K arg0)
	{
		// TODO Auto-generated method stub

	}

	public void entrySet(final K arg0, final V arg1)
	{
		System.out.println("set value: " + arg0 + "=" + arg1);

	}

	public void viewChange(final View arg0, final Vector<Address> arg1, final Vector<Address> arg2)
	{
		System.out.println("Members: " + arg0.getMembers() + "new member: " + arg1);

	}

}
