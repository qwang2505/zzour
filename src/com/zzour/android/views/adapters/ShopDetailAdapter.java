package com.zzour.android.views.adapters;

import java.util.ArrayList;

import com.zzour.android.R;
import com.zzour.android.models.Food;

import android.content.Context;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

public class ShopDetailAdapter extends BaseExpandableListAdapter{
	
	private static final String TAG = "ZZOUR";
	
	private ArrayList<String> categories = new ArrayList<String>();
	private ArrayList<ArrayList<Food>> foods = new ArrayList<ArrayList<Food>>();
	
	private Context mContext;
	
	public ShopDetailAdapter(Context context){
		mContext = context;
	}
	
	public void setFoods(String category, ArrayList<Food> foodList){
		// make sure the category and foods have the save position
		if (categories.size() != foods.size()){
			Log.e(TAG, "you better look at it, cateogry and foods do not have same size.");
			return;
		}
		categories.add(category);
		foods.add(foodList);
	}
	
    public TextView getGenericView() {
        // Layout parameters for the ExpandableListView
        AbsListView.LayoutParams lp = new AbsListView.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, 64);

        TextView textView = new TextView(mContext);
        textView.setLayoutParams(lp);
        // Center the text vertically
        textView.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
        // Set the text starting position
        textView.setPadding(36, 0, 0, 0);
        return textView;
    }

	@Override
	public Object getChild(int groupPosition, int position) {
		return foods.get(groupPosition).get(position).getName();
	}

	@Override
	public long getChildId(int groupPosition, int position) {
		return position;
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		return foods.get(groupPosition).size();
	}

	@Override
	public Object getGroup(int groupPosition) {
		return categories.get(groupPosition);
	}

	@Override
	public int getGroupCount() {
		return categories.size();
	}

	@Override
	public long getGroupId(int groupPosition) {
		return groupPosition;
	}

	@Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild,
            View convertView, ViewGroup parent) {
		if (convertView == null){
			convertView = LayoutInflater.from(mContext).inflate(R.layout.food_item, null);
			FoodViewCache cache = new FoodViewCache();
			cache.name = (TextView)convertView.findViewById(R.id.food_name);
			cache.price = (TextView)convertView.findViewById(R.id.food_price);
			cache.soldCount = (TextView)convertView.findViewById(R.id.food_sold_count);
			cache.buyCount = (EditText)convertView.findViewById(R.id.food_buy_count);
			cache.checked = (CheckBox)convertView.findViewById(R.id.food_checked);
			convertView.setTag(cache);
		}
		FoodViewCache cache = (FoodViewCache)convertView.getTag();
		Food f = this.foods.get(groupPosition).get(childPosition);
		cache.name.setText(f.getName());
		cache.price.setText(f.getPrice()+"£§");
		cache.soldCount.setText("“— €"+f.getSoldCount()+"∑›");
		cache.buyCount.setText("1");
        return convertView;
    }
	
	@Override
	public View getGroupView(int groupPosition, boolean isExpanded, View convertView,
            ViewGroup parent) {
        TextView textView = getGenericView();
        textView.setText(getGroup(groupPosition).toString());
        isExpanded = true;
        return textView;
    }

	@Override
	public boolean hasStableIds() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isChildSelectable(int arg0, int arg1) {
		// TODO Auto-generated method stub
		return true;
	}
	
	private class FoodViewCache{
		public TextView name;
		public TextView price;
		public TextView soldCount;
		public EditText buyCount;
		public CheckBox checked;
	}
}
