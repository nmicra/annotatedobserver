/*
 * User: nisann
 * Date: 04/08/2016 
 *
 * Copyright (2005) IDI. All rights reserved.
 * This software is a proprietary information of Israeli Direct Insurance.
 * Created by IntelliJ IDEA. 
 */
package org.annotatedobserver.background;

import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Responsible for Background execution of events, registered with async=true
 */
@Component
public class BackgroundExecutor {


	private ExecutorService executor;

	@PostConstruct
	protected void init(){
		String threads = System.getProperty("annotatedobserver.threads");
		int threadsNum =10;

		if (!StringUtils.isEmpty(threads)){
			threadsNum = Integer.valueOf(threads);
		}

		executor = Executors.newFixedThreadPool(threadsNum);
	}

	public void execute(MethodBackgroundUnit methodBackgroundUnit) {
		executor.execute(methodBackgroundUnit);
	}

}