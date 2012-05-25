package org.sickstache.task;

import java.net.URI;
import java.util.EnumSet;

import org.sickbeard.Show.QualityEnum;
import org.sickstache.helper.ImageCache;
import org.sickstache.helper.Preferences;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

public class PauseTask extends AsyncTask<Void,Void,Boolean> {

	private static final String pauseTaskLogName = "PauseTask";
	
	protected String  tvdbid;
	protected Boolean pause;
	
	public PauseTask( String tvdbid, Boolean pause )
	{
		this.tvdbid = tvdbid;
		this.pause = pause;
	}
	
	@Override
	protected Boolean doInBackground(Void... arg0) {
		try {
			return Preferences.singleton.getSickBeard().showPause(tvdbid, pause);
		} catch (Exception e) {
			Log.e(pauseTaskLogName, "Exception while pausing show. ERROR: " + e.getMessage());
			return null;
		}
	}
	
}
