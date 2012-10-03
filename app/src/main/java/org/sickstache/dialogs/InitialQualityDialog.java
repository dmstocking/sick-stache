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
import android.content.DialogInterface.OnMultiChoiceClickListener;
import android.os.Bundle;
import com.actionbarsherlock.app.SherlockDialogFragment;
import org.sickbeard.Show.QualityEnum;
import org.sickstache.R;

public class InitialQualityDialog extends SherlockDialogFragment {

	protected String title = null;
	protected boolean[] selected = null;
	protected OnMultiChoiceClickListener listListener = null;
	protected DialogInterface.OnClickListener okListener = null;
	
	protected boolean useContinue = false;
	
	
	public InitialQualityDialog()
	{
		super();
		selected = new boolean[7];
	}
	
	public InitialQualityDialog( boolean useContinue )
	{
		super();
		selected = new boolean[7];
		this.useContinue = useContinue;
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
		if ( useContinue )
			builder.setPositiveButton(R.string.continue_name, okListener);
		else
			builder.setPositiveButton(R.string.ok, okListener);
		builder.setNegativeButton(R.string.cancel, null);
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
