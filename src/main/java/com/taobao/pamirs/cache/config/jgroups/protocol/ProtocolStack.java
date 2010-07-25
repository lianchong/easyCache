package com.taobao.pamirs.cache.config.jgroups.protocol;

import java.util.Vector;

/**
 * @author mengzhu
 *         the protocol stack represents the full
 */
public class ProtocolStack
{
	private Vector<Protocol>	protocolStacks;

	public void addProtocol(final Protocol protocol)
	{
		protocolStacks.add(protocol);
	}

	public void removeProtocol(final Protocol protocol)
	{
		protocolStacks.remove(protocol);
	}

	public void updateProtocol(final Protocol protocol)
	{
	}
}
