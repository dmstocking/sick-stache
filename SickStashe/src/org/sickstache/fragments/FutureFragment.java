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

import org.sickbeard.SickBeard;
import org.sickbeard.SickBeard.StatusEnum;
import org.sickbeard.SickBeard.TimeEnum;
import org.sickbeard.json.FutureEpisodeJson;
import org.sickbeard.json.FutureJson;
import org.sickstache.EpisodeActivity;
import org.sickstache.app.LoadingListFragment;
import org.sickstache.helper.Preferences;
import org.sickstache.view.DefaultImageView;
import org.sickstache.R;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class FutureFragment extends LoadingListFragment<Void, Void, FutureJson> {
	
	private ArrayAdapter<FutureEpisodeJson> futureAdapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	    futureAdapter = new ArrayAdapter<FutureEpisodeJson>(this.getActivity(), R.layout.future_banner_item) {
			@Override
			public View getView( int position, View convertView, ViewGroup parent ) {
				View row = convertView;
				if ( convertView == null ) {
					row = getActivity().getLayoutInflater().inflate(R.layout.future_banner_item, null);
				}
				FutureEpisodeJson item = getItem(position);
				switch ( item.when ) {
				case MISSED:
					row.setBackgroundResource(R.color.sickbeard_missed_background);
					break;
				case TODAY:
					row.setBackgroundResource(R.color.sickbeard_today_background);
					break;
				case SOON:
					row.setBackgroundResource(R.color.sickbeard_soon_background);
					break;
				case LATER:
					row.setBackgroundResource(R.color.sickbeard_later_background);
					break;
				}
				DefaultImageView image = (DefaultImageView) row.findViewById(R.id.showImage);
				image.defaultResource = R.drawable.default_banner;
				try {
					image.setImageJavaURI( Preferences.singleton.getSickBeard().showGetBanner(item.tvdbid+"") );
				} catch (Exception e) {
					;
				}
				TextView ep = (TextView) row.findViewById(R.id.episode);
				ep.setText(item.season + "x" + item.episode + " - " + item.ep_name + " " + item.airdate);
				TextView air = (TextView) row.findViewById(R.id.airing);
				air.setText(item.airs + " on " + item.network + " [" + item.quality + "]");
				return row;
			}
		};
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		FutureEpisodeJson item = futureAdapter.getItem(position);
		Intent intent = new Intent( this.getActivity(), EpisodeActivity.class );
		intent.putExtra("tvdbid", item.tvdbid + "");
		intent.putExtra("show", item.show_name);
		intent.putExtra("season", item.season + "");
		intent.putExtra("episode", item.episode + "");
		if ( item.when == TimeEnum.MISSED ) {
			intent.putExtra("status", StatusEnum.WANTED.toString());
		} else {
			intent.putExtra("status", StatusEnum.UNAIRED.toString());
		}
		startActivity(intent);
	}

	@Override
	protected String getEmptyText() {
		return "No Future Episodes";
	}

	@Override
	protected Void[] getRefreshParams() {
		return null;
	}

	@Override
	protected FutureJson doInBackground(Void... arg0) throws Exception {
		return Preferences.singleton.getSickBeard().future( SickBeard.SortEnum.DATE );
	}

	@Override
	protected void onProgressUpdate(Void... values) {
		;
	}

	@Override
	protected void onPostExecute(FutureJson result) {
		setListAdapter(futureAdapter);
		futureAdapter.clear();
		for ( FutureEpisodeJson s : result.missed ) {
			futureAdapter.add(s);
		}
		for ( FutureEpisodeJson s : result.today ) {
			futureAdapter.add(s);
		}
		for ( FutureEpisodeJson s : result.soon ) {
			futureAdapter.add(s);
		}
		for ( FutureEpisodeJson s : result.later ) {
			futureAdapter.add(s);
		}
		if ( futureAdapter.getCount() == 0 ) {
			this.setListStatus(ListStatus.EMPTY);
		}
		// the order we get the episodes is the order we want
		futureAdapter.notifyDataSetChanged();
	}
}
