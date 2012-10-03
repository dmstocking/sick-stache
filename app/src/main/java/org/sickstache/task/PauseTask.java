package org.sickstache.task;

import org.sickstache.helper.Preferences;

public class PauseTask extends SickTask<Void,Void,Boolean> {
	
	protected String[] tvdbid;
	protected Boolean pause;
	
	public PauseTask( Preferences pref, String tvdbid, Boolean pause )
	{
		this( pref, new String[]{ tvdbid }, pause );
	}
	
	public PauseTask ( Preferences pref, String[] tvdbid, Boolean pause )
	{
		super(pref);
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
				return pref.getSickBeard().showPause(tvdbid[0], pause);
			else
				return pref.getSickBeard().showPause(tvdbid, pause);
		} catch (Exception e) {
			error=e;
			return null;
		}
	}
	
}
