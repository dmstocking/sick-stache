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

import org.sickbeard.json.HistoryJson;

public class HistoryItem {
	public String id;
	public String date;
	public String episode;
	public String season;
	public String show;
	public String status;
	public String provider;
	public String quality;
	public String filename;
	public String nzbname;
	
	public HistoryItem( HistoryJson item )
	{
		this.id = item.tvdbid;
		this.date = item.date;
		this.episode = String.valueOf(item.episode);
		this.season = String.valueOf(item.season);
		this.show = item.show_name;
		this.status = item.status;
		if ( item.provider == "-1" ) {
			this.provider = item.provider;
		} else {
			this.provider = "";
		}
		this.quality = item.quality;
		if ( item.status == "Snatch" ) {
			this.nzbname = item.resource;
		} else if ( item.status == "Downloaded" ){
			this.filename = item.resource;
		}
	}
	
	public HistoryItem( HistoryJson download, HistoryJson snatch )
	{
		this.id = download.tvdbid;
		this.date = download.date;
		this.episode = String.valueOf(download.episode);
		this.season = String.valueOf(download.season);
		this.show = download.show_name;
		this.status = "Downloaded";
		if ( download.provider == "-1" ) {
			this.provider = snatch.provider;
		} else {
			this.provider = download.provider + " - " + snatch.provider;
		}
		this.quality = download.quality;
		this.filename = download.resource;
		this.nzbname = snatch.resource;
	}
}
