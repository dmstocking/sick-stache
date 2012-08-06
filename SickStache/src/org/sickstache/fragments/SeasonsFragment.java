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
import java.util.Collections;
import java.util.List;

import org.sickbeard.Episode;
import org.sickbeard.Season;
import org.sickbeard.SeasonEpisodePair;
import org.sickbeard.Show;
import org.sickstache.EditShowActivity;
import org.sickstache.EpisodeActivity;
import org.sickstache.R;
import org.sickstache.app.ExpandableLoadingListFragment;
import org.sickstache.dialogs.ErrorDialog;
import org.sickstache.dialogs.StatusDialog;
import org.sickstache.helper.Preferences;
import org.sickstache.task.SetStatusTask;
import org.sickstache.widget.DefaultImageView;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.actionbarsherlock.view.ActionMode;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

public class SeasonsFragment extends ExpandableLoadingListFragment<Integer,Episode,String, Void, Show> {

	private String tvdbid;
	private String show;
	
	private LinearLayout header;
	
	private DefaultImageView showImage;
	private TextView showView;
	private TextView airs;
	private TextView quality;
	private TextView language;
	private TextView paused;
	private TextView airbydate;
	
	private LayoutInflater layoutInflater;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Intent parent = this.getActivity().getIntent();
		tvdbid = parent.getStringExtra("tvdbid");
		show = parent.getStringExtra("show");
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		layoutInflater = (LayoutInflater)getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if ( hasHeader() ) {
			header = (LinearLayout)inflater.inflate(R.layout.show_fragment_header, null);
		}
		return super.onCreateView(inflater, container, savedInstanceState);
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.seasons_menu, menu);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		if ( hasHeader() ) {
			this.getListView().addHeaderView(header,null,false);
		}
		showView = (TextView) view.findViewById(R.id.show);
		showView.setText(show);
		airs = (TextView) view.findViewById(R.id.airsTextView);
		quality = (TextView) view.findViewById(R.id.qualityTextView);
		language = (TextView) view.findViewById(R.id.languageTextView);
		paused = (TextView) view.findViewById(R.id.pausedTextView);
		airbydate = (TextView) view.findViewById(R.id.airbydateTextView);
		showImage = (DefaultImageView) view.findViewById(R.id.showImage);
		showImage.setBanner( tvdbid );
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch ( item.getItemId() ) {
		case R.id.editShowMenuItem:
			Intent intent = new Intent( this.getActivity(), EditShowActivity.class );
			intent.putExtra("tvdbid", tvdbid);
			intent.putExtra("show", show);
			this.startActivity(intent);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	protected String getEmptyText() {
		return "Show Could Not Be Found";
	}

	@Override
	protected String[] getRefreshParams() {
		return new String[] { tvdbid };
	}

	@Override
	protected Show doInBackground(String... arg0) throws Exception {
		return Preferences.singleton.getSickBeard().show(arg0[0],true);
	}

	@Override
	protected void onProgressUpdate(Void... values) {
		;
	}

	@Override
	protected void onPostExecute(Show result) {
		setListAdapter(adapter);
		airs.setText(result.airs);
		quality.setText(result.quality);
		language.setText(result.language);
		// TODO this is probably not the best way to handle this
		paused.setText(result.paused ? "Yes" : "No");
		airbydate.setText(result.airbydate ? "Yes" : "No");
		adapter.clear();
		Collections.reverse(result.seasonList);
		for ( Season s : result.seasonList ) {
			int group = adapter.addGroup(s.season);
			Collections.reverse(s.getEpisodes());
			for ( Episode e : s.getEpisodes() ) {
				adapter.addChild(group, e);
			}
		}
		adapter.notifyDataSetChanged();
	}

	@Override
	protected View getChildView(Integer group, Episode item, int groupNum, int itemNum, View convertView, ViewGroup root) {
		if ( convertView == null )
			convertView = layoutInflater.inflate(R.layout.episodes_item, root, false);
		View row = convertView;
		TextView text = (TextView) row.findViewById(R.id.episodesItemTextView);
		View overlay = (View) row.findViewById(R.id.episodesSelectedOverlay);
		text.setText(item.episode + " - " + item.name);
		switch ( item.status ) {
		case WANTED:
			text.setBackgroundResource(R.color.sickbeard_wanted_background);
			break;
		case DOWNLOADED:
		case SNATCHED:
		case ARCHIVED:
			text.setBackgroundResource(R.color.sickbeard_downloaded_background);
			break;
		case SKIPPED:
		case IGNORED:
			text.setBackgroundResource(R.color.sickbeard_skipped_background);
			break;
		case UNAIRED:
			text.setBackgroundResource(R.color.sickbeard_unaired_background);
			break;
		}
		int groupGid = adapter.getGid(groupNum,0)-1;
		int itemGid = adapter.getGid(groupNum,itemNum);
		if ( selected.contains(groupGid) || selected.contains(itemGid) ) {
			overlay.setVisibility(View.VISIBLE);
		} else {
			overlay.setVisibility(View.INVISIBLE);
		}
		return row;
	}

	@Override
	protected View getGroupView(Integer group, int groupNum, boolean visible, View convertView, ViewGroup root) {
		if ( convertView == null )
			convertView = layoutInflater.inflate(R.layout.seasons_item, root, false);
		View row = convertView;
		TextView text = (TextView) row.findViewById(R.id.seasonsItemTextView);
		View overlay = row.findViewById(R.id.seasonsSelectedOverlay);
		if ( group == 0 ) {
			text.setText("Specials" );
		} else {
			text.setText("Season " + group);
		}
		if ( visible ) {
			text.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.expander_close_holo_dark, 0);
		} else {
			text.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.expander_open_holo_dark, 0);
		}
		int groupGid = adapter.getGid(groupNum,0)-1;
		if ( selected.contains(groupGid) ) {
			overlay.setVisibility(View.VISIBLE);
		} else {
			overlay.setVisibility(View.INVISIBLE);
		}
		return row;
	}

	@Override
	protected void onChildItemClick(ListView l, View v, Integer group, Episode item) {
		Intent intent = new Intent( this.getActivity(), EpisodeActivity.class );
		intent.putExtra("tvdbid", this.tvdbid);
		intent.putExtra("show", this.show);
		intent.putExtra("season", group.toString());
		intent.putExtra("episode", item.episode);
		intent.putExtra("status", item.status.toString());
		startActivity(intent);
	}
	
