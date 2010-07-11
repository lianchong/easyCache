package com.taobao.pamirs.cache.store;

import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;

import org.jgroups.ChannelException;
import org.jgroups.JChannel;
import org.springframework.stereotype.Component;

@Component
public class ExtendedChannel extends JChannel
{
	private final String	connProp;

	public String getConnProp()
	{
		return connProp;
	}

	public ExtendedChannel(final String properties) throws ChannelException
	{
		super(properties);
		connProp = properties;
	}

	public static ExtendedChannel getInstance() throws ChannelException
	{
		String props = "TCP(bind_addr=IPADDRESS;start_port=20000;sock_conn_timeout=500;send_buf_size=100000 ;recv_buf_size=200000;loopback=true):"
		        +
		        "MPING( timeout=4000;"
		        +
		        "           bind_to_all_interfaces=true;"
		        +
		        "           mcast_addr=228.8.8.8;"
		        +
		        "           mcast_port=21000;"
		        +
		        "           ip_ttl=8;"
		        +
		        "           num_initial_members=2;"
		        +
		        "           num_ping_requests=1):"
		        +
		        "    MERGE2( max_interval=10000;"
		        +
		        "            min_interval=5000):"
		        +
		        "    FD_SOCK:"
		        +
		        "    FD( timeout=2000;"
		        +
		        "        max_tries=3):"
		        +
		        "   VERIFY_SUSPECT( timeout=3500):"
		        +
		        "    pbcast.NAKACK( gc_lag=50;"
		        +
		        "                  retransmit_timeout=600,1200,2400,4800):"
		        +
		        "    UNICAST( timeout=300,600,1200):" +
		        "    pbcast.STABLE( stability_delay=1000 ;" +
		        "                   desired_avg_gossip=20000; " +
		        "                   max_bytes=0):" +
		        "   VIEW_SYNC( avg_send_interval=60000 ):" +
		        "    pbcast.GMS( print_local_addr=true ;" +
		        "                join_timeout=5000 ;" +
		        "                shun=true)";

		final StringBuilder builder = new StringBuilder();

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
					builder.append(ia.getHostAddress());
					if (ias.hasMoreElements())
					{
						builder.append(",");
					}
				}
			}

			props = props.replaceAll("IPADDRESS", builder.toString());

		} catch (final Exception e)
		{
			e.printStackTrace();
		}

		System.out.println("Connection string: " + props);
		return new ExtendedChannel(props);
	}
}
