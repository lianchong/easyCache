package com.taobao.pamirs.sync.protocol;

import org.jgroups.blocks.GroupRequest;
import org.jgroups.blocks.RpcDispatcher;
import org.jgroups.protocols.TCPPING;
import org.jgroups.stack.IpAddress;
import org.jgroups.util.RspList;
import org.springframework.test.AbstractDependencyInjectionSpringContextTests;

import com.taobao.pamirs.sync.annotation.Sync;
import com.taobao.pamirs.sync.annotation.Sync.InstanceType;
import com.taobao.pamirs.sync.aspect.CacheAspect;
import com.taobao.pamirs.sync.helper.EthernetUtil;
import com.taobao.pamirs.sync.replicator.data.ExtendedChannel;

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

	private CacheAspect	cacheAspect;

	public CacheAspect getCacheAspect()
	{
		return cacheAspect;
	}

	public void setCacheAspect(final CacheAspect cacheAspect)
	{
		this.cacheAspect = cacheAspect;
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

	public Object print(final String params) throws Exception
	{
		System.out.println("Calling method from " + params);
		return params;
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

	@Sync(BeanType = InstanceType.NORMAL_INSTANCE)
	public void funny(final String place)
	{
		System.out.println("Being called at " + place);
	}

	private void sendMessage() throws Exception
	{

		for (int i = 0; i < 10; ++i)
		{
			final RspList rl = dispatcher.callRemoteMethods(null, "print", new Object[]
			        { EthernetUtil.getNonLoopAddress() }, new Class[]
			        { String.class }, GroupRequest.GET_ALL, 0);
			System.out.println("Responses: \n" + rl);
			Thread.sleep(900);
		}

	}

	public void testDispatch() throws Exception
	{
		final TCPPING ping = (TCPPING) cacheAspect.getServiceInvokeChannel().getProtocolStack()
		        .findProtocol(TCPPING.class);
		ping.getInitialHosts().add(new IpAddress("10.13.18.45", 7800));

		for (int i = 0; i < 17; ++i)
		{
			try
			{
				funny(EthernetUtil.getNonLoopAddress());
			} catch (final Exception e)
			{
				e.printStackTrace();
			}
			Thread.sleep(900);
		}
		ping.getInitialHosts().add(new IpAddress("10.13.57.131", 7800));
		for (int i = 0; i < 17; ++i)
		{
			try
			{
				funny(EthernetUtil.getNonLoopAddress());
			} catch (final Exception e)
			{
				e.printStackTrace();
			}
			Thread.sleep(900);
		}
	}
}