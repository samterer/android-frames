package com.androidfuture.set;


import com.androidfuture.frames.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.ToggleButton;

public class AFToggleSetItemView extends AFSetItemView {

	public AFToggleSetItemView(Context context,final AFSetItem data) {
		super(context, data);
		LayoutInflater inflate = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		RelativeLayout layout = (RelativeLayout) inflate.inflate(R.layout.set_toggle_item,null);
		((TextView)layout.findViewById(R.id.set_toggle_title)).setText(data.getTitle());
		((TextView)layout.findViewById(R.id.set_toggle_sub)).setText(data.getSubTitle());
		ToggleButton toggle = (ToggleButton) layout.findViewById(R.id.set_toggle_button);
		if (data instanceof AFToggleSetItem)
		{
			AFToggleSetItem item = (AFToggleSetItem)data;
			toggle.setChecked(item.isState());
			
			
			if (item.getOnText() != null 
					&& item.getOffText() != null)
			{
				if (!item.isState())
				{
					toggle.setText(item.getOffText());
				}else
				{
					toggle.setText(item.getOnText());
					;
				}
				toggle.setTextOn(item.getOnText());
				toggle.setTextOff(item.getOffText());
			}
		}
		addView(layout,new FrameLayout.LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT));
		toggle.setOnCheckedChangeListener(new OnCheckedChangeListener(){

			@Override
			public void onCheckedChanged(CompoundButton button, boolean state) {
				
				data.getListener().onToggleStateChange(AFToggleSetItemView.this, state, data.getId());
				if (data instanceof AFToggleSetItem)
				{
					AFToggleSetItem item = (AFToggleSetItem)data;
					item.setState(state);
				}
			}
		});
		
	}

}
