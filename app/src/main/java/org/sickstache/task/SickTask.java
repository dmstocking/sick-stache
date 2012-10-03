package org.sickstache.task;

import android.os.AsyncTask;
import android.util.Log;
import org.sickstache.helper.Preferences;

public abstract class SickTask<Params,Progress,Result> extends AsyncTask<Params,Progress,Result> {
	
	protected Exception error = null;
	
	protected Preferences pref;
	
	public SickTask()
	{
	}
	
	public SickTask( Preferences pref )
	{
		this.pref = pref;
	}
	
	public abstract String getTaskLogName();
	
	@Override
	protected void onPostExecute(Result result) {
		if ( error != null)
			Log.e(getTaskLogName(), "Exception occured. ERROR: " + error.getMessage());
	}
}
