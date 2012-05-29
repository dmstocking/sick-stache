/*
 * 	SickStache is a android application for managing SickBeard
 * 	Copyright (C) 2012  David Stocking dmstocking@gmail.com
 * 
 * 	http://code.google.com/p/sick-stashe/
 * 	
 * 	SickStache is free software: you can redistribute it and/or modify
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
package org.sickstache;

import org.sickstache.fragments.FutureFragment;
import org.sickstache.fragments.ShowsFragment;
import org.sickstache.helper.ImageCache;
import org.sickstache.helper.Preferences;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.viewpagerindicator.TitlePageIndicator;
import com.viewpagerindicator.TitleProvider;

public class HomeActivity extends SherlockFragmentActivity implements OnSharedPreferenceChangeListener {
	
	public static SharedPreferences pref;
	
	private static int PREFRENCES_ACTIVITY_REQUEST_CODE = 1;
	
	private boolean preferencesChanged = false;
	
	private ViewPager viewpager;
	private SlideAdapter pageAdapter;
	private TitlePageIndicator pageIndicator;
	
	private ShowsFragment showFrag;
	private FutureFragment futureFrag;
	
//	private PingChecker pinger;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // we have got to do this absolutely first
        // otherwise we could refresh a cache that doesn't exist
        if ( ImageCache.cache == null ) {
	        ImageCache.cache = new ImageCache( this );
	        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
	        pref = PreferenceManager.getDefaultSharedPreferences(this);
        }
        if ( Preferences.singleton == null )
        	Preferences.singleton = new Preferences( pref );
        pref.registerOnSharedPreferenceChangeListener(this);

        setContentView(R.layout.main);
        showFrag = new ShowsFragment();
        futureFrag = new FutureFragment();
        
        viewpager = ((ViewPager)findViewById(R.id.viewpager));
        pageIndicator = ((TitlePageIndicator)findViewById(R.id.viewPagerIndicator));
        pageAdapter =  new SlideAdapter( this.getSupportFragmentManager() );
        viewpager.setAdapter( pageAdapter );
        pageIndicator.setViewPager( viewpager );
        
//        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        
        Intent intent = this.getIntent();
        if ( intent != null ) {
        	Bundle extras = intent.getExtras();
        	if ( extras != null ) {
	        	String activity = extras.getString("activity");
	        	if ( activity != null ) {
	        		if ( activity.equals("ShowActivity") ) {
	        			Intent next = new Intent( this, ShowActivity.class );
	        			next.putExtras(intent);
	        			startActivity(next);
	        		}
	        	}
        	}
        }
        
//        pinger = new PingChecker();
//        pinger.execute();
    }
    
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
    	switch ( item.getItemId() )
    	{
    	case R.id.settingsMenuItem:
    		Intent intent = new Intent( this, PreferencesActivity.class );
    		this.startActivityForResult(intent, PREFRENCES_ACTIVITY_REQUEST_CODE);
    		return true;
    	case R.id.cacheMenuItem:
    		AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage(R.string.cache_delete)
			       .setCancelable(false)
			       .setPositiveButton( R.string.yes , new DialogInterface.OnClickListener() {
			           public void onClick(DialogInterface dialog, int id) {
			        	   ImageCache.cache.clear();
			           }
			       }).setNegativeButton( R.string.no , new DialogInterface.OnClickListener() {
			           public void onClick(DialogInterface dialog, int id) {
			        	   ; // do nothing
			           }
			       });
			AlertDialog alert = builder.create();
			alert.show();
    		return true;
    	case R.id.historyMenuItem:
    		Intent historyIntent = new Intent( this, HistoryActivity.class );
    		this.startActivity(historyIntent);
    		return true;
    	case R.id.logMenuItem:
    		Intent logIntent = new Intent( this, LogActivity.class );
    		this.startActivity(logIntent);
    		return true;
//    	case R.id.aboutMenuItem:
//    		Intent aboutIntent = new Intent( this, AboutActivity.class );
//    		this.startActivity(aboutIntent);
//    		return true;
    	}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// create the menu button options NOT ACTIONBAR!
		this.getSupportMenuInflater().inflate(R.menu.home_menu, menu);
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// if we came back from the PreferencesActivity
		if ( requestCode == PREFRENCES_ACTIVITY_REQUEST_CODE ) {
			if ( preferencesChanged ) {
				showFrag.refresh();
				futureFrag.refresh();
				preferencesChanged = false;
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
		preferencesChanged = true;
	}

	private class SlideAdapter extends FragmentPagerAdapter implements TitleProvider {

		public SlideAdapter(FragmentManager fm) {
			super(fm);
		}
		
		@Override
		public Fragment getItem(int arg0) {
			switch( arg0 ) {
			case 0:
				return showFrag;
			case 1:
				return futureFrag;
			}
			return null;
		}

		@Override
		public int getCount() {
			return 2;
		}

		public String getTitle(int position) {
			switch( position ) {
			case 0:
				return "Shows";
			case 1:
				return "Future Episodes";
			}
			return null;
		}
    }
	
//	private class PingChecker extends AsyncTask<Void, Void, Boolean> {
//
//    	public Exception error;
//    	
//    	@Override
//    	protected Boolean doInBackground(Void... arg0) {
//    		try {
//    			return Preferences.singleton.getSickBeard().sbPing();
//    		} catch (Exception e) {
//    			error = e;
//    		}
//    		return false;
//    	}
//
//    	@Override
//    	protected void onPostExecute(Boolean result) {
//    		if ( HomeActivity.this != null ) {
//				if ( result == false ) {
//					Toast warning = Toast.makeText(HomeActivity.this, "Cannot connect to SickBeard Server. Please check settings.", Toast.LENGTH_LONG);
//					warning.show();
//				} else {
//					// I think this isn't necessary anymore
////					Toast success = Toast.makeText(HomeActivity.this, "Successfully connected to SickBeard. Refreshing now.", Toast.LENGTH_LONG);
////					success.show();
////					showFrag.refresh();
////					futureFrag.refresh();
//				}
//    		}
//    	}
//    }
}