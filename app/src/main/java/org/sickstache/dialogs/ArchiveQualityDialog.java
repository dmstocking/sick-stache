/*
 * 	SickStache is a android application for managing SickBeard
 * 	Copyright (C) 2012  David Stocking dmstocking@gmail.com
 * 
 * 	http://code.google.com/p/sick-stashe/
 * 	
 * 	SickStache is free software: you can redistribute it and/or modify
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
package org.sickstache.dialogs;

import org.sickbeard.Show.QualityEnum;

public class ArchiveQualityDialog extends InitialQualityDialog {

	public ArchiveQualityDialog()
	{
		this.selected = new boolean[6];
	}
	
	@Override
	protected String[] getItems() {
		String[] items = QualityEnum.valuesToString();
		String[] ret = new String[6];
		for ( int i=0; i < 6; i++ ) {
			ret[i] = items[i+1];
		}
		return ret;
	}

}
