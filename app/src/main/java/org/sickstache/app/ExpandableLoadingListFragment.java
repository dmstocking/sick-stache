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
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import org.sickstache.app.ExpandableLoadingListFragment.EasyExpandableListAdapter.Pair;

import java.util.ArrayList;
import java.util.List;

public abstract class ExpandableLoadingListFragment<GroupType, ItemType, Params, Progress, Result> extends LoadingListFragment<Params, Progress, Result> {
	
	protected EasyExpandableListAdapter adapter = new EasyExpandableListAdapter();
	
	protected abstract boolean hasHeader();

	protected abstract View getChildView(GroupType group, ItemType item, int groupNum, int itemNum, View convertView, ViewGroup root);
	protected abstract View getGroupView(GroupType group, int groupNum, boolean visible, View convertView, ViewGroup root);
	
	protected abstract void onChildItemClick(ListView l, View v, GroupType group, ItemType item);
	
	protected boolean onLongChildItemClick(AdapterView<?> parent, View view, ItemType item, int pos, long id)
	{
		return false;
	}
	
	protected boolean onLongGroupItemClick(AdapterView<?> parent, View view, GroupType group, int pos, long id)
	{
		return false;
	}
	
	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view, int pos, long id) {
		if ( hasHeader() ) {
			pos--;
		}
		if ( adapter.getItemViewType(pos) == 0 ) {
			// section
			return onLongGroupItemClick(parent,view,(GroupType)adapter.getItem(pos),pos,id);
		} else {
			// item
			return onLongChildItemClick(parent,view,(ItemType)adapter.getItem(pos),pos,id);
		}
	}
	
	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		if ( hasHeader() ) {
			position--;
		}
		// if click is a group 
		if ( adapter.getItemViewType(position) == 0 ) {
			// then expand it
			adapter.setSwitchGroupVisibility(position);
		} else {
			// else hand it off to onChildItemClick
			Pair p = adapter.getGroupNChildFromPos(position);
			ItemType item = (ItemType)adapter.getChild(p.group, p.child);
			GroupType group = (GroupType)adapter.getGroup(p.group);
			onChildItemClick(l,v,group,item);
		}
		super.onListItemClick(l, v, position, id);
	}
	
	// LOLOLOL nice class name Me maybe I should just rename this Horribly Complicated Expandable List Adapter
	public class EasyExpandableListAdapter extends BaseAdapter {

		public class GroupWrapper {
			public boolean visible = false;
			public GroupType group;
			public List<ItemType> items;

//			public boolean isSelected() {
//				for ( ItemWrapper item : items ) {
//					if ( item.selected == false )
//						return false;
//				}
//				return true;
//			}
		}
		
		public class ItemWrapper {
			public boolean visible = false;
			public boolean selected = false;
			public ItemType item;
			
			public ItemWrapper( ItemType item )
			{
				this.item = item;
			}
		}
		
		public class Pair
		{
			public int group;
			public int child;
			
			public Pair( int group, int child )
			{
				this.group = group;
				this.child = child;
			}
		}
		
		List<GroupWrapper> items = new ArrayList<GroupWrapper>();

		public void clear()
		{
			items.clear();
		}
		
		public int addGroup(GroupType g) {
			GroupWrapper c = new GroupWrapper();
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
		
		public View getChildView(int groupNum, int itemNum, View convertView, ViewGroup parent) {
			GroupType g = getGroup(groupNum);
			ItemType item = getChild(groupNum, itemNum);
			return ExpandableLoadingListFragment.this.getChildView(g, item, groupNum, itemNum, convertView, parent);
		}
		
		public View getGroupView(int groupNum, View convertView, ViewGroup parent) {
			GroupWrapper g = this.items.get(groupNum);
			if ( groupNum == items.size()-1)
				return ExpandableLoadingListFragment.this.getGroupView(g.group, groupNum, g.visible, convertView, parent);
			else
				return ExpandableLoadingListFragment.this.getGroupView(g.group, groupNum, g.visible, convertView, parent);

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
			for ( GroupWrapper c : items ) {
				if ( c.visible == true ) {
					size += c.items.size();
				}
				size++;
			}
			return size;
		}
		
		public int getGlobalCount() {
			int size = 0;
			for ( GroupWrapper c : items ) {
				size += c.items.size()+1;
			}
			return size;
		}
		
		public int getGroupCount() {
			return items.size();
		}
		
		public int getChildCount(int group) {
			return items.get(group).items.size();
		}

		@Override
		public Object getItem(int position) {
			Pair p = this.getGroupNChildFromPos(position);
			if ( p.child < 0 ) {
				return this.items.get(p.group).group;
			} else {
				return this.items.get(p.group).items.get(p.child);
			}
		}
		
		public Object getGlobalItem(int gid) {
			Pair p = this.getGroupNChild(gid);
			if ( p.child < 0 ) {
				return this.items.get(p.group).group;
			} else {
				return this.items.get(p.group).items.get(p.child);
			}
		}
		
//		public int getGroupIndexOf(int arg0) {
//			int size = 0;
//			for ( int i=0; i < items.size(); i++ ) {
//				GroupWrapper c = items.get(i);
//				if ( arg0 == size )
//					return i;
//				if ( c.visible ) {
//					if ( arg0-size-1 < c.items.size() ) {
//						return i;
//					}
//					size += c.items.size()+1;
//				} else {
//					size++;
//				}
//			}
//			return -1;
//		}
		
//		public Object getGroupOf(int arg0) {
//			return items.get( getGroupIndexOf(arg0) );
//		}

		@Override
		public long getItemId(int pos) {
			return this.getGid(pos);
		}
		
//		/// Convert from position to global position because items are going to be hidden
//		public int getGlobalItemId(int arg0) {
//			int id = 0;
//			int globalId = 0;
//			for ( GroupWrapper c : items ) {
//				if ( id == arg0 ) {
//					return globalId;
//				}
//				if ( c.visible ) {
//					// everything is the same
//					if ( arg0-id-1 < c.items.size() ) {
//						return globalId+arg0-id-1;
//					} else {
//						id += c.items.size()+1;
//						globalId += c.items.size()+1;
//					}
//				} else {
//					id++;
//					globalId += c.items.size()+1;
//				}
////				if ( size == arg0 ) {
////					// then its a group
////					return c.group;
////				}
////				if (  arg0-size-1 < c.items.size() ) {
////					return c.items.get(arg0-size-1);
////				}
////				size += c.items.size()+1;
//			}
//			return -1;
//		}
		
//		/// Convert from position to global position because items are going to be hidden
//		public int getGlobalItemId(int group, int child) {
//			int id = 0;
//			for ( int i=0; i < items.size(); i++ ) {
//				if ( group == i ) {
//					return id+child;
//				}
//				GroupWrapper c = items.get(i);
//				if ( c.visible ) {
//					id += c.items.size()+1;
//				} else {
//					id++;
//				}
//			}
//			return -1;
//		}

		@Override
		public View getView(int pos, View convertView, ViewGroup parent) {
			Pair p = this.getGroupNChildFromPos(pos);
			if ( p.child < 0 ) {
				return getGroupView( p.group, convertView, parent );
			} else {
				return getChildView( p.group, p.child, convertView, parent );
			}
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
			Pair p = this.getGroupNChildFromPos(position);
			if ( p.child < 0 )
				return 0;
			else
				return 1;
		}
		
		public void setSwitchGroupVisibility(int position) {
			Pair p = this.getGroupNChildFromPos(position);
			GroupWrapper g = this.items.get(p.group);
			g.visible = !g.visible;
			this.notifyDataSetChanged();
		}
		
		public int getGid( int pos )
		{
			int gid = 0;
			int current = 0;
			for ( int i=0; i < items.size(); i++ ) {
				GroupWrapper g = items.get(i);
				int size = g.items.size();
				if ( g.visible ) {
					if ( current+size >= pos ) {
						return gid + pos - current;
					}
					current += size+1;
				} else {
					if ( current == pos ) {
						return gid;
					}
					current++;
				}
				gid += size+1;
			}
			return -1;
		}
		
		public int getGid( int group, int child )
		{
			int current = 0;
			for ( int i=0; i < items.size(); i++ ) {
				if ( group == i ) {
					return current+child+1;
				}
				GroupWrapper g = items.get(i);
				current += g.items.size()+1;
			}
			return -1;
		}
		
		public Pair getGroupNChild( int gid )
		{
			int current = 0;
			for ( int i=0; i < items.size(); i++ ) {
				if ( current == gid ) {
					return new Pair(i,-1);
				} else {
					int size = items.get(i).items.size();
					if ( current+size >= gid ) {
						return new Pair(i,gid-current-1);
					} else {
						current += size+1;
					}
				}
			}
			return null;
		}
		
		public Pair getGroupNChildFromPos( int pos )
		{
			int current = 0;
			for ( int i=0; i < items.size(); i++ ) {
				GroupWrapper g = items.get(i);
				if ( g.visible ) {
					int size = g.items.size();
					if ( current+size >= pos ) {
						return new Pair(i,pos-current-1);
					} else {
						current += size+1;
					}
				} else {
					if ( current == pos )
						return new Pair(i,-1);
					current++;
				}
			}
			return null;
		}
	}
}
