package org.sickstache.app;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public abstract class ExpandableLoadingListFragment<GroupType, ItemType, Params, Progress, Result> extends LoadingListFragment<Params, Progress, Result> {

	protected EasyExpandableListAdapter adapter = new EasyExpandableListAdapter();

	protected abstract View getChildView(GroupType group, ItemType item, int groupNum, int itemNum, boolean isLastView, View convertView, ViewGroup root);
	protected abstract View getGroupView(GroupType group, int groupNum, boolean isLastView, View convertView, ViewGroup root);

	public class EasyExpandableListAdapter extends BaseExpandableListAdapter {

		public class Container {
			public GroupType group;
			public List<ItemType> items;
		}

//		Map<GroupType, Integer> indexMapping = new HashMap<GroupType, Integer>();
		List<Container> items = new ArrayList<Container>();

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

		@Override
		public Object getChild(int arg0, int arg1) {
			return items.get(arg0).items.get(arg1);
		}

		@Override
		public long getChildId(int arg0, int arg1) {
			int id = 0;
			int i = 0;
			while (i < arg0) {
				id += items.get(i).items.size();
				i++;
			}
			return id + arg1;
		}

		@Override
		public View getChildView(int arg0, int arg1, boolean arg2, View arg3, ViewGroup arg4) {
			GroupType g = (GroupType) getGroup(arg0);
			ItemType item = (ItemType) getChild(arg0, arg1);
			return ExpandableLoadingListFragment.this.getChildView(g, item, arg0, arg1, arg2, arg3, arg4);
		}

		@Override
		public int getChildrenCount(int arg0) {
			return items.get(arg0).items.size();
		}

		@Override
		public Object getGroup(int arg0) {
			return items.get(arg0).group;
		}

		@Override
		public int getGroupCount() {
			return items.size();
		}

		@Override
		public long getGroupId(int arg0) {
			return arg0;
		}

		@Override
		public View getGroupView(int arg0, boolean arg1, View arg2, ViewGroup arg3) {
			GroupType g = (GroupType) getGroup(arg0);
			return ExpandableLoadingListFragment.this.getGroupView(g, arg0, arg1, arg2, arg3);
		}

		@Override
		public boolean hasStableIds() {
			return true;
		}

		@Override
		public boolean isChildSelectable(int arg0, int arg1) {
			return true;
		}

	}
}
