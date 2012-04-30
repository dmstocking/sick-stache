package org.sickstache.fragments;

import java.util.ArrayList;
import java.util.Comparator;

import org.sickbeard.Episode;
import org.sickbeard.SickBeard;
import org.sickbeard.SickBeard.StatusEnum;
import org.sickbeard.json.FutureEpisodeJson;
import org.sickbeard.json.FutureJson;
import org.sickstache.app.LoadingFragment;
import org.sickstache.helper.Preferences;
import org.sickstache.view.DefaultImageView;
import org.sickstache.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

public class EpisodeFragment extends LoadingFragment<String, Void, Episode> {

	public String tvdbid;
	public String show;
	public String season;
	public String episode;
	public StatusEnum status = null;
	
	public DefaultImageView showImage;
	
	public TextView showView;
	public TextView seasonView;
	public TextView episodeView;
	public TextView airbydate;
	public TextView statusView;
	public TextView name;
	public TextView descirption;
	
	public TextView search;
	public TextView setStatusView;
	
//	public DownloadEpisode downloader;
	
//	@Override
//	public View onCreateView(LayoutInflater inflater, ViewGroup container,
//			Bundle savedInstanceState) {
//		View view = inflater.inflate(R.layout.episode_fragment, container, false);
//		return view;
//	}


	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		this.showImage = (DefaultImageView)view.findViewById(R.id.showImage);
		this.showView = (TextView)view.findViewById(R.id.show);
		this.seasonView = (TextView)view.findViewById(R.id.seasonTextView);
		this.episodeView = (TextView)view.findViewById(R.id.episodeTextView);
		this.airbydate = (TextView)view.findViewById(R.id.airbydateTextView);
		this.name = (TextView)view.findViewById(R.id.nameTextView);
		this.descirption = (TextView)view.findViewById(R.id.descriptionTextView);
		this.search = (TextView)view.findViewById(R.id.searchTextView);
		this.statusView = (TextView)view.findViewById(R.id.statusTextView);
		this.setStatusView = (TextView)view.findViewById(R.id.setstatusTextView);
		
		this.search.setOnClickListener( new View.OnClickListener() {
			public void onClick(View v) {
				Toast searching = Toast.makeText(EpisodeFragment.this.getActivity(), "Searching for Episode. (may take a while)", Toast.LENGTH_LONG);
				searching.show();
				new SearchDownloader().execute();
			}
		});
		
		this.setStatusView.setOnClickListener( new View.OnClickListener() {
			public void onClick(View v) {
				AlertDialog.Builder build = new AlertDialog.Builder(EpisodeFragment.this.getActivity());
				build.setTitle("Set Status");
				build.setItems(SickBeard.StatusEnum.valuesSetableString(), new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						StatusDownloader downloader = new StatusDownloader();
						downloader.execute(StatusEnum.values()[which]);
					}
				});
				build.create().show();
			}
		});
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		Activity a = this.getActivity();
		Intent parent = a.getIntent();
		this.tvdbid = parent.getStringExtra("tvdbid");
		this.show = parent.getStringExtra("show");
		this.season = parent.getStringExtra("season");
		this.episode = parent.getStringExtra("episode");
		try {
			this.status = StatusEnum.valueOf(parent.getStringExtra("status"));
		} catch (Exception e) {
			this.status = StatusEnum.UNAIRED;
		}

		this.showView.setText(this.show);
		this.seasonView.setText(this.season);
		this.episodeView.setText(this.episode);
		
		try {
			showImage.setImageJavaURI( Preferences.singleton.getSickBeard().showGetBanner(tvdbid) );
		} catch (Exception e) {
			;
		}
		
		this.statusView.setText(status.toString());

		if ( this.status == StatusEnum.UNAIRED ) {
			this.search.setEnabled(true);
			this.setStatusView.setEnabled(false);
		} else {
			this.search.setEnabled(false);
			this.setStatusView.setEnabled(true);
		}
		
		if ( this.status == StatusEnum.WANTED ) {
			this.search.setEnabled(true);
		}
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	protected String getEmptyText() {
		// no way to be empty
		return null;
	}

	@Override
	protected int getLayoutResourceId() {
		return R.layout.episode_fragment;
	}

	@Override
	protected String[] getRefreshParams() {
		return new String[] { tvdbid, season, episode };
	}

	@Override
	protected Episode doInBackground(String... arg0) throws Exception {
		return Preferences.singleton.getSickBeard().episode( arg0[0], arg0[1], arg0[2] );
	}

	@Override
	protected void onProgressUpdate(Void... values) {
		;
	}

	@Override
	protected void onPostExecute(Episode result) {
		airbydate.setText(result.airdate);
		name.setText(result.name);
		descirption.setText(result.description);
	}
	
	private class SearchDownloader extends AsyncTask<Void, Void, Boolean> {

    	public Exception error;
    	
    	@Override
    	protected Boolean doInBackground(Void... arg0) {
    		try {
    			return Preferences.singleton.getSickBeard().episodeSearch(tvdbid, season, episode);
    		} catch (Exception e) {
    			error = e;
    		}
    		return false;
    	}

    	@Override
    	protected void onPostExecute(Boolean result) {
    		// checking if the fragment and the activity is alive otherwise we will crash
    		if ( EpisodeFragment.this != null &&
    				EpisodeFragment.this.getSherlockActivity() != null ) {
	    		if ( result != null && result == true ) {
	    			Toast searching = Toast.makeText(EpisodeFragment.this.getSherlockActivity(), "Searching Successful", Toast.LENGTH_LONG);
					searching.show();
	    		} else {
	    			Toast searching = Toast.makeText(EpisodeFragment.this.getSherlockActivity(), "Searching Failed", Toast.LENGTH_LONG);
					searching.show();
	    		}
    		}
    	}
    }
	
	private class StatusDownloader extends AsyncTask<StatusEnum, Void, Boolean> {

    	public Exception error;
    	
    	@Override
    	protected Boolean doInBackground(StatusEnum... arg0) {
    		try {
    			return Preferences.singleton.getSickBeard().episodeSetStatus(tvdbid, season, episode,arg0[0]);
    		} catch (Exception e) {
    			error = e;
    		}
    		return false;
    	}

    	@Override
    	protected void onPostExecute(Boolean result) {
    		// checking if the fragment and the activity is alive otherwise we will crash
    		if ( EpisodeFragment.this != null && 
    				EpisodeFragment.this.getSherlockActivity() != null ) {
	    		if ( result != null && result == true ) {
	    			Toast searching = Toast.makeText(EpisodeFragment.this.getSherlockActivity(), "Set Status Successful", Toast.LENGTH_LONG);
					searching.show();
	    		} else {
	    			Toast searching = Toast.makeText(EpisodeFragment.this.getSherlockActivity(), "Set Status Failed", Toast.LENGTH_LONG);
					searching.show();
	    		}
    		}
    	}
    }
}
