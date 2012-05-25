package org.sickstache.task;

import java.net.URI;
import java.util.EnumSet;

import org.sickbeard.Show.QualityEnum;
import org.sickstache.fragments.EpisodeFragment;
import org.sickstache.helper.ImageCache;
import org.sickstache.helper.Preferences;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

public class EpisodeSearchTask extends AsyncTask<Void,Void,Boolean> {

	private static String episodeSearchTaskLogName = "EpisodeSearchTask";
	
	private String tvdbid = null;
	private String season = null;
	private String episode = null;
	
	private Exception error;
	
	public EpisodeSearchTask( String tvdbid, String season, String episode )
	{
		this.tvdbid = tvdbid;
		this.season = season;
		this.episode = episode;
	}
	
	@Override
	protected Boolean doInBackground(Void... arg0) {
		try {
			return Preferences.singleton.getSickBeard().episodeSearch(tvdbid, season, episode);
		} catch (Exception e) {
			Log.e(episodeSearchTaskLogName, "Exception while search for episode. ERROR: " + e.getMessage());
			error = e;
			return null;
		}
	}
}
