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
package org.sickstache.fragments;

import org.sickbeard.json.ShowJson;
import org.sickstache.helper.Preferences;
import org.sickstache.widget.DefaultImageView;
import org.sickstache.R;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class ShowOptionsFragment extends ListFragment {

	public String tvdbid;
	
	public String[] options = { "Set Quality", "Pause/Unpause", "Refresh", "Update" };
	
	public ArrayAdapter<String> showOptionsAdapter;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		tvdbid = this.getActivity().getIntent().getStringExtra("tvdbid");
		showOptionsAdapter = new ArrayAdapter<String>(this.getActivity(), R.layout.simple_text_item, options){
			@Override
			public View getView( int position, View convertView, ViewGroup parent ) {
				View row = convertView;
				if ( row == null ) {
					row = getActivity().getLayoutInflater().inflate(R.layout.simple_text_item, null);
				}
				String item = getItem(position);
				TextView text = (TextView) row.findViewById(R.id.simple_text_item);
				text.setText(options[position]);
				return row;
			}
		};
		this.setListAdapter(showOptionsAdapter);
	}

	@Override
	public void onDestroyView() {
		// TODO Auto-generated method stub
		super.onDestroyView();
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		// TODO Auto-generated method stub
		super.onListItemClick(l, v, position, id);
	}

}
