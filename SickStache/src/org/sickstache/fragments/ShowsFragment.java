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

import java.util.ArrayList;

import org.sickbeard.Show;
import org.sickbeard.comparator.ShowNameComparator;
import org.sickstache.R;
import org.sickstache.SeasonsActivity;
import org.sickstache.app.LoadingListFragment;
import org.sickstache.dialogs.ErrorDialog;
import org.sickstache.dialogs.PauseDialog;
import org.sickstache.helper.Preferences;
import org.sickstache.task.PauseTask;
import org.sickstache.task.RefreshTask;
import org.sickstache.task.UpdateTask;
import org.sickstache.widget.DefaultImageView;
import org.sickstache.widget.SafeArrayAdapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.actionbarsherlock.view.ActionMode;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.viewpagerindicator.TitlePageIndicator;

public class ShowsFragment extends LoadingListFragment<Void, Void, ArrayList<Show>> implements ViewPager.OnPageChangeListener {

//	private static final String[] showActions = { "Set Quality", "Pause", "Refresh", "Update" };
	
	private SafeArrayAdapter<Show> showAdapter;
	
	private TitlePageIndicator pageIndicator = null;
	
	@Override
	protected int getChoiceMode() {
		return ListView.CHOICE_MODE_NONE;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	    showAdapter = new SafeArrayAdapter<Show>(this.getActivity(), R.layout.show_banner_item) {
			@Override
			public View getView( int position, View convertView, ViewGroup parent ) {
				View row = convertView;
				if ( row == null ) {
					row = layoutInflater.inflate(R.layout.show_banner_item, null);
				}
				Show item = getItem(position);
				TextView tv = (TextView) row.findViewById(R.id.show);
				tv.setText(item.showName);
				DefaultImageView image = (DefaultImageView) row.findViewById(R.id.showImage);
				image.defaultResource = R.drawable.default_banner;
				image.setBanner( item.id );

				ImageView overlay = (ImageView)row.findViewById(R.id.showSelectedOverlay);
				if ( selected.contains(position) ) {
					overlay.setVisibility(View.VISIBLE);
				} else {
					overlay.setVisibility(View.INVISIBLE);
				}
				return row;
			}
		};
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		try {
			pageIndicator = (TitlePageIndicator)this.getActivity().findViewById(R.id.viewPagerIndicator);
			pageIndicator.setOnPageChangeListener(this);
		} catch (Exception e) {
			; // there is no viewPagerIndicator
			// tried to do this with a check but it always failed
		}
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		Intent intent = new Intent( this.getActivity(), SeasonsActivity.class );
		Show item = showAdapter.getItem(position);
		intent.putExtra("tvdbid", item.id);
		intent.putExtra("show", item.showName);
		intent.putExtra("headerfooter", true);
		startActivity(intent);
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		if ( actionMode == null ) {
			actionMode = getSherlockActivity().startActionMode( new ActionMode.Callback() {
				
				@Override
				public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
					return false;
				}
				
				@Override
				public void onDestroyActionMode(ActionMode mode) {
					showAdapter.notifyDataSetChanged();
					selected.clear();
					actionMode = null;
				}
				
				@Override
				public boolean onCreateActionMode(ActionMode mode, Menu menu) {
					MenuInflater inflate = getSherlockActivity().getSupportMenuInflater();
					inflate.inflate(R.menu.shows_cab_menu, menu);
					return true;
				}
				
				@Override
				public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
					switch ( item.getItemId() ) {
					case R.id.pauseMenuItem:
						final PauseDialog pDialog = new PauseDialog();
						pDialog.setTitle("Set Pause");
						pDialog.setOnOkClick( new OnClickListener(){
							@Override
							public void onClick(DialogInterface arg0, int arg1) {
								final ProgressDialog dialog = ProgressDialog.show(ShowsFragment.this.getSherlockActivity(), "","Pausing Shows. Please wait...", true);
								dialog.setCancelable(true);
								dialog.show();
								String[] tvdbids = new String[selected.size()];
								for ( int i=0; i < selected.size(); i++ ) {
									tvdbids[i] = showAdapter.getItem(selected.get(i)).id;
								}
								PauseTask pause = new PauseTask(tvdbids, pDialog.getPause()){
									@Override
									protected void onPostExecute(Boolean result) {
										if ( dialog != null && dialog.isShowing() )
											dialog.dismiss();
										if ( error != null && getFragmentManager() != null ) {
											ErrorDialog dialog = new ErrorDialog();
											dialog.setMessage("Error pausing show.\nERROR: "+error.getMessage());
											dialog.show(getFragmentManager(), "pauseError");
										}
									}};
								pause.execute();
							}} );
						pDialog.show(getFragmentManager(), "update");
						return true;
					case R.id.refreshMenuItem:
						{
							final ProgressDialog dialog = ProgressDialog.show(ShowsFragment.this.getSherlockActivity(), "","Refreshing Shows. Please wait...", true);
							dialog.setCancelable(true);
							dialog.show();
							String[] tvdbids = new String[selected.size()];
							for ( int i=0; i < selected.size(); i++ ) {
								tvdbids[i] = showAdapter.getItem(selected.get(i)).id;
							}
							RefreshTask refresh = new RefreshTask(tvdbids){
								@Override
								protected void onPostExecute(Boolean result) {
									if ( dialog != null && dialog.isShowing() )
										dialog.dismiss();
									if ( error != null && getFragmentManager() != null ) {
										ErrorDialog dialog = new ErrorDialog();
										dialog.setMessage("Error refreshing show.\nERROR: "+error.getMessage());
										dialog.show(getFragmentManager(), "refreshError");
									}
								}};
								refresh.execute();
						}
						return true;
					case R.id.updateMenuItem:
						{
							final ProgressDialog dialog = ProgressDialog.show(ShowsFragment.this.getSherlockActivity(), "","Updating Shows. Please wait...", true);
							dialog.setCancelable(true);
							dialog.show();
							String[] tvdbids = new String[selected.size()];
							for ( int i=0; i < selected.size(); i++ ) {
								tvdbids[i] = showAdapter.getItem(selected.get(i)).id;
							}
							UpdateTask update = new UpdateTask(tvdbids){
								@Override
								protected void onPostExecute(Boolean result) {
									if ( dialog != null && dialog.isShowing() )
										dialog.dismiss();
									if ( error != null && getFragmentManager() != null ) {
										ErrorDialog dialog = new ErrorDialog();
										dialog.setMessage("Error updating show.\nERROR: "+error.getMessage());
										dialog.show(getFragmentManager(), "updateError");
									}
								}};
							update.execute();
						}
						return true;
//					case R.id.editMenuItem:
//						// get all selected items and create the edit show activity passing all of them
//						actionMode.finish();
//						return true;
					}
					return false;
				}
			});
		}
		ImageView overlay = (ImageView)arg1.findViewById(R.id.showSelectedOverlay);
		int i = selected.indexOf(arg2);
		if ( i >= 0 ) {
			selected.remove(i);
			overlay.setVisibility(View.INVISIBLE);
		} else {
			selected.add(arg2);
			overlay.setVisibility(View.VISIBLE);
		}
		actionMode.setTitle(selected.size() + " Items Selected");
		if ( selected.size() == 0 ) {
			actionMode.finish();
		}
		return true;
	}
	@Override
	public void onPageScrollStateChanged(int arg0) {
		// do nothing
	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
		// do nothing
	}

	@Override
	public void onPageSelected(int arg0) {
		if ( arg0 != 0 && actionMode != null ) {
			actionMode.finish();
		}
	}

	@Override
	protected String getEmptyText() {
		return "No Shows Available";
	}
	
	@Override
	protected Void[] getRefreshParams() {
		return null;
	}
	
	@Override
	protected ArrayList<Show> doInBackground(Void... arg0) throws Exception {
		return Preferences.singleton.getSickBeard().shows();
	}
	
	@Override
	protected void onProgressUpdate(Void... values) {
		;
	}
	
	@Override
	protected void onPostExecute(ArrayList<Show> result) {
		setListAdapter(showAdapter);
		showAdapter.clear();
		for ( Show s : result ) {
			showAdapter.add(s);
		}
		showAdapter.sort( new ShowNameComparator() );
		if ( showAdapter.getCount() == 0 ) {
			this.setListStatus(ListStatus.EMPTY);
		}
		showAdapter.notifyDataSetChanged();
	}
}
