package com.zzour.android.views.adapters;

import java.util.ArrayList;

import com.zzour.android.R;
import com.zzour.android.models.OrderSummary;
import com.zzour.android.settings.GlobalSettings;
import com.zzour.android.utils.ImageTool;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class OrderListItemsAdapter extends BaseAdapter{
	private Context mContext;
	
	private ArrayList<OrderSummary> orders = new ArrayList<OrderSummary>();
	
    public OrderListItemsAdapter(Context context, int defaultBitmapId) {
		this.mContext = context;
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
			convertView = LayoutInflater.from(mContext).inflate(R.layout.order_item, null);
			ItemViewCache viewCache = new ItemViewCache();
			viewCache.name = (TextView)convertView.findViewById(R.id.shop_name);
			viewCache.time = (TextView)convertView.findViewById(R.id.time);
			viewCache.shortDesc = (TextView)convertView.findViewById(R.id.status);
			viewCache.longDesc = (TextView)convertView.findViewById(R.id.status_desc);
			viewCache.price = (TextView)convertView.findViewById(R.id.price);
			convertView.setTag(viewCache);
		}
		ItemViewCache cache = (ItemViewCache)convertView.getTag();
		
		cache.name.setText(orders.get(position).getShopName());
		cache.time.setText(orders.get(position).getTime());
		cache.price.setText(orders.get(position).getPrice() + "");
		cache.shortDesc.setText(GlobalSettings.getStatusShortDesc(orders.get(position).getStatus()));
		cache.longDesc.setText(GlobalSettings.getStatusLongDesc(orders.get(position).getStatus()));
		return convertView;
	}

	private static class ItemViewCache{
		public TextView name;
		public TextView time;
		public TextView price;
		public TextView shortDesc;
		public TextView longDesc;
	}
}
