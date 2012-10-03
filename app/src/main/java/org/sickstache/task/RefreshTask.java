package org.sickstache.task;

import org.sickstache.helper.Preferences;

public class RefreshTask extends SickTask<Void,Void,Boolean> {
	
	protected String[] tvdbids;
	
	public RefreshTask( Preferences pref, String tvdbid )
	{
		this( pref, new String[] { tvdbid } );
	}
	
	public RefreshTask( Preferences pref, String[] tvdbids )
	{
		super(pref);
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
				return pref.getSickBeard().showRefresh(tvdbids[0]);
			else
				return pref.getSickBeard().showRefresh(tvdbids);
		} catch (Exception e) {
			error=e;
			return null;
		}
	}
	
}
