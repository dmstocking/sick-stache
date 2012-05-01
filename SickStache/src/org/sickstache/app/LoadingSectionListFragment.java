package org.sickstache.app;

import java.util.ArrayList;
import java.util.List;

import android.R;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

public abstract class LoadingSectionListFragment<ListType, Params, Progress, Result> extends LoadingListFragment<Params, Progress, Result> {
	
	protected SectionAdapter adapter = new SectionAdapter();
	
	protected abstract int getSectionLayoutId();
	protected abstract int getListTypeLayoutId();
	
	protected abstract View getSectionView(int position, String item, View convertView, ViewGroup parent);
	protected abstract View getListTypeView(int position, ListType item, View convertView, ViewGroup parent);

	public class SectionAdapter extends BaseAdapter {

		private class SectionType {
			public SectionType( int index, String label ) {
				this.index = index;
				this.label = label;
			}
			public int index;
			public String label;
		}

		private List<ListType> items = new ArrayList<ListType>();
		private List<SectionType> sections = new ArrayList<SectionType>();
		
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
				convertView = LayoutInflater.from(LoadingSectionListFragment.this.getSherlockActivity())
						.inflate(layoutType, parent,false);
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
