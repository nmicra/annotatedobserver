/*
 * User: nisann
 * Date: 01/08/2016 
 *
 * Copyright (2005) IDI. All rights reserved.
 * This software is a proprietary information of Israeli Direct Insurance.
 * Created by IntelliJ IDEA. 
 */
package org.annotatedobserver;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import javassist.util.proxy.MethodHandler;
import javassist.util.proxy.ProxyFactory;
import javassist.util.proxy.ProxyObject;
import org.annotatedobserver.background.BackgroundExecutor;
import org.annotatedobserver.background.MethodBackgroundUnit;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.lang.model.type.NullType;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Collection;

/**
 * Processing the events, which are annotated with the @Observable
 */
@Component
public class EventObservableBeanPostProcessor implements BeanPostProcessor {

	@Autowired
	private ApplicationContext applicationContext;

	@Autowired
	private ConfigurableListableBeanFactory listableBeanFactory;

	@Autowired
	private EventObserversProvider eventObserversProvider;

	@Autowired
	private BackgroundExecutor backgroundExecutor;

	private final LoadingCache<String, Class> beanClassProvider = CacheBuilder.newBuilder().build(new CacheLoader<String, Class>() {
		@Override
		public Class load(String beanName) {
			if (!listableBeanFactory.containsBeanDefinition(beanName)) {
				return NullType.class;
			}
			BeanDefinition beanDefinition = listableBeanFactory.getBeanDefinition(beanName);
			String className = beanDefinition.getBeanClassName();

			Class<?> beanClass;
			try {
				beanClass = Class.forName(className);
			} catch (ClassNotFoundException e) {
				throw new RuntimeException(e);
			}
			return beanClass;
		}
	});

	@Override
	public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
		return bean;
	}

	@Override
	public Object postProcessAfterInitialization(final Object bean, String beanName) throws BeansException {

		if (!isObservableMethodsPresent(beanName)) {
			return bean;
		}

		final Class beanClass = beanClassProvider.getUnchecked(beanName);

		if (beanClass.getInterfaces().length > 0) {
			// -------------------------------------------------
			//              Create proxy for classes that has interfaces
			// -------------------------------------------------

			return Proxy.newProxyInstance(beanClass.getClassLoader(), beanClass.getInterfaces(), new InvocationHandler() {
				@Override
				public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
					Method originalMethod = beanClass.getMethod(method.getName(), method.getParameterTypes());

					if (originalMethod.isAnnotationPresent(Observable.class)) {
						Object retVal = method.invoke(bean, args);
						Observable annotation = originalMethod.getAnnotation(Observable.class);
						String eventname = annotation.eventName();

						Collection<Method> methodsRegisteredForEvent;
						if (StringUtils.isEmpty(eventname)) {
							methodsRegisteredForEvent = eventObserversProvider.getMethodsForEvent(method.getName());
						} else {
							methodsRegisteredForEvent = eventObserversProvider.getMethodsForEvent(eventname);
						}


						for (final Method method1 : methodsRegisteredForEvent) {
							Object observerBean = applicationContext.getBean(method1.getDeclaringClass());
							Object[] observerArgs = {retVal};

							if (eventObserversProvider.isAsyncMethod(method1)) {
								MethodBackgroundUnit backgroundUnit = new MethodBackgroundUnit(method1, observerBean, observerArgs);
								backgroundExecutor.execute(backgroundUnit);
							} else {
								method1.invoke(observerBean, observerArgs);
							}

						}
						return retVal;
					} else {
						return method.invoke(bean, args);
					}
				}
			});
		}

		// -------------------------------------------------
		//              Create proxy for classes that has no interfaces
		// -------------------------------------------------

		ProxyFactory factory = new ProxyFactory();
		factory.setSuperclass(beanClass);
		Class proxyClass = factory.createClass();

		MethodHandler handler = new MethodHandler() {

			@Override
			public Object invoke(Object self, Method overridden, Method forwarder, Object[] args) throws Throwable {

				Method originalMethod = beanClass.getMethod(overridden.getName(), overridden.getParameterTypes());
				if (originalMethod.isAnnotationPresent(Observable.class)) {
					Object retVal = forwarder.invoke(self, args);

					Observable annotation = originalMethod.getAnnotation(Observable.class);
					String eventname = annotation.eventName();

					Collection<Method> methodsRegisteredForEvent;
					if (StringUtils.isEmpty(eventname)) {
						methodsRegisteredForEvent = eventObserversProvider.getMethodsForEvent(overridden.getName());
					} else {
						methodsRegisteredForEvent = eventObserversProvider.getMethodsForEvent(eventname);
					}

					for (final Method method1 : methodsRegisteredForEvent) {
						Object observerBean = applicationContext.getBean(method1.getDeclaringClass());
						Object[] observerArgs = {retVal};

						if (eventObserversProvider.isAsyncMethod(method1)) {
							MethodBackgroundUnit backgroundUnit = new MethodBackgroundUnit(method1, observerBean, observerArgs);
							backgroundExecutor.execute(backgroundUnit);
						} else {
							method1.invoke(observerBean, observerArgs);
						}
					}
					return retVal;
				} else {
					return forwarder.invoke(self, args);
				}
			}
		};
		Object obj = null;
		try {
			obj = proxyClass.newInstance();
		} catch (InstantiationException e) {
			new RuntimeException(e);
		} catch (IllegalAccessException e) {
			new RuntimeException(e);
		}
		((ProxyObject) obj).setHandler(handler);

		return obj;

	}


	/**
	 * returns true if at least one method is annotated with @Observable
	 */

	private boolean isObservableMethodsPresent(String beanName) {

		Class aClass = beanClassProvider.getUnchecked(beanName);
		if (aClass.isAssignableFrom(NullType.class)) {
			return false;
		}
		Method[] methods = aClass.getMethods();

		for (final Method method : methods) {
			if (method.isAnnotationPresent(Observable.class)) {
				return true;
			}
		}


		return false;
	}
}