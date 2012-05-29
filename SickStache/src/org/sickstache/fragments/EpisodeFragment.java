package org.sickstache.fragments;

import org.sickbeard.Episode;
import org.sickbeard.Episode.StatusEnum;
import org.sickstache.R;
import org.sickstache.app.LoadingFragment;
import org.sickstache.dialogs.StatusDialog;
import org.sickstache.helper.Preferences;
import org.sickstache.task.EpisodeSearchTask;
import org.sickstache.task.SetStatusTask;
import org.sickstache.widget.DefaultImageView;
import org.sickstache.widget.WorkingTextView;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

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
	
	public WorkingTextView search;
	public WorkingTextView setStatusView;


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
		this.search = (WorkingTextView)view.findViewById(R.id.searchWorkingTextView);
		this.statusView = (TextView)view.findViewById(R.id.statusTextView);
		this.setStatusView = (WorkingTextView)view.findViewById(R.id.setStatusWorkingTextView);
		
		this.search.text.setText("Search for Episode");
		this.search.text.setOnClickListener( new View.OnClickListener() {
			public void onClick(View v) {
				search.setIsWorking(true);
				EpisodeSearchTask task = new EpisodeSearchTask(tvdbid,season,episode){
					@Override
					protected void onPostExecute(Boolean result) {
						if ( result != null && result == true ) {
							search.setIsSuccessful(true);
						} else {
							search.setIsSuccessful(false);
						}
					}};
				task.execute();
			}
		});
		
		this.setStatusView.text.setText("Set Status");
		this.setStatusView.text.setOnClickListener( new View.OnClickListener() {
			public void onClick(View v) {
				StatusDialog sDialog = new StatusDialog();
				sDialog.setTitle("Set Status");
				sDialog.setOnListClick( new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						SetStatusTask task = new SetStatusTask(tvdbid,season,episode,StatusEnum.values()[which]){
							@Override
							protected void onPostExecute(Boolean result) {
								if ( result != null && result == true ) {
									setStatusView.setIsSuccessful(true);
									refresh();
								} else {
									setStatusView.setIsSuccessful(false);
								}
							}};
						task.execute();
					}
				});
				sDialog.show(getFragmentManager(), "status");
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
		
		setStatusEnum(this.status);
		super.onActivityCreated(savedInstanceState);
	}
	
	public void setStatusEnum(StatusEnum status)
	{
		this.status = status;
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
		setStatusEnum(status);
	}
}
