package org.sickstache.task;

import org.sickstache.helper.Preferences;

public class ShowDeleteTask extends SickTask<Void,Void,Boolean> {
	
	protected String tvdbid;
	
	public ShowDeleteTask( Preferences pref, String tvdbid )
	{
		super(pref);
		this.tvdbid = tvdbid;
	}

	@Override
	public String getTaskLogName() {
		return "ShowDeleteTask";
	}
	
	@Override
	protected Boolean doInBackground(Void... arg0) {
		try {
			return pref.getSickBeard().showDelete(tvdbid);
		} catch (Exception e) {
			error=e;
			return null;
		}
	}
	
}
