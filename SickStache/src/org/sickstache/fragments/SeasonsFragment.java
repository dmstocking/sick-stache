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

import java.util.Comparator;

import org.sickbeard.Season;
import org.sickbeard.Show;

import org.sickstache.EpisodesActivity;
import org.sickstache.app.LoadingListFragment;
import org.sickstache.helper.Preferences;
import org.sickstache.view.DefaultImageView;
import org.sickstache.R;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class SeasonsFragment extends LoadingListFragment<String, Void, Show> {

	private String tvdbid;
	private String show;
	
	private boolean headerfooter;
	
	private LinearLayout header;
//	private LinearLayout footer;
	
	private DefaultImageView showImage;
	private TextView showView;
	private TextView airs;
	private TextView quality;
	private TextView language;
	private TextView paused;
	private TextView airbydate;
	
	private ArrayAdapter<Integer> seasonAdapter;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Intent parent = this.getActivity().getIntent();
		tvdbid = parent.getStringExtra("tvdbid");
		show = parent.getStringExtra("show");
		headerfooter = this.getActivity().getIntent().getBooleanExtra("headerfooter", false);
		seasonAdapter = new ArrayAdapter<Integer>(this.getActivity(), R.layout.seasons_item) {
			@Override
			public View getView( int position, View convertView, ViewGroup parent ) {
				View row = convertView;
				int item = getItem(position);
				if ( row == null ) {
					row = getActivity().getLayoutInflater().inflate(R.layout.seasons_item, null);
				}
				TextView text = (TextView) row.findViewById(R.id.seasonsItemTextView);
				if ( item == 0 ) {
					text.setText("Specials" );
				} else {
					text.setText("Season " + item);
				}
				return row;
			}
		};
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if ( headerfooter ) {
			header = (LinearLayout)inflater.inflate(R.layout.show_fragment_header, null);
//			footer = (LinearLayout)inflater.inflate(R.layout.show_fragment_footer, null);
		}
		return super.onCreateView(inflater, container, savedInstanceState);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		if ( headerfooter ) {
			this.getListView().addHeaderView(header,null,false);
			// no footer for right now until i decide the best way to do options for a show
//			this.getListView().addFooterView(footer,null,false);
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
	public void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		// some genius thought that it would be a good idea to use the positions from the "hidden"
		// adapter they use to do headers and footers OH BOY!!! WTF ANDROID
		// you do this shit and you cant figure out how to put a list view in a scroll view
		// FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFf
		position -= 1;
		if ( position == seasonAdapter.getCount() ) {
			return;
		}
		Intent intent = new Intent( this.getActivity(), EpisodesActivity.class );
		intent.putExtra("tvdbid", this.tvdbid);
		intent.putExtra("show", this.show);
		intent.putExtra("season", seasonAdapter.getItem(position).toString());
		startActivity(intent);
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
		Show show = Preferences.singleton.getSickBeard().show(arg0[0]);
		show.seasonList = Preferences.singleton.getSickBeard().showSeasons(arg0[0]);
		return show;
	}

	@Override
	protected void onProgressUpdate(Void... values) {
		;
	}

	@Override
	protected void onPostExecute(Show result) {
		setListAdapter(seasonAdapter);
		airs.setText(result.airs);
		quality.setText(result.quality);
		language.setText(result.language);
		// TODO this is probably not the best way to handle this
		paused.setText(result.paused ? "Yes" : "No");
		airbydate.setText(result.airbydate ? "Yes" : "No");
		seasonAdapter.clear();
		for ( Season s : result.seasonList ) {
			seasonAdapter.add(s.season);
		}
		seasonAdapter.sort(new Comparator<Integer>() {
			public int compare( Integer a, Integer b ) {
				return - (a - b);
			}
		});
		seasonAdapter.notifyDataSetChanged();
	}
}
