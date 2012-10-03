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

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.List;

public abstract class LoadingSectionListFragment<ListType, Params, Progress, Result> extends LoadingListFragment<Params, Progress, Result> {
	
	protected SectionAdapter adapter;
	
	protected abstract int getSectionLayoutId();
	protected abstract int getListTypeLayoutId();
	
	protected abstract View getSectionView(int position, String item, View convertView, ViewGroup parent);
	protected abstract View getListTypeView(int position, ListType item, View convertView, ViewGroup parent);

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.adapter = new SectionAdapter(this.getActivity());
	}
	
	public class SectionAdapter extends BaseAdapter {

		private class SectionType {
			public SectionType( int index, String label ) {
				this.index = index;
				this.label = label;
			}
			public int index;
			public String label;
		}

		protected LayoutInflater layoutInflater;
		
		private List<ListType> items = new ArrayList<ListType>();
		private List<SectionType> sections = new ArrayList<SectionType>();
		
		public SectionAdapter(Context context) {
			layoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}
		
		public void add(ListType item) {
			items.add(item);
		}
		
		public void addSection(String label) {
			sections.add( new SectionType( this.getCount(), label ) );
		}
		
		public void clear() {
			items.clear();
			sections.clear();
		}
		
		@Override
		public int getCount() {
			return items.size() + sections.size();
		}

		@Override
		public Object getItem(int arg0) {
			int offset = 0;
			for ( SectionType sec : sections ) {
				if ( sec.index == arg0 ) {
					return sec;
				} else if ( sec.index <= arg0 ){
					offset++;
				}
			}
			return items.get(arg0-offset);
		}

		@Override
		public long getItemId(int arg0) {
			return arg0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			int viewType = getItemViewType(position);
			int layoutType = viewType == 0 ? getListTypeLayoutId() : getSectionLayoutId();
			
			if ( convertView == null ) {
				convertView = layoutInflater.inflate(layoutType, parent,false);
			}
			
			if ( viewType == 0 ) {
				// item
				ListType item = (ListType)getItem(position);
				return getListTypeView(position, item, convertView, parent);
			} else {
				// seperator
				SectionType item = (SectionType)getItem(position);
				return getSectionView(position, item.label, convertView, parent);
			}
		}
		
		@Override
		public boolean areAllItemsEnabled() {
			return false;
		}

		@Override
		public int getItemViewType(int position) {
			for ( SectionType sec : sections ) {
				if ( sec.index == position ) {
					return 1;
				}
			}
			return 0;
		}

		@Override
		public int getViewTypeCount() {
			return 2;
		}

		@Override
		public boolean isEnabled(int position) {
			for ( SectionType sec : sections ) {
				if ( sec.index == position ) {
					return false;
				}
			}
			return true;
		}
		
	}
}
