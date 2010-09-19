package com.taobao.pamirs.sync.protocol;

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
