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

import java.util.EnumSet;

import org.sickbeard.LanguageEnum;
import org.sickbeard.Show.QualityEnum;
import org.sickstache.HomeActivity;
import org.sickstache.helper.Preferences;
import org.sickstache.R;

import com.actionbarsherlock.app.SherlockFragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class AddShowFragment extends SherlockFragment {
	
	protected Dialog working;
	
	public TextView showTextView;
//	public TextView directoryTextView;
	public TextView languageTextView;
	public TextView seasonFolderTextView;
	public TextView statusTextView;
	public TextView initialQualityTextView;
	public TextView archiveQualityTextView;
	
	public LanguageEnum language = null;
	public Boolean seasonFolder = null;
	public EnumSet<QualityEnum> initialQuality = null;
	public EnumSet<QualityEnum> archiveQuality = null;
	
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
//		directoryTextView = (TextView)root.findViewById(R.id.directoryTextView);
//		directoryTextView.setOnClickListener( new OnClickListener() {
//			@Override
//			public void onClick(View arg0) {
//				final CharSequence[] items = { "Default" };
//				
//				AlertDialog.Builder builder = new AlertDialog.Builder(AddShowFragment.this.getSherlockActivity());
//				builder.setTitle("Select Directory");
//				builder.setItems(items, new DialogInterface.OnClickListener() {
//				    public void onClick(DialogInterface dialog, int item) {
//				    	
//				    }
//				});
//			}
//		});
		languageTextView = (TextView)root.findViewById(R.id.languageTextView);
		languageTextView.setOnClickListener( new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO probably should just make this once and save it
				String[] items = LanguageEnum.valuesToString();
				String[] itemsNDefault = new String[items.length+1];
				itemsNDefault[0] = "Default";
				for ( int i=0; i < items.length; i++ ) {
					itemsNDefault[i+1] = items[i];
				}
				AlertDialog.Builder builder = new AlertDialog.Builder(AddShowFragment.this.getSherlockActivity());
				builder.setTitle("Select Language");
				builder.setItems(itemsNDefault, new DialogInterface.OnClickListener() {
				    public void onClick(DialogInterface dialog, int item) {
				    	if ( item == 0 )
				    		language = null;
				    	else
				    		language = LanguageEnum.fromOrdinal(item-1);
				    }
				});
			}
		});
		statusTextView = (TextView)root.findViewById(R.id.statusTextView);
		seasonFolderTextView = (TextView)root.findViewById(R.id.seasonFolderTextView);
		seasonFolderTextView.setOnClickListener( new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				seasonFolder = !seasonFolder;
				if ( seasonFolder != null && seasonFolder)
					seasonFolderTextView.setBackgroundResource(R.drawable.ic_menu_more);
				else
					seasonFolderTextView.setBackgroundResource(R.drawable.ic_menu_more);
			}
		});
		initialQualityTextView = (TextView)root.findViewById(R.id.initialQualityTextView);
		initialQualityTextView.setOnClickListener( new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO probably should just make this once and save it
				String[] items = LanguageEnum.valuesToString();
				String[] itemsNDefault = new String[items.length+1];
				itemsNDefault[0] = "Default";
				for ( int i=0; i < items.length; i++ ) {
					itemsNDefault[i+1] = items[i];
				}
				AlertDialog.Builder builder = new AlertDialog.Builder(AddShowFragment.this.getSherlockActivity());
				builder.setTitle("Select Initial Quality");
				builder.setItems(itemsNDefault, new DialogInterface.OnClickListener() {
				    public void onClick(DialogInterface dialog, int item) {
				    	if ( item == 0 )
				    		language = null;
				    	else
				    		language = LanguageEnum.fromOrdinal(item-1);
				    }
				});
			}
		});
		archiveQualityTextView = (TextView)root.findViewById(R.id.archiveQualityTextView);
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
				working.dismiss();
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
