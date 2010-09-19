package com.taobao.pamirs.sync.helper;

import org.springframework.context.ApplicationContext;

/**
 * This class provides application-wide access to the Spring ApplicationContext.
 * The ApplicationContext is injected by the class "ApplicationContextProvider".
 */
public class AppContext
{

	private static ApplicationContext	ctx;

	/**
	 * Injected from the class "ApplicationContextProvider" which is
	 * automatically
	 * loaded during Spring-Initialization.
	 */
	public static void setApplicationContext(final ApplicationContext applicationContext)
	{
		ctx = applicationContext;
	}

	/**
	 * Get access to the Spring ApplicationContext from everywhere in your
	 * Application.
	 * 
	 * @return
	 */
	public static ApplicationContext getApplicationContext()
	{
		return ctx;
	}
}
