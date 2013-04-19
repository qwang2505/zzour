package com.zzour.android.views.adapters;

import java.util.ArrayList;

import com.zzour.android.R;
import com.zzour.android.models.ShopSummaryContent;
import com.zzour.android.utils.ImageTool;

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
	
	private Context mContext;
	private int mImageWidth;
	private int mImageHeight;
	private int mDefaultBitmapId;
	
    public ListItemsAdapter(Context context, int defaultBitmapId) {
		this.mContext=context;
		this.mImageWidth = (int)mContext.getResources().getDimension(R.dimen.list_image_width);
		this.mImageHeight = (int)mContext.getResources().getDimension(R.dimen.list_image_height);
		this.mDefaultBitmapId = defaultBitmapId;
	}
    
    public void updateShopBitmap(int position, Bitmap bitmap){
    	ShopSummaryContent s = shops.get(position);
    	s.setBitmap(bitmap);
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
	
	// add shop item and return the position of newly added item.
	public int addItem(ShopSummaryContent shop){
		int position = shops.size();
		shops.add(shop);
		return position;
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
			viewCache.mImageView = (ImageView)convertView.findViewById(R.id.item_image);
			convertView.setTag(viewCache);
		}
		ItemViewCache cache=(ItemViewCache)convertView.getTag();
		
		cache.mTitleView.setText(shops.get(position).getName());
		cache.mDescView.setText(shops.get(position).getDescription());
		Bitmap bmp = shops.get(position).getBitmap();
		if (bmp == null){
			cache.mImageView.setImageBitmap(ImageTool.getBitmapByStream(mDefaultBitmapId, 
					mContext.getResources().openRawResource(R.drawable.scroll_image_1), 
					mImageWidth, mImageHeight));
		} else {
			cache.mImageView.setImageBitmap(bmp);
		}
		return convertView;
	}

	private static class ItemViewCache{
		public TextView mTitleView;
		public TextView mDescView;
		public ImageView mImageView;
	}
}
