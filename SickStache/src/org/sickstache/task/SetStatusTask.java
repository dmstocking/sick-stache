package org.sickstache.task;

import java.net.URI;
import java.util.EnumSet;

import org.sickbeard.Episode.StatusEnum;
import org.sickbeard.Show.QualityEnum;
import org.sickstache.fragments.EpisodeFragment;
import org.sickstache.helper.ImageCache;
import org.sickstache.helper.Preferences;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

public class SetStatusTask extends AsyncTask<Void,Void,Boolean> {

	private static String setStatusTaskLogName = "SetStatusTask";
	
	private String tvdbid = null;
	private String season = null;
	private String episode = null;
	private StatusEnum status = null;
	
	private Exception error;
	
	public SetStatusTask( String tvdbid, String season, String episode, StatusEnum status )
	{
		this.tvdbid = tvdbid;
		this.season = season;
		this.episode = episode;
		this.status = status;
	}
	
	@Override
	protected Boolean doInBackground(Void... arg0) {
		try {
			return Preferences.singleton.getSickBeard().episodeSetStatus(tvdbid, season, episode, status);
		} catch (Exception e) {
			Log.e(setStatusTaskLogName, "Exception while setting status for an episode. ERROR: " + e.getMessage());
			error = e;
			return null;
		}
	}
}
