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

public class SetQualityTask extends AsyncTask<Void,Void,Boolean> {

	private static final String setQualityTaskLogName = "SetQualityTask";
	
	protected String  tvdbid;
	protected EnumSet<QualityEnum> initial;
	protected EnumSet<QualityEnum> archive;
	
	public SetQualityTask( String tvdbid, EnumSet<QualityEnum> initial, EnumSet<QualityEnum> archive )
	{
		this.tvdbid = tvdbid;
		this.initial = initial;
		this.archive = archive;
	}
	
	@Override
	protected Boolean doInBackground(Void... arg0) {
		try {
			return Preferences.singleton.getSickBeard().showSetQuality(tvdbid, initial, archive);
		} catch (Exception e) {
			Log.e(setQualityTaskLogName, "Exception while setting quality. ERROR: " + e.getMessage());
			return null;
		}
	}

}
