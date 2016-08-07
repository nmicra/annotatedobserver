/*
 * User: nisann
 * Date: 02/08/2016 
 *
 * Copyright (2005) IDI. All rights reserved.
 * This software is a proprietary information of Israeli Direct Insurance.
 * Created by IntelliJ IDEA. 
 */
package org.annotatedobserver.dao;

import org.annotatedobserver.Observable;
import org.annotatedobserver.data.Case;
import org.springframework.stereotype.Component;

/**
 *
 */
@Component
public class TempDao {

	@Observable
	public void deleteCase(Case mycase) {
		System.out.println(">>>case deleted: " + mycase);
	}


	@Observable(eventName = "tempCase2")
	public void tempCase(Case mycase) {
		System.out.println(">>> should fire event tempCase2");
	}

}