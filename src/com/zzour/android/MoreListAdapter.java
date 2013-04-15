package com.zzour.android;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class MoreListAdapter extends BaseAdapter 
{
	private final Context context;
	private final String[] items;
	private LayoutInflater layoutInflater = null;
	
	public MoreListAdapter(Context context, final String[] items) {
		this.context = context;
		this.items = items;
	}
	
	
	public int getCount() {
		return items.length;
	}

	
	public String getItem(int position) {
		return items[position];
	}

	
	public long getItemId(int position) {
		return 0;
	}

	
	public View getView(final int position, View convertView, ViewGroup parent) 
	{
		ViewHolder holder;
		View view = convertView;
		if (view == null) {
			holder = new ViewHolder();
			layoutInflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = layoutInflater.inflate(R.layout.morelistitem, parent, false);
			holder.txtTitle = (TextView) view.findViewById(R.id.moreListItem);
			
			view.setTag(holder);
		} else {
			holder = (ViewHolder) view.getTag();
		}
		
		if(getCount() > 0) {
			holder.txtTitle.setText(getItem(position));
			
		}
		
		return view;
	}
	
	public class ViewHolder 
	{
		private TextView txtTitle;
	}
}