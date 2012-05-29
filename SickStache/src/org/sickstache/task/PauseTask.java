package org.sickstache.task;

import org.sickstache.helper.Preferences;

public class PauseTask extends SickTask<Void,Void,Boolean> {
	
	protected String[] tvdbid;
	protected Boolean pause;
	
	protected Exception e;
	
	public PauseTask( String tvdbid, Boolean pause )
	{
		this.tvdbid = new String[]{ tvdbid };
		this.pause = pause;
	}
	
	public PauseTask ( String[] tvdbid, Boolean pause )
	{
		this.tvdbid = tvdbid;
		this.pause = pause;
	}

	@Override
	public String getTaskLogName() {
		return "PauseTask";
	}
	
	@Override
	protected Boolean doInBackground(Void... arg0) {
		try {
			if ( tvdbid.length == 1)
				return Preferences.singleton.getSickBeard().showPause(tvdbid[0], pause);
			else
				return Preferences.singleton.getSickBeard().showPause(tvdbid, pause);
		} catch (Exception e) {
			this.e = e;
			return null;
		}
	}
	
}
