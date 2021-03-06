package com.taobao.pamirs.sync.config;

import net.sf.ehcache.config.CacheConfiguration;
import net.sf.ehcache.config.CacheConfiguration.BootstrapCacheLoaderFactoryConfiguration;
import net.sf.ehcache.config.CacheConfiguration.CacheEventListenerFactoryConfiguration;
import net.sf.ehcache.config.Configuration;
import net.sf.ehcache.config.DiskStoreConfiguration;
import net.sf.ehcache.config.FactoryConfiguration;

import org.jgroups.Channel;
import org.jgroups.ChannelException;
import org.jgroups.JChannel;
import org.jgroups.blocks.RpcDispatcher;

import com.taobao.pamirs.sync.helper.EthernetUtil;

/**
 * Helper class to Generate EhCacheManager instance instead of depending static
 * ehcache.xml
 * and jgroups xml
 * 
 * @author mengzhu
 */
public class EhCacheConfigurator
{

	private FactoryConfiguration generateMulticastPeerProviderFactoryConfig()
	{
		final FactoryConfiguration factory = new FactoryConfiguration();
		final String properties = "connect=" +
		        "	 TCP(	" +
		        "		              bind_port=9981;	" +
		        "		              start_port=9981;	" +
		        "		              loopback=true;	" +
		        "		              recv_buf_size=20000000;	" +
		        "		              send_buf_size=640000;	" +
		        "		              discard_incompatible_packets=true;	" +
		        "		              	" +
		        "		              max_bundle_size=64000;	" +
		        "		              max_bundle_timeout=30;	" +
		        "		              use_incoming_packet_handler=true;	" +
		        "		              enable_bundling=true;	" +
		        "		              use_send_queues=false;	" +
		        "		              sock_conn_timeout=300;	" +
		        "		              skip_suspected_members=true;	" +
		        "		              use_concurrent_stack=true;	" +
		        "		              	" +
		        "		              thread_pool.enabled=true;	" +
		        "		              thread_pool.min_threads=1;	" +
		        "		              thread_pool.max_threads=25;	" +
		        "		              thread_pool.keep_alive_time=5000;	" +
		        "		              thread_pool.queue_enabled=false;	" +
		        "		              thread_pool.queue_max_size=100;	" +
		        "		              thread_pool.rejection_policy=run;	" +
		        "		        	" +
		        "		              oob_thread_pool.enabled=true;	" +
		        "		              oob_thread_pool.min_threads=1;	" +
		        "		              oob_thread_pool.max_threads=8;	" +
		        "		              oob_thread_pool.keep_alive_time=5000;	" +
		        "		              oob_thread_pool.queue_enabled=false;	" +
		        "		              oob_thread_pool.queue_max_size=100;	" +
		        "		              oob_thread_pool.rejection_policy=run	" +
		        "		           ): 	" +
		        "		           	" +
		        "		        MPING(	" +
		        "		              receive_on_all_interfaces=true;" +
		        "		               mcast_port=7500;" +
		        "		              timeout=4000; 	" +
		        " 				ip_ttl=8; " +
		        "				num_ping_requests=1;" +
		        "		              num_initial_members=3;	" +
		        "		              ): 	" +
		        "		       VERIFY_SUSPECT(	" +
		        "		            timeout=5500;	" +
		        "		                ): 	" +
		        "		        MERGE2( 	" +
		        "		                max_interval=100000;	" +
		        "		                min_interval=20000   	" +
		        "		                ):     	" +
		        "		        pbcast.NAKACK(	" +
		        "		                use_mcast_xmit=false;	" +
		        "		                gc_lag=50;	" +
		        "		                retransmit_timeout=3000	" +
		        "		                ):	" +
		        "		       FD_SOCK:	" +
		        "		        FD( 	" +
		        "		            timeout=10000;	" +
		        "		            max_tries=5;	" +
		        "		            shun=true	" +
		        "		        ):	" +
		        "		        pbcast.STABLE(	" +
		        "		            stability_delay=1000;	" +
		        "		            desired_avg_gossip=50000	" +
		        "		        ):	" +
		        "		        VIEW_SYNC(avg_send_interval=60000):	" +
		        "		        FC(	" +
		        "		            max_credits=2000000;	" +
		        "		            min_threshold=0.10	" +
		        "		        ):	" +
		        "		        FRAG2(frag_size=60000):	" +
		        "		        pbcast.STREAMING_STATE_TRANSFER:	" +
		        "		        pbcast.GMS(	" +
		        "		            join_timeout=5000;	" +
		        "		            shun=true; 	" +
		        "		            print_local_addr=true;	" +
		        "		            view_bundling=true	" +
		        ")";
		factory.setProperties(properties);
		factory.setPropertySeparator("::");
		factory.setClass("net.sf.ehcache.distribution.jgroups.JGroupsCacheManagerPeerProviderFactory");

		return factory;
	}

