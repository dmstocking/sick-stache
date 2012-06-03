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
package org.sickstache.fragments;

import java.util.EnumSet;

import org.sickbeard.Show.QualityEnum;
import org.sickstache.R;
import org.sickstache.dialogs.ArchiveQualityDialog;
import org.sickstache.dialogs.PauseDialog;
import org.sickstache.dialogs.InitialQualityDialog;
import org.sickstache.dialogs.QualityDialog;
import org.sickstache.helper.Preferences;
import org.sickstache.task.FetchBannerTask;
import org.sickstache.task.PauseTask;
import org.sickstache.task.RefreshTask;
import org.sickstache.task.SetQualityTask;
import org.sickstache.task.UpdateTask;
import org.sickstache.widget.DefaultImageView;
import org.sickstache.widget.WorkingTextView;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;

public class EditShowFragment extends SherlockFragment {
	
	protected String tvdbid;
	protected String show;

	protected DefaultImageView showImage;
	protected TextView showTextView;
	
	protected WorkingTextView banner;
	protected WorkingTextView quality;
//	protected WorkingTextView archiveQuality;
	protected WorkingTextView pause;
	protected WorkingTextView refresh;
	protected WorkingTextView update;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Intent intent = this.getActivity().getIntent();
		tvdbid = intent.getStringExtra("tvdbid");
		show = intent.getStringExtra("show");
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View root = inflater.inflate(R.layout.edit_show_fragment, container, false);
		showImage = (DefaultImageView)root.findViewById(R.id.showImage);
		showTextView = (TextView)root.findViewById(R.id.showTextView);
		banner = (WorkingTextView)root.findViewById(R.id.bannerWorkingTextView);
		quality = (WorkingTextView)root.findViewById(R.id.qualityWorkingTextView);
//		archiveQuality = (WorkingTextView)root.findViewById(R.id.archiveQualityWorkingTextView);
		pause = (WorkingTextView)root.findViewById(R.id.pauseWorkingTextView);
		refresh = (WorkingTextView)root.findViewById(R.id.refreshWorkingTextView);
		update = (WorkingTextView)root.findViewById(R.id.updateWorkingTextView);
		try {
			showImage.setImageJavaURI(Preferences.singleton.getSickBeard().showGetBanner(tvdbid));
		} catch (Exception e) {;}
		showTextView.setText(show);
		banner.text.setText("Fetch Banner");
		quality.text.setText("Quality");
//		archiveQuality.text.setText("Archive Quality");
		pause.text.setText("Pause");
		refresh.text.setText("Refresh");
		update.text.setText("Update");
		// C# async LOOKS REALLY AWSOME DOESNT IT!!
		banner.text.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				try {
					banner.setIsWorking(true);
					FetchBannerTask task = new FetchBannerTask(){
						@Override
						protected void onPostExecute(Bitmap result) {
							super.onPostExecute(result);
							if ( result != null ) {
								try {
									showImage.setImageJavaURI(Preferences.singleton.getSickBeard().showGetBanner(tvdbid));
								} catch (Exception e) {;}
								banner.setIsSuccessful(true);
							} else {
								banner.setIsSuccessful(false);
							}
						}
					};
					task.execute(Preferences.singleton.getSickBeard().showGetBanner(tvdbid));
				} catch (Exception e) {
					banner.setIsSuccessful(false);
				}
			}
		});
		quality.text.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				final QualityDialog qDialog = new QualityDialog();
				qDialog.setOnListClick( new DialogInterface.OnClickListener(){
					@Override
					public void onClick(DialogInterface dialog, int which) {
						quality.setIsWorking(true);
						SetQualityTask task = new SetQualityTask(tvdbid,qDialog.getInitialQuality(),qDialog.getArchiveQuality()){
							@Override
							protected void onPostExecute(Boolean result) {
								if ( result != null && result == true )
									quality.setIsSuccessful(true);
								else
									quality.setIsSuccessful(false);
							}
						};
						task.execute();
					}} );
				qDialog.show(getFragmentManager(), "quality");
