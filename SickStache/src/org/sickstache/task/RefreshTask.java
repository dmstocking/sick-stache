package org.sickstache.task;

import org.sickstache.helper.Preferences;

public class RefreshTask extends SickTask<Void,Void,Boolean> {
	
	protected String[] tvdbids;
	
	public RefreshTask( String tvdbid )
	{
		this.tvdbids = new String[]{ tvdbid };
	}
	
	public RefreshTask( String[] tvdbids )
	{
		this.tvdbids = tvdbids;
	}

	@Override
	public String getTaskLogName() {
		return "RefreshTask";
	}
	
	@Override
	protected Boolean doInBackground(Void... arg0) {
		try {
			if ( tvdbids.length == 1 )
				return Preferences.singleton.getSickBeard().showRefresh(tvdbids[0]);
			else
				return Preferences.singleton.getSickBeard().showRefresh(tvdbids);
		} catch (Exception e) {
			error=e;
			return null;
		}
	}
	
}
