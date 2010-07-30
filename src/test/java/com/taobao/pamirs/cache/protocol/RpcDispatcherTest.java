package com.taobao.pamirs.cache.protocol;

import org.jgroups.blocks.GroupRequest;
import org.jgroups.blocks.RpcDispatcher;
import org.jgroups.util.RspList;
import org.springframework.test.AbstractDependencyInjectionSpringContextTests;

import com.taobao.pamirs.cache.helper.EthernetUtil;
import com.taobao.pamirs.cache.replicator.data.ExtendedChannel;

public class RpcDispatcherTest extends
        AbstractDependencyInjectionSpringContextTests
{

	@Override
	protected String getConfigPath()
	{
		return "/pamirs-cache-config-test.xml";
	}

	public RpcDispatcherTest()
	{
		setAutowireMode(AUTOWIRE_BY_NAME);
	}

	private ExtendedChannel	serviceInvokeChannel;

	public ExtendedChannel getServiceInvokeChannel()
	{
		return serviceInvokeChannel;
	}

	public void setServiceInvokeChannel(final ExtendedChannel serviceInvokeChannel)
	{
		this.serviceInvokeChannel = serviceInvokeChannel;
	}

	private RpcDispatcher	dispatcher;

	public int print(final String params) throws Exception
	{
		System.out.println(params);
		return 2;
	}

	public void start() throws Exception
	{
		// channel =
		// ExtendedChannel.getInstance("10.13.18.101[7800],10.13.59.178[7800],192.168.207.118[7800]");
		System.out.println(serviceInvokeChannel);
		dispatcher = new RpcDispatcher(serviceInvokeChannel, null, null, this);
		serviceInvokeChannel.connect("RpcDispatcherTest");

		//
		sendMessage();

		//
		serviceInvokeChannel.close();
		dispatcher.stop();
	}

	private void sendMessage() throws Exception
	{

		for (int i = 0; i < 10; ++i)
		{
			final RspList rl = dispatcher.callRemoteMethods(null, "print", new Object[]
			        { EthernetUtil.getNonLoopAddress() }, new Class[]
			        { String.class }, GroupRequest.GET_ALL, 0);
			System.out.println("Responses: \n" + rl);
			Thread.sleep(500);
		}

	}

	public void testDispatch()
	{
		try
		{
			start();
		} catch (final Exception e)
		{
			e.printStackTrace();
		}
	}
}