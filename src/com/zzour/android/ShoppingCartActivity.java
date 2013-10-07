package com.zzour.android;

import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import com.zzour.android.base.BaseActivity;
import com.zzour.android.models.Address;
import com.zzour.android.models.Food;
import com.zzour.android.models.Order;
import com.zzour.android.models.OrderFormResult;
import com.zzour.android.models.ApiResult;
import com.zzour.android.models.Region;
import com.zzour.android.models.School;
import com.zzour.android.models.SchoolArea;
import com.zzour.android.models.ShoppingCart;
import com.zzour.android.models.SimpleOrder;
import com.zzour.android.models.User;
import com.zzour.android.models.dao.AddressDAO;
import com.zzour.android.network.api.OrderApi;
import com.zzour.android.network.api.RegionApi;
import com.zzour.android.settings.LocalPreferences;
import com.zzour.android.utils.ActivityTool;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class ShoppingCartActivity extends BaseActivity{
	
	private static final String TAG = "ZZOUR";
	
	private static final int LOGIN_REQUEST_CODE = 1;
	
	private TextView totalPriceView;
	
	private int mCurrentShop = -1;
	private int mCurrentFood = -1;
	private int mCurrentCount = -1;
	private EditText mCurrentView = null;
	
	private AlertDialog mNumberPicker;
	private AlertDialog mNewAddressDialog;
	
	private RadioButton mNewAddrBtn = null;
	// button handler
	private ButtonHandler bHandler; 
	
	private Address mCurrentAddr = null;
	private SimpleOrder mOrder = null;
	private ApiResult mOrderResult = null;
	
	private ArrayList<Food> foods = new ArrayList<Food>();
	private ArrayList<Address> addrs = null;
	private int shipId = -1;
	
	private ApiResult mRemoveResult = null;
	private ApiResult mUpdateResult = null;
	private ApiResult mClearCartResult = null;
	
	private HashMap<RadioButton, Address> mAddrMap = new HashMap<RadioButton, Address>();
	
	// regions
	private ArrayList<Region> mFirstLevelRegions = null;
	private ArrayList<Region> mSecondLevelRegions = null;
	private ArrayList<Region> mThirdLevelRegions = null;
	
	class  ButtonHandler  extends  Handler { 

         private  WeakReference<DialogInterface> mDialog; 

         public ButtonHandler(DialogInterface dialog) { 
            mDialog = new WeakReference<DialogInterface>(dialog); 
         } 

         public void handleMessage(Message msg) { 
             switch (msg.what) { 
                 case DialogInterface.BUTTON_POSITIVE: 
                 case DialogInterface.BUTTON_NEGATIVE: 
                 case DialogInterface.BUTTON_NEUTRAL: 
                    ((DialogInterface.OnClickListener) msg.obj).onClick(mDialog.get(), msg.what); 
                     break ; 
            } 
        } 
    } 
	
	private void popUpDialog(AlertDialog dialog) { 
        /* 
         * alert dialog's default handler will always close dialog whenever user 
         * clicks on which button. we have to replace default handler with our 
         * own handler for blocking close action. 
         * Reflection helps a lot. 
         */ 
        try { 
            Field field = dialog.getClass().getDeclaredField("mAlert"); 
            field.setAccessible(true); 
            
            //retrieve mAlert value 
            Object obj = field.get(dialog); 
            field = obj.getClass().getDeclaredField("mHandler"); 
            field.setAccessible(true); 
            //replace mHandler with our own handler 
            bHandler = new ButtonHandler(dialog);
            field.set(obj, bHandler); 
        } catch (SecurityException e) { 
            Log.e(TAG, e.getMessage()); 
        } catch (NoSuchFieldException e) { 
        	Log.e(TAG,e.getMessage()); 
        } catch (IllegalArgumentException e) { 
        	Log.e(TAG,e.getMessage()); 
        } catch (IllegalAccessException e) { 
        	Log.e(TAG,e.getMessage()); 
        } 
        
        //we can show this dialog now. 
        dialog.show(); 
    } 
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.shoping_cart);
		
		// if no shops, means no food, toast
		if (ShoppingCart.getShopsCount() == 0){
			// give some advice
			Toast.makeText(getApplicationContext(), "购物车还是空的，赶快去选购吧！", Toast.LENGTH_SHORT).show();
			return;
		}
		
		((Button)findViewById(R.id.back_btn)).setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				ShoppingCartActivity.this.onBackPressed();
				return;
			}
		});
		
		// call api to add foods to shopping cart
		foods.clear();
		Iterator<Integer> shopIds = ShoppingCart.getShops();
		while (shopIds.hasNext()){
			int shopId = shopIds.next();
			mCurrentShop = shopId;
			Iterator<Integer> foodIds = ShoppingCart.getFoods(shopId);
			while (foodIds.hasNext()){
				Integer foodId = foodIds.next();
				Food food = ShoppingCart.getFood(shopId, foodId);
				foods.add(food);
			}
		}
		// call api to add all foods into shopping cart
		new LoadingTask(ShoppingCartActivity.this).execute();
	}
	
	private String[] getRegionNames(ArrayList<Region> regions, int level){
		int size = regions.size();
		if (level <= 2){
			size += 1;
		}
		String[] names = new String[size];
		Iterator<Region> it = regions.iterator();
		int i = 0;
		if (level <= 2){
			i += 1;
			names[0] = "请选择";
		}
		while (it.hasNext()){
			names[i] = it.next().getName();
			i += 1;
		}
		return names;
	}
	
	private void showNewAddressDialog(){
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setCancelable(false);
		builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// valid input text and save.
				TextView name = (TextView)mNewAddressDialog.findViewById(R.id.new_address_name);
				if (name.getText().length() <= 0){
					Toast.makeText(getApplicationContext(), "姓名不能为空！", Toast.LENGTH_SHORT).show();
					return;
				}
				TextView phone = (TextView)mNewAddressDialog.findViewById(R.id.new_address_phone);
				if (phone.getText().length() <= 0){
					Toast.makeText(getApplicationContext(), "电话不能为空！", Toast.LENGTH_SHORT).show();
					return;
				}
				String addr = "";
				Spinner spiner = (Spinner)mNewAddressDialog.findViewById(R.id.new_address_school);
				if (spiner.getVisibility() != Spinner.VISIBLE || spiner.getSelectedItemPosition() == 0){
					Toast.makeText(getApplicationContext(), "请选择学校！", Toast.LENGTH_SHORT).show();
					return;
				}
				if (spiner.getVisibility() == Spinner.VISIBLE && spiner.getSelectedItemPosition() != 0){
					addr += (String)spiner.getSelectedItem();
					addr += " ";
				}
				spiner = (Spinner)mNewAddressDialog.findViewById(R.id.new_address_school_area);
				if (spiner.getVisibility() != Spinner.VISIBLE || spiner.getSelectedItemPosition() == 0){
					Toast.makeText(getApplicationContext(), "请选择校区！", Toast.LENGTH_SHORT).show();
					return;
				}
				if (spiner.getVisibility() == Spinner.VISIBLE && spiner.getSelectedItemPosition() != 0){
					addr += (String)spiner.getSelectedItem();
					addr += " ";
				}
				spiner = (Spinner)mNewAddressDialog.findViewById(R.id.new_address_school_area_detail);
				if (spiner.getVisibility() != Spinner.VISIBLE || spiner.getSelectedItemPosition() == 0){
					Toast.makeText(getApplicationContext(), "请选择详细区域！", Toast.LENGTH_SHORT).show();
					return;
				}
				int regionId = -1;
				String regionName = "";
				if (spiner.getVisibility() == Spinner.VISIBLE){
					addr += (String)spiner.getSelectedItem();
					addr += " ";
					Region r = mThirdLevelRegions.get(spiner.getSelectedItemPosition() - 1);
					regionId = r.getId();
					regionName = r.getName();
				}
				TextView address = (TextView)mNewAddressDialog.findViewById(R.id.new_address_detail);
				addr = address.getText().toString();
				if (addr.length() <= 0){
					Toast.makeText(getApplicationContext(), "地址不能为空！", Toast.LENGTH_SHORT).show();
					return;
				}
				// construct new address
				Address address1 = new Address();
				address1.setAddr(addr);
				address1.setId(-1);
				address1.setName(name.getText().toString());
				address1.setPhone(phone.getText().toString());
				address1.setRegionId(regionId);
				address1.setRegionName(regionName);
				address1.setUserId(-1);
				dialog.dismiss();
				// add new address into view
				View view = LayoutInflater.from(ShoppingCartActivity.this).inflate(R.layout.address_info, null);
				((TextView)view.findViewById(R.id.address_name)).setText(address1.getName());
				((TextView)view.findViewById(R.id.address_phone)).setText(address1.getPhone());
				((TextView)view.findViewById(R.id.address_detail)).setText(address1.getRegionName() + " " + address1.getAddr());
				LinearLayout parent = (LinearLayout)ShoppingCartActivity.this.findViewById(R.id.address_info);
				parent.addView(view, parent.getChildCount() - 1);
				RadioButton rb = (RadioButton)view.findViewById(R.id.address_radio_button);
				if (!mAddrMap.containsKey(rb)){
					// if radio button and address info not in map, add it.
					Log.d(TAG, "add new address into map: " + address1.getName());
					mAddrMap.put(rb, address1);
				}
				mNewAddrBtn.setChecked(false);
				// add address radio button status change listener
				rb.setOnCheckedChangeListener(new OnCheckedChangeListener(){
					@Override
					public void onCheckedChanged(CompoundButton button,
							boolean status) {
						if (!status){
							return;
						}
						Iterator<RadioButton> it = mAddrMap.keySet().iterator();
						while (it.hasNext()){
							RadioButton a = it.next();
							if (a != button){
								Log.d(TAG, "unset check status of others.");
								a.setChecked(false);
							}
						}
						Log.d(TAG, "set current addr and uncheck new addre button.");
						mCurrentAddr = mAddrMap.get(button);
						mNewAddrBtn.setChecked(false);
					}
				});
				// set current checked, and set current address
				rb.setChecked(true);
				mCurrentAddr = address1;
				// TODO save new address to cache.
				//AddressDAO dao = new AddressDAO(ShoppingCartActivity.this);
				//dao.insert(address1);
			}
		});
		builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// set address not on selected
				if (mNewAddrBtn == null){
					Log.e(TAG, "what happend? radio button is null");
				}
				mNewAddrBtn.setChecked(false);
				dialog.dismiss();
			}
		});
		// init dialog view
		View view = LayoutInflater.from(this).inflate(R.layout.new_address, null);
		builder.setView(view);
		if (mFirstLevelRegions == null){
			// TODO load level failed, reload and toast
			return;
		}
		// get first level regions names
		String[] names = this.getRegionNames(mFirstLevelRegions, 1);
		Spinner schoolSpinner = (Spinner)view.findViewById(R.id.new_address_school);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, names);
		adapter.setDropDownViewResource ( android.R.layout.simple_spinner_dropdown_item );
		schoolSpinner.setAdapter(adapter);
		schoolSpinner.setOnItemSelectedListener(new OnItemSelectedListener(){

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				Spinner areaSpinner = (Spinner)mNewAddressDialog.findViewById(R.id.new_address_school_area);
				if (position <= 0){
					// hide area spinner and detail spinner
					Spinner detailSpinner = (Spinner)mNewAddressDialog.findViewById(R.id.new_address_school_area_detail);
					detailSpinner.setVisibility(Spinner.GONE);
					areaSpinner.setVisibility(Spinner.GONE);
					return;
				}
				// load sub level region in async task, and then update ui in activity
				// show second spinner if there is.
				Region r = mFirstLevelRegions.get(position - 1);
				// call region api to get child regions
				new RegionTask(ShoppingCartActivity.this, r.getId(), 2).execute();
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {}
		});
		mNewAddressDialog = builder.create();
		popUpDialog(mNewAddressDialog);
		Spinner areaSpinner = (Spinner)mNewAddressDialog.findViewById(R.id.new_address_school_area);
		areaSpinner.setOnItemSelectedListener(new OnItemSelectedListener(){
			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				Spinner detailSpinner = (Spinner)mNewAddressDialog.findViewById(R.id.new_address_school_area_detail);
				if (position <= 0){
					detailSpinner.setVisibility(Spinner.GONE);
					return;
				}
				Region r = mSecondLevelRegions.get(position - 1);
				new RegionTask(ShoppingCartActivity.this, r.getId(), 3).execute();
			}
			@Override
			public void onNothingSelected(AdapterView<?> arg0) {}
		});
	}
	
	public void showNumberPickerDialog(int number){
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setCancelable(false);
		builder.setPositiveButton("确定", new DialogInterface.OnClickListener(){
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				if (mCurrentShop == -1 || mCurrentFood == -1 || mCurrentCount == -1 || mCurrentView == null){
					Log.e(TAG, "what happended?");
					return;
				}
				int count = Integer.valueOf(((TextView)mNumberPicker.findViewById(R.id.numpicker_input)).getText().toString());
				Food f = ShoppingCart.getFood(mCurrentShop, mCurrentFood);
				// calling api to set buy count
				f.setBuyCount(count);
				new UpdateTask(ShoppingCartActivity.this, f).execute();

			}
		});
		builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				mCurrentFood = -1;
				return;
			}
		});
		View view = LayoutInflater.from(this).inflate(R.layout.number_picker_main, null);
		((TextView)view.findViewById(R.id.numpicker_input)).setText("" + number);
		builder.setView(view);
		mNumberPicker = builder.create();
		mNumberPicker.show();
	}
	
	public void setOrderFormResult(OrderFormResult result){
		// set foods and addresses
		foods = result.getFoods();
		addrs = result.getAddrs();
		shipId = result.getShipMethods().get(0).getId();
	}
	
	public void finishOrderForm(){
		if (foods == null || addrs == null || shipId == -1){
			Log.d(TAG, "Something goes wrong, foods is null while finish order");
			this.realBack();
			return;
		}
		// show foods and addresses
		this.showForm();
		this.showAddresses();
	}
	
	public void showAddresses(){
		Iterator<Address> it = addrs.iterator();
		boolean first = true;
		while (it.hasNext()){
			Address address1 = it.next();
			View view = LayoutInflater.from(ShoppingCartActivity.this).inflate(R.layout.address_info, null);
			((TextView)view.findViewById(R.id.address_name)).setText(address1.getName());
			((TextView)view.findViewById(R.id.address_phone)).setText(address1.getPhone());
			((TextView)view.findViewById(R.id.address_detail)).setText(address1.getAddr());
			LinearLayout parent = (LinearLayout)ShoppingCartActivity.this.findViewById(R.id.address_info);
			parent.addView(view, parent.getChildCount() - 1);
			RadioButton rb = (RadioButton)view.findViewById(R.id.address_radio_button);
			if (!mAddrMap.containsKey(rb)){
				// if radio button and address info not in map, add it.
				mAddrMap.put(rb, address1);
			}
			mNewAddrBtn.setChecked(false);
			// add address radio button status change listener
			rb.setOnCheckedChangeListener(new OnCheckedChangeListener(){
				@Override
				public void onCheckedChanged(CompoundButton button,
						boolean status) {
					if (!status){
						return;
					}
					Iterator<RadioButton> it = mAddrMap.keySet().iterator();
					while (it.hasNext()){
						RadioButton a = it.next();
						if (a != button){
							Log.d(TAG, "unset check status of others.");
							a.setChecked(false);
						}
					}
					Log.d(TAG, "set current addr and uncheck new addre button.");
					mCurrentAddr = mAddrMap.get(button);
					mNewAddrBtn.setChecked(false);
				}
			});
			// set current checked, and set current address
			if (first){
				rb.setChecked(true);
				mCurrentAddr = address1;
			}
		}
	}
	
	public void showForm(){
		LinearLayout products = (LinearLayout)findViewById(R.id.products);
		float totalPrice = 0.0f;
		String shopName = "";
		
		// get product view
		LinearLayout main = (LinearLayout)getLayoutInflater().inflate(R.layout.product_main, null);
		// get shop id from result
		int shopId = mCurrentShop;
		if (shopName.length() == 0){
			shopName = ShoppingCart.getShopName(shopId);
		}
		ShoppingCart.clear();
		Iterator<Food> foods = this.foods.iterator();
		while (foods.hasNext()){
			Food food = foods.next();
			RelativeLayout foodItem = (RelativeLayout)getLayoutInflater().inflate(R.layout.product_food_item, null);
			TextView foodName = (TextView)foodItem.findViewById(R.id.product_food_item_name);
			TextView foodPrice = (TextView)foodItem.findViewById(R.id.product_food_item_price);
			EditText foodCount = (EditText)foodItem.findViewById(R.id.product_food_item_count);
			TextView foodItemId = (TextView)foodItem.findViewById(R.id.product_food_item_id);
			ImageButton deleteBtn = (ImageButton)foodItem.findViewById(R.id.product_food_item_delete_btn);
			OnClickListener listener = new OnClickListener(){
				@Override
				public void onClick(View v) {
					// find id view, get shop and delete it.
					RelativeLayout parent = (RelativeLayout)v.getParent();
					String text = (String) ((TextView)parent.findViewById(R.id.product_food_item_id)).getText();
					Food food = ShoppingCart.getFoodByStringId(text);
					// delete in task
					new DeleteTask(ShoppingCartActivity.this, food, v).execute();
				}
			};
			foodCount.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View v) {
					RelativeLayout parent = (RelativeLayout)v.getParent();
					// get position and save
					TextView idView = (TextView)parent.findViewById(R.id.product_food_item_id);
					String text = idView.getText().toString();
					String[] ids = text.split(";");
					if (ids.length != 2){
						return;
					}
					mCurrentFood = Integer.valueOf(ids[1]);
					Food food = ShoppingCart.getFoodByStringId(text);
					mCurrentCount = food.getBuyCount();
					mCurrentView = (EditText)v;
					// get current value
					showNumberPickerDialog(food.getBuyCount());
				}
				
			});
			deleteBtn.setOnClickListener(listener);
			foodName.setOnClickListener(listener);
			foodName.setText(food.getName());
			foodPrice.setText(String.valueOf(food.getPrice()));
			foodCount.setText(String.valueOf(food.getBuyCount()));
			foodItemId.setText(ShoppingCart.getStringId(shopId, food.getId()));
			main.addView(foodItem);
			totalPrice += food.getPrice() * food.getBuyCount();
			// if food not in shopping cart, add into.
			ShoppingCart.saveFood(shopId, food);
		}
		products.addView(main);
	
		// set shop name
		((TextView)findViewById(R.id.shop_name)).setText(shopName);
		// reset total price and total box price.
		totalPriceView = (TextView)findViewById(R.id.total_price);
		totalPriceView.setText(String.valueOf(totalPrice));
		
		// add on click listener for adding new address
		mNewAddrBtn = (RadioButton)findViewById(R.id.new_address_button);
		mNewAddrBtn.setOnCheckedChangeListener(new OnCheckedChangeListener(){
			@Override
			public void onCheckedChanged(CompoundButton button, boolean status) {
				// show dialog for adding new address
				if (!status){
					return;
				}
				// load first level region first, than show new address dialog.
				new RegionTask(ShoppingCartActivity.this, 0, 1).execute();
			}
		});
		// add order finish button click listener.
		Button finishOrder = (Button)findViewById(R.id.deal);
		finishOrder.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				// get and valid information first.
				// if no shop or food in shopping cart, give advice
				if (ShoppingCart.getShopsCount() == 0){
					Toast.makeText(getApplicationContext(), "购物车市空的，快去选择吧！", Toast.LENGTH_SHORT).show();
					return;
				}
				// if not login, require login
				if (!LocalPreferences.authed(ShoppingCartActivity.this)){
					ActivityTool.startActivityForResult(ShoppingCartActivity.this, LoginActivity.class, LOGIN_REQUEST_CODE);
					return;
				}
				if (mCurrentAddr == null){
					Toast.makeText(getApplicationContext(), "请选择收货地址！", Toast.LENGTH_LONG).show();
					return;
				}
				// add foods into order.
				Iterator<Integer> shopIds = ShoppingCart.getShops();
				boolean hasFood = false;
				while (shopIds.hasNext()){
					int shopId = shopIds.next();
					Iterator<Integer> foodIds = ShoppingCart.getFoods(shopId);
					while (foodIds.hasNext()){
						hasFood = true;
						break;
					}
					if (hasFood){
						break;
					}
				}
				if (!hasFood){
					Toast.makeText(getApplicationContext(), "购物车市空的，快去选择吧！", Toast.LENGTH_SHORT).show();
					return;
				}
				//ActivityTool.startActivity(ShoppingCartActivity.this, OrderSucceedActivity.class);
				//return;
				if (mOrder == null){
					mOrder = new SimpleOrder();
				}
				mOrder.setAddress(mCurrentAddr.getAddr());
				mOrder.setConsignee(mCurrentAddr.getName());
				mOrder.setPhone(mCurrentAddr.getPhone());
				mOrder.setRegionId(mCurrentAddr.getRegionId());
				Spinner timeInfo = (Spinner)findViewById(R.id.time_spinner);
				mOrder.setSendTime(timeInfo.getSelectedItem().toString());
				mOrder.setShipId(shipId);				
				mOrder.setShopId(mCurrentShop);
				EditText m = (EditText)findViewById(R.id.message);
				mOrder.setMessage(m.getText().toString());
				// all information ok, make the order.
				// send order request in async task, and show loading dialog while doing it.
				new OrderTask(ShoppingCartActivity.this, mOrder).execute();
			}
		});
	}
	
	public void setUpdateResult(ApiResult result){
		this.mUpdateResult = result;
	}
	
	public void showUpdateResult(Food food){
		if (mUpdateResult == null || !mUpdateResult.isSuccess()){
			Log.e(TAG, "update food count failed");
			Toast.makeText(getApplicationContext(), "修改数量失败，请重新尝试", Toast.LENGTH_SHORT).show();
			return;
		}
		int count = food.getBuyCount();
		// update total money, total box money
		float price = Float.valueOf(totalPriceView.getText().toString());
		if (mCurrentCount == count){
			return;
		} else if (mCurrentCount > count){
			price -= food.getPrice() * (mCurrentCount - count);
		} else {
			price += food.getPrice() * (count - mCurrentCount);
		}
		totalPriceView.setText(String.valueOf(price));
		mCurrentView.setText(String.valueOf(count));
		mCurrentFood = -1;
		mCurrentCount = -1;
		mCurrentView = null;
		this.mUpdateResult = null;
	}
	
	private class UpdateTask extends AsyncTask<String, Void, Boolean> {
		
		private ShoppingCartActivity activity = null;
		private Food food = null;
		private ProgressDialog mDialog = null;

		public UpdateTask(ShoppingCartActivity activity, Food food) {
	        this.activity = activity;
	        this.food = food;
	        this.mDialog = new ProgressDialog(activity);
	    }
		
		@Override
		protected Boolean doInBackground(String... arg0) {
			// call api to update food count
			User user = LocalPreferences.getUser(activity);
			ApiResult result = OrderApi.updateCount(food, user);
			activity.setUpdateResult(result);
			return true;
		}
		
		protected void onPreExecute() {
	        this.mDialog.setMessage("加载中，请稍候。。。");
	        this.mDialog.show();
	    }

	        @Override
	    protected void onPostExecute(final Boolean success) {
	        if (mDialog.isShowing()) {
	        	mDialog.dismiss();
	        }
	        // update count finished, update ui
	        activity.showUpdateResult(food);
	    }
	}
	
	public void setDeleteResult(ApiResult result){
		this.mRemoveResult = result;
	}
	
	public void showDeleteResult(Food food, View v){
		if (mRemoveResult == null || !mRemoveResult.isSuccess()){
			Toast.makeText(getApplicationContext(), "删除物品失败，请重新尝试", Toast.LENGTH_SHORT).show();
			Log.e(TAG, "remove failed");
			return;
		}
		RelativeLayout parent = (RelativeLayout)v.getParent();
		LinearLayout grandParent = (LinearLayout)parent.getParent();
		String text = (String) ((TextView)parent.findViewById(R.id.product_food_item_id)).getText();
		boolean shopEmpty = ShoppingCart.deleteFood(text);
		grandParent.removeView(parent);
		if (shopEmpty){
			LinearLayout grandgrandParent = (LinearLayout)grandParent.getParent();
			grandgrandParent.removeView(grandParent);
		}
		// reset total price and total box price.
		float oriTotalPrice = Float.valueOf(totalPriceView.getText().toString());
		oriTotalPrice -= food.getPrice() * food.getBuyCount();
		totalPriceView.setText(String.valueOf(oriTotalPrice));
		this.mRemoveResult = null;
	}
	
	private class DeleteTask extends AsyncTask<String, Void, Boolean> {
		
		private ShoppingCartActivity activity = null;
		private Food food = null;
		private View view = null;
		private ProgressDialog mDialog = null;

		public DeleteTask(ShoppingCartActivity activity, Food food, View view) {
	        this.activity = activity;
	        this.food = food;
	        this.view = view;
	        this.mDialog = new ProgressDialog(activity);
	    }
		
		@Override
		protected Boolean doInBackground(String... arg0) {
			try {
				// call api to remove food from shopping cart
				User user = LocalPreferences.getUser(activity);
				ApiResult result = OrderApi.removeFood(food, user);
				activity.setDeleteResult(result);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return true;
		}
		
		protected void onPreExecute() {
	        this.mDialog.setMessage("加载中，请稍候。。。");
	        this.mDialog.show();
	    }

	        @Override
	    protected void onPostExecute(final Boolean success) {
	        if (mDialog.isShowing()) {
	        	mDialog.dismiss();
	        }
	        // remove finished, update ui
	        activity.showDeleteResult(food, view);
	    }
	}
	
	private class OrderTask extends AsyncTask<String, Void, Boolean> {
		
		private ShoppingCartActivity activity = null;
		private SimpleOrder order = null;
		private ProgressDialog mDialog = null;

		public OrderTask(ShoppingCartActivity activity, SimpleOrder order) {
	        this.activity = activity;
	        this.order = order;
	        this.mDialog = new ProgressDialog(activity);
	    }
		
		@Override
		protected Boolean doInBackground(String... arg0) {
			// call api to make order
			User user = LocalPreferences.getUser(activity);
			ApiResult result = OrderApi.order(order, user);
			activity.setOrderResult(result);
			return true;
		}
		
		protected void onPreExecute() {
	        this.mDialog.setMessage("处理中，请稍候。。。");
	        this.mDialog.show();
	    }

	        @Override
	    protected void onPostExecute(final Boolean success) {
	        if (mDialog.isShowing()) {
	        	mDialog.dismiss();
	        }
	        activity.finishOrder();
	    }
	}
	
	public void setOrderResult(ApiResult result){
		mOrderResult = result;
	}
	
	public void finishOrder(){
		Log.e("ZZOUR", "finish order");
		if (mOrderResult == null){
			Toast.makeText(getApplicationContext(), "订单提交失败，请重新尝试！", Toast.LENGTH_SHORT).show();
			return;
		} else if (!mOrderResult.isSuccess()){
			Toast.makeText(getApplicationContext(), "订单提交失败，" + mOrderResult.getMsg(), Toast.LENGTH_SHORT).show();
			return;
		}
		// success, to success activity
		ActivityTool.startActivity(ShoppingCartActivity.this, OrderSucceedActivity.class);
	}
	
	private class LoadingTask extends AsyncTask<String, Void, Boolean> {
		
		private ShoppingCartActivity activity = null;
		private ProgressDialog mDialog = null;

		public LoadingTask(ShoppingCartActivity activity) {
	        this.activity = activity;
	        this.mDialog = new ProgressDialog(activity);
	    }
		
		@Override
		protected Boolean doInBackground(String... arg0) {
			User user = LocalPreferences.getUser(activity);
			// add foods into shopping cart by calling api
			if (foods.size() > 0){
				boolean success = false;
				try {
					success = OrderApi.addAll(foods, user);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if (!success){
					// set result
					Toast.makeText(getApplicationContext(), "添加商品到购物车失败，请重新尝试", Toast.LENGTH_SHORT).show();
					return true;
				}
			}
			// reset foods to empty
			foods = null;
			// get order form by calling api
			OrderFormResult result = OrderApi.getOrderForm(mCurrentShop, user);
			// set result to shopping cart activity
			activity.setOrderFormResult(result);
			return true;
		}
		
		protected void onPreExecute() {
	        this.mDialog.setMessage(this.activity.getResources().getString(R.string.loading_progress_text));
	        this.mDialog.show();
	    }

	        @Override
	    protected void onPostExecute(final Boolean success) {
	        if (mDialog.isShowing()) {
	        	mDialog.dismiss();
	        }
	        this.activity.finishOrderForm();
	    }
	}
	
	@Override  
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
        case LOGIN_REQUEST_CODE:
        	new LoadingTask(ShoppingCartActivity.this).execute();
            break;
        default:
            break;
        }  
    }
	
	public void setClearCartResult(ApiResult result){
		this.mClearCartResult = result;
	}
	
	public void finishClearCart(){
		if (mClearCartResult == null || !mClearCartResult.isSuccess()){
			// TODO toast
			return;
		}
		ShoppingCart.clear();
		this.realBack();
	}
	
	private class BackTask extends AsyncTask<String, Void, Boolean> {
		
		private ShoppingCartActivity activity = null;
		private ProgressDialog mDialog = null;

		public BackTask(ShoppingCartActivity activity) {
	        this.activity = activity;
	        this.mDialog = new ProgressDialog(activity);
	    }
		
		@Override
		protected Boolean doInBackground(String... arg0) {
			// call api to clear cart
			User user = LocalPreferences.getUser(activity);
			ApiResult result = OrderApi.clearCart(user);
			activity.setClearCartResult(result);
			return true;
		}
		
		protected void onPreExecute() {
	        this.mDialog.setMessage("处理中，请稍候。。。");
	        this.mDialog.show();
	    }

	        @Override
	    protected void onPostExecute(final Boolean success) {
	        if (mDialog.isShowing()) {
	        	mDialog.dismiss();
	        }
	        // finish
	        this.activity.finishClearCart();
	    }
	}
	
	private class RegionTask extends AsyncTask<String, Void, Boolean> {
		
		private ShoppingCartActivity activity = null;
		private int parentId = -1;
		private int level = -1;
		private ProgressDialog mDialog = null;

		public RegionTask(ShoppingCartActivity activity, int parentId, int level) {
	        this.activity = activity;
	        this.parentId = parentId;
	        this.level = level;
	        this.mDialog = new ProgressDialog(activity);
	    }
		
		@Override
		protected Boolean doInBackground(String... arg0) {
			// call api to load region
			Log.e("ZZOUR", "call api to load regions");
			ArrayList<Region> regions = RegionApi.getRegions(parentId);
			// call activity set method to set region result
			Log.e("ZZOUR", "set region result, got regions count: " + regions.size());
			activity.setRegionTaskResult(regions, this.level);
			return true;
		}
		
		protected void onPreExecute() {
	        this.mDialog.setMessage("加载中，请稍候。。。");
	        this.mDialog.show();
	    }

	        @Override
	    protected void onPostExecute(final Boolean success) {
	        if (mDialog.isShowing()) {
	        	mDialog.dismiss();
	        }
	        // load region finish, update ui
	        activity.finishRegionTask(level);
	    }
	}
	
	public void setRegionTaskResult(ArrayList<Region> regions, int level){
		if (level == 1){
			// save first level region
			mFirstLevelRegions = regions;
		} else if (level == 2){
			mSecondLevelRegions = regions;
		} else if (level == 3){
			mThirdLevelRegions = regions;
		} else{
			Log.e("ZZOUR", "what happended? region level is " + level);
		}
	}
	
	public void finishRegionTask(int level){
		if (level == 1){
			showNewAddressDialog();
		} else if (level == 2){
			Log.e("ZZOUR", "update level 2 ui");
			Spinner areaSpinner = (Spinner)mNewAddressDialog.findViewById(R.id.new_address_school_area);
			String[] areaNames = getRegionNames(mSecondLevelRegions, 2);
			if (areaNames.length > 0){
				ArrayAdapter<String> adapter = new ArrayAdapter<String>(ShoppingCartActivity.this,android.R.layout.simple_spinner_item, areaNames);
				adapter.setDropDownViewResource ( android.R.layout.simple_spinner_dropdown_item );
				areaSpinner.setAdapter(adapter);
				areaSpinner.setVisibility(Spinner.VISIBLE);
			} else {
				areaSpinner.setVisibility(Spinner.GONE);
			}
		} else if (level == 3){
			Log.e("ZZOUR", "update level 3 ui");
			Spinner detailSpinner = (Spinner)mNewAddressDialog.findViewById(R.id.new_address_school_area_detail);
			String[] names = getRegionNames(mThirdLevelRegions, 3);
			if (names != null && names.length > 0 && names[0].length() > 0){
				ArrayAdapter<String> adapter = new ArrayAdapter<String>(ShoppingCartActivity.this,android.R.layout.simple_spinner_item, names);
				adapter.setDropDownViewResource ( android.R.layout.simple_spinner_dropdown_item );
				detailSpinner.setAdapter(adapter);
				detailSpinner.setVisibility(Spinner.VISIBLE);
			} else {
				detailSpinner.setVisibility(Spinner.GONE);
			}
		} else{
			Log.e("ZZOUR", "what happended? region level is " + level);
		}
	}
	
	@Override
	public void onBackPressed(){
		showBackDialog();
	}
	
	public void realBack(){
		super.onBackPressed();
	}
	
	public void showBackDialog(){
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage("如继续返回，购物车将清空\n\n确定继续返回上一页吗？")
		       .setPositiveButton("确定", new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		        	   new BackTask(ShoppingCartActivity.this).execute();
		           }
		       })
		       .setNegativeButton("取消", new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		                dialog.cancel();
		           }
		       });
		AlertDialog alert = builder.create();
		alert.show();
	}
}
