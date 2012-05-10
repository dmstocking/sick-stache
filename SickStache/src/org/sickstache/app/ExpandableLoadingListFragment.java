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
package org.sickstache.app;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public abstract class ExpandableLoadingListFragment<GroupType, ItemType, Params, Progress, Result> extends LoadingListFragment<Params, Progress, Result> {
	
	protected EasyExpandableListAdapter adapter = new EasyExpandableListAdapter();

	protected abstract View getChildView(GroupType group, ItemType item, int groupNum, int itemNum, boolean isLastView, View convertView, ViewGroup root);
	protected abstract View getGroupView(GroupType group, int groupNum, boolean visible, boolean isLastView, View convertView, ViewGroup root);
	
	protected abstract void onChildItemClick(ListView l, View v, GroupType group, ItemType item);

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		// if click is a group 
		if ( adapter.getItemViewType(position) == 0 ) {
			// then expand it
			adapter.setSwitchGroupVisibility(position);
		} else {
			// else hand it off to onChildItemClick
			ItemType item = (ItemType)adapter.getItem(position);
			GroupType group = (GroupType)adapter.getGroupOf(position);
			onChildItemClick(l,v,group,item);
		}
		super.onListItemClick(l, v, position, id);
	}
	
	public class EasyExpandableListAdapter extends BaseAdapter {

		public class Container {
			public boolean visible = false;
			public GroupType group;
			public List<ItemType> items;
		}
		
		List<Container> items = new ArrayList<Container>();

		public void clear()
		{
			items.clear();
		}
		
		public int addGroup(GroupType g) {
			Container c = new Container();
			c.group = g;
			c.items = new ArrayList<ItemType>();
			items.add(c);
			return items.size()-1;
		}
		
		public void addChild(int group, ItemType item) {
			items.get(group).items.add(item);
		}
		
		public GroupType getGroup(int arg0) {
			return items.get(arg0).group;
		}
		
		public ItemType getChild(int arg0, int arg1) {
			return items.get(arg0).items.get(arg1);
		}
		
//		public long getChildId(int arg0, int arg1) {
//			int id = 0;
//			int i = 0;
//			while (i < arg0) {
//				id += items.get(i).items.size()+1;
//				i++;
//			}
//			return id + arg1;
//		}
		
		public View getChildView(int arg0, int arg1, boolean arg2, View arg3, ViewGroup arg4) {
			GroupType g = getGroup(arg0);
			ItemType item = getChild(arg0, arg1);
			return ExpandableLoadingListFragment.this.getChildView(g, item, arg0, arg1, arg2, arg3, arg4);
		}
		
		public View getGroupView(int arg0, boolean arg1, View arg2, ViewGroup arg3) {
			GroupType g = getGroup(arg0);
			if ( arg0 == items.size()-1)
				return ExpandableLoadingListFragment.this.getGroupView(g, arg0, items.get(arg0).visible, true, arg2, arg3);
			else
				return ExpandableLoadingListFragment.this.getGroupView(g, arg0, items.get(arg0).visible, false, arg2, arg3);

		}
		
//		public int getChildrenCount(int arg0) {
//			return items.get(arg0).items.size();
//		}
//		
//		
//		public int getGroupCount() {
//			return items.size();
//		}
//		
//		public long getGroupId(int arg0) {
//			return arg0;
//		}

		@Override
		public int getCount() {
			int size = 0;
			for ( Container c : items ) {
				if ( c.visible == true ) {
					size += c.items.size();
				}
				size++;
			}
			return size;
		}

		@Override
		public Object getItem(int arg0) {
			int size = 0;
			for ( Container c : items ) {
				if ( size == arg0 ) {
					// then its a group
					return c.group;
				}
				if ( c.visible ) {
					if (  arg0-size-1 < c.items.size() ) {
						return c.items.get(arg0-size-1);
					}
					size += c.items.size()+1;
				} else {
					size++;
				}
			}
			return null;
		}
		
		public Object getGroupOf(int arg0) {
			int size = 0;
			for ( Container c : items ) {
				if ( c.visible ) {
					if ( arg0-size-1 < c.items.size() ) {
						return c.group;
					}
					size += c.items.size()+1;
				} else {
					size++;
				}
			}
			return null;
		}

		@Override
		public long getItemId(int arg0) {
			return arg0;
		}

		@Override
		public View getView(int arg0, View arg1, ViewGroup arg2) {
			int size = 0;
			int i = 0;
			for ( Container c : items ) {
				if ( size == arg0 ) {
					// then its a group
					return getGroupView( i, true, arg1, arg2 );
				}
				if ( c.visible ) {
					if ( arg0-size-1 < c.items.size() ) {
						return getChildView( i, arg0-size-1, false, arg1, arg2 );
					}
//					} else if ( arg0-size-1 == c.items.size() ) {
//						return getChildView( i, arg0-size-1, true, arg1, arg2 );
//					}
					size += c.items.size()+1;
					i++;
				} else {
					size++;
					i++;
				}
			}
			throw new RuntimeException("Indexes are messed up. Given \"" + arg0 + "\"");
//			return null;
		}

		@Override
		public int getViewTypeCount() {
			return 2;
		}

		@Override
		public boolean areAllItemsEnabled() {
			return true;
		}

		@Override
		public int getItemViewType(int position) {
			int size = 0;
			for ( Container c : items ) {
				if ( size == position ) {
					// then its a group
					return 0;
				}
				if ( c.visible ) {
					if (  position-size-1 < c.items.size() ) {
						return 1;
					}
					size += c.items.size()+1;
				} else {
					size++;
				}
			}
			return 0;
		}
		
		public void setSwitchGroupVisibility(int position) {
			int size = 0;
			for ( Container c : items ) {
				if ( size == position ) {
					c.visible = !c.visible;
					break;
				}
				if ( c.visible )
					size += c.items.size()+1;
				else
					size++;
			}
			this.notifyDataSetChanged();
		}
	}
}
