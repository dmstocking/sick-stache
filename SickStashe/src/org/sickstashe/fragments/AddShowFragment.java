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
package org.sickstashe.fragments;

import org.sickstashe.R;
import org.sickstashe.AddShowActivity;
import org.sickstashe.HomeActivity;
import org.sickstashe.app.LoadableFragment;
import org.sickstashe.helper.Preferences;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class AddShowFragment extends Fragment {
	
	protected Dialog working;
	
	public TextView showTextView;
//	public TextView directoryTextView;
//	public TextView languageTextView;
//	public TextView seasonFolderTextView;
//	public TextView statusTextView;
//	public TextView intialQualityTextView;
//	public TextView archiveQualityTextView;
	
	public Button addButton;
	
	protected String tvdbid;
	protected String showName;

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		Intent intent = this.getActivity().getIntent();
		tvdbid = intent.getExtras().getString("tvdbid");
		showName = intent.getExtras().getString("show");
		showTextView.setText(showName);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View root = inflater.inflate(R.layout.add_show_fragment, container, false);
		showTextView = (TextView)root.findViewById(R.id.showTextView);
		addButton = (Button)root.findViewById(R.id.addButton);
		addButton.setOnClickListener( new OnClickListener() {
			  public void onClick(View v) {
			    onAddShowClick(v);
			  }
			});
		return root;
	}

	@Override
	public void onDestroyView() {
		if ( working != null ) {
			working.dismiss();
		}
		super.onDestroyView();
	}

	public void onAddShowClick(View v)
	{
		// put up a loading dialog
		working = ProgressDialog.show(this.getActivity(), "Adding Show", "Adding Show to SickBeard ...", true);
		// when this downloader returns display the show
		Downloader downloader = new Downloader();
		downloader.execute();
	}
	
	private class Downloader extends AsyncTask<Void, Void, Boolean> {

    	public Exception error = null;
    	
    	@Override
    	protected Boolean doInBackground(Void... arg0) {
    		try {
    			return Preferences.singleton.getSickBeard().showAddNew(tvdbid);
    		} catch (Exception e) {
    			error = e;
    		}
    		return null;
    	}

    	@Override
    	protected void onPostExecute(Boolean result) {
    		if ( AddShowFragment.this != null ) {
    			// finished loading
    			// if we have a error
    			if ( error != null ) {
    				// show some sort of error >.> i dont know how yet
    			} else if ( result != null ) {
    				Intent intent = new Intent( AddShowFragment.this.getActivity(), HomeActivity.class );
    	            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
    				intent.putExtra("activity", "ShowActivity");
    				intent.putExtra("tvdbid", tvdbid);
    				startActivity(intent);
    			}
    		}
    	}
    }
	
}
