package com.zzour.android.models;

// TODO add save to local storage logic.
public class SchoolArea {
	private int id;
	private String name;
	private String[] details;
	
	public SchoolArea(String name){
		this.name = name;
	}
	
	public String[] getDetails() {
		return details;
	}
	public void setDetails(String[] details) {
		this.details = details;
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
