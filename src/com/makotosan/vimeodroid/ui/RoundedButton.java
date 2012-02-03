/**
 * VimeoDroid - Unofficial Vimeo app for Android
 * Copyright (C) 2012 Makoto Schoppert
 * This program is free software; 
 * you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; 
 * either version 2 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program; 
 * if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 */

package com.makotosan.vimeodroid.ui;

import com.makotosan.vimeodroid.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
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
