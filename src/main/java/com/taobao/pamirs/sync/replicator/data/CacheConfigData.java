package com.taobao.pamirs.sync.replicator.data;

import java.io.Serializable;

public class CacheConfigData implements Serializable
{
	Object	configKey;

	Object	configValue;

	public Object getConfigKey()
	{
		return configKey;
	}

	public void setConfigKey(final Object configKey)
	{
		this.configKey = configKey;
	}

	public Object getConfigValue()
	{
		return configValue;
	}

	public void setConfigValue(final Object configValue)
	{
		this.configValue = configValue;
	}

}
