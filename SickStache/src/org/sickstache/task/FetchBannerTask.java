package org.sickstache.task;

import java.net.URI;

import org.sickstache.helper.ImageCache;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

public class FetchBannerTask extends AsyncTask<URI,Void,Bitmap> {

	private static final String fetchBannerTaskLogName = "FetchBannerTask";
	
	protected String key;
	
	@Override
	protected Bitmap doInBackground(URI... arg0) {
		try {
			key = arg0[0].toURL().toString();
			return BitmapFactory.decodeStream(arg0[0].toURL().openStream());
		} catch (Exception e) {
			Log.e(fetchBannerTaskLogName, "Exception while fetching banner. ERROR: " + e.getMessage());
			return null;
		}
	}

	@Override
	protected void onPostExecute(Bitmap result) {
		if ( result != null ) {
			ImageCache.cache.remove(key);
			ImageCache.cache.add(key, result);
		}
	}

}
