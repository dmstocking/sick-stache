/*
 * 	SickStache is a android application for managing SickBeard
 * 	Copyright (C) 2012  David Stocking dmstocking@gmail.com
 * 
 * 	http://code.google.com/p/sick-stashe/
 * 	
 * 	SickStache is free software: you can redistribute it and/or modify
 * 	it under the terms of the GNU General Public License as published by
 * 	the Free Software Foundation, either version 3 of the License, or
 * 	(at your option) any later version.
 * 	
 * 	This program is distributed in the hope that it will be useful,
 * 	but WITHOUT ANY WARRANTY; without even the implied warranty of
 * 	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * 	GNU General Public License for more details.
 * 	
 * 	You should have received a copy of the GNU General Public License
 * 	along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.sickstache.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import org.sickstache.R;

public class WorkingTextView extends RelativeLayout {
	
	public TextView text;
	public ProgressBar working;
	
	protected int errorDrawable = R.drawable.ic_text_error;
	protected int successDrawable = R.drawable.ic_text_success;

	public WorkingTextView(Context context) {
		super(context);
		constructor(context);
	}

	public WorkingTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		constructor(context);
	}

	public WorkingTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		constructor(context);
	}
	
	public void setIsWorking( boolean isWorking ) {
		if ( isWorking ) {
			working.setVisibility(View.VISIBLE);
			text.setEnabled(false);
		} else {
			working.setVisibility(View.GONE);
			text.setEnabled(true);
		}
	}
	
	public void setIsSuccessful( boolean success )
	{
		setIsWorking(false);
		if ( success ) {
			text.setCompoundDrawablesWithIntrinsicBounds(0, 0, successDrawable, 0);
		} else {
			text.setCompoundDrawablesWithIntrinsicBounds(0, 0, errorDrawable, 0);
		}
	}

	private void constructor(Context context) {
		LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View v = inflater.inflate(R.layout.working_text_view, this, true);
		text = (TextView)v.findViewById(R.id.itemTextView);
		working = (ProgressBar)v.findViewById(R.id.itemProgressBar);
	}

}