	public Channel createServiceInvokeChannel(final String clusterMembers) throws ChannelException
	{
		final String properties =
		        "	 TCP(	" +
		                "		              bind_port=9900;	" +
		                "		              start_port=9900;	" +
		                "		              loopback=true;	" +
		                "		              recv_buf_size=20000000;	" +
		                "		              send_buf_size=640000;	" +
		                "		              discard_incompatible_packets=true;	" +
		                "		              	" +
		                "		              max_bundle_size=64000;	" +
		                "		              max_bundle_timeout=30;	" +
		                "		              use_incoming_packet_handler=true;	" +
		                "		              enable_bundling=true;	" +
		                "		              use_send_queues=false;	" +
		                "		              sock_conn_timeout=300;	" +
		                "		              skip_suspected_members=true;	" +
		                "		              use_concurrent_stack=true;	" +
		                "		              	" +
		                "		              thread_pool.enabled=true;	" +
		                "		              thread_pool.min_threads=1;	" +
		                "		              thread_pool.max_threads=25;	" +
		                "		              thread_pool.keep_alive_time=5000;	" +
		                "		              thread_pool.queue_enabled=false;	" +
		                "		              thread_pool.queue_max_size=100;	" +
		                "		              thread_pool.rejection_policy=run;	" +
		                "		        	" +
		                "		              oob_thread_pool.enabled=true;	" +
		                "		              oob_thread_pool.min_threads=1;	" +
		                "		              oob_thread_pool.max_threads=8;	" +
		                "		              oob_thread_pool.keep_alive_time=5000;	" +
		                "		              oob_thread_pool.queue_enabled=false;	" +
		                "		              oob_thread_pool.queue_max_size=100;	" +
		                "		              oob_thread_pool.rejection_policy=run	" +
		                "		           ): 	" +
		                "		           	" +
		                "		        TCPPING(	" +
		                "		              initial_hosts=" + clusterMembers + ";" +
		                "		              port_range=1;	" +
		                "		              timeout=3000; 	" +
		                "		              num_initial_members=3;	" +
		                "		              ): 	" +
		                "		       VERIFY_SUSPECT(	" +
		                "		            timeout=5500;	" +
		                "		                ): 	" +
		                "		        MERGE2( 	" +
		                "		                max_interval=100000;	" +
		                "		                min_interval=20000   	" +
		                "		                ):     	" +
		                "		        pbcast.NAKACK(	" +
		                "		                use_mcast_xmit=false;	" +
		                "		                gc_lag=50;	" +
		                "		                retransmit_timeout=3000	" +
		                "		                ):	" +
		                "		       FD_SOCK:	" +
		                "		        FD( 	" +
		                "		            timeout=10000;	" +
		                "		            max_tries=5;	" +
		                "		            shun=true	" +
		                "		        ):	" +
		                "		        pbcast.STABLE(	" +
		                "		            stability_delay=1000;	" +
		                "		            desired_avg_gossip=50000	" +
		                "		        ):	" +
		                "		        VIEW_SYNC(avg_send_interval=60000):	" +
		                "		        FC(	" +
		                "		            max_credits=2000000;	" +
		                "		            min_threshold=0.10	" +
		                "		        ):	" +
		                "		        FRAG2(frag_size=60000):	" +
		                "		        pbcast.STREAMING_STATE_TRANSFER:	" +
		                "		        pbcast.GMS(	" +
		                "		            join_timeout=5000;	" +
		                "		            shun=true; 	" +
		                "		            print_local_addr=true;	" +
		                "		            view_bundling=true	" +
		                ")";
		final Channel ch = new JChannel(properties.replaceAll("IPADDRESS", EthernetUtil.getNonLoopAddress()));
		System.out.println(properties.replaceAll("IPADDRESS", EthernetUtil.getNonLoopAddress()).trim());
		ch.connect("SERVICE_INVOKE_TUNNEL");
		return ch;
	}

	public RpcDispatcher generateServiceInvokeDispatcher(final String clusterMembers) throws ChannelException
	{
		return new RpcDispatcher(createServiceInvokeChannel(clusterMembers), null, null, this, true,
		        true);
	}

