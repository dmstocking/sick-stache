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

import android.app.Activity;
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
import org.sickbeard.Episode.StatusEnum;
import org.sickbeard.LanguageEnum;
import org.sickbeard.Show.QualityEnum;
import org.sickstache.HomeActivity;
import org.sickstache.R;
import org.sickstache.app.SickFragment;
import org.sickstache.dialogs.ErrorDialog;
import org.sickstache.dialogs.QualityDialog;
import org.sickstache.dialogs.StatusDialog;
import org.sickstache.helper.Preferences;

import java.util.EnumSet;

public class AddShowFragment extends SickFragment {
	
	protected Dialog working;
	
	public TextView showTextView;
	public TextView languageTextView;
	public TextView seasonFolderTextView;
	public TextView statusTextView;
	public TextView qualityTextView;
	
	public LanguageEnum language = null;
	public Boolean seasonFolder = null;
	public StatusEnum status = null;
	public EnumSet<QualityEnum> initialQuality = EnumSet.noneOf(QualityEnum.class);
	public EnumSet<QualityEnum> archiveQuality = EnumSet.noneOf(QualityEnum.class);
	
	public Button addButton;
	
	protected String tvdbid;
	protected String showName;
	
	@Override
	protected boolean isRetainInstance() {
		return true;
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		Intent intent = activity.getIntent();
		tvdbid = intent.getExtras().getString("tvdbid");
		showName = intent.getExtras().getString("show");
		try {
			language = LanguageEnum.valueOf(intent.getExtras().getString("lang").toLowerCase());
		} catch (Exception e) { language = null; }
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View root = inflater.inflate(R.layout.add_show_fragment, container, false);
		showTextView = (TextView)root.findViewById(R.id.showTextView);
		showTextView.setText(showName);
		statusTextView = (TextView)root.findViewById(R.id.statusTextView);
		statusTextView.setOnClickListener( new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				final StatusDialog sDialog = new StatusDialog();
				sDialog.setTitle("Set Initial Status");
				sDialog.setOnListClick( new DialogInterface.OnClickListener() {
				    public void onClick(DialogInterface dialog, int item) {
				    	status = sDialog.getStatus(item);
				    }
				});
				sDialog.show(getFragmentManager(), "status");
			}
		});
		seasonFolderTextView = (TextView)root.findViewById(R.id.seasonFolderTextView);
		seasonFolderTextView.setOnClickListener( new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if ( seasonFolder == null )
					seasonFolder = true;
				else
					seasonFolder = !seasonFolder;
				if ( seasonFolder != null && seasonFolder)
					seasonFolderTextView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.btn_check_on, 0);
				else
					seasonFolderTextView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.btn_check_off, 0);
			}
		});
		if ( Preferences.getSingleton(getSherlockActivity()).getSickBeard().getApiVersion() >= 3 ) {
			seasonFolderTextView.setText("Flatten Folders");
		}
		qualityTextView = (TextView)root.findViewById(R.id.qualityTextView);
		qualityTextView.setOnClickListener( new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				final QualityDialog qDialog = new QualityDialog();
				qDialog.setTitle("Select Initial Quality");
				qDialog.setOnListClick( new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which ) {
						initialQuality = qDialog.getInitialQuality();
						archiveQuality = qDialog.getArchiveQuality();
					}
				});
				qDialog.show(getFragmentManager(), "quality");
			}
		});
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
		try {
			// put up a loading dialog
			working = ProgressDialog.show(this.getSherlockActivity(), "Adding Show", "Adding Show to SickBeard ...", true);
			// when this downloader returns display the show
			Downloader downloader = new Downloader(this.tvdbid,
					this.language,
					this.seasonFolder,
					this.status,
					this.initialQuality,
					this.archiveQuality);
			downloader.execute();
		} catch (Exception e) {
			ErrorDialog dialog = new ErrorDialog();
			dialog.setTitle("Error On Clicking Add New Show");
			dialog.setMessage("Error: \nERROR: "+e.getMessage());
			dialog.show(getFragmentManager(), "addShowClickError");
		}
	}
	
	private class Downloader extends AsyncTask<Void, Void, Boolean> {

    	public Exception error = null;
    	
    	private String tvdbid = null;
    	private LanguageEnum language = null;
    	private StatusEnum status = null;
    	private Boolean seasonFolder = null;
    	private EnumSet<QualityEnum> initial = null;
    	private EnumSet<QualityEnum> archive = null;
    	
    	public Downloader( String tvdbid, LanguageEnum language, Boolean seasonFolder, StatusEnum status, EnumSet<QualityEnum> inital, EnumSet<QualityEnum> archive )
    	{
    		this.tvdbid = tvdbid;
    		this.language = language;
    		this.status = status;
    		this.seasonFolder = seasonFolder;
    		this.initial = inital;
    		this.archive = archive;
    	}
    	
    	@Override
    	protected Boolean doInBackground(Void... arg0) {
    		try {
    			return Preferences.getSingleton(AddShowFragment.this.getSherlockActivity()).getSickBeard().showAddNew(tvdbid,
    					language,
    					seasonFolder,
    					status,
    					initial,
    					archive);
    		} catch (Exception e) {
    			error = e;
    		}
    		return null;
    	}

    	@Override
    	protected void onPostExecute(Boolean result) {
    		if ( AddShowFragment.this != null ) {
    			// finished loading
    			if ( working != null )
    				working.dismiss();
    			// if we have a error
    			if ( result != null && result == true ) {
    				// we want the activity to be recreated so no SINGLE_TOP
    				Intent intent = new Intent( AddShowFragment.this.getSherlockActivity(), HomeActivity.class );
    	            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
    				startActivity(intent);
    			}
    			if ( error != null && getFragmentManager() != null ) {
    				ErrorDialog dialog = new ErrorDialog();
    				dialog.setTitle("Error Adding Show");
    				dialog.setMessage("Error adding show.\nERROR: "+error.getMessage());
    				dialog.show(getFragmentManager(), "addShowError");
    			}
    		}
    	}
    }
	
}
