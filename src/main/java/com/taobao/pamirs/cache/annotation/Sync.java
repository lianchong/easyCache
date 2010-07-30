package com.taobao.pamirs.cache.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>
 * 用于同步服务的注解
 * </p>
 * 
 * @author mengzhu
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(
{ ElementType.METHOD })
public @interface Sync
{
	int delay() default 0;

	/**
	 * 指定同步的成员列表,格式为:<br/>
	 * <code>host1[port],host2[port],..</code>
	 */
	String targetAddress() default "";
}
