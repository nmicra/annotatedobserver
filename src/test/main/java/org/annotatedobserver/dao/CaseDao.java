/*
 * User: nisann
 * Date: 28/07/2016 
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
public class CaseDao implements CaseDaoI {

	@Observable
	public Case saveCase(Case mycase) {
		return new Case(mycase);
	}


	@Observable
	public String printCase(Case mycase) {
		return mycase.getTitle();
	}

}