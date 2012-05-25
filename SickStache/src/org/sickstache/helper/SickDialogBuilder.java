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
package org.sickstache.helper;

import org.sickbeard.LanguageEnum;
import org.sickbeard.Episode.StatusEnum;
import org.sickbeard.Show.QualityEnum;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;

public class SickDialogBuilder {
	
	/*
	 * Builds a a multi-choice dialog for choosing qualities.
	 * 
	 * @param	context		the context needed to build the dialog
	 * @param	title		title of the dialog
	 * @param	defaults	a array of 8 booleans that represent what values have already been chosen
	 * @param	listener	a listener that is called after any item is checked
	 * @return	the dialog
	 */
	public static Dialog buildQualityDialog( Context context, String title, boolean[] defaults, DialogInterface.OnMultiChoiceClickListener listener )
	{
		String[] items = QualityEnum.valuesToString();
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle(title);
		builder.setMultiChoiceItems(items, defaults, listener);
		return builder.create();
	}
	
	/*
	 * Builds a a multi-choice dialog for choosing qualities with buttons for ok and cancel.
	 * 
	 * @param	context		the context needed to build the dialog
	 * @param	title		title of the dialog
	 * @param	defaults	a array of 8 booleans that represent what values have already been chosen
	 * @param	listener	a listener that is called after the positive button is clicked
	 * @return	the dialog
	 */
	public static Dialog buildQualityDialog( Context context, String title, boolean[] defaults, DialogInterface.OnClickListener listener )
	{
		String[] items = QualityEnum.valuesToString();
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle(title);
		builder.setMultiChoiceItems(items, defaults, null);
		builder.setPositiveButton("Ok", listener);
		builder.setNegativeButton("Cancel", null);
		return builder.create();
	}
	
	public static Dialog buildPauseDialog( Context context, String title, DialogInterface.OnClickListener listener )
	{
		String[] items = new String[]{ "Pause", "Unpause" };
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle(title);
		builder.setItems(items, listener);
		return builder.create();
	}
	
	public static Dialog buildStatusDialog( Context context, String title, boolean withDefault, DialogInterface.OnClickListener listener )
	{
		String[] rawItems = StatusEnum.valuesToString();
		String[] items = null;
		if ( withDefault ) {
			items = new String[rawItems.length+1];
			items[0] = "Default";
			for ( int i=0; i < rawItems.length; i++ ) {
				items[i+1] = rawItems[i];
			}
		} else {
			rawItems = items;
		}
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle(title);
		builder.setItems(items, listener);
		return builder.create();
	}
	
	public static Dialog buildLanguageDialog( Context context, String title, boolean withDefault, DialogInterface.OnClickListener listener )
	{
		String[] rawItems = LanguageEnum.valuesToString();
		String[] items = null;
		if ( withDefault ) {
			items = new String[rawItems.length+1];
			items[0] = "Default";
			for ( int i=0; i < rawItems.length; i++ ) {
				items[i+1] = rawItems[i];
			}
		} else {
			items = rawItems;
		}
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle(title);
		builder.setItems(items, listener);
		return builder.create();
	}
}
