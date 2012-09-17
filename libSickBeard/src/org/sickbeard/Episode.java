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
		UNKNOWN("Unknown"),
		UNAIRED("Unaired"),
		SNATCHED("Snatched"),
		DOWNLOADED("Downloaded"),
		SKIPPED("Skipped"),
		SNATCHED_PROPER("Snatched (Proper)"),
		WANTED("Wanted"),
		ARCHIVED("Archived"),
		IGNORED("Ignored");

		private final String statusString;
		
		private static StatusEnum[] setable;

		private StatusEnum(String statusString)
		{
			this.statusString = statusString;
		}
		
		public static StatusEnum[] valuesSetable()
		{
			if ( setable == null ) {
				setable = new StatusEnum[]{WANTED, SKIPPED, ARCHIVED, IGNORED};
			}
			return setable;
		}

		public static String[] valuesSetableToString()
		{
			String[] result = new String[valuesSetable().length];
			for (int i = 0; i < result.length; i++) {
				result[i] = valuesSetable()[i].toJson();
			}
			return result;
		}

		public static StatusEnum fromJson( String statusString )
		{
			for (StatusEnum status : values()) {
				if (status.statusString.equalsIgnoreCase(statusString)) {
					return status;
				}
			}
			return StatusEnum.UNKNOWN;
		}

		public String toJson()
		{
			return this.name().toLowerCase();
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
