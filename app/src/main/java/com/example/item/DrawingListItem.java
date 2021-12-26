package com.example.item;

public class DrawingListItem {

	private  String description;
	private  int icon;
	private  String name;

	public DrawingListItem(String description, String name, int icon) {
		this.name = name;
		this.description = description;
		this.icon=icon;
	}

	public String getDescription() {
		return description;
	}

	public int getIcon() {
		return icon;
	}

	public String getName() {
		return name;
	}

}
