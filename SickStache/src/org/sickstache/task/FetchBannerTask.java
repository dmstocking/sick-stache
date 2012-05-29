package org.sickstache.task;

import java.net.URI;

import org.sickstache.helper.ImageCache;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class FetchBannerTask extends SickTask<URI,Void,Bitmap> {

	protected String key;
	
	@Override
	public String getTaskLogName() {
		return "FetchBannerTask";
	}
	
	@Override
	protected Bitmap doInBackground(URI... arg0) {
		try {
			key = arg0[0].toURL().toString();
			return BitmapFactory.decodeStream(arg0[0].toURL().openStream());
		} catch (Exception e) {
			error=e;
			return null;
		}
	}

	@Override
	protected void onPostExecute(Bitmap result) {
		super.onPostExecute(result);
		if ( result != null ) {
			ImageCache.cache.remove(key);
			ImageCache.cache.add(key, result);
		}
	}

}