//				final InitialQualityDialog qDialog = new InitialQualityDialog( true );
//				qDialog.setTitle("Set Quality");
//				qDialog.setOnOkClick( new DialogInterface.OnClickListener(){
//					@Override
//					public void onClick(DialogInterface arg0, int arg1) {
//						final ArchiveQualityDialog aDialog = new ArchiveQualityDialog();
//						aDialog.setTitle("Set Archive Quality");
//						aDialog.setOnOkClick( new DialogInterface.OnClickListener(){
//							@Override
//							public void onClick(DialogInterface dialog, int which) {
//								quality.setIsWorking(true);
//								boolean[] archiveQualities = new boolean[7];
//								archiveQualities[0] = false;
//								for ( int i=0; i < 6; i++ ) {
//									archiveQualities[i+1] = aDialog.getSelected()[i];
//								}
//								EnumSet<QualityEnum> initial = QualityEnum.fromBooleans( qDialog.getSelected() );
//								EnumSet<QualityEnum> archive = QualityEnum.fromBooleans( archiveQualities );
//								SetQualityTask task = new SetQualityTask(tvdbid,initial,archive){
//									@Override
//									protected void onPostExecute(Boolean result) {
//										if ( result != null && result == true )
//											quality.setIsSuccessful(true);
//										else
//											quality.setIsSuccessful(false);
//									}
//								};
//								task.execute();
//							}});
//						aDialog.show(getFragmentManager(), "archive quality");
//					}});
//				qDialog.show(getFragmentManager(), "quality");
			}
		});
		// removed and make two dialogs for quality
//		archiveQuality.text.setOnClickListener(new OnClickListener(){
//			@Override
//			public void onClick(View arg0) {
//				final ArchiveQualityDialog qDialog = new ArchiveQualityDialog();
//				qDialog.setTitle("Set Archive Quality");
//				qDialog.setOnOkClick( new DialogInterface.OnClickListener(){
//					@Override
//					public void onClick(DialogInterface arg0, int arg1) {
//						archiveQuality.setIsWorking(true);
//						boolean[] rawQualities = qDialog.getSelected();
//						boolean[] qualities = new boolean[7];
//						qualities[0] = false;
//						for ( int i=0; i < 6; i++ ) {
//							qualities[i+1] = rawQualities[i];
//						}
//						EnumSet<QualityEnum> archive = QualityEnum.fromBooleans( qualities );
//						SetQualityTask task = new SetQualityTask(tvdbid,null,archive){
//							@Override
//							protected void onPostExecute(Boolean result) {
//								if ( result != null && result == true )
//									archiveQuality.setIsSuccessful(true);
//								else
//									archiveQuality.setIsSuccessful(false);
//							}
//						};
//						task.execute();
//					}});
//				qDialog.show(getFragmentManager(), "archiveQuality");
//			}
//		});
		pause.text.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				final PauseDialog pDialog = new PauseDialog();
				pDialog.setTitle("Set Pause");
				pDialog.setOnOkClick( new DialogInterface.OnClickListener(){
					@Override
					public void onClick(DialogInterface dialog, int which) {
						pause.setIsWorking(true);
						PauseTask task = new PauseTask(tvdbid, pDialog.getPause()){
							@Override
							protected void onPostExecute(Boolean result) {
								super.onPostExecute(result);
								if ( result != null && result == true ) {
									EditShowFragment.this.pause.setIsSuccessful(true);
								} else {
									EditShowFragment.this.pause.setIsSuccessful(false);
								}
							}
						};
						task.execute();
					}});
				pDialog.show(getFragmentManager(), "pause");
			}
		});
		refresh.text.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				refresh.setIsWorking(true);
				RefreshTask task = new RefreshTask(tvdbid){
					@Override
					protected void onPostExecute(Boolean result) {
						super.onPostExecute(result);
						if ( result != null && result == true ) {
							refresh.setIsSuccessful(true);
						} else {
							refresh.setIsSuccessful(false);
						}
					}
				};
				task.execute();
			}
		});
		update.text.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				update.setIsWorking(true);
				UpdateTask task = new UpdateTask(tvdbid){
					@Override
					protected void onPostExecute(Boolean result) {
						super.onPostExecute(result);
						if ( result != null && result == true ) {
							update.setIsSuccessful(true);
						} else {
							update.setIsSuccessful(false);
						}
					}
				};
				task.execute();
			}
		});
		return root;
	}
}
