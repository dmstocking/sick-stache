package org.sickstache.task;

import android.os.AsyncTask;
import android.util.Log;

public abstract class SickTask<Params,Progress,Result> extends AsyncTask<Params,Progress,Result> {
	
	protected Exception error = null;
	
	public SickTask()
	{
	}
	
	public abstract String getTaskLogName();
	
	@Override
	protected void onPostExecute(Result result) {
		if ( error != null)
			Log.e(getTaskLogName(), "Exception occured. ERROR: " + error.getMessage());
	}
}