//	@Override
//	public void onListItemClick(ListView l, View v, int position, long id) {
//		if ( hasHeader() ) {
//			if ( position == 0 ) {
//				// when the header is clicked
////				Intent intent = new Intent( this.getActivity(), EditShowActivity.class );
////				intent.putExtra("tvdbid", tvdbid);
////				intent.putExtra("show", show);
////				this.startActivity(intent);
//			}
//			super.onListItemClick(l, v, position-1, id);
//		} else {
//			super.onListItemClick(l, v, position, id);
//		}
//	}

	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view, int pos, long id) {
		if ( actionMode == null ) {
			actionMode = getSherlockActivity().startActionMode( new ActionMode.Callback() {
				
				@Override
				public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
					return false;
				}
				
				@Override
				public void onDestroyActionMode(ActionMode mode) {
					selected.clear();
					actionMode = null;
					adapter.notifyDataSetChanged();
				}
				
				@Override
				public boolean onCreateActionMode(ActionMode mode, Menu menu) {
					MenuInflater inflate = getSherlockActivity().getSupportMenuInflater();
					inflate.inflate(R.menu.seasons_cab_menu, menu);
					return true;
				}
				
				@Override
				public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
					switch ( item.getItemId() ) {
					case R.id.setStatusMenuItem:
						final StatusDialog sDialog = new StatusDialog();
						sDialog.setTitle("Status");
						sDialog.setOnListClick( new OnClickListener(){
							@Override
							public void onClick(DialogInterface arg0, int arg1) {
								final ProgressDialog dialog = ProgressDialog.show(SeasonsFragment.this.getSherlockActivity(), "","Setting Status. Please wait...", true);
								dialog.setCancelable(true);
								dialog.show();
								SetStatusTask task = new SetStatusTask(SeasonsFragment.this.tvdbid, SeasonsFragment.this.getSelected(), sDialog.getStatus(arg1)){
									@Override
									protected void onPostExecute(Boolean result) {
										super.onPostExecute(result);
										if ( dialog != null && dialog.isShowing() )
											dialog.dismiss();
										if ( error != null && getFragmentManager() != null ) {
											ErrorDialog dialog = new ErrorDialog();
											dialog.setMessage("Error setting status.\nERROR: "+error.getMessage());
											dialog.show(getFragmentManager(), "statusError");
										}
									}};
								task.execute();
							}});
						sDialog.show(getFragmentManager(), "setStatus");
						return true;
					}
					return false;
				}
			});
		}
		return super.onItemLongClick(parent, view, pos, id);
	}

	@Override
	protected boolean onLongChildItemClick(AdapterView<?> parent, View view, Episode item, int pos, long id)
	{
		// WOW this isn't overly verbose
		// TODO this entire expandable list needs a redesign this is just retarded
		ExpandableLoadingListFragment<Integer,Episode,String, Void, Show>.EasyExpandableListAdapter.Pair p = adapter.getGroupNChild((int) id);
		int group = adapter.getGid(p.group, -1);
		int size = adapter.getChildCount(p.group);
		if ( selected.contains((Object) group) ) {
			selected.remove((Object) group);
			// add everyone else
			for ( int i=1; i <= size; i++) {
				selected.add(group+i);
			}
			// remove this one
			selected.remove((Object)((int) id));
		} else {
			if ( selected.contains((int) id) ) {
				selected.remove((Object)((int) id));
			} else {
				selected.add((int) id);
				boolean groupSelected = true;
				for ( int i=1; i <= size; i++) {
					if ( selected.contains((Object) (group+i)) ) {
						;
					} else {
						groupSelected = false;
						break;
					}
				}
				if ( groupSelected ) {
					selected.add(group);
					for ( int i=1; i <= size; i++) {
						selected.remove((Object) (group+i));
					}
				}
			}
		}
		adapter.notifyDataSetChanged();
		actionMode.setTitle(selected.size() + " Items Selected");
		if ( selected.size() == 0 )
			actionMode.finish(); // stop cab
		return true;
	}
	
	@Override
	protected boolean onLongGroupItemClick(AdapterView<?> parent, View view, Integer group, int pos, long id)
	{
		if ( selected.contains((Object)((int) id)) ) {
			selected.remove((Object)((int) id));
		} else {
			selected.add((int) id);
			ExpandableLoadingListFragment<Integer,Episode,String, Void, Show>.EasyExpandableListAdapter.Pair p = adapter.getGroupNChild((int) id);
			int first = (int) id + 1;
			int last = first + adapter.getChildCount(p.group);
			for ( int i=first; i <= last; i++ ) {
				selected.remove((Object) i);
			}
		}
		adapter.notifyDataSetChanged();
		actionMode.setTitle(selected.size() + " Items Selected");
		if ( selected.size() == 0 )
			actionMode.finish(); // stop cab
		return true;
	}

	protected boolean hasHeader() {
		return true;
	}

	protected boolean hasFooter() {
		return false;
	}
	
	private List<SeasonEpisodePair> getSelected()
	{
		List<SeasonEpisodePair> ret = new ArrayList<SeasonEpisodePair>();
		for ( int gid : selected ) {
			ExpandableLoadingListFragment<Integer,Episode,String, Void, Show>.EasyExpandableListAdapter.Pair p = adapter.getGroupNChild(gid);
			Integer season = adapter.getGroup(p.group);
			if ( p.child < 0 ) {
				// add all of the items for this season
				for ( int i=0; i < adapter.getChildCount(p.group); i++ ) {
					Episode ep = adapter.getChild(p.group, i);
					ret.add( new SeasonEpisodePair( season, Integer.valueOf(ep.episode) ) );
				}
			} else {
				Episode ep = adapter.getChild(p.group, p.child);
				ret.add( new SeasonEpisodePair( season, Integer.valueOf(ep.episode) ) ); // just add this one
			}
		}
		return ret;
	}
}
