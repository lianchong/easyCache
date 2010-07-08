package com.taobao.pamirs.cache;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface Cached
{
	int macCapacity() default 1000;

	boolean cache() default true;
}
