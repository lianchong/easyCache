package com.taobao.pamirs.sync.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>
 * ����ͬ�������ע��
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
	 * ָ��ͬ���ĳ�Ա�б�,��ʽΪ:<br/>
	 * <code>host1[port],host2[port],..</code>
	 */
	String TargetAddress() default "";

	String BeanName() default "";

	String MethodName() default "";

	String AppName() default "DEFAULT";

	boolean SkipLocal() default true;

	InstanceType BeanType() default InstanceType.SPRING_BEAN;

	enum InstanceType
	{
		SPRING_BEAN, NORMAL_INSTANCE
	}
}
