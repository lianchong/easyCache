package com.taobao.pamirs.sync.spi;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.Vector;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.distribution.CacheManagerPeerProvider;
import net.sf.ehcache.distribution.CachePeer;
import net.sf.ehcache.distribution.jgroups.JGroupManager;

import org.jgroups.Address;
import org.jgroups.blocks.GroupRequest;
import org.jgroups.blocks.RpcDispatcher;
import org.jgroups.stack.IpAddress;
import org.jgroups.util.RspList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.taobao.pamirs.commons.dao.Query;
import com.taobao.pamirs.commons.dao.Result;
import com.taobao.pamirs.sync.helper.AppContext;
import com.taobao.pamirs.sync.helper.ObjectUtil;
import com.taobao.pamirs.sync.protocol.DefaultProtocol;
import com.taobao.pamirs.sync.replicator.data.CacheConfigData;
import com.taobao.pamirs.sync.replicator.data.CacheManagerStatData;
import com.taobao.pamirs.sync.replicator.data.ExtendedChannel;

@Component
public class CacheManagerStatFacade
{
	@Autowired
	private CacheManager	cacheManager;

	@Autowired
	private ExtendedChannel	serviceInvokeChannel;

	private static Logger	logger	= LoggerFactory.getLogger(CacheManagerStatFacade.class);

	public CacheManagerStatData getStatus()
	{
		final CacheManagerStatData data = new CacheManagerStatData();
		data.setStatus(cacheManager.getStatus().toString());
		data.setName(cacheManager.getName());
		data.setRegisteredCaches(join(Arrays.asList(cacheManager.getCacheNames()), ","));
		data.setUuid(cacheManager.getClusterUUID());
		final StringBuilder builder = new StringBuilder();
		for (final String cacheName : cacheManager.getCacheNames())
		{
			final Cache cache = cacheManager.getCache(cacheName);
			final List<CachePeer> peerList = acquireCachePeers(cache);
			for (final CachePeer peer : peerList)
			{
				final JGroupManager jgm = (JGroupManager) peer;
				final List<Address> members = jgm.getBusMembership();
				for (final Address member : members)
				{
					builder.append(member.toString() + ";");
				}
			}
			break;
		}

		data.setHosts(builder.toString());
		return data;

	}

	public List<CacheConfigData> getHostDetail()
	{
		final List<CacheConfigData> ccds = new ArrayList<CacheConfigData>();
		for (final String cacheName : cacheManager.getCacheNames())
		{
			final Cache cache = cacheManager.getCache(cacheName);
			final List<CachePeer> peerList = acquireCachePeers(cache);
			for (final CachePeer peer : peerList)
			{
				final CacheConfigData ccd = new CacheConfigData();
				final JGroupManager jgm = (JGroupManager) peer;
				final IpAddress ip = (IpAddress) jgm.getBusLocalAddress();
				ccd.setConfigKey(cacheName + "[" + ip.getIpAddress() + ":" + ip.getPort() + "]");
				final List<Address> members = jgm.getBusMembership();
				final StringBuilder builder = new StringBuilder();
				for (final Address member : members)
				{
					builder.append(member.toString() + ";");
				}
				ccd.setConfigValue(builder.toString());
				ccds.add(ccd);
			}
		}

		return ccds;
	}

	public List<CacheConfigData> getCommunicationDetail() throws IOException
	{
		final List<CacheConfigData> ccd = new ArrayList<CacheConfigData>();
		String tcp = DefaultProtocol.TCP.split(":")[0];
		tcp = tcp.replaceAll("TCP\\(", "");
		tcp = tcp.replaceAll("\\)", "").trim();
		final StringBuilder builder = new StringBuilder();
		for (final String cacheName : cacheManager.getCacheNames())
		{
			final Cache cache = cacheManager.getCache(cacheName);
			final List<CachePeer> peerList = acquireCachePeers(cache);
			for (final CachePeer peer : peerList)
			{
				final JGroupManager jgm = (JGroupManager) peer;
				final List<Address> members = jgm.getBusMembership();
				for (final Address member : members)
				{
					builder.append(member.toString() + ";");
				}
			}
			break;
		}
		tcp = tcp.replaceAll("CLUSTER_MEMBERS", builder.toString());
		final String[] pairs = tcp.split(";");

		for (final String pair : pairs)
		{
			final CacheConfigData cd = new CacheConfigData();
			final String key = pair.split("=")[0];
			cd.setConfigKey(key.trim());
			if (pair.split("=").length > 1)
			{
				final String value = pair.split("=")[1];
				cd.setConfigValue(value.trim());
			}
			ccd.add(cd);
		}

		return ccd;
	}

