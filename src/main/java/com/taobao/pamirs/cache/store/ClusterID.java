package com.taobao.pamirs.cache.store;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.jgroups.Address;
import org.jgroups.util.Streamable;
import org.jgroups.util.Util;

public class ClusterID implements Streamable
{
	private Address	   creator;
	private int	       id;

	private static int	next_id	= 1;

	public ClusterID()
	{
	}

	public ClusterID(final Address creator, final int id)
	{
		this.creator = creator;
		this.id = id;
	}

	public Address getCreator()
	{
		return creator;
	}

	public int getId()
	{
		return id;
	}

	public static synchronized ClusterID create(final Address addr)
	{
		return new ClusterID(addr, next_id++);
	}

	@Override
	public int hashCode()
	{
		return creator.hashCode() + id;
	}

	@Override
	public boolean equals(final Object obj)
	{
		final ClusterID other = (ClusterID) obj;
		return creator.equals(other.creator) && (id == other.id);
	}

	@Override
	public String toString()
	{
		return creator + "::" + id;
	}

	public void writeTo(final DataOutputStream out) throws IOException
	{
		Util.writeAddress(creator, out);
		out.writeInt(id);
	}

	public void readFrom(final DataInputStream in) throws IOException, IllegalAccessException,
	        InstantiationException
	{
		creator = Util.readAddress(in);
		id = in.readInt();
	}
}