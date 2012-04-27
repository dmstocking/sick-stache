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

import org.sickbeard.SearchResult;
import org.sickbeard.SearchResults;
import org.sickbeard.comparator.SearchResultComparator;
import org.sickstache.AddShowActivity;
import org.sickstache.app.LoadingListFragment;
import org.sickstache.helper.Preferences;
import org.sickstache.R;

import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class SearchFragment extends LoadingListFragment<String, Void, SearchResults> {
	
	private ArrayAdapter<SearchResult> searchAdapter;
	
	private String query;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		searchAdapter = new ArrayAdapter<SearchResult>(this.getActivity(), R.layout.search_item) {
			@Override
			public View getView( int position, View convertView, ViewGroup parent ) {
				View row = convertView;
				if ( convertView == null ) {
					row = getActivity().getLayoutInflater().inflate(R.layout.search_item, null);
				}
				SearchResult item = getItem(position);
				TextView ep = (TextView) row.findViewById(R.id.searchItemTextView);
				if ( item.getYear() != null )
					ep.setText(item.getTitle() + " (" + item.getYear() + ")");
				else
					ep.setText(item.getTitle());
				// 1 is even because position starts at 0 not 1
				if ( position % 2 == 1 ) {
					ep.setBackgroundResource(R.color.sickbeard_even_row);
				} else {
					ep.setBackgroundResource(R.color.sickbeard_odd_row);
				}
				return row;
			}
		};
		super.onCreate(savedInstanceState);
		// DO NOT SET ADAPTER UNTIL THE DATA HAS BEEN AQUIRED!!!!
		// this makes the list have a progress spinner for us
	}
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		Intent intent = this.getActivity().getIntent();
	    if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
	    	query = intent.getStringExtra(SearchManager.QUERY);
	    }
		super.onViewCreated(view, savedInstanceState);
	}
	
	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		this.setListAdapter(null);
		this.setListStatus(ListStatus.NORMAL);
		if ( position >= searchAdapter.getCount() ) {
			return;
		}
		Intent intent = new Intent( this.getActivity(), AddShowActivity.class );
		intent.putExtra("tvdbid", searchAdapter.getItem(position).getId());
		intent.putExtra("show", searchAdapter.getItem(position).getTitle());
		startActivity(intent);
	}

	@Override
	protected String getEmptyText() {
		return "No Results Found";
	}

	@Override
	protected String[] getRefreshParams() {
		return new String[] { query };
	}

	@Override
	protected SearchResults doInBackground(String... arg0) throws Exception {
		return Preferences.singleton.getSickBeard().sbSearchTvDb(arg0[0]);
	}

	@Override
	protected void onProgressUpdate(Void... values) {
		;
	}

	@Override
	protected void onPostExecute(SearchResults result) {
		SearchFragment.this.setListAdapter(searchAdapter);
		searchAdapter.clear();
		for ( SearchResult s : result.results ) {
			searchAdapter.add(s);
		}
		// make a sorter that sorts by year
		searchAdapter.sort( new SearchResultComparator() );
		searchAdapter.notifyDataSetChanged();
		if ( searchAdapter.getCount() == 0 ) {
			this.setListStatus(ListStatus.EMPTY);
		}
	}
}