	public List<CacheConfigData> getDiscoveryDetail() throws IOException
	{
		final List<CacheConfigData> ccd = new ArrayList<CacheConfigData>();
		String tcp = DefaultProtocol.TCP.split(":")[1];
		tcp = tcp.replaceAll("TCPPING\\(", "");
		tcp = tcp.replaceAll("\\)", "").trim();
		final StringBuilder builder = new StringBuilder();
		for (final String cacheName : cacheManager.getCacheNames())
		{
			final Cache cache = cacheManager.getCache(cacheName);
			final List<CachePeer> peerList = acquireCachePeers(cache);
			for (final CachePeer peer : peerList)
			{
				final JGroupManager jgm = (JGroupManager) peer;
				final List<Address> members = jgm.getBusMembership();
				for (final Address member : members)
				{
					builder.append(member.toString() + ";");
				}
			}
			break;
		}
		tcp = tcp.replaceAll("CLUSTER_MEMBERS", builder.toString());
		final String[] pairs = tcp.split(";");
		for (final String pair : pairs)
		{
			final CacheConfigData cd = new CacheConfigData();
			final String key = pair.split("=")[0];
			cd.setConfigKey(key.trim());
			if (pair.split("=").length > 1)
			{
				final String value = pair.split("=")[1];
				cd.setConfigValue(value.trim());
			}
			ccd.add(cd);
		}

		return ccd;
	}

	public static <T> String join(final Collection<T> objs, final String delimiter)
	{
		if ((objs == null) || objs.isEmpty())
		{
			return "";
		}
		final Iterator<T> iter = objs.iterator();
		if (!iter.hasNext())
		{
			return "";
		}
		final StringBuffer buffer = new StringBuffer(String.valueOf(iter.next()));
		while (iter.hasNext())
		{
			buffer.append(delimiter).append(String.valueOf(iter.next()));
		}
		return buffer.toString();
	}

	protected List acquireCachePeers(final Ehcache cache)
	{

		long timeForClusterToForm = 0;
		final CacheManagerPeerProvider cacheManagerPeerProvider = cache.getCacheManager()
		        .getCacheManagerPeerProvider("JGroups");
		if (cacheManagerPeerProvider != null)
		{
			timeForClusterToForm = cacheManagerPeerProvider.getTimeForClusterToForm();
		}

		List cachePeers = null;
		for (int i = 0; i <= timeForClusterToForm; i = i + 1000)
		{
			cachePeers = listRemoteCachePeers(cache);
			/*
			 * if (cachePeers == null) { break; } if (cachePeers.size() > 0) {
			 * break; }
			 */
			try
			{
				Thread.sleep(1000);
			} catch (final InterruptedException e)
			{
			}
		}

		return cachePeers;
	}

	protected List listRemoteCachePeers(final Ehcache cache)
	{
		final CacheManagerPeerProvider provider = cache.getCacheManager().getCacheManagerPeerProvider(
		        "JGroups");
		if (provider == null)
		{
			return null;
		} else
		{
			return provider.listRemoteCachePeers(cache);
		}

	}

	public static Properties createProperties(final String[] values) throws IllegalArgumentException
	{
		if (values.length % 2 != 0)
			throw new IllegalArgumentException("One value is missing.");

		final Properties props = new Properties();

		for (int i = 0; i < values.length; i += 2)
		{
			props.setProperty(values[i], values[i + 1]);
		}

		return props;
	}

	public List<IpAddress> getClusterMembers()
	{
		final List<IpAddress> hosts = new ArrayList<IpAddress>();
		for (final Address addr : serviceInvokeChannel.getView().getMembers())
		{
			hosts.add((IpAddress) addr);
		}

		return hosts;
	}

	public List<CacheConfigData> getMembership()
	{
		final List<CacheConfigData> hosts = new ArrayList<CacheConfigData>();
		for (final Address addr : serviceInvokeChannel.getView().getMembers())
		{
			final IpAddress ip = (IpAddress) addr;
			final CacheConfigData ccd = new CacheConfigData();
			ccd.setConfigKey(ip.getIpAddress().toString());
			hosts.add(ccd);
		}
		return hosts;
	}

