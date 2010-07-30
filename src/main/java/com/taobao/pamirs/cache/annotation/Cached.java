package com.taobao.pamirs.cache.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * ���ڽ���Ա����ͬ������ע��
 * <p>
 * ֻ�е������������<b>java.util.Map</b>����������ʱ,<br/>
 * ���ע��Ż���Ч
 * </p>
 * <p>
 * <b>TODO:</b> ���������������͵�֧��
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
	 * ����������ͬ���Ļ���ͨ��, ��Stringֵ��Ϊȫ��Ψһ��ʶ��(<b>Fully Qualified Name</b>)
	 * </p>
	 * <p>
	 * Ĭ��ֵ: PAMIRS_CACHE
	 * </p>
	 * 
	 * @return
	 */
	String CacheName() default "PAMIRS_CACHE";

	String Description() default "Add description here";

}
