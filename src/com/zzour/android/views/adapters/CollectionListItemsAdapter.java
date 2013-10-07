package com.zzour.android.views.adapters;

import java.util.ArrayList;

import com.zzour.android.CollectionActivity;
import com.zzour.android.R;
import com.zzour.android.models.ShopSummaryContent;
import com.zzour.android.utils.ImageTool;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class CollectionListItemsAdapter extends BaseAdapter{
	
	ArrayList<ShopSummaryContent> shops = new ArrayList<ShopSummaryContent>();
	
	private CollectionActivity mContext;
	private int mImageWidth;
	private int mImageHeight;
	private int mDefaultBitmapId;
	
	private boolean deleteStatus = false;
	
    public boolean isDeleteStatus() {
		return deleteStatus;
	}

	public void changeDeleteStatus() {
		this.deleteStatus = !this.deleteStatus;
	}

	public CollectionListItemsAdapter(CollectionActivity context, int defaultBitmapId) {
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
			convertView=LayoutInflater.from(mContext).inflate(R.layout.collection_item, null);
			ItemViewCache viewCache=new ItemViewCache();
			viewCache.mTitleView=(TextView)convertView.findViewById(R.id.item_title);
			viewCache.mDescView = (TextView)convertView.findViewById(R.id.item_credit);
			viewCache.mImageView = (ImageView)convertView.findViewById(R.id.item_image);
			viewCache.mItemRating = (RatingBar)convertView.findViewById(R.id.item_rating);
			viewCache.mActionImage = (ImageView)convertView.findViewById(R.id.collect_action);
			convertView.setTag(viewCache);
		}
		ItemViewCache cache=(ItemViewCache)convertView.getTag();
		
		cache.mTitleView.setText(shops.get(position).getName());
		cache.mTitleView.setFocusableInTouchMode(false);
		cache.mDescView.setText(shops.get(position).getCreditValue() + "人认为该店不错");
		cache.mItemRating.setRating(shops.get(position).getGrade());
		Bitmap bmp = shops.get(position).getBitmap();
		if (bmp == null){
			cache.mImageView.setImageBitmap(ImageTool.getBitmapByStream(mDefaultBitmapId, 
					mContext.getResources().openRawResource(mDefaultBitmapId), 
					mImageWidth, mImageHeight));
		} else {
			cache.mImageView.setImageBitmap(bmp);
		}
		cache.mActionImage.setTag(position);
		if (this.deleteStatus){
			cache.mActionImage.setImageResource(R.drawable.cancel);
			// TODO add cancel image on click listener
			cache.mActionImage.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					int position = Integer.parseInt(v.getTag().toString());
					int shopId = shops.get(position).getId();
					mContext.addRemovedShop(shopId);
					shops.remove(position);
					CollectionListItemsAdapter.this.notifyDataSetChanged();
				}
			});
		} else {
			cache.mActionImage.setImageResource(R.drawable.arrow);
			cache.mActionImage.setOnClickListener(null);
		}
		return convertView;
	}

	private static class ItemViewCache{
		public TextView mTitleView;
		public TextView mDescView;
		public ImageView mImageView;
		public RatingBar mItemRating;
		public ImageView mActionImage;
	}
}
