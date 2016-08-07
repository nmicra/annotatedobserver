/*
 * User: nisann
 * Date: 02/08/2016 
 *
 * Copyright (2005) IDI. All rights reserved.
 * This software is a proprietary information of Israeli Direct Insurance.
 * Created by IntelliJ IDEA. 
 */
package org.annotatedobserver.dao;

import org.annotatedobserver.data.Case;

/**
 *
 */
public interface CaseDaoI {

	Case saveCase(Case mycase);
	String printCase(Case mycase);

}