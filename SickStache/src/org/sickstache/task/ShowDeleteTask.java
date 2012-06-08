package org.sickstache.task;

import org.sickstache.helper.Preferences;

public class ShowDeleteTask extends SickTask<Void,Void,Boolean> {
	
	protected String tvdbid;
	
	protected Exception e;
	
	public ShowDeleteTask( String tvdbid )
	{
		this.tvdbid = tvdbid;
	}

	@Override
	public String getTaskLogName() {
		return "ShowDeleteTask";
	}
	
	@Override
	protected Boolean doInBackground(Void... arg0) {
		try {
			return Preferences.singleton.getSickBeard().showDelete(tvdbid);
		} catch (Exception e) {
			this.e = e;
			return null;
		}
	}
	
}
