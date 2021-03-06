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
import android.content.Intent;
import android.graphics.Bitmap;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

public class HomeActivity extends BaseActivity {
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
		
	private View mLoadMoreView = null;
	private Button mLoadMoreButton = null;
	
	private int pageNum = 1;
	private static final int RESULT_COUNT = 6;
	
	private HashMap<Integer, String> mImages = new HashMap<Integer, String>();
	
	private long prevBackTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.activity_main);

        this.initScrollImageView();
    
        // add load more to list view
        mLoadMoreView = getLayoutInflater().inflate(R.layout.loadmore, null);
        mLoadMoreButton = (Button)mLoadMoreView.findViewById(R.id.loadMoreButton);
        ListView list = (ListView)findViewById(R.id.list);
        list.addFooterView(mLoadMoreView);
        
        // init adapter and set scroll view to list.
        mAdapterTemp = new ListItemsAdapter(this, R.drawable.default_shop_logo);
        list.setAdapter(mAdapterTemp);
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
		// press twice to exit application
		long now = System.currentTimeMillis();
		if (now - this.prevBackTime < 2000){
			SysApplication.getInstance().exit();
		} else {
			Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
			this.prevBackTime = now;
			return;
		}
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
    	final ShopList shopList = ShopListApi.getShopList(this, pageNum, RESULT_COUNT);

    	if (shopList == null){
    		mLoadMoreHandler.post(new Runnable(){
    		   public void run(){
    			   Toast.makeText(HomeActivity.this, "加载数据失败，请检查网络状况", Toast.LENGTH_LONG).show();
    		   }
	    	});
    		return;
    	}
    	if (shopList.size() == 0){
    		mLoadMoreHandler.post(new Runnable(){
     		   public void run(){
     			   Toast.makeText(HomeActivity.this, "没有更多数据了", Toast.LENGTH_SHORT).show();
     		   }
 	    	});
     		return;
    	}
    	pageNum += 1;
		for (int i=0; i < shopList.size(); i++){
    		final ShopSummaryContent shop = shopList.get(i);
    		int p = mAdapterTemp.addItem(shop);
    		mImages.put(p, shop.getLogo());
    	}
		Log.e(TAG, "add shop over");
    	// start new thread to download image and update
    	Iterator<Integer> it = mImages.keySet().iterator();
    	Log.e(TAG, "image count need to download: " + mImages.size());
		final int width = (int)getResources().getDimension(R.dimen.list_image_width);
		final int height = (int)getResources().getDimension(R.dimen.list_image_height);
    	while (it.hasNext()){
    		final int position = (Integer)it.next();
    		final Bitmap bmp = ImageTool.getBitmapByUrl(mImages.get(position), width, height, HomeActivity.this);
    		if (bmp != null){
	    		mLoadMoreHandler.post(new Runnable(){
	    			public void run(){
	    				mAdapterTemp.updateShopBitmap(position, bmp);
	    				mAdapterTemp.notifyDataSetChanged();
	    			}
	    		});
    		}
    	}
    }
    
    private void initScrollImageView(){
        // TODO get images and set to HorizontalImageScrollView
        //mImageScrollView = (HorizontalImageScrollView)findViewById(R.id.image_scroll);
    	mImageScrollView = new HorizontalImageScrollView(this.getApplicationContext());
        // get device size, and calculate image size.
//        DisplayMetrics metrics = new DisplayMetrics();
//        this.getWindowManager().getDefaultDisplay().getMetrics(metrics);
//        int width = metrics.widthPixels - 10;
//        int height = 160;
//        
//        ArrayList<Bitmap> items = new ArrayList<Bitmap>();
//        Bitmap bmp = ImageTool.getBitmapByStream(R.drawable.scroll_image_1, 
//        		getResources().openRawResource(R.drawable.scroll_image_1), width, height);
//    	items.add(bmp);
//    	bmp = ImageTool.getBitmapByStream(R.drawable.scroll_image_2, 
//    			getResources().openRawResource(R.drawable.scroll_image_2), width, height);
//    	items.add(bmp);
//    	mImageScrollView.setFeatureItems(items);
//    	mScreenWidth = metrics.widthPixels;
    	ListView list = (ListView)findViewById(R.id.list);
    	mImageScrollView.setVisibility(HorizontalImageScrollView.GONE);
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
			if (mLoadMoreButton != null){
				mLoadMoreButton.setText("商家信息加载中，请稍候");
			}
			loadMoreData();
			mHandler.post(new Runnable(){
				public void run(){
					mAdapterTemp.notifyDataSetChanged();
					mLoadMoreButton.setText(getString(R.string.load_more_button_text));
				}
			});
		}
    };
}
