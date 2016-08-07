/*
 * User: nisann
 * Date: 04/08/2016 
 *
 * Copyright (2005) IDI. All rights reserved.
 * This software is a proprietary information of Israeli Direct Insurance.
 * Created by IntelliJ IDEA. 
 */
package org.annotatedobserver.background;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Details of the method that will be executed in async mode.
 */
public class MethodBackgroundUnit implements Runnable {

	private Method method;

	private Object object;

	private Object[] args = {};

	public MethodBackgroundUnit(Method method, Object object) {
		this.method = method;
		this.object = object;
	}

	public MethodBackgroundUnit(Method method, Object object, Object[] args) {
		this.method = method;
		this.object = object;
		this.args = args;
	}

	@Override
	public void run() {
		try {
			method.invoke(object, args);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		} catch (InvocationTargetException e) {
			throw new RuntimeException(e);
		}
	}
}