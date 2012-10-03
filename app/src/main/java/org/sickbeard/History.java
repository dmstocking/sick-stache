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

import java.util.ArrayList;

public class History {

	public ArrayList<HistoryItem> items = new ArrayList<HistoryItem>();
	
	// this is way to easy to have an error and is confusing so just forget it
//	public History(ArrayList<HistoryJson> items) {
//		for ( int i = 0; i < items.size(); i++ ) {
//			int j = i+1;
//			for ( ; j < items.size(); j++ ) {
//				if ( items.get(i).compareTo(items.get(j)) == 0 ) {
//					break;
//				}
//			}
//			if ( j == items.size() ) {
//				this.items.add( new HistoryItem( items.get(i) ) );
//				items.remove(i);
//				i = i-1;
//			} else {
//				if ( items.get(j).status.compareTo("Snatched") == 0 ) {
//					this.items.add( new HistoryItem( items.get(i), items.get(j) ) );
//				} else {
//					this.items.add( new HistoryItem( items.get(j), items.get(i) ) );
//				}
//				// make sure to do it in this order otherwise removing i first makes j = j-1
//				items.remove(j);
//				items.remove(i);
//				// only remove 1 because j is ALWAYS after i
//				i = i-1;
//			}
//		}
//	}
	
	public History(ArrayList<HistoryJson> items) {
		for ( HistoryJson item : items ) {
			this.items.add(new HistoryItem(item));
		}
	}
}
