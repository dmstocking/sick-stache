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

public class RefreshTask extends AsyncTask<Void,Void,Boolean> {

	private static final String refreshTaskLogName = "RefreshTask";
	
	protected String  tvdbid;
	
	public RefreshTask( String tvdbid )
	{
		this.tvdbid = tvdbid;
	}
	
	@Override
	protected Boolean doInBackground(Void... arg0) {
		try {
			return Preferences.singleton.getSickBeard().showRefresh(tvdbid);
		} catch (Exception e) {
			Log.e(refreshTaskLogName, "Exception while refreshing show. ERROR: " + e.getMessage());
			return null;
		}
	}
	
}
