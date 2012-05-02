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

import java.text.SimpleDateFormat;
import java.util.Date;

import org.sickbeard.SickBeard;
import org.sickbeard.SickBeard.StatusEnum;
import org.sickbeard.SickBeard.TimeEnum;
import org.sickbeard.json.FutureEpisodeJson;
import org.sickbeard.json.FutureJson;
import org.sickstache.EpisodeActivity;
import org.sickstache.app.LoadingListFragment;
import org.sickstache.app.LoadingSectionListFragment;
import org.sickstache.helper.Preferences;
import org.sickstache.widget.DefaultImageView;
import org.sickstache.R;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class FutureFragment extends LoadingSectionListFragment<FutureEpisodeJson, Void, Void, FutureJson> {

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		FutureEpisodeJson item = (FutureEpisodeJson)adapter.getItem(position);
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
	protected int getSectionLayoutId() {
		return R.layout.section_item;
	}

	@Override
	protected int getListTypeLayoutId() {
		return R.layout.future_banner_item;
	}

	@Override
	protected View getListTypeView(int position, FutureEpisodeJson item, View convertView, ViewGroup parent) {
		View row = convertView;
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

	@Override
	protected View getSectionView(int position, String item, View convertView, ViewGroup parent) {
		View row = convertView;
		TextView label = (TextView)row.findViewById(R.id.sectionTextView);
		label.setText(item);
		return row;
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
		setListAdapter(adapter);
		adapter.clear();
		if ( result.missed.size() > 0 ) {
			adapter.addSection("Missing");
			for ( FutureEpisodeJson s : result.missed ) {
				adapter.add(s);
			}
		}
		if ( result.today.size() > 0 ) {
			adapter.addSection("Today");
			for ( FutureEpisodeJson s : result.today ) {
				adapter.add(s);
			}
		}
		if ( result.soon.size() > 0 ) {
			SimpleDateFormat in = new SimpleDateFormat("yyyy-MM-dd");
			SimpleDateFormat out = new SimpleDateFormat("EEEE");
			FutureEpisodeJson prev = null;
			for ( FutureEpisodeJson s : result.soon ) {
				if ( prev == null || prev.airdate.compareTo(s.airdate) != 0) {
					// add section
					try {
						Date when = in.parse(s.airdate);
						adapter.addSection(out.format(when));
					} catch ( Exception e ) {
						Log.e("@FutureFragment", "Failed to parse airdate.");
					}
				}
				adapter.add(s);
				prev = s;
			}
		}
		if ( result.later.size() > 0 ) {
			adapter.addSection("Later");
			for ( FutureEpisodeJson s : result.later ) {
				adapter.add(s);
			}
		}
		if ( adapter.getCount() == 0 ) {
			this.setListStatus(ListStatus.EMPTY);
		}
		// the order we get the episodes is the order we want
		adapter.notifyDataSetChanged();
	}

}
