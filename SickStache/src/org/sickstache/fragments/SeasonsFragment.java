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

import java.util.Collections;
import java.util.Comparator;

import org.sickbeard.Episode;
import org.sickbeard.Season;
import org.sickbeard.Show;

import org.sickstache.EditShowActivity;
import org.sickstache.EpisodeActivity;
import org.sickstache.EpisodesActivity;
import org.sickstache.app.ExpandableLoadingListFragment;
import org.sickstache.app.LoadingListFragment;
import org.sickstache.helper.Preferences;
import org.sickstache.widget.DefaultImageView;
import org.sickstache.R;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class SeasonsFragment extends ExpandableLoadingListFragment<Integer,Episode,String, Void, Show> {

	private String tvdbid;
	private String show;
	
	private LinearLayout header;
	
	private DefaultImageView showImage;
	private TextView showView;
	private TextView airs;
	private TextView quality;
	private TextView language;
	private TextView paused;
	private TextView airbydate;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Intent parent = this.getActivity().getIntent();
		tvdbid = parent.getStringExtra("tvdbid");
		show = parent.getStringExtra("show");
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if ( hasHeader() ) {
			header = (LinearLayout)inflater.inflate(R.layout.show_fragment_header, null);
		}
		return super.onCreateView(inflater, container, savedInstanceState);
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.seasons_menu, menu);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		if ( hasHeader() ) {
			this.getListView().addHeaderView(header,null,false);
		}
		showView = (TextView) view.findViewById(R.id.show);
		showView.setText(show);
		airs = (TextView) view.findViewById(R.id.airsTextView);
		quality = (TextView) view.findViewById(R.id.qualityTextView);
		language = (TextView) view.findViewById(R.id.languageTextView);
		paused = (TextView) view.findViewById(R.id.pausedTextView);
		airbydate = (TextView) view.findViewById(R.id.airbydateTextView);
		showImage = (DefaultImageView) view.findViewById(R.id.showImage);
		try {
			showImage.setImageJavaURI( Preferences.singleton.getSickBeard().showGetBanner(tvdbid) );
		} catch (Exception e) {
			;
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch ( item.getItemId() ) {
		case R.id.editShowMenuItem:
			Intent intent = new Intent( this.getActivity(), EditShowActivity.class );
			intent.putExtra("tvdbid", tvdbid);
			intent.putExtra("show", show);
			this.startActivity(intent);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	protected String getEmptyText() {
		return "Show Could Not Be Found";
	}

	@Override
	protected String[] getRefreshParams() {
		return new String[] { tvdbid };
	}

	@Override
	protected Show doInBackground(String... arg0) throws Exception {
		return Preferences.singleton.getSickBeard().show(arg0[0],true);
	}

	@Override
	protected void onProgressUpdate(Void... values) {
		;
	}

	@Override
	protected void onPostExecute(Show result) {
		setListAdapter(adapter);
		airs.setText(result.airs);
		quality.setText(result.quality);
		language.setText(result.language);
		// TODO this is probably not the best way to handle this
		paused.setText(result.paused ? "Yes" : "No");
		airbydate.setText(result.airbydate ? "Yes" : "No");
		adapter.clear();
		Collections.reverse(result.seasonList);
		for ( Season s : result.seasonList ) {
			int group = adapter.addGroup(s.season);
			Collections.reverse(s.getEpisodes());
			for ( Episode e : s.getEpisodes() ) {
				adapter.addChild(group, e);
			}
		}
		adapter.notifyDataSetChanged();
	}

	@Override
	protected View getChildView(Integer group, Episode item, int groupNum, int itemNum, boolean isLastView, View convertView, ViewGroup root) {
		if ( convertView == null )
			convertView = LayoutInflater.from(getSherlockActivity()).inflate(R.layout.episodes_item, root, false);
		View row = convertView;
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

	@Override
	protected View getGroupView(Integer group, int groupNum, boolean visible, boolean isLastView, View convertView, ViewGroup root) {
		if ( convertView == null )
			convertView = LayoutInflater.from(getSherlockActivity()).inflate(R.layout.seasons_item, root, false);
		View row = convertView;
		TextView text = (TextView) row.findViewById(R.id.seasonsItemTextView);
		if ( group == 0 ) {
			text.setText("Specials" );
		} else {
			text.setText("Season " + group);
		}
		if ( visible ) {
			text.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.expander_close_holo_dark, 0);
		} else {
			text.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.expander_open_holo_dark, 0);
		}
		return row;
	}

	@Override
	protected void onChildItemClick(ListView l, View v, Integer group, Episode item) {
		Intent intent = new Intent( this.getActivity(), EpisodeActivity.class );
		intent.putExtra("tvdbid", this.tvdbid);
		intent.putExtra("show", this.show);
		intent.putExtra("season", group.toString());
		intent.putExtra("episode", item.episode);
		intent.putExtra("status", item.status.toString());
		startActivity(intent);
	}
	
	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		if ( hasHeader() ) {
			if ( position == 0 ) {
				// when the header is clicked
//				Intent intent = new Intent( this.getActivity(), EditShowActivity.class );
//				intent.putExtra("tvdbid", tvdbid);
//				intent.putExtra("show", show);
//				this.startActivity(intent);
			}
			super.onListItemClick(l, v, position-1, id);
		} else {
			super.onListItemClick(l, v, position, id);
		}
	}

	protected boolean hasHeader() {
		return true;
	}

	protected boolean hasFooter() {
		return false;
	}
}
