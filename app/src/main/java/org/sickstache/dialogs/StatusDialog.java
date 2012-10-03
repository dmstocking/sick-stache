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

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import com.actionbarsherlock.app.SherlockDialogFragment;
import org.sickbeard.Episode.StatusEnum;

public class StatusDialog extends SherlockDialogFragment {

	String title = null;
	DialogInterface.OnClickListener listListener = null;
	
	
	public StatusDialog()
	{
		super();
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
		builder.setItems(items, listListener);
		return builder.create();
	}
	
	public void setTitle( String title )
	{
		this.title = title;
	}
	
	public String getTitle( String title )
	{
		return title;
	}
	
	public StatusEnum getStatus( int clicked )
	{
		return StatusEnum.valuesSetable()[clicked];
	}
	
	public void setOnListClick( OnClickListener listener )
	{
		listListener = listener;
	}
	
	protected String[] getItems()
	{
		return StatusEnum.valuesSetableToString();
	}

}
