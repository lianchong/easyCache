package com.taobao.pamirs.sync.replicator.service;

import java.lang.reflect.Method;
import java.net.UnknownHostException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import org.jgroups.Address;
import org.jgroups.ChannelException;
import org.jgroups.blocks.GroupRequest;
import org.jgroups.blocks.RpcDispatcher;
import org.jgroups.util.RspList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.taobao.pamirs.sync.helper.AppContext;
import com.taobao.pamirs.sync.helper.EthernetUtil;
import com.taobao.pamirs.sync.replicator.data.ExtendedChannel;

@Component
public class RpcManager
{
	@Autowired
	ExtendedChannel	           serviceInvokeChannel;

	private static Set<String>	invokeRegistry	= new HashSet<String>();

	public Set<String> getRegisteredInvocations()
	{
		return invokeRegistry;
	}

	public Object invokeMethod(final String className, final String methodName, final boolean isSpringBean,
	        final Object[] params, final boolean skipLocal) throws InstantiationException,
	        IllegalAccessException, ClassNotFoundException, ChannelException, UnknownHostException
	{
		Object instance = null;
		if (isSpringBean)
		{
			/* 从当前的spring context取实例 */
			instance = AppContext.getApplicationContext().getBean(className);
		} else
		{
			instance = Class.forName(className).newInstance();
		}

		final RpcDispatcher dispatcher =
		        new RpcDispatcher(serviceInvokeChannel, null, null, instance);
		serviceInvokeChannel.connect("SERVICE_INVOKE_CHANNEL");

		RspList rl = null;
		if (params.length == 0)
		{
			rl = dispatcher.callRemoteMethods(skipLocal ? getNonLocalAddressList(instance) : null,
			        methodName, new Object[]
			{}, new Class[]
			{}, GroupRequest.GET_ALL, 0);
			dispatcher.stop();
		} else
		{
			final Method[] methods = instance.getClass().getMethods();
			Method method = null;
			for (final Method innerMethod : methods)
			{
				if (innerMethod.getName().equals(methodName))
				{
					method = innerMethod;
					break;
				}
			}
			if (method != null)
			{
				rl = dispatcher.callRemoteMethods(skipLocal ? getNonLocalAddressList(instance) : null,
				        method.getName(), params,
				        method.getParameterTypes(), GroupRequest.GET_ALL, 0);
				System.out.println(rl.getSuspectedMembers());
				System.out.println(rl.numReceived());
				dispatcher.stop();
			}

		}

		System.out.println(rl);

		return rl.getFirst();
	}

	private Vector<Address> getNonLocalAddressList(final Object obj) throws ChannelException
	{
		final Vector<Address> target = serviceInvokeChannel.getView().getMembers();
		final Address localAddress = serviceInvokeChannel.getLocalAddress();
		final Vector<Address> callAddresses = new Vector<Address>();
		for (final Address addr : target)
		{
			if (addr.equals(localAddress))
			{
				// skip resending to local

				continue;
			}
			callAddresses.add(addr);
		}
		return callAddresses;
	}

	public int reload(final List<String> value)
	{
		for (int i = 0; i < 10; ++i)
		{
			System.out.println(EthernetUtil.getLocalAddress()
			        + "Reloading...................................." + value);
			try
			{
				Thread.sleep(3000);
			} catch (final InterruptedException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return 1;
	}
}
