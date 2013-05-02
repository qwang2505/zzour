package com.zzour.android;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;

import com.zzour.android.R;
import com.zzour.android.base.BaseActivity;
import com.zzour.android.base.SysApplication;
import com.zzour.android.models.ShopList;
import com.zzour.android.models.ShopSummaryContent;
import com.zzour.android.network.api.ShopListApi;
import com.zzour.android.utils.ActivityTool;
import com.zzour.android.utils.ImageTool;
import com.zzour.android.views.HorizontalImageScrollView;
import com.zzour.android.views.adapters.ListItemsAdapter;

import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Toast;

public class HomeActivity extends BaseActivity {
	
	Spinner mSearchCategory = null;
	int mSearchCategoryValue = 0;
	Timer mScrollTimer = null;
	TimerTask mScrollScheduler = null;
	HorizontalImageScrollView mImageScrollView = null;
	// TODO reset value after read from api.
	int mCurrentImageIndex = 0;
	int mMaxImageIndex = 1;
	int mScreenWidth = 0;
	
	ListItemsAdapter mAdapterTemp = null;
	private Handler mHandler = new Handler();
	private Handler mLoadMoreHandler = new Handler();
	private Handler mAdapterHandler = new Handler();
		
	private View mLoadMoreView = null;
	private Button mLoadMoreButton = null;
	
	private View mContentView = null;
	
	private HashMap<Integer, String> mImages = new HashMap<Integer, String>();
	