	public Result<CacheConfigData> getAllBeans(final Query<CacheConfigData> query)
	{
		final List<CacheConfigData> ccds = new ArrayList<CacheConfigData>();
		for (final String bean : BeanFactoryUtils
		        .beanNamesIncludingAncestors(AppContext.getApplicationContext()
		                ))
		{
			final CacheConfigData ccd = new CacheConfigData();
			ccd.setConfigKey(bean);
			ccd.setConfigValue(AppContext.getApplicationContext().getBean(bean).getClass().toString());
			ccds.add(ccd);
		}
		Collections.sort(ccds, new Comparator<CacheConfigData>()
		{

			@Override
			public int compare(final CacheConfigData arg0, final CacheConfigData arg1)
			{
				return arg0.getConfigKey().toString().compareTo(arg1.getConfigKey().toString());
			}
		});
		final Result<CacheConfigData> result = new Result<CacheConfigData>();
		int start = query.getStart();
		int limit = query.getLimit();
		if (query.getStart() > ccds.size())
		{
			return null;
		}
		if (query.getLimit() > ccds.size())
		{
			limit = ccds.size();
		}
		if (query.getLimit() + query.getStart() > ccds.size())
		{
			start = query.getStart();
			limit = ccds.size() - start;
		}
		result.setDataList(ccds.subList(start, limit + start));
		result.setTotal(ccds.size());
		return result;
	}

	public List<CacheConfigData> getMethods(final String beanName)
	{
		final List<CacheConfigData> ccds = new ArrayList<CacheConfigData>();
		try
		{
			final Object bean = AppContext.getApplicationContext().getBean(beanName);
			for (final Method method : bean.getClass().getMethods())
			{
				final CacheConfigData ccd = new CacheConfigData();
				ccd.setConfigKey(method.getName());
				ccd.setConfigValue(method.toString());
				ccds.add(ccd);
			}
		} catch (final Exception e)
		{

		}

		return ccds;
	}

	/*
	 * 留下指定的成员
	 */
	private Vector<Address> filterMachine(final String address)
	{
		final Vector<Address> specAddress = new Vector<Address>();
		for (final Address addr : getClusterMembers())
		{
			final IpAddress ip = (IpAddress) addr;
			final String[] addresses = address.split(",");
			boolean flag = false;
			for (final String target : addresses)
			{
				if (target.trim().equals("") == false && ip.getIpAddress().toString().indexOf(target) >= 0)
				{
					flag = true;
					break;
				}
			}
			if (flag)
			{
				specAddress.add(addr);
			}
		}
		logger.info("Target Machine: " + specAddress);
		return specAddress;
	}

	/**
	 * 定向调用集群结点的远程方法
	 * 
	 * @param address
	 * @param beanName
	 * @param methodName
	 * @param arguments
	 * @return
	 */
	public synchronized CacheConfigData proxyInvoke(final String address, final String beanName,
	        final String methodName,
	        final String arguments)
	{
		logger.info("Invoking Remote Call: " + address + "=>" + beanName + ":" + methodName);

		final CacheConfigData ccd = new CacheConfigData();
		try
		{
			final RpcDispatcher dispatcher = new RpcDispatcher(serviceInvokeChannel, null, null, this);
			serviceInvokeChannel.connect("SERVICE_INVOKE_CHANNEL");
			final RspList response = dispatcher.callRemoteMethods(filterMachine(address), "invoke",
			        new Object[]
			{ beanName, methodName, arguments }, new Class[]
			{ String.class, String.class, String.class }, GroupRequest.GET_ALL, 0);
			logger.info("Response: " + response);
			ccd.setConfigKey(response.getResults());
			dispatcher.stop();
		} catch (final Exception e)
		{
			e.printStackTrace();
			ccd.setConfigKey(e.getMessage());
			return ccd;
		}

		return ccd;
	}

	public synchronized Object invoke(final String beanName, final String methodName, final String arguments)
	{
		try
		{
			final Object beanInstance = AppContext.getApplicationContext().getBean(beanName);
			final String[] argPairs = arguments.split(",");
			final List<Object> values = new LinkedList<Object>();
			final List<Class<?>> types = new LinkedList<Class<?>>();
			for (final String arg : argPairs)
			{
				if (arg.indexOf(":") >= 0)
				{
					values.add(ObjectUtil.convertToRightObject(arg.split(":")[1], arg.split(":")[0]));
					types.add(ObjectUtil.convertToRightClass(arg.split(":")[1]));
				} else if (arg.trim().equals(""))
				{

				} else
				{
					values.add(ObjectUtil.convertToRightObject("Object", arg.split(":")[0]));
					types.add(Object.class);
				}
			}
			final Method method =
			        beanInstance.getClass().getMethod(methodName, types.toArray(new Class[]
			        {}));
			final Object v = method.invoke(beanInstance, values.toArray(new Object[]
			{}));
			return v;
		} catch (final Exception e)
		{
			e.printStackTrace();
			return null;
		}
	}
}
