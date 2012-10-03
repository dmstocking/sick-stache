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
import org.sickbeard.json.SeasonsJson;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Season {
	
	public int season;
	
	public List<Episode> getEpisodes() {
		if ( episodes == null ) {
			episodes = new ArrayList<Episode>();
		}
		return episodes;
	}
	
	private List<Episode> episodes;
	
	public Season( int season )
	{
		this.season = season;
	}
	
	public Season( int season, List<EpisodeJson> episodes )
	{
		this.season = season;
		for ( EpisodeJson json : episodes ) {
			this.getEpisodes().add( new Episode( json ) );
		}
	}
	
	public Season( String season, SeasonsJson episodes )
	{
		this.season = Integer.parseInt(season);
		for ( Map.Entry<String, EpisodeJson> json : episodes.entrySet() ) {
			Episode ep = new Episode( json.getValue() );
			ep.episode = json.getKey();
			this.getEpisodes().add( ep );
		}
	}
}
