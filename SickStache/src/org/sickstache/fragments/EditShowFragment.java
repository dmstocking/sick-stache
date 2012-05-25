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

import org.sickbeard.LanguageEnum;
import org.sickbeard.Episode.StatusEnum;
import org.sickbeard.Show.QualityEnum;
import org.sickstache.HomeActivity;
import org.sickstache.dialogs.PauseDialog;
import org.sickstache.dialogs.QualityDialog;
import org.sickstache.helper.ImageCache;
import org.sickstache.helper.Preferences;
import org.sickstache.helper.SickDialogBuilder;
import org.sickstache.task.FetchBannerTask;
import org.sickstache.task.PauseTask;
import org.sickstache.task.RefreshTask;
import org.sickstache.task.SetQualityTask;
import org.sickstache.task.UpdateTask;
import org.sickstache.widget.DefaultImageView;
import org.sickstache.widget.WorkingTextView;
import org.sickstache.R;

import com.actionbarsherlock.app.SherlockFragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class EditShowFragment extends SherlockFragment {
	
	protected String tvdbid;
	protected String show;

	protected DefaultImageView showImage;
	protected TextView showTextView;
	
	protected WorkingTextView banner;
	protected WorkingTextView quality;
	protected WorkingTextView archiveQuality;
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
		archiveQuality = (WorkingTextView)root.findViewById(R.id.archiveQualityWorkingTextView);
		pause = (WorkingTextView)root.findViewById(R.id.pauseWorkingTextView);
		refresh = (WorkingTextView)root.findViewById(R.id.refreshWorkingTextView);
		update = (WorkingTextView)root.findViewById(R.id.updateWorkingTextView);
		try {
			showImage.setImageJavaURI(Preferences.singleton.getSickBeard().showGetBanner(tvdbid));
		} catch (Exception e) {;}
		showTextView.setText(show);
		banner.text.setText("Fetch Banner");
		quality.text.setText("Quality");
		archiveQuality.text.setText("Archive Quality");
		pause.text.setText("Pause");
		refresh.text.setText("Refresh");
		update.text.setText("Update");
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
				qDialog.setTitle("Set Quality");
				qDialog.setOnOkClick( new DialogInterface.OnClickListener(){
					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						quality.setIsWorking(true);
						boolean[] qualities = qDialog.getSelected();
						EnumSet<QualityEnum> initial = QualityEnum.fromBooleans( qualities );
						SetQualityTask task = new SetQualityTask(tvdbid,initial,null){
							@Override
							protected void onPostExecute(Boolean result) {
								if ( result != null && result == true )
									quality.setIsSuccessful(true);
								else
									quality.setIsSuccessful(false);
							}
						};
						task.execute();
					}});
				qDialog.show(getFragmentManager(), "quality");
			}
		});
		archiveQuality.text.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				final QualityDialog qDialog = new QualityDialog();
				qDialog.setTitle("Set Archive Quality");
				qDialog.setOnOkClick( new DialogInterface.OnClickListener(){
					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						archiveQuality.setIsWorking(true);
						boolean[] qualities = qDialog.getSelected();
						EnumSet<QualityEnum> archive = QualityEnum.fromBooleans( qualities );
						SetQualityTask task = new SetQualityTask(tvdbid,null,archive){
							@Override
							protected void onPostExecute(Boolean result) {
								if ( result != null && result == true )
									archiveQuality.setIsSuccessful(true);
								else
									archiveQuality.setIsSuccessful(false);
							}
						};
						task.execute();
					}});
				qDialog.show(getFragmentManager(), "archiveQuality");
			}
		});
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
