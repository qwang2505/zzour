package com.zzour.android.views.adapters;

import java.util.ArrayList;
import java.util.Iterator;

import com.zzour.android.R;
import com.zzour.android.ShopDetailActivity;
import com.zzour.android.models.Food;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class ShopDetailAdapter extends BaseExpandableListAdapter{
	
	private static final String TAG = "ZZOUR";
	
	private ArrayList<String> categories = new ArrayList<String>();
	private ArrayList<ArrayList<Food>> foods = new ArrayList<ArrayList<Food>>();
	
	private ShopDetailActivity mActivity;
	
	private AlertDialog mNumberPicker;
	
	private int mCurrentGroup = -1;
	private int mCurrentPosition = -1;
	
	public ShopDetailAdapter(ShopDetailActivity context){
		mActivity = context;
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
	
	public void changeCheckStatus(Food food){
//		if (food.isChecked()){
//			food.setChecked(false);
//		} else {
//			food.setChecked(true);
//		}
		this.notifyDataSetChanged();
	}
	
    public TextView getGenericView() {
        // Layout parameters for the ExpandableListView
        AbsListView.LayoutParams lp = new AbsListView.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, 64);

        TextView textView = new TextView(mActivity);
        textView.setLayoutParams(lp);
        // Center the text vertically
        textView.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
        // Set the text starting position
        textView.setPadding(56, 0, 0, 0);
        textView.setTextSize(16);
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
			convertView = LayoutInflater.from(mActivity).inflate(R.layout.food_item, null);
			FoodViewCache cache = new FoodViewCache();
			cache.name = (TextView)convertView.findViewById(R.id.food_name);
			cache.price = (TextView)convertView.findViewById(R.id.food_price);
			cache.soldCount = (TextView)convertView.findViewById(R.id.food_sold_count);
			cache.buyCount = (TextView)convertView.findViewById(R.id.food_buy_count);
			cache.id = (TextView)convertView.findViewById(R.id.food_id);
			convertView.setTag(cache);
		}
		FoodViewCache cache = (FoodViewCache)convertView.getTag();
		Food f = this.foods.get(groupPosition).get(childPosition);
		cache.name.setText(f.getName());
		cache.name.setSelected(true);
		cache.price.setText("£§"+f.getPrice());
		cache.soldCount.setText("“— €"+f.getSoldCount()+"∑›");
		TextView buyCountText1 = (TextView)convertView.findViewById(R.id.food_buy_count_text);
		TextView buyCountText2 = (TextView)convertView.findViewById(R.id.food_buy_count_text_2);
		if (f.isChecked()){
			cache.buyCount.setText(f.getBuyCount() +"");
			buyCountText1.setVisibility(TextView.VISIBLE);
			buyCountText2.setVisibility(TextView.VISIBLE);
		} else {
			cache.buyCount.setText("");
			buyCountText1.setVisibility(TextView.GONE);
			buyCountText2.setVisibility(TextView.GONE);
		}
		cache.id.setText(generateStringId(groupPosition, childPosition));
		LinearLayout info = (LinearLayout)convertView.findViewById(R.id.food_info);
		final TextView delete = (TextView)convertView.findViewById(R.id.delete);
		info.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				TextView idView = (TextView)v.findViewById(R.id.food_id);
				String text = idView.getText().toString();
				Food food = getFoodByStringId(text);
				int originalCount = food.getBuyCount();
				if (!food.isChecked()){
					food.setChecked(true);
					delete.setVisibility(TextView.VISIBLE);
				} else {
					food.setBuyCount(originalCount + 1);
				}
				changeCheckStatus(food);
				// update total money display
				updateTotal();
			}
		});
		delete.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				TextView idView = (TextView)((RelativeLayout)v.getParent()).findViewById(R.id.food_id);
				String text = idView.getText().toString();
				Food food = getFoodByStringId(text);
				int originalCount = food.getBuyCount();
				if (originalCount == 1){
					food.setChecked(false);
					delete.setVisibility(TextView.GONE);
				} else {
					food.setBuyCount(originalCount - 1);
				}
				changeCheckStatus(food);
				// update total money display
				updateTotal();
			}
			
		});
        return convertView;
    }
	
	@Override
	public View getGroupView(int groupPosition, boolean isExpanded, View convertView,
            ViewGroup parent) {
        TextView textView = getGenericView();
        textView.setText(getGroup(groupPosition).toString());
        textView.setBackgroundColor(mActivity.getResources().getColor(R.color.shop_detail_category_background_color));
        textView.setTextColor(Color.WHITE);
        //isExpanded = false;
        //ExpandableListView eLV = (ExpandableListView) parent;
        //eLV.expandGroup(groupPosition);
        return textView;
    }

	@Override
	public boolean hasStableIds() {
		return false;
	}

	@Override
	public boolean isChildSelectable(int arg0, int arg1) {
		return true;
	}
	
	private class FoodViewCache{
		public TextView name;
		public TextView price;
		public TextView soldCount;
		public TextView buyCount;
		public TextView id;
	}
	
	public void updateTotal(){
		if (!mActivity.isOnline()){
			return;
		}
		float total = 0f;
		Iterator<ArrayList<Food>> it1 = foods.iterator();
		while (it1.hasNext()){
			Iterator<Food> it2 = it1.next().iterator();
			while (it2.hasNext()){
				Food f = it2.next();
				if (f.isChecked()){
					total += f.getPrice() * f.getBuyCount();
				}
			}
		}
		TextView symbol = (TextView)mActivity.findViewById(R.id.money_symbol);
		TextView number = (TextView)mActivity.findViewById(R.id.total_money);
		if (total > 0){
			Log.d(TAG, "should show number");
			symbol.setVisibility(View.VISIBLE);
			number.setVisibility(View.VISIBLE);
			number.setText(String.valueOf(total));
		} else {
			symbol.setVisibility(View.GONE);
			number.setVisibility(View.GONE);
			Log.d(TAG, "should hidden number");
		}
		this.notifyDataSetChanged();
	}
	
	private Food getFoodByStringId(String id){
		String[] ids = id.split(";");
		if (ids.length != 2){
			return null;
		}
		int groupId = Integer.valueOf(ids[0]);
		int childId = Integer.valueOf(ids[1]);
		return foods.get(groupId).get(childId);
	}
	
	private String generateStringId(int groupId, int childId){
		return groupId + ";" + childId;
	}
	
	public ArrayList<Food> getBoughtFoods(){
		ArrayList<Food> fs = new ArrayList<Food>();
		Iterator<ArrayList<Food>> it1 = this.foods.iterator();
		while (it1.hasNext()){
			Iterator<Food> it2 = it1.next().iterator();
			while (it2.hasNext()){
				Food f = it2.next();
				if (f.isChecked() && f.getBuyCount() > 0){
					fs.add(f);
				}
			}
		}
		return fs;
	}
}
