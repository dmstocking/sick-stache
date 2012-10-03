package org.sickstache.task;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import org.sickstache.helper.BannerCache;
import org.sickstache.helper.Preferences;

import java.net.URI;

public class FetchInternetBannerTask extends SickTask<Void,Void,Bitmap> {

	protected String tvdbid;
	
	private BannerCache cache;
	
	public FetchInternetBannerTask( Preferences pref, BannerCache cache, String tvdbid )
	{
		super(pref);
		this.cache = cache;
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
			
			URI uri = pref.getSickBeard().showGetBanner(tvdbid);
			Bitmap bitmap = BitmapFactory.decodeStream(uri.toURL().openStream());
			if ( bitmap != null )
				cache.put(key, bitmap);
			
			return bitmap;
		} catch (Exception e) {
			error=e;
			return null;
		}
	}

}
