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

import android.util.Log;
import org.sickbeard.json.TvDbResultJson;

import java.text.SimpleDateFormat;

public class SearchResultTvDb implements SearchResult {
	
	private static SimpleDateFormat tvdbdate = new SimpleDateFormat("yyyy-mm-dd");
	private static SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy");
	
	private String year = null;
	private String title = "";
	private String id = "";
	private final String provider = "TheTVDB";
	
	public SearchResultTvDb() {
		;
	}
	
	public SearchResultTvDb( TvDbResultJson json ) {
		this();
		try {
			if ( json.first_aired != null )
				this.year = yearFormat.format(tvdbdate.parse(json.first_aired));
		} catch (Exception e) {
			if ( json.first_aired != null )
				Log.i("SearchResultTvDb", "Error parsing string " + json.first_aired.toString());
		}
		this.title = json.name;
		this.id = String.valueOf(json.tvdbid);
	}
	
	public void setYear(String year) {
		this.year = year;
	}
	
	public void setTitle(String title) {
		this.title = title;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	@Override
	public String getYear() {
		return year;
	}
	@Override
	public String getTitle() {
		return title;
	}
	@Override
	public String getId() {
		return id;
	}
	@Override
	public String getProvider() {
		return provider;
	}
}
