package org.sickstache.task;

import java.net.URI;

import org.sickstache.helper.BannerCache;
import org.sickstache.helper.Preferences;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class FetchBannerTask extends SickTask<Void,Void,Bitmap> {

	protected String tvdbid;
	protected int width;
	protected int height;
	
	public FetchBannerTask( String tvdbid, int width, int height )
	{
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
			
			Bitmap bitmap = BannerCache.singleton.getFromMemory(key);
			// if the bitmap was not in the memory cache
			if ( bitmap == null ) {
				// check if it is on the disk
				if ( BannerCache.singleton.inDisk(key) == true ) {
					bitmap = BannerCache.singleton.getFromDisk(key);
				}
				// if it wasn't on the disk then finally go get to url
				if ( bitmap == null ) {
					URI uri = Preferences.singleton.getSickBeard().showGetBanner(tvdbid);
					bitmap = BitmapFactory.decodeStream(uri.toURL().openStream());
					if ( bitmap != null )
						BannerCache.singleton.put(key, bitmap);
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
