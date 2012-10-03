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

import org.sickbeard.Show.QualityEnum;
import org.sickbeard.json.GetQualityJson;

import java.util.EnumSet;

public class GetQuality {

	public EnumSet<QualityEnum> archive;
	public EnumSet<QualityEnum> initial;
	
	public GetQuality()
	{
		archive = EnumSet.noneOf(QualityEnum.class);
		initial = EnumSet.noneOf(QualityEnum.class);
	}
	
	public GetQuality( GetQualityJson json )
	{
		this();
		for ( String arch : json.archive ) {
			archive.add(QualityEnum.valueOf(arch));
		}
		for ( String init : json.initial ) {
			initial.add(QualityEnum.valueOf(init));
		}
	}
	
}
