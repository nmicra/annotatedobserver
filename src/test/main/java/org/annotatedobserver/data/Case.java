/*
 * User: nisann
 * Date: 28/07/2016 
 *
 * Copyright (2005) IDI. All rights reserved.
 * This software is a proprietary information of Israeli Direct Insurance.
 * Created by IntelliJ IDEA. 
 */
package org.annotatedobserver.data;

/**
 *
 */
public class Case {

	int id;

	String title;

	public Case(int id, String title) {
		this.id = id;
		this.title = title;
	}

	public Case(Case mycase) {
		id = mycase.getId();
		title = mycase.getTitle();
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	@Override
	public String toString() {
		return "Case{" +
				"id=" + id +
				", title='" + title + '\'' +
				'}';
	}
}