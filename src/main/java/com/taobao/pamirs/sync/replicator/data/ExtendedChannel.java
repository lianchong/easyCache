package com.taobao.pamirs.sync.replicator.data;

import java.lang.management.ManagementFactory;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Enumeration;

import javax.management.MBeanServer;
import javax.management.MBeanServerFactory;

import org.jgroups.ChannelException;
import org.jgroups.JChannel;
import org.jgroups.jmx.JmxConfigurator;

/**
 * 基于JGroups Channel的一个扩展通道,类建TCP和UDP的配置
 */
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

	public static ExtendedChannel getInstance(final String hosts) throws ChannelException
	{
		final String port = "7800";
		String props =
		        "TCP(bind_addr=IPADDRESS;start_port=" +
		                port +
		                ";" +
		                "sock_conn_timeout=500;send_buf_size=100000 ;" +
		                "recv_buf_size=200000;loopback=true):" +
		                "MPING( timeout=4000;" + "           receive_on_all_interfaces=true;"
		                + "           mcast_port=7500;" +
		                "           ip_ttl=8;" + "           num_initial_members=2;"
		                + "           num_ping_requests=1):" +
		                "    MERGE2( max_interval=10000;" + "            min_interval=5000):"
		                + "    FD_SOCK:" +
		                "    FD( timeout=2000;" + "        max_tries=3):"
		                + " pbcast.STREAMING_STATE_TRANSFER: " +
		                "    pbcast.NAKACK(use_mcast_xmit=false;gc_lag=50;" +
		                "                  retransmit_timeout=600,1200,2400,4800):"
		                + "    UNICAST( timeout=300,600,1200):" +
		                "    pbcast.STABLE( stability_delay=1000 ;"
		                + "                   desired_avg_gossip=20000; " +
		                "                   max_bytes=0):" + "    pbcast.GMS( print_local_addr=true ;" +
		                "                join_timeout=5000 ;" + "                shun=true)";

		String props1 =
		        " TCP(      bind_port="
		                + port
		                + ";           loopback=true;"
		                +
		                "          recv_buf_size=20000000;"
		                + "          send_buf_size=640000;"
		                +
		                "          discard_incompatible_packets=true;"
		                + "          max_bundle_size=64000;"
		                +
		                "          max_bundle_timeout=30;"
		                + "          enable_bundling=true;"
		                + "          use_send_queues=true;"
		                +
		                "          sock_conn_timeout=300;"
		                + "          timer.num_threads=4;"
		                +
		                "singleton_name=SERVICE_INVOKE_CHANNEL;"
		                +
		                "         thread_pool.enabled=true;"
		                + "          thread_pool.min_threads=1;"
		                +
		                "          thread_pool.max_threads=10;"
		                + "          thread_pool.keep_alive_time=5000;"
		                +
		                "          thread_pool.queue_enabled=false;"
		                + "          thread_pool.queue_max_size=100;"
		                +
		                "          thread_pool.rejection_policy=run;"
		                +

		                "          oob_thread_pool.enabled=true;"
		                + "          oob_thread_pool.min_threads=1;"
		                +
		                "          oob_thread_pool.max_threads=8;"
		                + "          oob_thread_pool.keep_alive_time=5000;"
		                +
		                "          oob_thread_pool.queue_enabled=false;"
		                + "          oob_thread_pool.queue_max_size=100;"
		                +
		                "          oob_thread_pool.rejection_policy=run):"
		                + "TCPPING( timeout=3000; "
		                +
		                "             initial_hosts="

		                + "127.0.0.1[7800];"
		                + "             port_range=1; "
		                +
		                "             num_initial_members=3): "
		                + " BARRIER: "
		                + " FC(max_credits=2000000; min_threshold=0.10): "
		                +
		                " FRAG2(frag_size=60000):"
		                + "    MERGE2( max_interval=10000;"
		                + "            min_interval=5000):"
		                +
		                "    FD_SOCK:"
		                + "    FD( timeout=10000;"
		                + "        max_tries=5):"
		                + "VERIFY_SUSPECT(timeout=1500):"
		                +
		                "    pbcast.NAKACK(use_mcast_xmit=false;gc_lag=50;"
		                +
		                "                  retransmit_timeout=600,1200,2400,4800;discard_delivered_msgs=false):"
		                +
		                "    UNICAST( timeout=300,600,1200):" + "    pbcast.STABLE( stability_delay=1000 ;" +
		                "                   desired_avg_gossip=50000; "
		                + "                   max_bytes=400000):" +
		                "    pbcast.GMS( print_local_addr=true;" + "                join_timeout=5000 ;" +
		                "                view_bundling=true):" + "	 pbcast.FLUSH(timeout=10000)";

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
					if (nis.hasMoreElements())
					{
						builder.append(",");
					}
				}
			}

			props1 = props1.replaceAll("IPADDRESS", builder.toString());
			props = props.replaceAll("IPADDRESS", builder.toString());

		} catch (final Exception e)
		{
			e.printStackTrace();
		}

		// System.out.println("Connection string: " + props1);

		final ExtendedChannel ch = new ExtendedChannel(props1);
		// System.out.println(ch.getProperties());

		ch.connect("SERVICE_INVOKE_CHANNEL");
		try
		{
			JmxConfigurator
			        .registerChannel(ch, getPlatformMBeanServer(), "PAMIRS_SYNC:name=SERVICE_INVOKE_CHANNEL");
		} catch (final Exception e)
		{
			e.printStackTrace();
		}
		return ch;
	}

	public static MBeanServer getPlatformMBeanServer()
	{
		final MBeanServer mBeanServer = ManagementFactory.getPlatformMBeanServer();
		return mBeanServer;
	}

	public static MBeanServer getMBeanServer()
	{
		final ArrayList servers = MBeanServerFactory.findMBeanServer(null);
		if (servers != null && !servers.isEmpty())
		{
			// return 'jboss' server if available
			for (int i = 0; i < servers.size(); i++)
			{
				final MBeanServer srv = (MBeanServer) servers.get(i);
				if ("jboss".equalsIgnoreCase(srv.getDefaultDomain()))
					return srv;
			}

			// return first available server
			return (MBeanServer) servers.get(0);
		} else
		{
			// if it all fails, create a default
			return MBeanServerFactory.createMBeanServer();
		}
	}
}
