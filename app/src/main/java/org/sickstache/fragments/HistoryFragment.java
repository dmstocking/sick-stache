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

import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import org.sickbeard.History;
import org.sickbeard.HistoryItem;
import org.sickstache.EpisodeActivity;
import org.sickstache.R;
import org.sickstache.app.LoadingSectionListFragment;
import org.sickstache.helper.Preferences;

public class HistoryFragment extends LoadingSectionListFragment<HistoryItem, Void, Void, History> {
	
	@Override
	protected boolean isRetainInstance() {
		return true;
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		HistoryItem item = (HistoryItem)adapter.getItem(position);
		Intent intent = new Intent( this.getActivity(), EpisodeActivity.class );
		intent.putExtra("tvdbid", item.id);
		intent.putExtra("show", item.show);
		intent.putExtra("season", item.season);
		intent.putExtra("episode", item.episode);
//		if ( item.status == TimeEnum.MISSED ) {
//			intent.putExtra("status", StatusEnum.WANTED.toString());
//		} else {
//			intent.putExtra("status", StatusEnum.UNAIRED.toString());
//		}
		startActivity(intent);
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
		return Preferences.getSingleton(getSherlockActivity()).getSickBeard().history();
	}

	@Override
	protected void onProgressUpdate(Void... values) {
		;
	}

	@Override
	protected void onPostExecute(History result) {
		this.setListAdapter(adapter);
		adapter.clear();
//		if ( result.items.size() > 0 ) {
//			NotificationsTask.updateLastHistoryItem(result.items.get(0));
//		}
		String lastDate = "";
		for ( HistoryItem item : result.items ) {
			if ( item.date.regionMatches(0, lastDate, 0, 10) == false ) {
				adapter.addSection(item.date.substring(0, 10));
				lastDate = item.date;
			}
			adapter.add(item);
		}
		if ( adapter.getCount() == 0 ) {
			this.setListStatus(ListStatus.EMPTY);
		}
		adapter.notifyDataSetChanged();
	}

	@Override
	protected int getSectionLayoutId() {
		return R.layout.section_item;
	}

	@Override
	protected int getListTypeLayoutId() {
		return R.layout.history_item;
	}

	@Override
	protected View getSectionView(int position, String item, View convertView, ViewGroup parent) {
		View row = convertView;
		TextView label = (TextView)row.findViewById(R.id.sectionTextView);
		label.setText(item);
		return row;
	}

	@Override
	protected View getListTypeView(int position, HistoryItem item, View convertView, ViewGroup parent) {
		View row = convertView;
		((TextView)row.findViewById(R.id.historyEpisodeTextView)).setText(item.date.substring(11) + " - " + item.show + " - " + item.season + "x" + item.episode + " [" + item.quality + " ]");
		if ( item.status.compareTo("Downloaded") == 0 ) {
			row.setBackgroundResource(R.color.sickbeard_today_background);
		} else {
			row.setBackgroundResource(R.color.sickbeard_soon_background);
		}
		return row;
	}
}
