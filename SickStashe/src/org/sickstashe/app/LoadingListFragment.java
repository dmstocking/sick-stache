/*
 * 	SickStashe is a android application for managing SickBeard
 * 	Copyright (C) 2012  David Stocking dmstocking@gmail.com
 * 
 * 	http://code.google.com/p/sick-stashe/
 * 	
 * 	SickStashe is free software: you can redistribute it and/or modify
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
package org.sickstashe.app;

import org.sickstashe.R;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;


import com.actionbarsherlock.app.SherlockListFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.MenuInflater;

public abstract class LoadingListFragment<Params, Progress, Result> extends SherlockListFragment {
	
	public enum ListStatus { NORMAL, ERROR, EMPTY };

	protected MenuItem searchMenuItem;
	protected MenuItem refreshMenuItem;
	protected ProgressBar refreshMenuActionView;
	
	protected ProgressBar spinner;
	protected TextView error;
	protected TextView empty;
	// this should just be a string but i cant because java ... its always because java
	protected abstract String getEmptyText();
	protected abstract Params[] getRefreshParams();
	
	protected AsyncTask<Params,Progress,Result> downloader = new Downloader();
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setHasOptionsMenu(true);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View root = inflater.inflate(R.layout.loadable_list_fragment, container, false);
		spinner = (ProgressBar)root.findViewById(R.id.workingProgressBar);
		error = (TextView)root.findViewById(R.id.errorTextView);
		empty = (TextView)root.findViewById(R.id.emptyTextView);
		empty.setText(getEmptyText());
		
		refreshMenuActionView = new ProgressBar(this.getActivity());
	    refreshMenuActionView.setIndeterminateDrawable(this.getActivity().getResources().getDrawable(R.drawable.refresh_spinner));
		
		return root;
	}
	
	// this isnt used because the activity might have information needed to refresh or query
//	@Override
//	public void onViewCreated(View view, Bundle savedInstanceState) {
//		super.onViewCreated(view, savedInstanceState);
//		this.refresh();
//	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		this.refresh();
	}
	
	@Override
	public void onDestroyView()
	{
		if ( downloader != null ) {
			downloader.cancel(true);
		}
		super.onDestroyView();
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.loadable_menu, menu);
		refreshMenuItem = menu.findItem(R.id.refreshMenuItem);
		if ( downloader != null && downloader.getStatus() == AsyncTask.Status.RUNNING ) {
			refreshMenuItem.setActionView(refreshMenuActionView);
		}
		searchMenuItem = menu.findItem(R.id.searchMenuItem);
		super.onCreateOptionsMenu(menu, inflater);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch ( item.getItemId() ) {
		case R.id.refreshMenuItem:
			item.setActionView(refreshMenuActionView);
			this.refresh();
			break;
		case R.id.searchMenuItem:
			this.getActivity().onSearchRequested();
			break;
		}
		return super.onOptionsItemSelected(item);
	}
	
	public void setListStatus( ListStatus status )
	{
		switch ( status ) {
		case NORMAL:
			spinner.setVisibility(ProgressBar.VISIBLE);
			error.setVisibility(TextView.GONE);
			empty.setVisibility(TextView.GONE);
			break;
		case ERROR:
			spinner.setVisibility(ProgressBar.GONE);
			error.setVisibility(TextView.VISIBLE);
			empty.setVisibility(TextView.GONE);
			break;
		case EMPTY:
			spinner.setVisibility(ProgressBar.GONE);
			error.setVisibility(TextView.GONE);
			empty.setVisibility(TextView.VISIBLE);
			break;
		}
	}
	
	public void refresh()
	{
		this.setListAdapter(null);
		this.setListStatus(ListStatus.NORMAL);
		if ( downloader != null ) {
			downloader.cancel(true);
		}
		downloader = new Downloader();
		downloader.execute(this.getRefreshParams());
	}
	
	protected abstract Result doInBackground(Params...arg0) throws Exception;
	protected abstract void onProgressUpdate(Progress...values);
	protected abstract void onPostExecute(Result result);
	
	private class Downloader extends AsyncTask<Params, Progress, Result> {

    	public Exception error = null;
    	
    	@Override
    	protected Result doInBackground(Params... arg0) {
    		try {
    			return LoadingListFragment.this.doInBackground(arg0);
    		} catch (Exception e) {
    			error = e;
    		}
    		return null;
    	}
    	
    	@Override
    	protected void onProgressUpdate(Progress...values)
    	{
    		LoadingListFragment.this.onProgressUpdate(values);
    	}

    	@Override
    	protected void onPostExecute(Result result) {
    		// checking if the fragment and the activity is alive otherwise we will crash
    		if ( LoadingListFragment.this != null &&
    				LoadingListFragment.this.getSherlockActivity() != null ) {
    			// finished loading
    			if ( refreshMenuItem != null ) {
    				refreshMenuItem.setActionView(null);
    			}
    			// if we have a error
    			if ( error != null ) {
    				LoadingListFragment.this.setListStatus(ListStatus.ERROR);
    			} else if ( result != null ) {
    				LoadingListFragment.this.onPostExecute(result);
    			}
    		}
    	}
    }
}
