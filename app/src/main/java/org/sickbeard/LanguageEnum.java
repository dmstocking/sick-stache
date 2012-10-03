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

public enum LanguageEnum {
	// DO NOT EDIT ORDER OR ADD NEW LANGUAGES WITHOUT ALSO CHANGING THE ABBREVS
	English, Chinese, Croatian, Czech, Danish, Dutch, Finnish, French, German, Greek,
	Hebrew, Hungarian, Italian, Japanese, Korean, Norwegian, Polish, Portuguese, Russian,
	Slovenian, Spanish, Swedish, Turkish;
	
	private static final String[] abbrevs = new String[]{ "EN", "ZH", "HR", "CS", "DA",
		"NL", "FI", "FR","DE", "EL", "HE", "HU", "IT", "JA", "KO", "NO", "PL", "PT", "RU",
		"SL", "ES", "SV", "TR" };
	
	public String getAbbrev()
	{
		return abbrevs[this.ordinal()].toLowerCase();
	}
	
	public static String[] valuesToString()
	{
		LanguageEnum[] enums = LanguageEnum.values();
		String[] items = new String[enums.length];
		for ( int i=0; i < enums.length; i++ ) {
			items[i] = enums[i].toString();
		}
		return items;
	}
	
	public static LanguageEnum fromOrdinal(int index)
	{
		return LanguageEnum.values()[index];
	}
}