	private FactoryConfiguration generatePeerProviderFactoryConfig(final String clusterMembers)
	{
		final FactoryConfiguration factory = new FactoryConfiguration();
		final String properties = "connect=" +
		        "	 TCP(	" +
		        "		              bind_port=9981;	" +
		        "		              start_port=9981;	" +
		        "		              loopback=true;	" +
		        "		              recv_buf_size=20000000;	" +
		        "		              send_buf_size=640000;	" +
		        "		              discard_incompatible_packets=true;	" +
		        "		              	" +
		        "		              max_bundle_size=64000;	" +
		        "		              max_bundle_timeout=30;	" +
		        "		              use_incoming_packet_handler=true;	" +
		        "		              enable_bundling=true;	" +
		        "		              use_send_queues=false;	" +
		        "		              sock_conn_timeout=300;	" +
		        "		              skip_suspected_members=true;	" +
		        "		              use_concurrent_stack=true;	" +
		        "		              	" +
		        "		              thread_pool.enabled=true;	" +
		        "		              thread_pool.min_threads=1;	" +
		        "		              thread_pool.max_threads=25;	" +
		        "		              thread_pool.keep_alive_time=5000;	" +
		        "		              thread_pool.queue_enabled=false;	" +
		        "		              thread_pool.queue_max_size=100;	" +
		        "		              thread_pool.rejection_policy=run;	" +
		        "		        	" +
		        "		              oob_thread_pool.enabled=true;	" +
		        "		              oob_thread_pool.min_threads=1;	" +
		        "		              oob_thread_pool.max_threads=8;	" +
		        "		              oob_thread_pool.keep_alive_time=5000;	" +
		        "		              oob_thread_pool.queue_enabled=false;	" +
		        "		              oob_thread_pool.queue_max_size=100;	" +
		        "		              oob_thread_pool.rejection_policy=run	" +
		        "		           ): 	" +
		        "		           	" +
		        "		        TCPPING(	" +
		        "		              initial_hosts=" + clusterMembers + ";" +
		        "		              port_range=1;	" +
		        "		              timeout=3000; 	" +
		        "		              num_initial_members=3;	" +
		        "		              ): 	" +
		        "		       VERIFY_SUSPECT(	" +
		        "		            timeout=5500;	" +
		        "		                ): 	" +
		        "		        MERGE2( 	" +
		        "		                max_interval=100000;	" +
		        "		                min_interval=20000   	" +
		        "		                ):     	" +
		        "		        pbcast.NAKACK(	" +
		        "		                use_mcast_xmit=false;	" +
		        "		                gc_lag=50;	" +
		        "		                retransmit_timeout=3000	" +
		        "		                ):	" +
		        "		       FD_SOCK:	" +
		        "		        FD( 	" +
		        "		            timeout=10000;	" +
		        "		            max_tries=5;	" +
		        "		            shun=true	" +
		        "		        ):	" +
		        "		        pbcast.STABLE(	" +
		        "		            stability_delay=1000;	" +
		        "		            desired_avg_gossip=50000	" +
		        "		        ):	" +
		        "		        VIEW_SYNC(avg_send_interval=60000):	" +
		        "		        FC(	" +
		        "		            max_credits=2000000;	" +
		        "		            min_threshold=0.10	" +
		        "		        ):	" +
		        "		        FRAG2(frag_size=60000):	" +
		        "		        pbcast.STREAMING_STATE_TRANSFER:	" +
		        "		        pbcast.GMS(	" +
		        "		            join_timeout=5000;	" +
		        "		            shun=true; 	" +
		        "		            print_local_addr=true;	" +
		        "		            view_bundling=true	" +
		        ")";
		factory.setProperties(properties);
		factory.setPropertySeparator("::");
		factory.setClass("net.sf.ehcache.distribution.jgroups.JGroupsCacheManagerPeerProviderFactory");

		return factory;
	}

	private DiskStoreConfiguration generateDiskStoreConfig()
	{
		final DiskStoreConfiguration config = new DiskStoreConfiguration();
		config.setPath(System.getProperty("java.io.tmpdir"));
		return config;
	}

	/*
	 * maxElementsInMemory="10000" eternal="true"
	 * overflowToDisk="true" timeToIdleSeconds="0" timeToLiveSeconds="0"
	 * diskPersistent="false" diskExpiryThreadIntervalSeconds="120">
	 * class="net.sf.ehcache.distribution.jgroups.JGroupsCacheReplicatorFactory"
	 * properties="replicateAsynchronously=false, replicatePuts=true,
	 * replicateUpdates=true, replicateUpdatesViaCopy=false,
	 * replicateRemovals=true" />
	 * <bootstrapCacheLoaderFactory
	 * class=
	 * "net.sf.ehcache.distribution.jgroups.JGroupsBootstrapCacheLoaderFactory"
	 * properties="bootstrapAsynchronously=true" />
	 */

