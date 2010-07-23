package com.taobao.pamirs.cache.protocol;

import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;

public class EthernetUtil
{
	public static String getNonLoopAddress()
	{
		final StringBuilder builder2 = new StringBuilder();

		try
		{
			final Enumeration<?> nis = NetworkInterface.getNetworkInterfaces();
			InetAddress ia = null;
			while (nis.hasMoreElements())
			{
				final NetworkInterface ni = (NetworkInterface) nis.nextElement();
				final Enumeration<InetAddress> ias = ni.getInetAddresses();
				while (ias.hasMoreElements())
				{
					ia = ias.nextElement();
					if ((ia instanceof Inet6Address) || "127.0.0.1".equals(ia.getHostAddress()))
					{
						continue;// skip ipv6
					}
					builder2.append(ia.getHostAddress());
					if (ias.hasMoreElements())
					{
						builder2.append(",");
					}
				}
			}
			return builder2.toString();

		} catch (final Exception e)
		{
			e.printStackTrace();
			return null;
		}
	}

}
