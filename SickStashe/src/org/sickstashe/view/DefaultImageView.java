/*
 * 	SickStashe is a android application for managing SickBeard
 * 	Copyright (C) 2012  David Stocking dmstocking@gmail.com
 * 
 * 	http://code.google.com/p/sick-stashe/
 * 	
 * 	SickStashe is free software: you can redistribute it and/or modify
 * 	it under the terms of the GNU General Public License as published by
 * 	the Free Software Foundation, either version 3 of the License, or
 * 	(at your option) any later version.
 * 	
 * 	This program is distributed in the hope that it will be useful,
 * 	but WITHOUT ANY WARRANTY; without even the implied warranty of
 * 	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * 	GNU General Public License for more details.
 * 	
 * 	You should have received a copy of the GNU General Public License
 * 	along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.sickstashe.view;

import java.net.URI;
import java.net.URL;

import org.sickstashe.helper.ImageCache;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.provider.MediaStore;

public class DefaultImageView extends ImageView {

	public int defaultResource;
	
	public GetUrlImageTask urlTask;
	public GetExternalCacheImageTask externalFileTask;
	
	public DefaultImageView(Context context) {
		super(context);
	}
	
	public DefaultImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	public DefaultImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}
	
	// not used at this moment in time
//	@Override
//	public void setImageURI(Uri uri) {
//		// Clear current image with default
//		this.setImageResource(defaultResource);
//		// if our uri is trying to load something off the sd card
//		if ( uri.toString().startsWith(MediaStore.Images.Media.EXTERNAL_CONTENT_URI.toString()) ) {
//			if ( externalTask != null ) {
//				externalTask.cancel(true);
//			}
//			externalTask = new GetExternalImageTask( this.getContext() );
//			externalTask.execute(uri);
//		// if i have no idea what the uri wants
//		} else {
//			super.setImageURI(uri);
//		}
//	}
	
//	public void setImageJavaURI(URI uri)
//	{
//		if ( ImageCache.cache.containsKey(uri.toURL().toString()) ) {
//			setImageBitmap(uri.toURL().toString());
//		} else {
//			this.setImageResource(defaultResource);
//			if ( urlTask != null ) {
//				urlTask.cancel(true);
//			}
//			urlTask = new GetUrlImageTask( this.getContext(), filename );
//			urlTask.execute(uri);
//		}
//	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		Drawable pic = this.getDrawable();
		int height = pic.getIntrinsicHeight();
		int width = pic.getIntrinsicWidth();
		double aspect = (double)(width) / height;
		
		heightMeasureSpec = View.MeasureSpec.makeMeasureSpec((int)(Math.ceil( (double)(View.MeasureSpec.getSize(widthMeasureSpec)) / aspect )), View.MeasureSpec.EXACTLY);
		
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}

	public void setImageJavaURI(URI uri)
	{
		// we are asking for a new uri so we do not want the current
		if ( urlTask != null ) {
			urlTask.cancel(true);
		}
		this.setImageResource(defaultResource);
		urlTask = new GetUrlImageTask( this.getContext(), uri );
		urlTask.execute();
//		try {
//			String filename = uri.toURL().toString();
//			if ( ImageCache.cache.in(filename) ) {
//				setImageBitmap( filename );
//			} else {
//				ConnectivityManager manage = (ConnectivityManager)this.getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
//				switch ( manage.getActiveNetworkInfo().getType() ) {
//				case ConnectivityManager.TYPE_ETHERNET:
//				case ConnectivityManager.TYPE_WIFI:
//					// everything after this point is only enabled because of testing
//				case ConnectivityManager.TYPE_MOBILE:
//					urlTask = new GetUrlImageTask( this.getContext(), filename );
//					urlTask.execute(uri);
//				}
//			}
//		} catch (Exception e) {
//			Log.e("SickGoatee", "Bad stuff happended with the image set.");
//		}
	}
	
	public void setImageBitmap(String filename) {
		if ( externalFileTask != null ) {
			externalFileTask.cancel(true);
		}
		// Clear current image with default
		this.setImageResource(defaultResource);
		externalFileTask = new GetExternalCacheImageTask( this.getContext() );
		externalFileTask.execute(filename);
	}
	
//	public class GetExternalImageTask extends AsyncTask<Uri,Void,Bitmap>
//	{
//		public Context c;
//		
//		public GetExternalImageTask(Context c)
//		{
//			this.c = c;
//		}
//
//		@Override
//		protected Bitmap doInBackground(Uri... params) {
//			try {
//				return MediaStore.Images.Media.getBitmap(c.getContentResolver(), params[0]);
//			} catch (Exception e) {
//				;
//			}
//			return null;
//		}
//
//		@Override
//		protected void onPostExecute(Bitmap result) {
//			super.onPostExecute(result);
//			if ( result != null ) {
//				DefaultImageView.super.setImageBitmap(result);
//			}
//		}
//	}
	
	public class GetUrlImageTask extends AsyncTask<Void,Void,Bitmap>
	{
		public Context c;
		public URI uri;
		public String key;
		
		public GetUrlImageTask(Context c, URI uri)
		{
			this.c = c;
			this.uri = uri;
			try {
				this.key = uri.toURL().toString();
			} catch (Exception e) {
				;
			}
		}

		@Override
		protected Bitmap doInBackground(Void... params) {
			try {
				if ( ImageCache.cache.in(key) == true ) {
					// createScaledBitmap is boned
					return ImageCache.cache.get(key);
				} else {
					Bitmap bitmap = BitmapFactory.decodeStream(uri.toURL().openStream());
					ImageCache.cache.add(key, bitmap);
					return bitmap;
				}
			} catch (Exception e) {
				Log.e("@GetUrlImageTaks.doInBackground", e.getMessage());
			}
			return null;
		}

		@Override
		protected void onPostExecute(Bitmap result) {
			super.onPostExecute(result);
			if ( result != null ) {
				DefaultImageView.super.setImageBitmap(result);
			}
		}
	}
	
	public class GetExternalCacheImageTask extends AsyncTask<String,Void,Bitmap>
	{
		public Context c;
		
		public GetExternalCacheImageTask(Context c)
		{
			this.c = c;
		}

		@Override
		protected Bitmap doInBackground(String... params) {
			try {
				if ( ImageCache.cache.in(params[0]) == true ) {
					return ImageCache.cache.get(params[0]);
				}
				return null;
			} catch (Exception e) {
				Log.e("@GetExternalCacheImageTask.doInBackground", e.getMessage());
			}
			return null;
		}

		@Override
		protected void onPostExecute(Bitmap result) {
			super.onPostExecute(result);
			if ( result != null ) {
				DefaultImageView.super.setImageBitmap(result);
			} else {
				Log.e("@GetExternalCacheImageTask.onPostExecute", "Returned Bitmap was null.");
			}
		}
	}
}
