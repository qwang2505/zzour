package com.zzour.android.models;

import java.util.ArrayList;

// TODO add save school information to local storage logic.
public class School {
	private int id;
	private String name;
	private ArrayList<SchoolArea> area = new ArrayList<SchoolArea>();
	
	public School(String name){
		this.name = name;
	}
	
	public ArrayList<SchoolArea> getArea() {
		return area;
	}
	public void setArea(ArrayList<SchoolArea> area) {
		this.area = area;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
}
