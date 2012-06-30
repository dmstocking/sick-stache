package org.sickstache.task;

import java.net.URI;

import org.sickstache.helper.BannerCache;
import org.sickstache.helper.Preferences;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class FetchInternetBannerTask extends SickTask<Void,Void,Bitmap> {

	protected String tvdbid;
	
	public FetchInternetBannerTask( String tvdbid )
	{
		this.tvdbid = tvdbid;
	}
	
	@Override
	public String getTaskLogName() {
		return "FetchInternetBannerTask";
	}
	
	@Override
	protected Bitmap doInBackground(Void... arg0) {
		try {
			// THE KEY IS THE TVDBID!!!!!!!!!
			String key = tvdbid;
			
			URI uri = Preferences.singleton.getSickBeard().showGetBanner(tvdbid);
			Bitmap bitmap = BitmapFactory.decodeStream(uri.toURL().openStream());
			BannerCache.singleton.put(key, bitmap);
			
			return bitmap;
		} catch (Exception e) {
			error=e;
			return null;
		}
	}

}
