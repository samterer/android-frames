package com.androidfuture.set;

public class AFToggleSetItem extends AFSetItem {
	private boolean state;
	private String onText;
	private String offText;
	public boolean isState() {
		return state;
	}

	public void setState(boolean state) {
		this.state = state;
	}

	public AFToggleSetItem(int id, ItemType type, AFSetEventListener l,
			String t, String sub) {
		super(id, type, l, t, sub);
		state = false;
		// TODO Auto-generated constructor stub
	}

	public AFToggleSetItem(int id, ItemType type, AFSetEventListener l,
			String t, String sub, boolean state) {
		super(id, type, l, t, sub);
		this.state = state;
		// TODO Auto-generated constructor stub
	}
	
	public AFToggleSetItem(int id, ItemType type, AFSetEventListener l,
			String t, String sub, boolean state, String onText, String offText)
	{
		this(id, type, l, t, sub, state);
		this.onText = onText;
		this.offText = offText;
	}
	public String getOnText() {
		// TODO Auto-generated method stub
		return onText;
	}
	public String getOffText()
	{
		return offText;
	}
}
