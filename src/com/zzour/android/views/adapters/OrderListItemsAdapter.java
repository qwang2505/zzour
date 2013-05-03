package com.zzour.android.views.adapters;

import java.util.ArrayList;

import com.zzour.android.R;
import com.zzour.android.models.OrderSummary;
import com.zzour.android.utils.ImageTool;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class OrderListItemsAdapter extends BaseAdapter{
	private Context mContext;
	private int mImageWidth;
	private int mImageHeight;
	private int mDefaultBitmapId;
	
	private ArrayList<OrderSummary> orders = new ArrayList<OrderSummary>();
	
    public OrderListItemsAdapter(Context context, int defaultBitmapId) {
		this.mContext = context;
		this.mImageWidth = (int)mContext.getResources().getDimension(R.dimen.order_list_image_width);
		this.mImageHeight = (int)mContext.getResources().getDimension(R.dimen.order_list_image_height);
		this.mDefaultBitmapId = defaultBitmapId;
	}
    
    public int addItem(OrderSummary order){
    	int p = this.orders.size();
    	this.orders.add(order);
    	return p;
    }

	public int getCount() {
		if (orders == null){
			return 0;
		}
		return orders.size();
	}

	public Object getItem(int position) {
		return null;
	}

	public long getItemId(int position) {
		return position;
	}
	
	public OrderSummary getOrderAtPosition(int position){
		return orders.get(position);
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		// use cache for optimize
		if(convertView == null){
			convertView=LayoutInflater.from(mContext).inflate(R.layout.order_item, null);
			ItemViewCache viewCache=new ItemViewCache();
			viewCache.image = (ImageView)convertView.findViewById(R.id.order_item_image);
			viewCache.name = (TextView)convertView.findViewById(R.id.shop_name);
			viewCache.time = (TextView)convertView.findViewById(R.id.order_time);
			viewCache.price = (TextView)convertView.findViewById(R.id.order_price);
			convertView.setTag(viewCache);
		}
		ItemViewCache cache = (ItemViewCache)convertView.getTag();
		
		cache.name.setText(orders.get(position).getShopName());
		cache.time.setText(orders.get(position).getTime());
		cache.price.setText("¹²" + orders.get(position).getPrice() + "£¤");
		String image = orders.get(position).getImage();
		Bitmap bmp = ImageTool.cachedImage(image, mContext);
		if (image == null || bmp == null){
			cache.image.setImageBitmap(ImageTool.getBitmapByStream(mDefaultBitmapId, 
					mContext.getResources().openRawResource(mDefaultBitmapId), 
					mImageWidth, mImageHeight));
		} else {
			cache.image.setImageBitmap(bmp);
		}
		return convertView;
	}

	private static class ItemViewCache{
		public ImageView image;
		public TextView name;
		public TextView time;
		public TextView price;
	}
}
