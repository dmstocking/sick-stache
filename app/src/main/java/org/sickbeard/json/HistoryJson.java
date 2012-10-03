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
package org.sickbeard.json;

public class HistoryJson {
	public String tvdbid;
	public String date;
	public int episode;
	public int season;
	public String show_name;
	public String status;
	public String provider;
	public String quality;
	public String resource;
	public String resouce_path;

	public int compareTo( HistoryJson other )
	{
		if ( this.show_name.compareTo(other.show_name) == 0 && this.episode == other.episode && this.season == other.season && this.quality.compareTo(other.quality) == 0 ) {
			return 0;
		}
		return -1;
	}
}
