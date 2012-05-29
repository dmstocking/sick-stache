package org.sickstache.task;

import java.util.EnumSet;

import org.sickbeard.Show.QualityEnum;
import org.sickstache.helper.Preferences;

public class SetQualityTask extends SickTask<Void,Void,Boolean> {
	
	protected String  tvdbid;
	protected EnumSet<QualityEnum> initial;
	protected EnumSet<QualityEnum> archive;
	
	public SetQualityTask( String tvdbid, EnumSet<QualityEnum> initial, EnumSet<QualityEnum> archive )
	{
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
			return Preferences.singleton.getSickBeard().showSetQuality(tvdbid, initial, archive);
		} catch (Exception e) {
			error=e;
			return null;
		}
	}

}
