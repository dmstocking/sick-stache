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

import org.sickbeard.json.type.JsonBoolean;

import java.util.ArrayList;

public class ShowJson {
	//NOTE id is the key of the json you get from sickbeard so you have to get the json then add this to the class after words
	public String id;
	public JsonBoolean air_by_date;
	public String airs;
	public CacheStatusJson cache;
	public ArrayList<String> genre;
	public String language;
	public String location;
	public String network;
	public String next_ep_airdate;
	public JsonBoolean paused;
	public String quality;
	public QualityDetailsJson quality_details;
	public JsonBoolean season_folders;
	public ArrayList<Integer> season_list;
	public String show_name;
	public String status;
	public int tvrage_id;
	public String tvrage_name;
	
	public class CacheStatusJson {
		public JsonBoolean banner;
		public JsonBoolean poster;
		
		public CacheStatusJson()
		{
			banner = new JsonBoolean();
			poster = new JsonBoolean();
		}
	}
	
	public class QualityDetailsJson {
		public ArrayList<String> archive;
		public ArrayList<String> initial;
	}
	
	public ShowJson()
	{
		air_by_date = new JsonBoolean();
		paused = new JsonBoolean();
		season_folders = new JsonBoolean();
	}
}
