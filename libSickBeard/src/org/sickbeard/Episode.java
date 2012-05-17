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

import org.sickbeard.json.EpisodeJson;

public class Episode {
	
	public enum StatusEnum {
		WANTED, SKIPPED, ARCHIVED, IGNORED, UNAIRED, SNATCHED, DOWNLOADED;
		
		public static String[] valuesSetableToString()
		{
			StatusEnum[] s = StatusEnum.values();
			String[] ret = new String[4];
			for ( int i=0; i < ret.length; i++ ) {
				ret[i] = s[i].toJson();
			}
			return ret;
		}
		
		public static StatusEnum fromJson( String status )
		{
			return StatusEnum.valueOf(status.toUpperCase());
		}
		
		public String toJson()
		{
			return this.toString().toLowerCase();
		}
		
		public static String[] valuesToString()
		{
			StatusEnum[] enums = StatusEnum.values();
			String[] items = new String[enums.length];
			for ( int i=0; i < enums.length; i++ ) {
				items[i] = enums[i].toString();
			}
			return items;
		}
		
		public static StatusEnum fromOrdinal(int index)
		{
			return StatusEnum.values()[index];
		}
	}
	
	public String tvdbid;
	public String episode;
	public String airdate;
	public String description;
	public String location;
	public String name;
	public String quality;
	public StatusEnum status;
	
	public Episode( EpisodeJson json ) {
		episode = json.episode;
		airdate = json.airdate;
		description = json.description;
		location = json.location;
		name = json.name;
		quality = json.quality;
		status = StatusEnum.fromJson(json.status);
	}
}
