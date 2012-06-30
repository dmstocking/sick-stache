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
package org.sickstache.app;

import org.sickstache.R;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

public abstract class LoadingFragment<Params, Progress, Result> extends SherlockFragment {
	
	public enum Status { NORMAL, WORKING, ERROR, EMPTY };
	
	protected MenuItem searchMenuItem;
	protected MenuItem refreshMenuItem;
	protected ProgressBar refreshMenuActionView;
	
	protected LinearLayout loadableLinearLayout;
	protected ProgressBar spinner;
	protected TextView error;
	protected TextView empty;
	protected LinearLayout userViewGroup;
	
	protected abstract String getEmptyText();
	protected abstract int getLayoutResourceId();
	protected abstract Params[] getRefreshParams();
	
	protected AsyncTask<Params,Progress,Result> downloader = new Downloader();
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setHasOptionsMenu(true);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View root = inflater.inflate(R.layout.loadable_fragment, container, false);
		loadableLinearLayout = (LinearLayout)root.findViewById(R.id.loadableLinearLayout);
		spinner = (ProgressBar)root.findViewById(R.id.workingProgressBar);
		error = (TextView)root.findViewById(R.id.errorTextView);
		empty = (TextView)root.findViewById(R.id.emptyTextView);
		empty.setText(getEmptyText());
		userViewGroup = (LinearLayout)root.findViewById(R.id.userViewGroup);
		inflater.inflate(this.getLayoutResourceId(), userViewGroup, true );
		
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
//		if ( downloader != null && downloader.getStatus() == AsyncTask.Status.RUNNING ) {
//			refreshMenuItem.setActionView(refreshMenuActionView);
//		}
		searchMenuItem = menu.findItem(R.id.searchMenuItem);
		super.onCreateOptionsMenu(menu, inflater);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch ( item.getItemId() ) {
		case R.id.refreshMenuItem:
			this.refresh();
			break;
		case R.id.searchMenuItem:
			this.getActivity().onSearchRequested();
			break;
		}
		return super.onOptionsItemSelected(item);
	}
	
	public void setStatus( Status status )
	{
		switch ( status ) {
		case NORMAL:
			loadableLinearLayout.setVisibility(LinearLayout.GONE);
			spinner.setVisibility(ProgressBar.GONE);
			error.setVisibility(TextView.GONE);
			empty.setVisibility(TextView.GONE);
			userViewGroup.setVisibility(LinearLayout.VISIBLE);
			break;
		case WORKING:
			loadableLinearLayout.setVisibility(LinearLayout.VISIBLE);
			spinner.setVisibility(ProgressBar.VISIBLE);
			error.setVisibility(TextView.GONE);
			empty.setVisibility(TextView.GONE);
			userViewGroup.setVisibility(LinearLayout.GONE);
			break;
		case ERROR:
			loadableLinearLayout.setVisibility(LinearLayout.VISIBLE);
			spinner.setVisibility(ProgressBar.GONE);
			error.setVisibility(TextView.VISIBLE);
			empty.setVisibility(TextView.GONE);
			userViewGroup.setVisibility(LinearLayout.GONE);
			break;
		case EMPTY:
			loadableLinearLayout.setVisibility(LinearLayout.VISIBLE);
			spinner.setVisibility(ProgressBar.GONE);
			error.setVisibility(TextView.GONE);
			empty.setVisibility(TextView.VISIBLE);
			userViewGroup.setVisibility(LinearLayout.GONE);
			break;
		}
	}
	
	public void refresh()
	{
//		refreshMenuItem.setActionView(refreshMenuActionView);
		this.setStatus(Status.WORKING);
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
    			return LoadingFragment.this.doInBackground(arg0);
    		} catch (Exception e) {
    			error = e;
    		}
    		return null;
    	}
    	
    	@Override
    	protected void onProgressUpdate(Progress...values)
    	{
    		LoadingFragment.this.onProgressUpdate(values);
    	}

    	@Override
    	protected void onPostExecute(Result result) {
    		// checking if the fragment and the activity is alive otherwise we will crash
    		if ( LoadingFragment.this != null &&
    				LoadingFragment.this.getSherlockActivity() != null ) {
    			// finished loading
    			// disabled action bar menu action view because it breaks 4.0.3
//    			if ( refreshMenuItem != null ) {
//    				refreshMenuItem.setActionView((View)null);
//    			}
    			// if we have a error
    			if ( error != null ) {
    				LoadingFragment.this.error.setText("Error Retrieving Results\n"+error.getMessage());
    				LoadingFragment.this.setStatus(LoadingFragment.Status.ERROR);
    			} else if ( result != null ) {
        			LoadingFragment.this.setStatus(LoadingFragment.Status.NORMAL);
    				LoadingFragment.this.onPostExecute(result);
    			}
    		}
    	}
    }
}
