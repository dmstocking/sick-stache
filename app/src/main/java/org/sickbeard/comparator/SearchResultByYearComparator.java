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
package org.sickbeard.comparator;

import org.sickbeard.SearchResult;

import java.util.Comparator;


public class SearchResultByYearComparator implements Comparator<SearchResult> {

	@Override
	public int compare(SearchResult lhs, SearchResult rhs) {
		String lhsYear = lhs.getYear();
		String rhsYear = rhs.getYear();
		// check both for null
		int cmp;
		if ( lhsYear == null ) {
			if ( rhsYear == null )
				cmp = 0;
			else
				cmp = -1;
		} else {
			if ( rhsYear == null )
				cmp = 1;
			else
				cmp = lhsYear.compareTo(rhsYear);
		}
		cmp = -cmp; // higher years are better
		if ( cmp == 0 ) {
			String s1 = lhs.getTitle().toLowerCase();
			String s2 = rhs.getTitle().toLowerCase();
			if ( s1.startsWith("the ") )
				s1 = s1.substring(4);
			if ( s2.startsWith("the ") )
				s2 = s2.substring(4);

			return s1.compareTo(s2);
		} else {
			return cmp;
		}
	}
	
}
