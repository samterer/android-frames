package com.androidfuture.set;



import com.androidfuture.frames.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.RelativeLayout.LayoutParams;

public class AFSimpleSetItemView extends AFSetItemView {

	public AFSimpleSetItemView(Context context, final AFSetItem data) {
		super(context, data);
		LayoutInflater inflate = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		RelativeLayout layout = (RelativeLayout) inflate.inflate(R.layout.set_simple_item,null);
		((TextView)layout.findViewById(R.id.set_simple_title)).setText(data.getTitle());
		((TextView)layout.findViewById(R.id.set_simple_sub)).setText(data.getSubTitle());
		addView(layout,new FrameLayout.LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT));
		this.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View arg0) {
				if(data.getListener() != null)
				{
					data.getListener().onSetItemClick(AFSimpleSetItemView.this, data.getId());
				}
				
			}
			
		});
	}

}
