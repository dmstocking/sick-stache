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

import org.sickstache.SeasonsActivity;
import org.sickstache.app.LoadingListFragment;
import org.sickstache.helper.Preferences;
import org.sickstache.widget.DefaultImageView;
import org.sickstache.R;

import com.actionbarsherlock.view.ActionMode;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class ShowsFragment extends LoadingListFragment<Void, Void, ArrayList<Show>> {

	private static final String[] showActions = { "Set Quality", "Pause", "Refresh", "Update" };
	
	private ArrayAdapter<Show> showAdapter;
	
	@Override
	protected int getChoiceMode() {
		return ListView.CHOICE_MODE_NONE;
	}

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
					image.setImageJavaURI( Preferences.singleton.getSickBeard().showGetBanner(item.id) );
				} catch (Exception e) {
					;
				}

				ImageView overlay = (ImageView)row.findViewById(R.id.showSelectedOverlay);
				if ( selected.contains(position) ) {
					overlay.setVisibility(View.VISIBLE);
				} else {
					overlay.setVisibility(View.INVISIBLE);
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

//	@Override
//	public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
//		if ( actionMode == null ) {
//			actionMode = getSherlockActivity().startActionMode( new ActionMode.Callback() {
//				
//				@Override
//				public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
//					return false;
//				}
//				
//				@Override
//				public void onDestroyActionMode(ActionMode mode) {
//					showAdapter.notifyDataSetChanged();
//					selected.clear();
//					actionMode = null;
//				}
//				
//				@Override
//				public boolean onCreateActionMode(ActionMode mode, Menu menu) {
//					MenuInflater inflate = getSherlockActivity().getSupportMenuInflater();
//					inflate.inflate(R.menu.shows_cab_menu, menu);
//					return true;
//				}
//				
//				@Override
//				public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
//					switch ( item.getItemId() ) {
//					case R.id.editMenuItem:
//						// get all selected items and create the edit show activity passing all of them
//						actionMode.finish();
//						break;
//					}
//					return true;
//				}
//			});
//		}
//		ImageView overlay = (ImageView)arg1.findViewById(R.id.showSelectedOverlay);
//		int i = selected.indexOf(arg2);
//		if ( i >= 0 ) {
//			selected.remove(i);
//			overlay.setVisibility(View.INVISIBLE);
//		} else {
//			selected.add(arg2);
//			overlay.setVisibility(View.VISIBLE);
//		}
//		if ( selected.size() == 0 ) {
//			actionMode.finish();
//		}
//		return true;
//	}

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
