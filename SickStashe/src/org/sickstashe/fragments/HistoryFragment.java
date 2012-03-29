/*
 * 	SickStashe is a android application for managing SickBeard
 * 	Copyright (C) 2012  David Stocking dmstocking@gmail.com
 * 
 * 	http://code.google.com/p/sick-stashe/
 * 	
 * 	SickStashe is free software: you can redistribute it and/or modify
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
package org.sickstashe.fragments;

import org.sickbeard.History;
import org.sickbeard.HistoryItem;
import org.sickstashe.R;
import org.sickstashe.app.LoadingListFragment;
import org.sickstashe.helper.Preferences;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class HistoryFragment extends LoadingListFragment<Void, Void, History> {
	
	private ArrayAdapter<HistoryItem> historyAdapter;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		historyAdapter = new ArrayAdapter<HistoryItem>( this.getActivity(), R.layout.history_item ){
			@Override
			public View getView( int position, View convertView, ViewGroup parent ) {
				View row;
				if ( convertView == null ) {
					row = getActivity().getLayoutInflater().inflate(R.layout.history_item, null);
				} else {
					row = convertView;
				}
				HistoryItem item = getItem(position);
				((TextView)row.findViewById(R.id.historyDateTextView)).setText(item.date);
				((TextView)row.findViewById(R.id.historyEpisodeTextView)).setText(item.show + " - " + item.season + "x" + item.episode + " [" + item.quality + " ]");
				if ( item.status.compareTo("Downloaded") == 0 ) {
					row.setBackgroundResource(R.color.sickbeard_today_background);
				} else {
					row.setBackgroundResource(R.color.sickbeard_soon_background);
				}
				return row;
			}
		};
	}
	
	@Override
	protected String getEmptyText() {
		return "Empty History";
	}

	@Override
	protected Void[] getRefreshParams() {
		return null;
	}

	@Override
	protected History doInBackground(Void... arg0) throws Exception {
		return Preferences.singleton.getSickBeard().history();
	}

	@Override
	protected void onProgressUpdate(Void... values) {
		;
	}

	@Override
	protected void onPostExecute(History result) {
		this.setListAdapter(historyAdapter);
		historyAdapter.clear();
		for ( HistoryItem item : result.items ) {
			historyAdapter.add(item);
		}
		if ( historyAdapter.getCount() == 0 ) {
			this.setListStatus(ListStatus.EMPTY);
		}
		historyAdapter.notifyDataSetChanged();
	}
}
