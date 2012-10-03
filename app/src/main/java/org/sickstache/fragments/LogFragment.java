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

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import org.sickbeard.Logs;
import org.sickstache.R;
import org.sickstache.app.LoadingListFragment;
import org.sickstache.helper.Preferences;
import org.sickstache.widget.SafeArrayAdapter;

public class LogFragment extends LoadingListFragment<Void, Void, Logs> {

	public ArrayAdapter<String> logAdapter;
	
	@Override
	protected boolean isRetainInstance() {
		return true;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		logAdapter = new SafeArrayAdapter<String>(this.getActivity(), R.layout.simple_text_item) {
			@Override
			public View getView( int position, View convertView, ViewGroup parent ) {
				View row = convertView;
				if ( row == null ) {
					row = layoutInflater.inflate(R.layout.simple_text_item, null);
				}
				((TextView)row.findViewById(R.id.simple_text_item)).setText(getItem(position));
				return row;
			}
		};
	}

	@Override
	protected String getEmptyText() {
		return "Empty Log";
	}

	@Override
	protected Void[] getRefreshParams() {
		return null;
	}

	@Override
	protected Logs doInBackground(Void... arg0) throws Exception {
		return Preferences.getSingleton(getSherlockActivity()).getSickBeard().logs( Logs.LevelEnum.INFO );
	}

	@Override
	protected void onProgressUpdate(Void... values) {
		;
	}

	@Override
	protected void onPostExecute(Logs result) {
		setListAdapter(logAdapter);
		logAdapter.clear();
		for ( String item : result.items ) {
			logAdapter.add(item);
		}
		if ( logAdapter.getCount() == 0 ) {
			this.setListStatus(ListStatus.EMPTY);
		}
		logAdapter.notifyDataSetChanged();
	}
}
