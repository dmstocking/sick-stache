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
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import org.sickbeard.FutureEpisode;
import org.sickbeard.FutureEpisodes;
import org.sickbeard.FutureEpisodes.SortEnum;
import org.sickstache.EpisodeActivity;
import org.sickstache.R;
import org.sickstache.app.LoadingSectionListFragment;
import org.sickstache.helper.Preferences;
import org.sickstache.widget.DefaultImageView;

import java.text.SimpleDateFormat;
import java.util.Date;

public class FutureFragment extends LoadingSectionListFragment<FutureEpisode, Void, Void, FutureEpisodes> {
	
	@Override
	protected boolean isRetainInstance() {
		return true;
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		FutureEpisode item = (FutureEpisode)adapter.getItem(position);
		Intent intent = new Intent( this.getActivity(), EpisodeActivity.class );
		intent.putExtra("tvdbid", item.tvdbid + "");
		intent.putExtra("show", item.show_name);
		intent.putExtra("season", item.season + "");
		intent.putExtra("episode", item.episode + "");
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
	protected View getListTypeView(int position, FutureEpisode item, View convertView, ViewGroup parent) {
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
		image.setBanner( item.tvdbid+"" );
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
	protected FutureEpisodes doInBackground(Void... arg0) throws Exception {
		return Preferences.getSingleton(getSherlockActivity()).getSickBeard().future( SortEnum.DATE );
	}

	@Override
	protected void onProgressUpdate(Void... values) {
		;
	}

	@Override
	protected void onPostExecute(FutureEpisodes result) {
		setListAdapter(adapter);
		adapter.clear();
		if ( result.missed.size() > 0 ) {
			adapter.addSection("Missing");
			for ( FutureEpisode s : result.missed ) {
				adapter.add(s);
			}
		}
		if ( result.today.size() > 0 ) {
			adapter.addSection("Today");
			for ( FutureEpisode s : result.today ) {
				adapter.add(s);
			}
		}
		if ( result.soon.size() > 0 ) {
			SimpleDateFormat in = new SimpleDateFormat("yyyy-MM-dd");
			SimpleDateFormat out = new SimpleDateFormat("EEEE");
			FutureEpisode prev = null;
			for ( FutureEpisode s : result.soon ) {
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
			for ( FutureEpisode s : result.later ) {
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
