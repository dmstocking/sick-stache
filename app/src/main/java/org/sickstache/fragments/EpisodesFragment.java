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
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import org.sickbeard.Episode;
import org.sickbeard.Season;
import org.sickstache.EpisodeActivity;
import org.sickstache.R;
import org.sickstache.app.LoadingListFragment;
import org.sickstache.helper.Preferences;

import java.util.Comparator;

@Deprecated
public class EpisodesFragment extends LoadingListFragment<Void, Void, Season> {
	
//	private LinearLayout header;
	
	private ArrayAdapter<Episode> episodesAdapter;
	
	private String tvdbid;
	private String show;
	private String season;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Intent parent = this.getActivity().getIntent();
		tvdbid = parent.getStringExtra("tvdbid");
		show = parent.getStringExtra("show");
		season = parent.getStringExtra("season");
		episodesAdapter = new ArrayAdapter<Episode>(this.getActivity(), R.layout.episodes_item) {
			@Override
			public View getView( int position, View convertView, ViewGroup parent ) {
				View row = convertView;
				Episode item = getItem(position);
				if ( row == null ) {
					row = getActivity().getLayoutInflater().inflate(R.layout.episodes_item, null);
				}
				TextView text = (TextView) row.findViewById(R.id.episodesItemTextView);
				text.setText(item.episode + " - " + item.name);
				switch ( item.status ) {
				case WANTED:
					text.setBackgroundResource(R.color.sickbeard_wanted_background);
					break;
				case DOWNLOADED:
				case SNATCHED:
				case ARCHIVED:
					text.setBackgroundResource(R.color.sickbeard_downloaded_background);
					break;
				case SKIPPED:
				case IGNORED:
					text.setBackgroundResource(R.color.sickbeard_skipped_background);
					break;
				case UNAIRED:
					text.setBackgroundResource(R.color.sickbeard_unaired_background);
					break;
				}
				return row;
			}
		};
	}
	
//	@Override
//	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//		header = (LinearLayout)inflater.inflate(R.layout.show_fragment_header, null);
//		return super.onCreateView(inflater, container, savedInstanceState);
//	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		Intent intent = new Intent( this.getActivity(), EpisodeActivity.class );
		intent.putExtra("tvdbid", this.tvdbid);
		intent.putExtra("show", this.show);
		intent.putExtra("season", this.season);
		intent.putExtra("episode", this.episodesAdapter.getItem(position).episode);
		this.startActivity(intent);
	}

	@Override
	protected String getEmptyText() {
		return "No Episodes";
	}

	@Override
	protected Void[] getRefreshParams() {
		return null;
	}

	@Override
	protected Season doInBackground(Void... arg0) throws Exception {
		return Preferences.getSingleton(getSherlockActivity()).getSickBeard().showSeasons( tvdbid, season );
	}

	@Override
	protected void onProgressUpdate(Void... values) {
		;
	}

	@Override
	protected void onPostExecute(Season result) {
		this.setListAdapter(episodesAdapter);
		episodesAdapter.clear();
		for ( Episode e : result.getEpisodes() ) {
			episodesAdapter.add(e);
		}
		episodesAdapter.sort(new Comparator<Episode>() {
			public int compare( Episode a, Episode b ) {
				return - Integer.valueOf(a.episode).compareTo(Integer.valueOf(b.episode));
			}
		});
		if ( episodesAdapter.getCount() == 0 ) {
			this.setListStatus(ListStatus.EMPTY);
		}
		episodesAdapter.notifyDataSetChanged();
	}
}
