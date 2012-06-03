package org.sickstache.task;

import java.util.List;

import org.sickbeard.Episode.StatusEnum;
import org.sickbeard.SeasonEpisodePair;
import org.sickstache.helper.Preferences;

public class SetStatusTask extends SickTask<Void,Void,Boolean> {
	
	private String tvdbid = null;
	// this or
	private String season = null;
	private String episode = null;
	// this BUT NOT BOTH
	private List<SeasonEpisodePair> episodes;
	private StatusEnum status = null;
	
	public SetStatusTask( String tvdbid, String season, String episode, StatusEnum status )
	{
		this.tvdbid = tvdbid;
		this.season = season;
		this.episode = episode;
		this.status = status;
	}
	
	public SetStatusTask( String tvdbid, List<SeasonEpisodePair> episodes, StatusEnum status )
	{
		this.tvdbid = tvdbid;
		this.episodes = episodes;
		this.status = status;
	}

	@Override
	public String getTaskLogName() {
		return "SetStatusTask";
	}
	
	@Override
	protected Boolean doInBackground(Void... arg0) {
		try {
			if ( this.episodes == null ) {
				return Preferences.singleton.getSickBeard().episodeSetStatus(tvdbid, season, episode, status);
			} else {
				return Preferences.singleton.getSickBeard().episodeSetStatus(tvdbid, episodes, status);
			}
		} catch (Exception e) {
			error = e;
			return null;
		}
	}
}