	private CacheConfiguration generateDefaultCacheConfig()
	{
		final CacheConfiguration cacheConfig = new CacheConfiguration();

		cacheConfig.setMaxElementsInMemory(10000);
		cacheConfig.setEternal(true);
		cacheConfig.setOverflowToDisk(true);
		cacheConfig.setTimeToIdleSeconds(0);
		cacheConfig.setTimeToLiveSeconds(0);
		cacheConfig.setDiskPersistent(false);
		cacheConfig.setDiskExpiryThreadIntervalSeconds(120);

		final CacheEventListenerFactoryConfiguration factory = new CacheEventListenerFactoryConfiguration();
		factory.setClass("net.sf.ehcache.distribution.jgroups.JGroupsCacheReplicatorFactory");
		factory.setProperties("replicateAsynchronously=false, replicatePuts=true,replicateUpdates=true, replicateUpdatesViaCopy=false,replicateRemovals=true");
		cacheConfig.addCacheEventListenerFactory(factory);

		final BootstrapCacheLoaderFactoryConfiguration bclf = new BootstrapCacheLoaderFactoryConfiguration();
		bclf.setClass("net.sf.ehcache.distribution.jgroups.JGroupsBootstrapCacheLoaderFactory");
		bclf.setProperties("bootstrapAsynchronously=true");
		cacheConfig.addBootstrapCacheLoaderFactory(bclf);

		return cacheConfig;
	}

	public CacheConfiguration generateDefinedCacheConfig(final String cacheName)
	{
		final CacheConfiguration cacheConfig = generateDefaultCacheConfig();
		cacheConfig.setName(cacheName);
		cacheConfig.setStatistics(true);
		return cacheConfig;
	}

	public CacheConfiguration generateServiceInvokeTunnelConfig()
	{
		final CacheConfiguration cacheConfig = new CacheConfiguration();

		cacheConfig.setMaxElementsInMemory(10000);
		cacheConfig.setEternal(true);
		cacheConfig.setOverflowToDisk(true);
		cacheConfig.setTimeToIdleSeconds(0);
		cacheConfig.setTimeToLiveSeconds(0);
		cacheConfig.setDiskPersistent(false);
		cacheConfig.setDiskExpiryThreadIntervalSeconds(120);
		cacheConfig.setName("SERVICE_INVOKE_TUNNEL");

		final CacheEventListenerFactoryConfiguration factory = new CacheEventListenerFactoryConfiguration();
		factory.setClass("com.taobao.pamirs.cache.event.PamirsCacheEventFactory");
		factory.setProperties("replicateAsynchronously=false, replicatePuts=true,replicateUpdates=true, replicateUpdatesViaCopy=false,replicateRemovals=true");
		cacheConfig.addCacheEventListenerFactory(factory);

		return cacheConfig;
	}

	public Configuration sit(final String clusterMembers)
	{
		final Configuration config = new Configuration();
		config.addDiskStore(generateDiskStoreConfig());
		config.addCacheManagerPeerProviderFactory(generatePeerProviderFactoryConfig(clusterMembers));
		config.setDefaultCacheConfiguration(generateDefaultCacheConfig());
		config.addCache(generateServiceInvokeTunnelConfig());

		config.setDynamicConfig(true);
		config.setUpdateCheck(false);
		config.setMonitoring("on");

		return config;
	}

	public Configuration suck(final String clusterMembers, final String caches)
	{
		final Configuration config = new Configuration();
		config.addDiskStore(generateDiskStoreConfig());
		config.addCacheManagerPeerProviderFactory(generatePeerProviderFactoryConfig(clusterMembers));
		config.setDefaultCacheConfiguration(generateDefaultCacheConfig());
		for (final String cacheName : caches.split(","))
		{
			config.addCache(generateDefinedCacheConfig(cacheName.trim()));
		}

		config.setDynamicConfig(true);
		config.setUpdateCheck(false);
		config.setMonitoring("on");

		return config;
	}

	public Configuration multiple_suck(final String caches)
	{
		final Configuration config = new Configuration();
		config.addDiskStore(generateDiskStoreConfig());
		config.addCacheManagerPeerProviderFactory(generateMulticastPeerProviderFactoryConfig());
		config.setDefaultCacheConfiguration(generateDefaultCacheConfig());
		for (final String cacheName : caches.split(","))
		{
			config.addCache(generateDefinedCacheConfig(cacheName.trim()));
		}

		config.setDynamicConfig(true);
		config.setUpdateCheck(false);

		return config;
	}
}
