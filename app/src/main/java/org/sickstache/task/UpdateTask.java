package org.sickstache.task;

import org.sickstache.helper.Preferences;

public class UpdateTask extends SickTask<Void,Void,Boolean> {
	
	protected String[] tvdbids;
	
	public UpdateTask( Preferences pref, String tvdbid )
	{
		this(pref, new String[]{ tvdbid } );
	}
	
	public UpdateTask( Preferences pref, String[] tvdbids )
	{
		super(pref);
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
				return pref.getSickBeard().showUpdate(tvdbids[0]);
			else
				return pref.getSickBeard().showUpdate(tvdbids);
		} catch (Exception e) {
			error=e;
			return null;
		}
	}
	
}
