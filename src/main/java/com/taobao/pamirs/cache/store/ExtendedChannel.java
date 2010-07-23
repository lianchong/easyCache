package com.taobao.pamirs.cache.store;

import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;

import org.jgroups.ChannelException;
import org.jgroups.JChannel;

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

	public ExtendedChannel reconnect(final String... hostIp)
	        throws ChannelException
	{
		final StringBuilder builder = new StringBuilder();
		for (int i = 0; i < hostIp.length; ++i)
		{
			builder.append(hostIp[i] + "[7800]");
			if (i < hostIp.length - 1)
			{
				builder.append(",");
			}
		}

		String props1 = " TCP(bind_addr=IPADDRESS;        bind_port=7900; "
		        +
		        "          loopback=true;"
		        +
		        "          recv_buf_size=20M;"
		        +
		        "          send_buf_size=640K;"
		        +
		        "          discard_incompatible_packets=true;"
		        +
		        "          max_bundle_size=64K;"
		        +
		        "          max_bundle_timeout=30;"
		        +
		        "          enable_bundling=true;"
		        +
		        "          use_send_queues=true;"
		        +
		        "          sock_conn_timeout=300;"
		        +
		        "          timer.num_threads=4;"
		        +

		        "         thread_pool.enabled=true;"
		        +
		        "          thread_pool.min_threads=1;"
		        +
		        "          thread_pool.max_threads=10;"
		        +
		        "          thread_pool.keep_alive_time=5000;"
		        +
		        "          thread_pool.queue_enabled=false;"
		        +
		        "          thread_pool.queue_max_size=100;"
		        +
		        "          thread_pool.rejection_policy=discard;"
		        +

		        "          oob_thread_pool.enabled=true;"
		        +
		        "          oob_thread_pool.min_threads=1;"
		        +
		        "          oob_thread_pool.max_threads=8;"
		        +
		        "          oob_thread_pool.keep_alive_time=5000;"
		        +
		        "          oob_thread_pool.queue_enabled=false;"
		        +
		        "          oob_thread_pool.queue_max_size=100;"
		        +
		        "          oob_thread_pool.rejection_policy=discard):"
		        +
		        "TCPPING( timeout=3000; "
		        +
		        "             initial_hosts=" + builder.toString() + "; "
		        +
		        "             port_range=1; " +
		        "             num_initial_members=3): " +
		        " BARRIER: " +
		        " FC(max_credits=2M; min_threshold=0.10): " +
		        " FRAG2(frag_size=60K):" +
		        "    MERGE2( max_interval=10000;"
		        +
		        "            min_interval=5000):"
		        +
		        "    FD_SOCK:"
		        +
		        "    FD( timeout=2000;"
		        +
		        "        max_tries=3):"
		        + "VERIFY_SUSPECT(timeout=1500):"
		        +
		        "    pbcast.NAKACK(use_mcast_xmit=false;gc_lag=50;"
		        +
		        "                  retransmit_timeout=600,1200,2400,4800):"
		        +
		        "    UNICAST( timeout=300,600,1200):" +
		        "    pbcast.STABLE( stability_delay=1000 ;" +
		        "                   desired_avg_gossip=50000; " +
		        "                   max_bytes=0):" +
		        "    pbcast.GMS( print_local_addr=true;" +
		        "                join_timeout=5000 ;" +
		        "                view_bundling=true):" +
		        " pbcast.FLUSH:";

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

			props1 = props1.replaceAll("IPADDRESS", builder2.toString());

		} catch (final Exception e)
		{
			e.printStackTrace();
		}
		System.out.println("Re Connection string: " + props1);
		return new ExtendedChannel(props1);

	}

	public static ExtendedChannel getInstance(final String hosts) throws ChannelException
	{

		String props = "TCP(bind_addr=IPADDRESS;start_port=7800;sock_conn_timeout=500;send_buf_size=100000 ;recv_buf_size=200000;loopback=true):"
		        +
		        "MPING( timeout=4000;"
		        +
		        "           receive_on_all_interfaces=true;"
		        +
		        "           mcast_port=7500;"
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
		        + " pbcast.STREAMING_STATE_TRANSFER: " +
		        "    pbcast.NAKACK(use_mcast_xmit=false;gc_lag=50;"
		        +
		        "                  retransmit_timeout=600,1200,2400,4800):"
		        +
		        "    UNICAST( timeout=300,600,1200):" +
		        "    pbcast.STABLE( stability_delay=1000 ;" +
		        "                   desired_avg_gossip=20000; " +
		        "                   max_bytes=0):" +
		        "    pbcast.GMS( print_local_addr=true ;" +
		        "                join_timeout=5000 ;" +
		        "                shun=true)";

		String props1 = " TCP(bind_addr=IPADDRESS;        bind_port=7900; "
		        +
		        "          loopback=true;"
		        +
		        "          recv_buf_size=20M;"
		        +
		        "          send_buf_size=640K;"
		        +
		        "          discard_incompatible_packets=true;"
		        +
		        "          max_bundle_size=64K;"
		        +
		        "          max_bundle_timeout=30;"
		        +
		        "          enable_bundling=true;"
		        +
		        "          use_send_queues=true;"
		        +
		        "          sock_conn_timeout=300;"
		        +
		        "          timer.num_threads=4;"
		        +

		        "         thread_pool.enabled=true;"
		        +
		        "          thread_pool.min_threads=1;"
		        +
		        "          thread_pool.max_threads=10;"
		        +
		        "          thread_pool.keep_alive_time=5000;"
		        +
		        "          thread_pool.queue_enabled=false;"
		        +
		        "          thread_pool.queue_max_size=100;"
		        +
		        "          thread_pool.rejection_policy=run;"
		        +

		        "          oob_thread_pool.enabled=true;"
		        +
		        "          oob_thread_pool.min_threads=1;"
		        +
		        "          oob_thread_pool.max_threads=8;"
		        +
		        "          oob_thread_pool.keep_alive_time=5000;"
		        +
		        "          oob_thread_pool.queue_enabled=false;"
		        +
		        "          oob_thread_pool.queue_max_size=100;"
		        +
		        "          oob_thread_pool.rejection_policy=run):"
		        +
		        "TCPPING( timeout=3000; "
		        +
		        "             initial_hosts=" + hosts + ";" +
		        "             port_range=1; " +
		        "             num_initial_members=3): " +
		        " BARRIER: " +
		        " FC(max_credits=2M; min_threshold=0.10): " +
		        " FRAG2(frag_size=60K):" +
		        " pbcast.STREAMING_STATE_TRANSFER: " +
		        "    MERGE2( max_interval=10000;"
		        +
		        "            min_interval=5000):"
		        +
		        "    FD_SOCK:"
		        +
		        "    FD( timeout=10000;"
		        +
		        "        max_tries=5):"
		        + "VERIFY_SUSPECT(timeout=1500):"
		        +
		        "    pbcast.NAKACK(use_mcast_xmit=false;gc_lag=50;"
		        +
		        "                  retransmit_timeout=600,1200,2400,4800;discard_delivered_msgs=false):"
		        +
		        "    UNICAST( timeout=300,600,1200):" +
		        "    pbcast.STABLE( stability_delay=1000 ;" +
		        "                   desired_avg_gossip=50000; " +
		        "                   max_bytes=400000):" +
		        "    pbcast.GMS( print_local_addr=true;" +
		        "                join_timeout=5000 ;" +
		        "                view_bundling=true):" +
		        "	 pbcast.FLUSH(timeout=10000)";

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

			props1 = props1.replaceAll("IPADDRESS", builder.toString());
			props = props.replaceAll("IPADDRESS", builder.toString());

		} catch (final Exception e)
		{
			e.printStackTrace();
		}

		System.out.println("Connection string: " + props1);
		return new ExtendedChannel(props1);
	}
}
