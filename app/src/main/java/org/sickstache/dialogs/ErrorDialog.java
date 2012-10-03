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

import android.R;
import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import com.actionbarsherlock.app.SherlockDialogFragment;

public class ErrorDialog extends SherlockDialogFragment {

	private String title = "Error";
	private String message = "";
	
	public ErrorDialog()
	{
		super();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this.getSherlockActivity());
		builder.setTitle(title);
		builder.setIcon(R.drawable.ic_dialog_alert);
		builder.setMessage(message);
		builder.setPositiveButton(R.string.ok, null);
		builder.setCancelable(false);
		return builder.create();
	}
	
	public void setMessage( String message )
	{
		this.message = message;
	}
	
	public String getMessage( String message )
	{
		return message;
	}
	
	public void setTitle( String title )
	{
		this.title = title;
	}
	
	public String getTitle( String title )
	{
		return title;
	}
	
}
