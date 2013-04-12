package com.zzour.android.views.adapters;

import java.util.ArrayList;

import com.zzour.android.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ListItemsAdapter extends BaseAdapter{
	
	// TODO read these data from api
	ArrayList<String> mTitles = new ArrayList<String>();
	ArrayList<String> mDescs = new ArrayList<String>();
	ArrayList<Bitmap> mImages = new ArrayList<Bitmap>();
	
	private Context mContext;
    public ListItemsAdapter(Context context) {
		this.mContext=context;
	}

	public int getCount() {
		return mTitles.size();
	}

	public Object getItem(int position) {
		return null;
	}

	public long getItemId(int position) {
		return 0;
	}
	
	public void addItem(String title, String desc, Bitmap image){
		mTitles.add(title);
		mDescs.add(desc);
		mImages.add(image);
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		// use cache for optimize
		if(convertView==null){
			convertView=LayoutInflater.from(mContext).inflate(R.layout.item, null);
			ItemViewCache viewCache=new ItemViewCache();
			viewCache.mTitleView=(TextView)convertView.findViewById(R.id.item_title);
			viewCache.mDescView = (TextView)convertView.findViewById(R.id.item_desc);
			viewCache.mImageView=(ImageView)convertView.findViewById(R.id.item_image);
			convertView.setTag(viewCache);
		}
		ItemViewCache cache=(ItemViewCache)convertView.getTag();
		
		cache.mTitleView.setText(mTitles.get(position));
		cache.mDescView.setText(mDescs.get(position));
		cache.mImageView.setImageBitmap(mImages.get(position));
		return convertView;
	}

	private static class ItemViewCache{
		public TextView mTitleView;
		public TextView mDescView;
		public ImageView mImageView;
	}
}
