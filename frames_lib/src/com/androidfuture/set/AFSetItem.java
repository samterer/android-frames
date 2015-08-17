package com.androidfuture.set;

public class AFSetItem {
	public enum ItemType
	{
		SIMPLE_ITEM,
		TOGGLE_ITEM,
		SELECT_ITEM,
	}
private String title;
private String subTitle;
private ItemType type;
private AFSetEventListener listener;
public AFSetEventListener getListener() {
	return listener;
}
public void setListener(AFSetEventListener listener) {
	this.listener = listener;
}
public ItemType getType() {
	return type;
}
public void setType(ItemType type) {
	this.type = type;
}
private int id;
public int getId() {
	return id;
}
public void setId(int id) {
	this.id = id;
}
public AFSetItem(int id, ItemType type, AFSetEventListener l, String t, String sub)
{
	this.id = id;
	this.title = t;
	this.subTitle = sub;
	this.type = type;
	this.listener = l;
}
public String getTitle() {
	return title;
}
public void setTitle(String title) {
	this.title = title;
}
public String getSubTitle() {
	return subTitle;
}
public void setSubTitle(String subTitle) {
	this.subTitle = subTitle;
}

}
