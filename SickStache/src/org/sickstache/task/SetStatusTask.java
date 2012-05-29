package org.sickstache.task;

import org.sickbeard.Episode.StatusEnum;
import org.sickstache.helper.Preferences;

public class SetStatusTask extends SickTask<Void,Void,Boolean> {
	
	private String tvdbid = null;
	private String season = null;
	private String episode = null;
	private StatusEnum status = null;
	
	public SetStatusTask( String tvdbid, String season, String episode, StatusEnum status )
	{
		this.tvdbid = tvdbid;
		this.season = season;
		this.episode = episode;
		this.status = status;
	}

	@Override
	public String getTaskLogName() {
		return "SetStatusTask";
	}
	
	@Override
	protected Boolean doInBackground(Void... arg0) {
		try {
			return Preferences.singleton.getSickBeard().episodeSetStatus(tvdbid, season, episode, status);
		} catch (Exception e) {
			error = e;
			return null;
		}
	}
}
