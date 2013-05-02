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
import com.zzour.android.models.OrderResult;
import com.zzour.android.models.School;
import com.zzour.android.models.SchoolArea;
import com.zzour.android.models.ShoppingCart;
import com.zzour.android.models.dao.AddressDAO;
import com.zzour.android.network.api.OrderApi;
import com.zzour.android.network.api.SchoolApi;
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
	private TextView totalBoxPriceView;
	
	private int mCurrentShop = -1;
	private int mCurrentFood = -1;
	private int mCurrentCount = -1;
	private EditText mCurrentView = null;
	
	private AlertDialog mNumberPicker;
	private AlertDialog mNewAddressDialog;
	
	private ArrayList<School> mSchools;
	private School mCurrentSchool = null;
	
	private RadioButton mNewAddrBtn = null;
	// button handler
	private ButtonHandler bHandler; 
	
	private Address mCurrentAddr = null;
	private Order mOrder = null;
	
	private HashMap<RadioButton, Address> mAddrMap = new HashMap<RadioButton, Address>();
	
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
		
		// get foods from shopping cart, and render
		Iterator<Integer> shopIds = ShoppingCart.getShops();
		LinearLayout products = (LinearLayout)findViewById(R.id.products);
		float totalPrice = 0.0f;
		float totalBoxPrice = 0.0f;
		while (shopIds.hasNext()){
			// get product view
			LinearLayout main = (LinearLayout)getLayoutInflater().inflate(R.layout.product_main, null);
			TextView shopName = (TextView)main.findViewById(R.id.product_shop_name);
			int shopId = shopIds.next();
			shopName.setText(ShoppingCart.getShopName(shopId));
			Iterator<Integer> foodIds = ShoppingCart.getFoods(shopId);
			while (foodIds.hasNext()){
				Integer foodId = foodIds.next();
				Food food = ShoppingCart.getFood(shopId, foodId);
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
						LinearLayout grandParent = (LinearLayout)parent.getParent();
						String text = (String) ((TextView)parent.findViewById(R.id.product_food_item_id)).getText();
						Food food = ShoppingCart.getFoodByStringId(text);
						boolean shopEmpty = ShoppingCart.deleteFood(text);
						grandParent.removeView(parent);
						if (shopEmpty){
							LinearLayout grandgrandParent = (LinearLayout)grandParent.getParent();
							grandgrandParent.removeView(grandParent);
						}
						// reset total price and total box price.
						float oriTotalPrice = Float.valueOf(totalPriceView.getText().toString());
						float oriTotalBoxPrice = Float.valueOf(totalBoxPriceView.getText().toString());
						oriTotalPrice -= food.getPrice() * food.getBuyCount();
						oriTotalBoxPrice -= food.getBoxPrice() * food.getBuyCount();
						totalPriceView.setText(String.valueOf(oriTotalPrice));
						totalBoxPriceView.setText(String.valueOf(oriTotalBoxPrice));
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
						mCurrentShop = Integer.valueOf(ids[0]);
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
				foodItemId.setText(ShoppingCart.getStringId(shopId, foodId));
				main.addView(foodItem);
				totalBoxPrice += food.getBoxPrice() * food.getBuyCount();
				totalPrice += food.getPrice() * food.getBuyCount();
			}
			products.addView(main);
		}
		// reset total price and total box price.
		totalPriceView = (TextView)findViewById(R.id.total_price);
		totalPriceView.setText(String.valueOf(totalPrice));
		totalBoxPriceView = (TextView)findViewById(R.id.box_price);
		totalBoxPriceView.setText(String.valueOf(totalBoxPrice));
		
		// TOOD add on click listener for adding new address
		mNewAddrBtn = (RadioButton)findViewById(R.id.new_address_button);
		mNewAddrBtn.setOnCheckedChangeListener(new OnCheckedChangeListener(){
			@Override
			public void onCheckedChanged(CompoundButton button, boolean status) {
				// show dialog for adding new address
				if (!status){
					return;
				}
				showNewAddressDialog();
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
				// add foods into order.
				Iterator<Integer> shopIds = ShoppingCart.getShops();
				mOrder = new Order();
				while (shopIds.hasNext()){
					int shopId = shopIds.next();
					Iterator<Integer> foodIds = ShoppingCart.getFoods(shopId);
					while (foodIds.hasNext()){
						int foodId = foodIds.next();
						Food food = ShoppingCart.getFood(shopId, foodId);
						mOrder.addFood(shopId, ShoppingCart.getShopName(shopId), ShoppingCart.getShopImage(shopId), food);
					}
				}
				if (mOrder.getFoodCount() == 0){
					Toast.makeText(getApplicationContext(), "购物车市空的，快去选择吧！", Toast.LENGTH_LONG).show();
					return;
				}
				if (mCurrentAddr == null){
					Toast.makeText(getApplicationContext(), "请选择收货地址！", Toast.LENGTH_LONG).show();
					return;
				}
				mOrder.setAddress(mCurrentAddr);
				mOrder.setTotalPrice(Float.valueOf(totalPriceView.getText().toString()));
				mOrder.setTotalBoxPrice(Float.valueOf(totalBoxPriceView.getText().toString()));
				Spinner timeInfo = (Spinner)findViewById(R.id.time_spinner);
				mOrder.setSendTime(timeInfo.getSelectedItem().toString());
				EditText m = (EditText)findViewById(R.id.message);
				mOrder.setMessage(m.getText().toString());
				// all information ok, make the order.
				// if not login, require login
				if (!LocalPreferences.authed(ShoppingCartActivity.this)){
					ActivityTool.startActivityForResult(ShoppingCartActivity.this, LoginActivity.class, LOGIN_REQUEST_CODE);
					return;
				}
				// send order request in async task, and show loading dialog while doing it.
				new LoadingTask(ShoppingCartActivity.this).execute();
				// TODO clear shopping cart
			}
		});
		
		loadAddress();
	}
	
	private void loadAddress(){
		AddressDAO dao = new AddressDAO(this);
		ArrayList<Address> addrs = dao.get();
		if (addrs == null || addrs.size() == 0){
			return;
		}
		Iterator<Address> it = addrs.iterator();
		while (it.hasNext()){
			Address addr = it.next();
			// add new address into view
			View view = LayoutInflater.from(ShoppingCartActivity.this).inflate(R.layout.address_info, null);
			((TextView)view.findViewById(R.id.address_name)).setText(addr.getName());
			((TextView)view.findViewById(R.id.address_phone)).setText(addr.getPhone());
			((TextView)view.findViewById(R.id.address_detail)).setText(addr.getAddr());
			LinearLayout parent = (LinearLayout)ShoppingCartActivity.this.findViewById(R.id.address_info);
			parent.addView(view, parent.getChildCount() - 1);
			RadioButton rb = (RadioButton)view.findViewById(R.id.address_radio_button);
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
			mAddrMap.put(rb, addr);
			if (!it.hasNext()){
				rb.setChecked(true);
			}
		}
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
				if (spiner.getVisibility() == Spinner.VISIBLE && spiner.getSelectedItemPosition() != 0){
					addr += (String)spiner.getSelectedItem();
					addr += " ";
				}
				spiner = (Spinner)mNewAddressDialog.findViewById(R.id.new_address_school_area);
				if (spiner.getVisibility() == Spinner.VISIBLE && spiner.getSelectedItemPosition() != 0){
					addr += (String)spiner.getSelectedItem();
					addr += " ";
				}
				spiner = (Spinner)mNewAddressDialog.findViewById(R.id.new_address_school_area_detail);
				if (spiner.getVisibility() == Spinner.VISIBLE){
					addr += (String)spiner.getSelectedItem();
					addr += " ";
				}
				TextView address = (TextView)mNewAddressDialog.findViewById(R.id.new_address_detail);
				addr += address.getText();
				if (addr.length() <= 0){
					Toast.makeText(getApplicationContext(), "地址不能为空！", Toast.LENGTH_SHORT).show();
					return;
				}
				Address address1 = new Address(name.getText().toString(), phone.getText().toString(), addr);
				dialog.dismiss();
				// add new address into view
				View view = LayoutInflater.from(ShoppingCartActivity.this).inflate(R.layout.address_info, null);
				((TextView)view.findViewById(R.id.address_name)).setText(address1.getName());
				((TextView)view.findViewById(R.id.address_phone)).setText(address1.getPhone());
				((TextView)view.findViewById(R.id.address_detail)).setText(address1.getAddr());
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
				// save new address to cache.
				AddressDAO dao = new AddressDAO(ShoppingCartActivity.this);
				dao.insert(address1);
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
		// init schools
		if (mSchools == null){
			mSchools = SchoolApi.getSchoolList();
		}
		String[] names = getSchoolNames();
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
				// show second spinner if there is.
				School s = mSchools.get(position-1);
				mCurrentSchool = s;
				ArrayList<SchoolArea> areas = s.getArea();
				String[] areaNames = getSchoolAreaNames(areas);
				if (areaNames.length > 0){
					ArrayAdapter<String> adapter = new ArrayAdapter<String>(ShoppingCartActivity.this,android.R.layout.simple_spinner_item, areaNames);
					adapter.setDropDownViewResource ( android.R.layout.simple_spinner_dropdown_item );
					areaSpinner.setAdapter(adapter);
					areaSpinner.setVisibility(Spinner.VISIBLE);
				} else {
					areaSpinner.setVisibility(Spinner.GONE);
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				
			}
			
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
				if (mCurrentSchool == null){
					Log.e(TAG, "what happend? current school should not be null");
				}
				String[] names = mCurrentSchool.getArea().get(position-1).getDetails();
				if (names != null && names.length > 0 && names[0].length() > 0){
					ArrayAdapter<String> adapter = new ArrayAdapter<String>(ShoppingCartActivity.this,android.R.layout.simple_spinner_item, names);
					adapter.setDropDownViewResource ( android.R.layout.simple_spinner_dropdown_item );
					detailSpinner.setAdapter(adapter);
					detailSpinner.setVisibility(Spinner.VISIBLE);
				} else {
					detailSpinner.setVisibility(Spinner.GONE);
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				
			}
			
		});
	}
	
	private String[] getSchoolAreaNames(ArrayList<SchoolArea> areas){
		String[] schools = new String[areas.size()+1];
		schools[0] = "选择校区";
		Iterator<SchoolArea> it = areas.iterator();
		int i = 1;
		while (it.hasNext()){
			schools[i] = it.next().getName();
			i += 1;
		}
		return schools;
	}
	
	private String[] getSchoolNames(){
		String[] schools = new String[mSchools.size()+1];
		schools[0] = "选择学校";
		Iterator<School> it = mSchools.iterator();
		int i = 1;
		while (it.hasNext()){
			schools[i] = it.next().getName();
			i += 1;
		}
		return schools;
	}
	
	public void showNumberPickerDialog(int number){
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setCancelable(false);
		builder.setPositiveButton("确定", new DialogInterface.OnClickListener(){
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				if (mCurrentShop == -1 || mCurrentFood == -1 || mCurrentCount == -1 || mCurrentView == null){
					Log.d(TAG, "what happended?");
					return;
				}
				int count = Integer.valueOf(((TextView)mNumberPicker.findViewById(R.id.numpicker_input)).getText().toString());
				Food f = ShoppingCart.getFood(mCurrentShop, mCurrentFood);
				f.setBuyCount(count);
				// update total money, total box money
				float price = Float.valueOf(totalPriceView.getText().toString());
				float boxPrice = Float.valueOf(totalBoxPriceView.getText().toString());
				if (mCurrentCount == count){
					return;
				} else if (mCurrentCount > count){
					price -= f.getPrice() * (mCurrentCount - count);
					boxPrice -= f.getBoxPrice() * (mCurrentCount - count);
				} else {
					price += f.getPrice() * (count - mCurrentCount);
					boxPrice += f.getBoxPrice() * (count - mCurrentCount);
				}
				totalPriceView.setText(String.valueOf(price));
				totalBoxPriceView.setText(String.valueOf(boxPrice));
				mCurrentView.setText(String.valueOf(count));
				mCurrentShop = -1;
				mCurrentFood = -1;
				mCurrentCount = -1;
				mCurrentView = null;
			}
		});
		builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				mCurrentShop = -1;
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
	
	public void setOrderResult(OrderResult result){
		if (mOrder == null){
			Log.d(TAG, "Something goes wrong, mOrder is null while set order id");
			return;
		}
		mOrder.setId(result.getId());
		mOrder.setResultMsg(result.getMsg());
	}
	
	public void finishOrder(){
		if (mOrder == null){
			Log.d(TAG, "Something goes wrong, mOrder is null while finish order");
			return;
		}
		if (mOrder.getId() == null){
			// not success, to the fail activity
			Toast.makeText(this, "提交订单错误：" + mOrder.getResultMsg(), Toast.LENGTH_LONG);
			return;
		} else {
			// success, to the success activity
			ActivityTool.startActivity(ShoppingCartActivity.this, OrderSucceedActivity.class);
		}
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
			OrderResult result = OrderApi.order(mOrder, ShoppingCartActivity.this);
			activity.setOrderResult(result);
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
	        this.activity.finishOrder();
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

}
