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
import org.sickstache.R;

public class PauseDialog extends SherlockDialogFragment {

	String title = null;
	Boolean pause = null;
	DialogInterface.OnClickListener listListener = null;
	DialogInterface.OnClickListener okListener = null;
	
	
	public PauseDialog()
	{
		super();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		String[] items = new String[] { "Default", "Pause", "Unpause" };
		AlertDialog.Builder builder = new AlertDialog.Builder(this.getSherlockActivity());
		builder.setTitle(title);
		builder.setSingleChoiceItems(items, 0, new OnClickListener(){
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				switch ( arg1 ) {
				case 0:
					pause = null;
					break;
				case 1:
					pause = true;
					break;
				case 2:
					pause = false;
					break;
				}
				if ( listListener != null )
					listListener.onClick(arg0, arg1);
				return;
			}
		});
		builder.setPositiveButton(R.string.ok, okListener);
		builder.setNegativeButton(R.string.cancel, null);
		return builder.create();
	}
	
	public void setPause( Boolean pause )
	{
		this.pause = pause;
	}
	
	public Boolean getPause()
	{
		return pause;
	}
	
	public void setTitle( String title )
	{
		this.title = title;
	}
	
	public String getTitle( String title )
	{
		return title;
	}
	
	public void setOnListClick( OnClickListener listener )
	{
		listListener = listener;
	}
	
	public void setOnOkClick( OnClickListener listener )
	{
		okListener = listener;
	}

}
