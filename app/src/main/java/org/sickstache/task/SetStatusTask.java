package org.sickstache.task;

import org.sickbeard.Episode.StatusEnum;
import org.sickbeard.SeasonEpisodePair;
import org.sickstache.helper.Preferences;

import java.util.List;

public class SetStatusTask extends SickTask<Void,Void,Boolean> {
	
	private String tvdbid = null;
	// this
	private String season = null;
	private String episode = null;
	// or this BUT NOT BOTH
	private List<SeasonEpisodePair> episodes;
	private StatusEnum status = null;
	
	public SetStatusTask( Preferences pref, String tvdbid, String season, String episode, StatusEnum status )
	{
		super(pref);
		this.tvdbid = tvdbid;
		this.season = season;
		this.episode = episode;
		this.status = status;
	}
	
	public SetStatusTask( Preferences pref, String tvdbid, List<SeasonEpisodePair> episodes, StatusEnum status )
	{
		super(pref);
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
				return pref.getSickBeard().episodeSetStatus(tvdbid, season, episode, status);
			} else {
				return pref.getSickBeard().episodeSetStatus(tvdbid, episodes, status);
			}
		} catch (Exception e) {
			error = e;
			return null;
		}
	}
}
