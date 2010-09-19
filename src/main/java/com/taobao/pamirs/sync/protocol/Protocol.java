package com.taobao.pamirs.sync.protocol;

import java.util.Properties;

/**
 * @author mengzhu
 *         the protcol class represents the protocol stack definition of
 *         JGroups, and
 *         should be used for cluster/groups communication
 */
public class Protocol
{
	Properties	protocolDetail;

	public void addConfig(final String key, final String value)
	{
		protocolDetail.put(key, value);
	}

	public void removeConfig(final String key, final String value)
	{
		protocolDetail.put(key, value);
	}

	public String value()
	{
		return protocolDetail.toString();
	}
}
