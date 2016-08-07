/*
 * User: nisann
 * Date: 28/07/2016 
 *
 * Copyright (2005) IDI. All rights reserved.
 * This software is a proprietary information of Israeli Direct Insurance.
 * Created by IntelliJ IDEA. 
 */
package org.annotatedobserver.listener;

import org.annotatedobserver.EventObserver;
import org.annotatedobserver.data.Case;
import org.springframework.stereotype.Component;

/**
 *
 */
@Component
public class TestEventObserver {

	int saveCase;

	int printCase;

	int deleteCase;

	int negativeExample;

	int tempCase;

	int tempCase2;

	int multiple;

	int asyc;

	public void cleanup() {
		saveCase = 0;
		printCase = 0;
		deleteCase = 0;
		negativeExample = 0;
		tempCase = 0;
		tempCase2 = 0;
		multiple = 0;
		asyc = 0;
	}

	@EventObserver(events = "saveCase")
	public void saveCaseObserver(Object input) {
		Case mycase = (Case) input;
		saveCase += mycase.getId();
	}

	@EventObserver(events = "printCase")
	public void printCaseObserver(Object input) {
		String str = (String) input;
		printCase += str.length();
	}

	@EventObserver(events = "deleteCase")
	public void deleteCaseObserver(Object input) {
		deleteCase++;
	}

	@EventObserver(events = "negativeExample")
	public void negativeExampleObserver(Object input) {
		negativeExample++;
	}

	@EventObserver(events = "tempCase")
	public void tempCaseExampleObserver(Object input) {
		tempCase++;
	}

	@EventObserver(events = "tempCase2")
	public void tempCase2ExampleObserver(Object input) {
		tempCase2++;
	}

	@EventObserver(events = {"tempCase2", "deleteCase", "printCase"})
	public void multipleExampleObserver(Object input) {
		multiple++;
	}

	@EventObserver(events = "saveCase", async = true)
	public void asyncObserver(Object input) {
		Case mycase = (Case) input;
		asyc += mycase.getId();
	}


	public int getSaveCase() {
		return saveCase;
	}

	public int getPrintCase() {
		return printCase;
	}

	public int getDeleteCase() {
		return deleteCase;
	}

	public int getNegativeExample() {
		return negativeExample;
	}

	public int getTempCase() {
		return tempCase;
	}

	public int getTempCase2() {
		return tempCase2;
	}

	public int getMultiple() {
		return multiple;
	}

	public int getAsyc() {
		return asyc;
	}
}