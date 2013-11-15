package com.zzour.android.views.adapters;

import java.util.ArrayList;

import com.zzour.android.R;
import com.zzour.android.models.ShopSummaryContent;
import com.zzour.android.utils.ImageTool;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
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
	
	private String getTitle(ShopSummaryContent shop){
		String title = shop.getName();
		String online = shop.isOnlineOrder() ? "<div style='float:right;background-color:#f28a49;color:#ffffff'>在线下单</div>" : "<div style='float:right;background-color:#efecea;color:#f28a49'>电话订餐</div>";
		return "<div style='float:left'>" + title + "</div>" + online;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		// use cache for optimize
		if(convertView==null){
			convertView=LayoutInflater.from(mContext).inflate(R.layout.item, null);
			ItemViewCache viewCache=new ItemViewCache();
			viewCache.mTitleView=(TextView)convertView.findViewById(R.id.item_title);
			viewCache.mStatusView = (TextView)convertView.findViewById(R.id.item_status);
			viewCache.mDescView = (TextView)convertView.findViewById(R.id.item_credit);
			viewCache.mScoreView = (TextView)convertView.findViewById(R.id.item_score);
			viewCache.mSpeedView = (TextView)convertView.findViewById(R.id.item_speed);
			viewCache.mImageView = (ImageView)convertView.findViewById(R.id.item_image);
			convertView.setTag(viewCache);
		}
		ItemViewCache cache=(ItemViewCache)convertView.getTag();
		
		cache.mTitleView.setText(shops.get(position).getName());
		//cache.mTitleView.loadData(this.getTitle(shops.get(position)), "text/html; charset=UTF-8", null);
		//cache.mTitleView.setOnTouchListener(new WebViewClickListener(cache.mTitleView, parent, position));
		//cache.mTitleView.setFocusableInTouchMode(false);
		if (!shops.get(position).isAlive()){
			cache.mStatusView.setBackgroundColor(Color.parseColor("#AEAEAE"));
			cache.mStatusView.setTextColor(Color.WHITE);
			cache.mStatusView.setText("暂不接单");
		} else {
			if (shops.get(position).isOnlineOrder()){
				cache.mStatusView.setBackgroundColor(Color.parseColor("#f28a49"));
				cache.mStatusView.setTextColor(Color.WHITE);
				cache.mStatusView.setText("在线订餐");
			} else {
				cache.mStatusView.setBackgroundColor(Color.parseColor("#efecea"));
				cache.mStatusView.setTextColor(Color.parseColor("#f28a49"));
				cache.mStatusView.setText("电话订餐");
			}
		}
		cache.mDescView.setText(shops.get(position).getCreditValue() + "");
		cache.mScoreView.setText(shops.get(position).getScore() + "分");
		cache.mSpeedView.setText(shops.get(position).getSpeed() + "");
		Bitmap bmp = shops.get(position).getBitmap();
		if (bmp == null){
			cache.mImageView.setImageBitmap(ImageTool.getBitmapByStream(mDefaultBitmapId, 
					mContext.getResources().openRawResource(mDefaultBitmapId), 
					mImageWidth, mImageHeight));
		} else {
			cache.mImageView.setImageBitmap(bmp);
		}
		return convertView;
	}

	private static class ItemViewCache{
		public TextView mTitleView;
		public TextView mStatusView;
		public TextView mDescView;
		public TextView mScoreView;
		public TextView mSpeedView;
		public ImageView mImageView;
	}
	
	private class WebViewClickListener implements View.OnTouchListener {
	    private int position;
	    private ViewGroup vg;
	    private WebView wv;
	  
	    public WebViewClickListener(WebView wv, ViewGroup vg, int position) {
	        this.vg = vg;
	        this.position = position;
	        this.wv = wv;
	    }
	  
	    public boolean onTouch(View v, MotionEvent event) {
	        int action = event.getAction();
	  
	        switch (action) {
	            case MotionEvent.ACTION_CANCEL:
	                return true;
	            case MotionEvent.ACTION_UP:
	                sendClick();
	                return true;
	        }
	  
	        return false;
	    }
	  
	    public void sendClick() {
	        ListView lv = (ListView) vg;
	        lv.performItemClick(wv, position+1, 0);
	    }
	}
}
