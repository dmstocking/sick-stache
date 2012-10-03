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

import org.sickbeard.json.ShowJson;
import org.sickbeard.json.ShowJson.QualityDetailsJson;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

public class Show {
	
	public enum QualityEnum {
		SDTV, SDDVD, HDTV, HDWEBDL, HDBLURAY, FULLHDBLURAY, UNKNOWN;
		
		public static QualityEnum fromJson( String quality )
		{
			return QualityEnum.valueOf(quality.toUpperCase());
		}
		
		public static String[] valuesToString()
		{
			QualityEnum[] enums = QualityEnum.values();
			String[] items = new String[enums.length];
			for ( int i=0; i < enums.length; i++ ) {
				items[i] = enums[i].toString();
			}
			return items;
		}
		
		public static QualityEnum fromOrdinal(int index)
		{
			return QualityEnum.values()[index];
		}
		
		public static EnumSet<QualityEnum> fromBooleans( boolean[] values )
		{
			QualityEnum[] enums = QualityEnum.values();
			if ( values.length != enums.length )
				throw new RuntimeException("QualityEnum.fromBoolens: Array Mismatch values must be length \"" + enums.length + "\"");
			EnumSet<QualityEnum> ret = EnumSet.noneOf(QualityEnum.class);
			for ( int i=0; i < enums.length; i++ ) {
				if ( values[i] )
					ret.add(enums[i]);
			}
			return ret;
		}
	}
	
	public String id;
	public boolean airbydate;
	public String airs;
	public CacheStatus cache;
	public List<String> genre;
	public String language;
	public String location;
	public String network;
	public String nextAirDate;
	public boolean paused;
	public String quality;
	public QualityDetails qualityDetails;
	public boolean seasonFolders;
	public List<Season> seasonList;
	public String showName;
	public String status;
	public int tvrageId;
	public String tvrageName;
	
	public class QualityDetails {
		public List<String> archive;
		public List<String> initial;
		
		public QualityDetails( QualityDetailsJson json )
		{
			if ( json == null ) {
				archive = new ArrayList<String>();
				initial = new ArrayList<String>();
			} else {
				archive = (json.archive != null ? json.archive : new ArrayList<String>());
				initial = (json.initial != null ? json.initial : new ArrayList<String>());
			}
		}
	}
	
	public Show( ShowJson json )
	{
		id = json.id;
		airbydate = json.air_by_date.value;
		airs = json.airs;
		cache = new CacheStatus( json.cache );
		cache.banner = json.cache.banner.value;
		cache.poster = json.cache.poster.value;
		genre = (json.genre != null ? json.genre : new ArrayList<String>());
		language = json.language;
		location = json.location;
		network = json.network;
		nextAirDate = json.next_ep_airdate;
		paused = json.paused.value;
		quality = json.quality;
		qualityDetails = new QualityDetails( json.quality_details );
		seasonFolders = json.season_folders.value;
		seasonList = new ArrayList<Season>();
		if ( json.season_list != null ) {
			for ( Integer s : json.season_list ) {
				seasonList.add(new Season(s));
			}
		}
		showName = json.show_name;
		status = json.status;
		tvrageId = json.tvrage_id;
		tvrageName = json.tvrage_name;
	}
}
