package com.taobao.pamirs.sync.aspect;

import java.io.Serializable;
import java.lang.management.ManagementFactory;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

import javax.management.MBeanServer;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.management.ManagementService;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.taobao.pamirs.sync.annotation.Cached;
import com.taobao.pamirs.sync.annotation.Sync;
import com.taobao.pamirs.sync.config.EhCacheConfigurator;
import com.taobao.pamirs.sync.replicator.data.ExtendedChannel;
import com.taobao.pamirs.sync.replicator.data.PamirsReplicatedData;
import com.taobao.pamirs.sync.replicator.service.RpcManager;

/**
 * This file defines the Aspects
 * 
 * @author mengzhu
 */
@Aspect
public class CacheAspect implements Serializable
{
	private static final long	                           serialVersionUID	   = 1L;
	private static Logger	                               logger	           = LoggerFactory
	                                                                                   .getLogger(CacheAspect.class);
	private CacheManager	                               cacheManager;
	private static final ConcurrentHashMap<String, String>	cacheRegistry	   = new ConcurrentHashMap<String, String>(
	                                                                                   5);
	/**
	 * 用于记录方法调用
	 */
	private static final ConcurrentHashMap<String, String>	invocationRegistry	= new ConcurrentHashMap<String, String>();
	private ExtendedChannel	                               serviceInvokeChannel;
	@Autowired
	private RpcManager	                                   rpcManager;

	public RpcManager getRpcManager()
	{
		return rpcManager;
	}

	public void setRpcManager(final RpcManager rpcManager)
	{
		this.rpcManager = rpcManager;
	}

	public ExtendedChannel getServiceInvokeChannel()
	{
		return serviceInvokeChannel;
	}

	public void setServiceInvokeChannel(final ExtendedChannel serviceInvokeChannel)
	{
		this.serviceInvokeChannel = serviceInvokeChannel;
	}

	public static ConcurrentHashMap<String, String> getCacheRegistry()
	{
		return cacheRegistry;
	}

	public CacheManager getCacheManager()
	{
		return cacheManager;
	}

	public void setCacheManager(final CacheManager cacheManager)
	{
		this.cacheManager = cacheManager;
		try
		{
			/* 尝试注册 jmx, 失败就不管了 */
			final MBeanServer mBeanServer = ManagementFactory.getPlatformMBeanServer();
			ManagementService.registerMBeans(cacheManager, mBeanServer, false, false, false, true);
		} catch (final Exception e)
		{

		}
	}

	@Pointcut("set(@com.taobao.pamirs.sync.annotation.Cached * *) && @annotation(cached)")
	public void fieldIntercept(final Cached cached)
	{

	}

	/**
	 * intialize a map, and change it to PamirsReplicatedHashMap
	 */
	@Around("fieldIntercept(cached)")
	public Object fieldRegitry(final ProceedingJoinPoint pjp, final Cached cached) throws Throwable
	{
		try
		{
			final String data = pjp.getSignature().toLongString();
			/**
			 * 任意java.util.Map的子类将被替换
			 */
			if (data.indexOf("java.util.Map") >= 0)
			{
				logger.info(data + " 被截获, 类型转换为[com.taobao.pamirs.sync.replicator.data.PamirsReplicatedData]");
				cacheRegistry.put(cached.CacheName(), data);
				Cache cache = cacheManager.getCache(cached.CacheName());
				logger.info("获取cache实例[" + cached.CacheName() + "]: " + cache);
				if (cache == null)
				{
					logger.warn("按需创建新的Cache实例:...");
					cache = new Cache(
					        new EhCacheConfigurator().generateDefinedCacheConfig(cached.CacheName()));
					cacheManager.addCache(cache);
				}
				return pjp.proceed(new Object[]
				{
				        new PamirsReplicatedData<Serializable, Serializable>(
				                new HashMap<Serializable, Serializable>(), cache)
				});
			} else
			{
				return pjp.proceed();
			}
		} catch (final Exception e)
		{
			e.printStackTrace();
			return pjp.proceed();
		}
	}

	@Pointcut("execution(@com.taobao.pamirs.sync.annotation.Sync * *(..))")
	public void pc()
	{

	}

	@Around("execution(@com.taobao.pamirs.sync.annotation.Sync * *(..)) && @annotation(sync)")
	public Object test(final ProceedingJoinPoint jp, final Sync sync) throws Throwable
	{
		String className = sync.BeanName();
		if (className.equals(""))
		{
			className = jp.getSignature().getDeclaringTypeName();
		}

		String methodName = sync.MethodName();
		if (methodName.equals(""))
		{
			methodName = jp.getSignature().getName();
		}

		final Object[] args = jp.getArgs();
		rpcManager.invokeMethod(className, methodName, true, args, sync.SkipLocal());

		return jp.proceed();
	}

	@Around("execution(* com.taobao.pamirs.sync.replicator.data.PamirsReplicatedData.put(..))")
	public Object cacheInvoke(final ProceedingJoinPoint jp) throws Throwable
	{
		return jp.proceed();
	}

	public Object invoke(final String target)
	{
		System.out.println(target);
		return target;
	}
}
