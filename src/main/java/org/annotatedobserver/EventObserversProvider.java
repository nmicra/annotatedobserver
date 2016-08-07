/*
 * User: nisann
 * Date: 01/08/2016 
 *
 * Copyright (2005) IDI. All rights reserved.
 * This software is a proprietary information of Israeli Direct Insurance.
 * Created by IntelliJ IDEA. 
 */
package org.annotatedobserver;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Provides metadata of the methods listening for events.
 */
@Component
public class EventObserversProvider {

	@Autowired
	private ConfigurableListableBeanFactory listableBeanFactory;

	/**
	 * EventName, Methods to fire when events is occured
	 */
	private Multimap<String, Method> registeredForEvents;

	/**
	 * list of methods that have to be executed asynchronously.
	 */
	private Set<Method> asyncMethods = new HashSet<Method>();

	@PostConstruct
	public void init() {
		registeredForEvents = HashMultimap.create();
		String[] beanDefinitionNames = listableBeanFactory.getBeanDefinitionNames();

		for (final String beanDefinitionName : beanDefinitionNames) {
			Method[] methodsForBean = getMethodsForBean(beanDefinitionName);
			for (final Method method : methodsForBean) {
				if (method.isAnnotationPresent(EventObserver.class)) {
					EventObserver annotation = method.getAnnotation(EventObserver.class);
					String[] events = annotation.events();
					for (final String event : events) {
						registeredForEvents.put(event, method);
					}

					if (annotation.async()){
						asyncMethods.add(method);
					}
				}
			}
		}
	}


	private Method[] getMethodsForBean(String beanName) {
		BeanDefinition beanDefinition = listableBeanFactory.getBeanDefinition(beanName);
		String className = beanDefinition.getBeanClassName();
		Class<?> beanClass;
		try {
			beanClass = Class.forName(className);
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
		Method[] methods = beanClass.getMethods();
		return methods;
	}


	public Collection<Method> getMethodsForEvent(String event) {
		return registeredForEvents.get(event);
	}

	public boolean isAsyncMethod(Method method){
		if (asyncMethods.contains(method)){
			return true;
		}

		return false;
	}
}