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

import java.util.ArrayList;

import org.sickbeard.Show;
import org.sickbeard.comparator.ShowNameComparator;
import org.sickbeard.SickBeard;

import org.sickstache.PreferencesActivity;
import org.sickstache.SeasonsActivity;
import org.sickstache.ShowActivity;
import org.sickstache.app.LoadingListFragment;
import org.sickstache.helper.ImageCache;
import org.sickstache.helper.Preferences;
import org.sickstache.view.DefaultImageView;
import org.sickstache.R;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class ShowsFragment extends LoadingListFragment<Void, Void, ArrayList<Show>> {

	private static final String[] showActions = { "Set Quality", "Pause", "Refresh", "Update" };
	
	private ArrayAdapter<Show> showAdapter;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	    showAdapter = new ArrayAdapter<Show>(this.getActivity(), R.layout.show_banner_item) {
			@Override
			public View getView( int position, View convertView, ViewGroup parent ) {
				View row = convertView;
				if ( row == null ) {
					row = getActivity().getLayoutInflater().inflate(R.layout.show_banner_item, null);
				}
				Show item = getItem(position);
				TextView tv = (TextView) row.findViewById(R.id.show);
				tv.setText(item.showName);
				DefaultImageView image = (DefaultImageView) row.findViewById(R.id.showImage);
				image.defaultResource = R.drawable.default_banner;
				try {
					// TODO this needs to be fixed
					// if we load to fast we get the sickbeard default banner
					image.setImageJavaURI( Preferences.singleton.getSickBeard().showGetBanner(item.id) );
				} catch (Exception e) {
					;
				}
				return row;
			}
		};
	}
	
	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		Intent intent = new Intent( this.getActivity(), SeasonsActivity.class );
		Show item = showAdapter.getItem(position);
		intent.putExtra("tvdbid", item.id);
		intent.putExtra("show", item.showName);
		intent.putExtra("headerfooter", true);
		startActivity(intent);
	}

	@Override
	protected String getEmptyText() {
		return "No Shows Available";
	}
	
	@Override
	protected Void[] getRefreshParams() {
		return null;
	}
	
	@Override
	protected ArrayList<Show> doInBackground(Void... arg0) throws Exception {
		return Preferences.singleton.getSickBeard().shows();
	}
	
	@Override
	protected void onProgressUpdate(Void... values) {
		;
	}
	
	@Override
	protected void onPostExecute(ArrayList<Show> result) {
		setListAdapter(showAdapter);
		showAdapter.clear();
		for ( Show s : result ) {
			showAdapter.add(s);
		}
		showAdapter.sort( new ShowNameComparator() );
		if ( showAdapter.getCount() == 0 ) {
			this.setListStatus(ListStatus.EMPTY);
		}
		showAdapter.notifyDataSetChanged();
	}
}
