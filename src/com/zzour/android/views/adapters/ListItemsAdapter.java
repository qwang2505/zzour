package com.zzour.android.views.adapters;

import java.util.ArrayList;

import com.zzour.android.R;
import com.zzour.android.models.ShopSummaryContent;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ListItemsAdapter extends BaseAdapter{
	
	ArrayList<ShopSummaryContent> shops = new ArrayList<ShopSummaryContent>();
	ArrayList<Bitmap> mImages = new ArrayList<Bitmap>();
	
	private Context mContext;
    public ListItemsAdapter(Context context) {
		this.mContext=context;
	}

	public int getCount() {
		return shops.size();
	}

	public Object getItem(int position) {
		return null;
	}

	public long getItemId(int position) {
		return position;
	}
	
	public void addItem(ShopSummaryContent shop, Bitmap image){
		shops.add(shop);
		mImages.add(image);
	}
	
	public ShopSummaryContent getShopSummaryAtPosition(int position){
		if (shops.size() < position + 1){
			return null;
		}
		return shops.get(position);
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
		
		cache.mTitleView.setText(shops.get(position).getName());
		cache.mDescView.setText(shops.get(position).getDescription());
		cache.mImageView.setImageBitmap(mImages.get(position));
		return convertView;
	}

	private static class ItemViewCache{
		public TextView mTitleView;
		public TextView mDescView;
		public ImageView mImageView;
	}
}
