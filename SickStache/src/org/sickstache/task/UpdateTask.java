package org.sickstache.task;

import org.sickstache.helper.Preferences;

public class UpdateTask extends SickTask<Void,Void,Boolean> {
	
	protected String[] tvdbids;
	
	public UpdateTask( String tvdbid )
	{
		this.tvdbids = new String[]{ tvdbid };
	}
	
	public UpdateTask( String[] tvdbids )
	{
		this.tvdbids = tvdbids;
	}

	@Override
	public String getTaskLogName() {
		return "UpdateTask";
	}
	
	@Override
	protected Boolean doInBackground(Void... arg0) {
		try {
			if ( tvdbids.length == 1 )
				return Preferences.singleton.getSickBeard().showUpdate(tvdbids[0]);
			else
				return Preferences.singleton.getSickBeard().showUpdate(tvdbids);
		} catch (Exception e) {
			error=e;
			return null;
		}
	}
	
}
