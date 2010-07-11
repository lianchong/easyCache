package com.taobao.pamirs.cache.proxy;

import java.io.Serializable;
import java.lang.management.ManagementFactory;

import javax.management.MBeanServer;

import net.sf.ehcache.CacheManager;
import net.sf.ehcache.management.ManagementService;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.taobao.pamirs.cache.event.MethodSyncServerBean;

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

	public void setMethodSyncServerBean(final MethodSyncServerBean methodSyncServerBean)
	{
		System.out.println("setting " + methodSyncServerBean);
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

	// @Pointcut("@annotation(com.taobao.pamirs.cache.Cached)")
	// private void testBeanExecution()
	// {
	// }
	//
	// @Around(
	// value =
	// "within(comd.taobao.pamirs..*) && call(* java.util.Map.get(..)) || call(* java.util.Map.contain*(..))")
	// public Object queryCache(final ProceedingJoinPoint jp) throws Throwable
	// {
	// if (logger.isDebugEnabled())
	// {
	// logger.debug(jp.getSignature().getName());
	// logger.debug(jp.toLongString());
	// logger.debug("[queryCache]得到数据: " + Arrays.toString(jp.getArgs()));
	// }
	// final Object key = jp.getArgs()[0];
	//
	// final Cache cache = cacheManager.getCache("pamirs-cache");
	// if (cache != null)
	// {
	// if (cache.get(key) == null)
	// {
	// return jp.proceed();
	// } else
	// {
	// logger.info("元素[" + cache.get(key) + "]已经被缓存");
	// return cache.get(key);
	// }
	// } else
	// {
	// logger.info("缓存获取失败");
	// }
	//
	// return jp.proceed();
	// }
	//
	// @Around("call(* java.util.Map.remove(..))")
	// public Object removeCache(final ProceedingJoinPoint jp) throws Throwable
	// {
	// try
	// {
	// final Cache cache = cacheManager.getCache("pamirs-cache");
	// final Object key = jp.getArgs()[0];
	//
	// if (cache != null)
	// {
	// if (cache.get(key) != null)
	// {
	// cache.remove(key, false);
	//
	// logger.info("Remove Element: key=" + key + "");
	// } else
	// {
	// logger.info("Element has been removed!");
	// }
	// }
	// return jp.proceed();
	// } catch (final Exception e)
	// {
	// e.printStackTrace();
	// return null;
	// }
	// }
	//
	// @Around("call(* java.util.Map.put(..))")
	// public Object putCache(final ProceedingJoinPoint jp) throws Throwable
	// {
	// try
	// {
	// final Cache cache = cacheManager.getCache("pamirs-cache");
	// final Object key = jp.getArgs()[0];
	// final Object value = jp.getArgs()[1];
	//
	// if (cache != null)
	// {
	// if ((cache.get(key) == null) || !cache.get(key).getValue().equals(value))
	// {
	// final Element element = new Element(key, value);
	// cache.put(element, false);
	// } else
	// {
	//
	// }
	// }
	// return jp.proceed();
	// } catch (final Exception e)
	// {
	// e.printStackTrace();
	// return null;
	// }
	// }

	@Pointcut("execution(@com.taobao.pamirs.cache.Sync * *(..))")
	public void pc()
	{

	}

	@Around("pc() || call(@com.taobao.pamirs.cache.Sync * *(..))")
	public Object test(final ProceedingJoinPoint jp) throws Throwable
	{
		System.out.println("调用方法: " + methodSyncServerBean.getServerInstance().info());

		methodSyncServerBean
		        .getServerInstance()
		        .callRemoteMethods(
		                "invokeMethod",
		                new Object[]
		                                                                            { jp.getSignature()
		                                                                                    .toLongString() },
		                new Class[]
		                                                                                                        { String.class });
		return jp.proceed();
	}
}
