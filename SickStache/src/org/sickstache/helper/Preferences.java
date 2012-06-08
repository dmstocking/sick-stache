/*
 * 	SickStache is a android application for managing SickBeard
 * 	Copyright (C) 2012  David Stocking dmstocking@gmail.com
 * 
 * 	http://code.google.com/p/sick-stashe/
 * 	
 * 	SickStache is free software: you can redistribute it and/or modify
 * 	it under the terms of the GNU General Public License as published by
 * 	the Free Software Foundation, either version 3 of the License, or
 * 	(at your option) any later version.
 * 	
 * 	This program is distributed in the hope that it will be useful,
 * 	but WITHOUT ANY WARRANTY; without even the implied warranty of
 * 	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * 	GNU General Public License for more details.
 * 	
 * 	You should have received a copy of the GNU General Public License
 * 	along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.sickstache.helper;

import org.sickbeard.SickBeard;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;

public class Preferences implements OnSharedPreferenceChangeListener {
	
	public static Preferences singleton;
	
	private SharedPreferences pref;
	private SickBeard sick;
	
	public Preferences( SharedPreferences pref )
	{
		this.pref = pref;
		pref.registerOnSharedPreferenceChangeListener( this );
		updateSickBeard();
	}
	
	public String getHost()
	{
		return pref.getString("host", "192.168.0.1");
	}
	
	public String getPort()
	{
		return pref.getString("port", "8081");
	}
	
	public String getAPI()
	{
		return pref.getString("api", "");
	}
	
	public boolean getHTTPS()
	{
		return pref.getBoolean("https", false);
	}
	
	public String getPath()
	{
		return pref.getString("path","");
	}
	
	public String getUsername()
	{
		return pref.getString("username", "");
	}
	
	public String getPassword()
	{
		return pref.getString("password", "");
	}
	
	public SickBeard getSickBeard()
	{
		// hope this works if it doesn't use copy constructor
		return sick;
	}
	
	public void setSickBeard( String host, String port, String api, String path, String username, String password )
	{
		SharedPreferences.Editor edit = pref.edit();
		edit.putString("host", host);
		edit.putString("port", port);
		edit.putString("api", api);
		edit.putString("path", path);
		edit.putString("username", username);
		edit.putString("password", password);
		edit.commit();
		updateSickBeard();
	}
	
	private void updateSickBeard()
	{
		sick = new SickBeard( getHost(), getPort(), getAPI(), getHTTPS(), getPath(), getUsername(), getPassword() );
	}

	public void onSharedPreferenceChanged(SharedPreferences arg0, String arg1) {
		this.updateSickBeard();
	}
}
