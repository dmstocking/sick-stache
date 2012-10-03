package org.sickstache.fragments;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import org.sickbeard.Episode;
import org.sickbeard.Episode.StatusEnum;
import org.sickstache.R;
import org.sickstache.app.LoadingFragment;
import org.sickstache.dialogs.ErrorDialog;
import org.sickstache.dialogs.StatusDialog;
import org.sickstache.helper.Preferences;
import org.sickstache.task.EpisodeSearchTask;
import org.sickstache.task.SetStatusTask;
import org.sickstache.widget.DefaultImageView;
import org.sickstache.widget.WorkingTextView;

public class EpisodeFragment extends LoadingFragment<String, Void, Episode> {

	public String tvdbid;
	public String show;
	public String season;
	public String episode;
	public StatusEnum status = null;
	public String airbydateText = null;
	public String nameText = null;
	public String descriptionText = null;
	
	public DefaultImageView showImage;
	
	public TextView showView;
	public TextView seasonView;
	public TextView episodeView;
	public TextView airbydate;
	public TextView statusView;
	public TextView name;
	public TextView descirption;
	
	public WorkingTextView search;
	public WorkingTextView setStatusView;
	
	@Override
	protected boolean isRetainInstance() {
		return true;
	}
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		Intent parent = activity.getIntent();
		this.tvdbid = parent.getStringExtra("tvdbid");
		this.show = parent.getStringExtra("show");
		this.season = parent.getStringExtra("season");
		this.episode = parent.getStringExtra("episode");
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		this.showImage = (DefaultImageView)view.findViewById(R.id.showImage);
		this.showView = (TextView)view.findViewById(R.id.show);
		this.seasonView = (TextView)view.findViewById(R.id.seasonTextView);
		this.episodeView = (TextView)view.findViewById(R.id.episodeTextView);
		this.airbydate = (TextView)view.findViewById(R.id.airbydateTextView);
		if ( this.airbydateText != null )
			this.airbydate.setText(this.airbydateText);
		this.name = (TextView)view.findViewById(R.id.nameTextView);
		if ( this.nameText != null )
			this.name.setText(this.nameText);
		this.descirption = (TextView)view.findViewById(R.id.descriptionTextView);
		if ( this.descriptionText != null )
			this.descirption.setText(this.descriptionText);
		this.search = (WorkingTextView)view.findViewById(R.id.searchWorkingTextView);
		this.statusView = (TextView)view.findViewById(R.id.statusTextView);
		if ( status != null )
			this.setStatusEnum(status);
		this.setStatusView = (WorkingTextView)view.findViewById(R.id.setStatusWorkingTextView);
		
		this.showView.setText(this.show);
		this.seasonView.setText(this.season);
		this.episodeView.setText(this.episode);
		
		showImage.setBanner(tvdbid);
		
		this.search.text.setText("Search for Episode");
		this.search.text.setOnClickListener( new View.OnClickListener() {
			public void onClick(View v) {
				search.setIsWorking(true);
				Preferences pref = Preferences.getSingleton(v.getContext());
				EpisodeSearchTask task = new EpisodeSearchTask(pref,tvdbid,season,episode){
					@Override
					protected void onPostExecute(Boolean result) {
						if ( result != null && result == true ) {
							search.setIsSuccessful(true);
						} else {
							search.setIsSuccessful(false);
						}
						if ( error != null && getFragmentManager() != null ) {
							ErrorDialog dialog = new ErrorDialog();
							dialog.setMessage("Error searching for show.\nERROR: "+error.getMessage());
							dialog.show(getFragmentManager(), "searchError");
						}
					}};
				task.execute();
			}
		});
		
		this.setStatusView.text.setText("Set Status");
		this.setStatusView.text.setOnClickListener( new View.OnClickListener() {
			public void onClick(View v) {
				final StatusDialog sDialog = new StatusDialog();
				sDialog.setTitle("Set Status");
				sDialog.setOnListClick( new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						Preferences pref = Preferences.getSingleton(EpisodeFragment.this.getSherlockActivity());
						SetStatusTask task = new SetStatusTask(pref,tvdbid,season,episode,sDialog.getStatus(which)){
							@Override
							protected void onPostExecute(Boolean result) {
								if ( result != null && result == true ) {
									setStatusView.setIsSuccessful(true);
									refresh();
								} else {
									setStatusView.setIsSuccessful(false);
								}
								if ( error != null && getFragmentManager() != null ) {
									ErrorDialog dialog = new ErrorDialog();
									dialog.setMessage("Error setting status for show.\nERROR: "+error.getMessage());
									dialog.show(getFragmentManager(), "statusError");
								}
							}};
						task.execute();
					}
				});
				sDialog.show(getFragmentManager(), "status");
			}
		});
	}
	
	public void setStatusEnum(StatusEnum status)
	{
		this.status = status;
		this.statusView.setText(status.toString());

		// let people do wtf they want
//		switch ( this.status ) {
//		case UNAIRED:
//			this.search.setEnabled(true);
//			this.setStatusView.setEnabled(false);
//		default:
//			this.search.setEnabled(true);
//			this.setStatusView.setEnabled(true);
//		}
	}

	@Override
	protected String getEmptyText() {
		// no way to be empty
		return "How is this empty? Please File a bug report so I can fix this.";
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
		return Preferences.getSingleton(getSherlockActivity()).getSickBeard().episode( arg0[0], arg0[1], arg0[2] );
	}

	@Override
	protected void onProgressUpdate(Void... values) {
		;
	}

	@Override
	protected void onPostExecute(Episode result) {
		airbydateText = result.airdate;
		nameText = result.name;
		descriptionText = result.description;
		airbydate.setText(airbydateText);
		name.setText(nameText);
		descirption.setText(descriptionText);
		setStatusEnum(result.status);
	}
}
