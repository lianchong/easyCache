package com.taobao.pamirs.cache.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 用于将成员对象同步化的注解
 * <p>
 * 只有当对象的类型是<b>java.util.Map</b>的任意子类时,<br/>
 * 这个注解才会生效
 * </p>
 * <p>
 * <b>TODO:</b> 增加其它数据类型的支持
 * </p>
 * 
 * @author mengzhu
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Cached
{
	int MaxCapacity() default 10000;

	/**
	 * <p>
	 * 代表着用于同步的缓存通道, 以String值作为全局唯一标识符(<b>Fully Qualified Name</b>)
	 * </p>
	 * <p>
	 * 默认值: PAMIRS_CACHE
	 * </p>
	 * 
	 * @return
	 */
	String CacheName() default "PAMIRS_CACHE";

	String Description() default "Add description here";

}
