/*
 * 	libSickBeard is a java library for communication with sickbeard
 * 	Copyright (C) 2012  David Stocking dmstocking@gmail.com
 * 
 * 	http://code.google.com/p/sick-stashe/
 * 	
 * 	libSickBeard is free software: you can redistribute it and/or modify
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
package org.sickbeard;


public class FutureEpisode {
	
	public enum TimeEnum {
		MISSED, TODAY, SOON, LATER;
	}
	
	public String airdate;
	public String airs;
	public String ep_name;
	public String ep_plot;
	public int episode;
	public String network;
	public int paused;
	public String quality;
	public int season;
	public String show_name;
	public String show_status;
	public int tvdbid;
	// the day of the week >.> 1-7 mon-sun
	public int weekday;
	public TimeEnum when;
}
