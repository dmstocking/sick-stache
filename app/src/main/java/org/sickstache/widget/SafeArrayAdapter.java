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
import android.view.LayoutInflater;
import android.widget.ArrayAdapter;

import java.util.List;

public class SafeArrayAdapter<T> extends ArrayAdapter<T> {
	
	// WOW GOOGLE way to fucking private a layout inflater that everyone needed so I have to roll my own
	protected LayoutInflater layoutInflater;

	public SafeArrayAdapter(Context context, int resource, int textViewResourceId, List<T> objects) {
		super(context, resource, textViewResourceId, objects);
		init(context);
	}

	public SafeArrayAdapter(Context context, int resource, int textViewResourceId, T[] objects) {
		super(context, resource, textViewResourceId, objects);
		init(context);
	}

	public SafeArrayAdapter(Context context, int resource,
			int textViewResourceId) {
		super(context, resource, textViewResourceId);
		init(context);
	}

	public SafeArrayAdapter(Context context, int textViewResourceId, List<T> objects) {
		super(context, textViewResourceId, objects);
		init(context);
	}

	public SafeArrayAdapter(Context context, int textViewResourceId, T[] objects) {
		super(context, textViewResourceId, objects);
		init(context);
	}

	public SafeArrayAdapter(Context context, int textViewResourceId) {
		super(context, textViewResourceId);
		init(context);
	}
	
	private void init(Context context) {
		layoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
}
