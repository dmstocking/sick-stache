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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.sickbeard.LanguageEnum;
import org.sickbeard.SearchResult;
import org.sickbeard.SearchResults;
import org.sickbeard.comparator.SearchResultByTitleComparator;
import org.sickbeard.comparator.SearchResultByYearComparator;
import org.sickstache.AddShowActivity;
import org.sickstache.R;
import org.sickstache.app.LoadingListFragment;
import org.sickstache.dialogs.LanguageDialog;
import org.sickstache.fragments.SearchFragment.SearchParams;
import org.sickstache.helper.Preferences;
import org.sickstache.widget.SafeArrayAdapter;

import android.app.SearchManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

public class SearchFragment extends LoadingListFragment<SearchParams, Void, SearchResults> {
	
	public class SearchParams {
		public String query;
		public LanguageEnum language;
	}
	
	private String query;
	private LanguageEnum language;
	
	private SafeArrayAdapter<SearchResult> searchAdapter;
	private Comparator<SearchResult> sorter;
	private Pattern hasYear = Pattern.compile("\\(\\d{4}\\)\\s*$");
	
	private SearchResults lastResults = null;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		sorter = new Comparator<SearchResult>(){
			@Override
			public int compare(SearchResult lhs, SearchResult rhs) {
				return 0;
			}
		};
		searchAdapter = new SafeArrayAdapter<SearchResult>(this.getActivity(), R.layout.search_item) {
			@Override
			public View getView( int position, View convertView, ViewGroup parent ) {
				View row = convertView;
				if ( convertView == null ) {
					row = layoutInflater.inflate(R.layout.search_item, null);
				}
				SearchResult item = getItem(position);
				TextView ep = (TextView) row.findViewById(R.id.searchItemTextView);
				ep.setText(item.getTitle());
				if ( item.getYear() != null ) {
					Matcher match = hasYear.matcher(item.getTitle());
					if ( match.find() == false ) {
						ep.setText(item.getTitle() + " (" + item.getYear() + ")");
					}
				}
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
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		menu.removeItem(R.id.searchMenuItem);
		inflater.inflate(R.menu.search_menu, menu);
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
		if ( position >= searchAdapter.getCount() ) {
			return;
		}
		Intent intent = new Intent( this.getActivity(), AddShowActivity.class );
		intent.putExtra("tvdbid", searchAdapter.getItem(position).getId());
		intent.putExtra("show", searchAdapter.getItem(position).getTitle());
		if ( language != null )
			intent.putExtra("lang", language.toString());
		startActivity(intent);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch ( item.getItemId() ) {
		case R.id.sortByRelevanceMenuItem:
			sorter = new Comparator<SearchResult>(){
				@Override
				public int compare(SearchResult lhs, SearchResult rhs) {
					return 0;
				}
			};
			onPostExecute(lastResults);
			return true;
		case R.id.sortByTitleMenuItem:
			sorter = new SearchResultByTitleComparator();
			searchAdapter.sort(sorter);
			return true;
		case R.id.sortByYearMenuItem:
			sorter = new SearchResultByYearComparator();
			searchAdapter.sort(sorter);
			return true;
		case R.id.searchLanguageMenuItem:
			final LanguageDialog lDialog = new LanguageDialog();
			lDialog.setTitle("Search Language");
			lDialog.setOnListClick( new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					language = lDialog.getLang();
					SearchFragment.this.refresh();
				}
			});
			lDialog.show(getFragmentManager(), "language");
			break;
		case R.id.searchMenuItem:
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected String getEmptyText() {
		return "No Results Found";
	}

	@Override
	protected SearchParams[] getRefreshParams() {
		SearchParams params = new SearchParams();
		params.query = query;
		params.language = language;
		return new SearchParams[] { params };
	}

	@Override
	protected SearchResults doInBackground(SearchParams... arg0) throws Exception {
		return Preferences.singleton.getSickBeard().sbSearchTvDb(arg0[0].query, arg0[0].language);
	}

	@Override
	protected void onProgressUpdate(Void... values) {
		;
	}

	@Override
	protected void onPostExecute(SearchResults result) {
		if ( result != null && searchAdapter != null ) {
			SearchFragment.this.setListAdapter(searchAdapter);
			lastResults = result;
			searchAdapter.clear();
			for ( SearchResult s : result.results ) {
				searchAdapter.add(s);
			}
			// make a sorter that sorts by year
			searchAdapter.sort( sorter );
			searchAdapter.notifyDataSetChanged();
			if ( searchAdapter.getCount() == 0 ) {
				this.setListStatus(ListStatus.EMPTY);
			}
		}
	}
}
