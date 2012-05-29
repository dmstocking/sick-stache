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

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnMultiChoiceClickListener;
import android.os.Bundle;

import com.actionbarsherlock.app.SherlockDialogFragment;

public class QualityDialog extends SherlockDialogFragment {

	String title = null;
	boolean[] selected = null;
	OnMultiChoiceClickListener listListener = null;
	DialogInterface.OnClickListener okListener = null;
	
	
	public QualityDialog()
	{
		super();
		selected = new boolean[7];
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		String[] items = getItems();
		AlertDialog.Builder builder = new AlertDialog.Builder(this.getSherlockActivity());
		builder.setTitle(title);
		builder.setMultiChoiceItems(items, selected, new OnMultiChoiceClickListener(){
			@Override
			public void onClick(DialogInterface arg0, int arg1, boolean arg2) {
				selected[arg1] = arg2;
				if ( listListener != null )
					listListener.onClick(arg0, arg1, arg2);
			}});
		builder.setPositiveButton("Ok", okListener);
		builder.setNegativeButton("Cancel", null);
		return builder.create();
	}
	
	public void setSelected( boolean[] selected )
	{
		this.selected = selected;
	}
	
	public boolean[] getSelected()
	{
		return selected;
	}
	
	public void setTitle( String title )
	{
		this.title = title;
	}
	
	public String getTitle( String title )
	{
		return title;
	}
	
	public void setOnListClick( OnMultiChoiceClickListener listener )
	{
		listListener = listener;
	}
	
	public void setOnOkClick( OnClickListener listener )
	{
		okListener = listener;
	}
	
	protected String[] getItems()
	{
		return QualityEnum.valuesToString();
	}

}
