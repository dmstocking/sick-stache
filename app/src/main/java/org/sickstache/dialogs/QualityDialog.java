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
import org.sickbeard.Show.QualityEnum;

import java.util.EnumSet;

public class QualityDialog extends SherlockDialogFragment {

	protected String title = "Set Quality";
	protected OnClickListener listListener = null;
	protected EnumSet<QualityEnum> initialQuality;
	protected EnumSet<QualityEnum> archiveQuality;
	
	protected boolean useContinue = false;
	
	
	public QualityDialog()
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
		builder.setItems(items, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				switch( which ) {
				case 0:
					// SD
					initialQuality = EnumSet.of(QualityEnum.SDTV, QualityEnum.SDDVD);
					break;
				case 1:
					// HD
					initialQuality = EnumSet.of(QualityEnum.HDTV, QualityEnum.HDWEBDL, QualityEnum.HDBLURAY);
					break;
				case 2:
					// CUSTOM
					final InitialQualityDialog qDialog = new InitialQualityDialog( true );
					qDialog.setTitle("Set Quality");
					qDialog.setOnOkClick( new DialogInterface.OnClickListener(){
						@Override
						public void onClick(DialogInterface arg0, int arg1) {
							final ArchiveQualityDialog aDialog = new ArchiveQualityDialog();
							aDialog.setTitle("Set Archive Quality");
							aDialog.setOnOkClick( new DialogInterface.OnClickListener(){
								@Override
								public void onClick(DialogInterface dialog, int which) {
									boolean[] archiveQualities = new boolean[7];
									archiveQualities[0] = false;
									for ( int i=0; i < 6; i++ ) {
										archiveQualities[i+1] = aDialog.getSelected()[i];
									}
									initialQuality = QualityEnum.fromBooleans(qDialog.getSelected());
									archiveQuality = QualityEnum.fromBooleans( archiveQualities );
									if ( listListener != null ) {
										listListener.onClick(dialog, which);
									}
								}});
							aDialog.show(qDialog.getFragmentManager(), "archiveQuality");
						}});
					qDialog.show(QualityDialog.this.getFragmentManager(), "initialQuality");
					break;
				case 3:
					// ANY
					initialQuality = EnumSet.of(QualityEnum.SDTV, QualityEnum.SDDVD, QualityEnum.HDTV, QualityEnum.HDWEBDL, QualityEnum.HDBLURAY, QualityEnum.UNKNOWN);
					break;
				}
				if ( which != 2 )
					listListener.onClick(dialog,which);
				return;
			}
		});
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
	
	public EnumSet<QualityEnum> getInitialQuality()
	{
		return initialQuality;
	}
	
	public EnumSet<QualityEnum> getArchiveQuality()
	{
		return archiveQuality;
	}
	
	public void setOnListClick( OnClickListener listener )
	{
		listListener = listener;
	}
	
	protected String[] getItems()
	{
		return new String[]{"SD","HD","CUSTOM","ANY"};
	}

}
