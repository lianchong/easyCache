package com.taobao.pamirs.cache.protocol;

/**
 * @author mengzhu
 */
public interface AbstractProtocolBuilder
{
	/**
	 * build the jgroups protocol stack
	 * 
	 * @param objects
	 * @return
	 */
	Protocol build(Object... objects);

}
