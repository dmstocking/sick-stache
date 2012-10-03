package org.sickstache.task;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import org.sickstache.helper.BannerCache;
import org.sickstache.helper.Preferences;

import java.net.URI;

public class FetchBannerTask extends SickTask<Void,Void,Bitmap> {

	protected String tvdbid;
	protected int width;
	protected int height;
	
	private BannerCache cache;
	
	public FetchBannerTask( Preferences pref, BannerCache cache, String tvdbid, int width, int height )
	{
		super(pref);
		this.cache = cache;
		this.tvdbid = tvdbid;
		this.width = width;
		this.height = height;
	}
	
	@Override
	public String getTaskLogName() {
		return "FetchBannerTask";
	}
	
	@Override
	protected Bitmap doInBackground(Void... arg0) {
		try {
			// THE KEY IS THE TVDBID!!!!!!!!!
			String key = tvdbid;
			
			Bitmap bitmap = cache.getFromMemory(key);
			// if the bitmap was not in the memory cache
			if ( bitmap == null ) {
				// check if it is on the disk
				if ( cache.inDisk(key) == true ) {
					bitmap = cache.getFromDisk(key);
				}
				// if it wasn't on the disk then finally go get to url
				if ( bitmap == null ) {
					URI uri = pref.getSickBeard().showGetBanner(tvdbid);
					bitmap = BitmapFactory.decodeStream(uri.toURL().openStream());
					if ( bitmap != null )
						cache.put(key, bitmap);
				}
			}
			// if we have a bitmap scale it
//			if ( bitmap != null && width > 0 && height > 0 ) {
//				bitmap = Bitmap.createScaledBitmap(bitmap, width, height, true);
//			}
			return bitmap;
		} catch (Exception e) {
			error=e;
			return null;
		}
	}
	
}
