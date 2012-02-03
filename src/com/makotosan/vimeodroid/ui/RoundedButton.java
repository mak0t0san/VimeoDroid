package com.makotosan.vimeodroid.ui;

import com.makotosan.vimeodroid.R;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

public class RoundedButton extends View {

	private TextView descText;
	private TextView valueText;
	private int mBgColor;
	
	/**
	 * Construct object
	 * @param context
	 * @param attrs
	 * @see android.view.View#View(android.content.Context, android.util.AttributeSet)
	 */
	public RoundedButton(Context context, AttributeSet attrs) {
		super(context);
		/*
		 TypedArray a = context.obtainStyledAttributes(attrs,
	                R.styleable.LabelView);

	        CharSequence s = a.getString(R.styleable.LabelView_text);
	        if (s != null) {
	            setText(s.toString());
	        }
*/
		
		String infSvc = Context.LAYOUT_INFLATER_SERVICE;
		LayoutInflater inflater;
		inflater = (LayoutInflater) getContext().getSystemService(infSvc);
		//inflater.inflate(R.layout.button_layout, this, true);

		descText = (TextView) findViewById(R.id.button_desc);
		// descText.setText(description);

		valueText = (TextView) findViewById(R.id.button_value);
		
		// valueText.setText(value);
		
		// this.setBackgroundColor(bgColor);
	}
	
	public void setDescription(String description){
		this.descText.setText(description);
	}
	
	public void setValue(String value){
		this.valueText.setText(value);
	}
	
	public void setBgColor(int bgcolor){
		this.mBgColor = bgcolor;
	}
}
