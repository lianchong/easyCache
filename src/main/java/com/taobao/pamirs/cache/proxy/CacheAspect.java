package com.taobao.pamirs.cache.proxy;

import java.io.Serializable;
import java.lang.management.ManagementFactory;
import java.util.HashMap;

import javax.management.MBeanServer;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.management.ManagementService;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.jgroups.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.taobao.pamirs.cache.event.MethodSyncServerBean;
import com.taobao.pamirs.cache.store.PamirsReplicatedHashMap;

/**
 * This file defines the Aspects
 * 
 * @author mengzhu
 */
@Aspect
public class CacheAspect implements Serializable
{
	/**
     * 
     */
	private static final long	 serialVersionUID	= 1L;
	private static Logger	     logger	          = LoggerFactory.getLogger(CacheAspect.class);
	private CacheManager	     cacheManager;
	private MethodSyncServerBean	methodSyncServerBean;
	private Channel	             channel;

	public Channel getChannel()
	{
		return channel;
	}

	public void setChannel(final Channel channel)
	{
		this.channel = channel;
	}

	public void setMethodSyncServerBean(final MethodSyncServerBean methodSyncServerBean)
	{
		this.methodSyncServerBean = methodSyncServerBean;
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


	/**
	 * intialize a map, and change it to PamirsReplicatedHashMap
	 */
	@Around("set(@com.taobao.pamirs.cache.Cached * *)")
	public Object fieldRegitry(final ProceedingJoinPoint pjp) throws Throwable
	{
		try
		{
			String data = pjp.getSignature().toLongString();
			if (data.indexOf("java.util.Map")>=0)
			{
				Cache cache = cacheManager.getCache("PAMIRS_CACHE");
				return pjp.proceed(new Object[]{new PamirsReplicatedHashMap<Serializable, Serializable>(new HashMap<Serializable, Serializable>(), cache)});
			}
			else
			{
				return pjp.proceed();
			}
		} catch (final Exception e)
		{
			e.printStackTrace();
			return pjp.proceed();
		}
	}

	@Pointcut("execution(@com.taobao.pamirs.cache.Sync * *(..))")
	public void pc()
	{

	}

	@Around("pc() || call(@com.taobao.pamirs.cache.Sync * *(..))")
	public Object test(final ProceedingJoinPoint jp) throws Throwable
	{
		return jp.proceed();
	}
	
	@Around("call(* com.taobao.pamirs.cache.store.PamirsReplicatedHashMap.*(..))")
	public Object cacheInvoke(ProceedingJoinPoint jp) throws Throwable
	{
		System.err.println("Invoking my map! Hey, you are putting '" + jp.getArgs()[0] + "' with value '" + jp.getArgs()[1] + "' into my container!");
		return jp.proceed();
	}
}
