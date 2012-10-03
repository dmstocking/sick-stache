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

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;
import org.sickstache.HomeActivity;
import org.sickstache.R;
import org.sickstache.app.SickFragment;
import org.sickstache.dialogs.ErrorDialog;
import org.sickstache.dialogs.PauseDialog;
import org.sickstache.dialogs.QualityDialog;
import org.sickstache.helper.BannerCache;
import org.sickstache.helper.Preferences;
import org.sickstache.task.*;
import org.sickstache.widget.DefaultImageView;
import org.sickstache.widget.WorkingTextView;

public class EditShowFragment extends SickFragment {
	
	protected String tvdbid;
	protected String show;

	protected DefaultImageView showImage;
	protected TextView showTextView;
	
	protected WorkingTextView banner;
	protected WorkingTextView quality;
	protected WorkingTextView pause;
	protected WorkingTextView refresh;
	protected WorkingTextView update;
	protected WorkingTextView delete;
	
	// retaining instance with this fragment doesn't actually do anything
//	@Override
//	protected boolean isRetainInstance() {
//		return true;
//	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
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
		pause = (WorkingTextView)root.findViewById(R.id.pauseWorkingTextView);
		refresh = (WorkingTextView)root.findViewById(R.id.refreshWorkingTextView);
		update = (WorkingTextView)root.findViewById(R.id.updateWorkingTextView);
		delete = (WorkingTextView)root.findViewById(R.id.deleteWorkingTextView);
		showImage.setBanner(tvdbid);
		showTextView.setText(show);
		banner.text.setText("Fetch Banner");
		quality.text.setText("Quality");
		pause.text.setText("Pause");
		refresh.text.setText("Refresh");
		update.text.setText("Update");
		delete.text.setText("Delete");
		// C# async LOOKS REALLY AWSOME DOESNT IT!!
		banner.text.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				try {
					banner.setIsWorking(true);
					Preferences pref = Preferences.getSingleton(arg0.getContext());
					BannerCache cache = BannerCache.getSingleton(arg0.getContext());
					FetchInternetBannerTask task = new FetchInternetBannerTask(pref, cache, tvdbid){
						@Override
						protected void onPostExecute(Bitmap result) {
							super.onPostExecute(result);
							if ( result != null ) {
								showImage.setImageBitmap(result);
								banner.setIsSuccessful(true);
							} else {
								banner.setIsSuccessful(false);
							}
							if ( error != null && getFragmentManager() != null ) {
								ErrorDialog dialog = new ErrorDialog();
								dialog.setMessage("Error fetching banner for show.\nERROR: "+error.getMessage());
								dialog.show(getFragmentManager(), "bannerError");
							}
						}
					};
					task.execute();
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
						Preferences pref = Preferences.getSingleton(EditShowFragment.this.getSherlockActivity());
						SetQualityTask task = new SetQualityTask(pref, tvdbid, qDialog.getInitialQuality(), qDialog.getArchiveQuality()){
							@Override
							protected void onPostExecute(Boolean result) {
								if ( result != null && result == true ) {
									quality.setIsSuccessful(true);
								} else {
									quality.setIsSuccessful(false);
								}
								if ( error != null && getFragmentManager() != null ) {
									ErrorDialog dialog = new ErrorDialog();
									dialog.setMessage("Error setting quality for show.\nERROR: "+error.getMessage());
									dialog.show(getFragmentManager(), "qualityError");
								}
							}
						};
						task.execute();
					}} );
				qDialog.show(getFragmentManager(), "quality");
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
						Preferences pref = Preferences.getSingleton(EditShowFragment.this.getSherlockActivity());
						PauseTask task = new PauseTask(pref, tvdbid, pDialog.getPause()){
							@Override
							protected void onPostExecute(Boolean result) {
								super.onPostExecute(result);
								if ( result != null && result == true ) {
									EditShowFragment.this.pause.setIsSuccessful(true);
								} else {
									EditShowFragment.this.pause.setIsSuccessful(false);
								}
								if ( error != null && getFragmentManager() != null ) {
									ErrorDialog dialog = new ErrorDialog();
									dialog.setMessage("Error un/pause for show.\nERROR: "+error.getMessage());
									dialog.show(getFragmentManager(), "pauseError");
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
				Preferences pref = Preferences.getSingleton(arg0.getContext());
				RefreshTask task = new RefreshTask(pref, tvdbid){
					@Override
					protected void onPostExecute(Boolean result) {
						super.onPostExecute(result);
						if ( result != null && result == true ) {
							refresh.setIsSuccessful(true);
						} else {
							refresh.setIsSuccessful(false);
						}
						if ( error != null && getFragmentManager() != null ) {
							ErrorDialog dialog = new ErrorDialog();
							dialog.setMessage("Error refreshing show.\nERROR: "+error.getMessage());
							dialog.show(getFragmentManager(), "refreshError");
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
				Preferences pref = Preferences.getSingleton(arg0.getContext());
				UpdateTask task = new UpdateTask(pref, tvdbid){
					@Override
					protected void onPostExecute(Boolean result) {
						super.onPostExecute(result);
						if ( result != null && result == true ) {
							update.setIsSuccessful(true);
						} else {
							update.setIsSuccessful(false);
						}
						if ( error != null && getFragmentManager() != null ) {
							ErrorDialog dialog = new ErrorDialog();
							dialog.setMessage("Error updating show.\nERROR: "+error.getMessage());
							dialog.show(getFragmentManager(), "updateError");
						}
					}
				};
				task.execute();
			}
		});
		delete.text.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				AlertDialog.Builder builder = new AlertDialog.Builder(getSherlockActivity());
				builder.setTitle("Delete Show");
				builder.setMessage("Are you sure you want to delete this show?\n\nThis operation CANNOT be undone.");
				builder.setNegativeButton(R.string.cancel, null);
				builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener(){
					@Override
					public void onClick(DialogInterface dialog, int which) {
						delete.setIsWorking(true);
						Preferences pref = Preferences.getSingleton(EditShowFragment.this.getSherlockActivity());
						ShowDeleteTask task = new ShowDeleteTask(pref, tvdbid){
							@Override
							protected void onPostExecute(Boolean result) {
								super.onPostExecute(result);
								if ( result != null && result == true ) {
									delete.setIsSuccessful(true);
									// we want to refresh so no SINGLE_TOP
									Intent intent = new Intent( getSherlockActivity(), HomeActivity.class );
				    	            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//				    	            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
				    				startActivity(intent);
								} else {
									delete.setIsSuccessful(false);
								}
								if ( error != null && getFragmentManager() != null ) {
									ErrorDialog dialog = new ErrorDialog();
									dialog.setMessage("Error deleting show.\nERROR: "+error.getMessage());
									dialog.show(getFragmentManager(), "deleteError");
								}
							}
						};
						task.execute();
					}
				});
				builder.show();
			}
		});
		return root;
	}
}
