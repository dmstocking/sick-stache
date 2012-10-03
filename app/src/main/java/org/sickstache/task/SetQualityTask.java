package org.sickstache.task;

import org.sickbeard.Show.QualityEnum;
import org.sickstache.helper.Preferences;

import java.util.EnumSet;

public class SetQualityTask extends SickTask<Void,Void,Boolean> {
	
	protected String  tvdbid;
	protected EnumSet<QualityEnum> initial;
	protected EnumSet<QualityEnum> archive;
	
	public SetQualityTask( Preferences pref, String tvdbid, EnumSet<QualityEnum> initial, EnumSet<QualityEnum> archive )
	{
		super(pref);
		this.tvdbid = tvdbid;
		this.initial = initial;
		this.archive = archive;
	}

	@Override
	public String getTaskLogName() {
		return "SetQualityTask";
	}
	
	@Override
	protected Boolean doInBackground(Void... arg0) {
		try {
			return pref.getSickBeard().showSetQuality(tvdbid, initial, archive);
		} catch (Exception e) {
			error=e;
			return null;
		}
	}

}