	// TODO read categories from api, or at least from xml file.
	final CharSequence[] mSearchCategories = new CharSequence[] {	
		"商家",
		"菜品",
	};
	final int[] mSearchCategoryValues = new int[]{
		0,
		1,
	};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.activity_main);
        
        mSearchCategory = (Spinner)findViewById(R.id.search_category);
        ArrayAdapter<CharSequence> adapter = new ArrayAdapter<CharSequence>(this,
                android.R.layout.simple_spinner_item, mSearchCategories);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSearchCategory.setAdapter(adapter);
        mSearchCategory.setSelection(0);
        mSearchCategory.setOnItemSelectedListener(
                new OnItemSelectedListener() {
                    public void onItemSelected(
                            AdapterView<?> parent, View view, int position, long id) {
                        mSearchCategoryValue = mSearchCategoryValues[position];
                    }

                    public void onNothingSelected(AdapterView<?> parent) {
                        mSearchCategoryValue = mSearchCategoryValues[0];
                    }
                });
        
        // TODO get search initial text from api, and reset. Here just reset.
        EditText searchText = (EditText)findViewById(R.id.search);
        String text = getString(R.string.search_initial_text);
        searchText.setText(text);
        this.initScrollImageView();
    
        // add load more to list view
        mLoadMoreView = getLayoutInflater().inflate(R.layout.loadmore, null);
        mLoadMoreButton = (Button)mLoadMoreView.findViewById(R.id.loadMoreButton);
        mLoadMoreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            	mLoadMoreButton.setText(getString(R.string.loading_text));
        	    new Thread(new Runnable() {
        	       @Override
        	       public void run() {
        	    	   loadMoreData();
        	    	   mLoadMoreHandler.post(new Runnable(){
        	    		   public void run(){
        	    			   mAdapterTemp.notifyDataSetChanged();
        	    			   mLoadMoreButton.setText(getString(R.string.load_more_button_text));
        	    		   }
        	    	   });
        	       }
        	    }).start();
        	}
        });
        ListView list = (ListView)findViewById(R.id.list);
        list.addFooterView(mLoadMoreView);
        
        // init adapter and set scroll view to list.
        mAdapterTemp = new ListItemsAdapter(this, R.drawable.logo);
        new Thread(initList).start();
        
        this.addListItemClickListener();
    }
    
    private void addListItemClickListener(){
    	ListView lv = (ListView)findViewById(R.id.list);
    	lv.setOnItemClickListener(new OnItemClickListener()
    	{
    	    @Override 
    	    public void onItemClick(AdapterView<?> arg0, View arg1,int position, long arg3)
    	    { 
    	    	//Toast.makeText(MainActivity.this, "" + position, Toast.LENGTH_SHORT).show();
    	    	if (mAdapterTemp == null){
    	    		Log.e(TAG, "list adapter is null, something happende");
    	    		return;
    	    	}
    	    	ShopSummaryContent shop = mAdapterTemp.getShopSummaryAtPosition(position-1);
    	    	if (shop == null){
    	    		Log.e(TAG, "strange! shop can't be found in list adapter! at position: " + (position-1));
    	    		return;
    	    	}
    	    	Intent intent = new Intent(HomeActivity.this, ShopDetailActivity.class);
    	    	intent.putExtra("shop_id", shop.getId());
    	    	ActivityTool.startActivity(HomeActivity.this, ShopDetailActivity.class, intent);
    	    }
    	});
    }
    
	@Override
	public void onBackPressed(){
		// not all back to main
		SysApplication.getInstance().exit();
	}
    
	@Override
	public void onResume() {
	    super.onResume();
	    overridePendingTransition(R.anim.left_slide_in, R.anim.right_slide_out);
	}
    
    @Override
    protected void onDestroy(){
    	if (this.mScrollTimer != null){
    		this.mScrollTimer.cancel();
    		this.mScrollTimer = null;
    	}
    	if (this.mScrollScheduler != null){
    		this.mScrollScheduler.cancel();
    		this.mScrollScheduler = null;
    	}
    	this.mLoadMoreView = null;
    	this.mLoadMoreButton = null;
    	super.onDestroy();
    }
    
    private void loadMoreData(){
    	// pass in activity instance to get preference
    	final ShopList shopList = ShopListApi.getShopList(this);

    	if (shopList == null){
    		mLoadMoreHandler.post(new Runnable(){
    		   public void run(){
    			   Toast.makeText(HomeActivity.this, "加载数据失败，请检查网络状况", Toast.LENGTH_LONG).show();
    		   }
	    	});
    		return;
    	}
		for (int i=0; i < shopList.size(); i++){
    		final ShopSummaryContent shop = shopList.get(i);
    		int p = mAdapterTemp.addItem(shop);
    		mImages.put(p, shop.getImage());
    	}
    	// start new thread to download image and update
    	Iterator<Integer> it = mImages.keySet().iterator();
    	Log.d(TAG, "image count need to download: " + mImages.size());
		final int width = (int)getResources().getDimension(R.dimen.list_image_width);
		final int height = (int)getResources().getDimension(R.dimen.list_image_height);
    	while (it.hasNext()){
    		final int position = (Integer)it.next();
    		final Bitmap bmp = ImageTool.getBitmapByUrl(mImages.get(position), width, height, HomeActivity.this);
    		mLoadMoreHandler.post(new Runnable(){
    			public void run(){
    				Log.d(TAG, "update shop bitmap at position " + position);
    				mAdapterTemp.updateShopBitmap(position, bmp);
    				mAdapterTemp.notifyDataSetChanged();
    			}
    		});
    	}
    }
    
    private void initScrollImageView(){
        // TODO get images and set to HorizontalImageScrollView
        //mImageScrollView = (HorizontalImageScrollView)findViewById(R.id.image_scroll);
    	mImageScrollView = new HorizontalImageScrollView(this.getApplicationContext());
        // get device size, and calculate image size.
        DisplayMetrics metrics = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int width = metrics.widthPixels - 10;
        int height = 160;
        
        ArrayList<Bitmap> items = new ArrayList<Bitmap>();
        Bitmap bmp = ImageTool.getBitmapByStream(R.drawable.scroll_image_1, 
        		getResources().openRawResource(R.drawable.scroll_image_1), width, height);
    	items.add(bmp);
    	bmp = ImageTool.getBitmapByStream(R.drawable.scroll_image_2, 
    			getResources().openRawResource(R.drawable.scroll_image_2), width, height);
    	items.add(bmp);
    	mImageScrollView.setFeatureItems(items);
    	mScreenWidth = metrics.widthPixels;
    	ListView list = (ListView)findViewById(R.id.list);
    	list.addHeaderView(mImageScrollView);
    	this.startAutoScrolling();
    }
    
    public void startAutoScrolling(){
        if (mScrollTimer == null) {
            mScrollTimer =   new Timer();
            final Runnable Timer_Tick = new Runnable() {
                public void run() {
                	if (mCurrentImageIndex >= mMaxImageIndex){
                		mCurrentImageIndex = 0;
                	}else{
                		mCurrentImageIndex++;
                	}
                	
                	int scrollPos = mCurrentImageIndex * mScreenWidth;
                	
                	mImageScrollView.smoothScrollTo(scrollPos, 0);
                }
            };

            if(mScrollScheduler != null){
                mScrollScheduler.cancel();
                mScrollScheduler = null;
            }
            mScrollScheduler = new TimerTask(){
                @Override
                public void run(){
                    runOnUiThread(Timer_Tick);
                }
            };

            mScrollTimer.schedule(mScrollScheduler, 5 * 1000, 5 * 1000);
        }
    }
    
    Runnable initList = new Runnable(){
		@Override
		public void run() {
			loadMoreData();
			mHandler.post(new Runnable(){
				public void run(){
					ListView list = (ListView)findViewById(R.id.list);
					list.setAdapter(mAdapterTemp);
				}
			});
		}
    };
}
