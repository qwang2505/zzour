package com.zzour.android;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

import com.zzour.android.base.BaseActivity;
import com.zzour.android.models.ShopSummaryContent;
import com.zzour.android.models.dao.CollectionDAO;
import com.zzour.android.utils.ActivityTool;
import com.zzour.android.utils.ImageTool;
import com.zzour.android.views.adapters.CollectionListItemsAdapter;

public class CollectionActivity extends BaseActivity {
	
	private View mLoadMoreView = null;
	private Button mLoadMoreButton = null;
	
	CollectionListItemsAdapter mAdapterTemp = null;
	private Handler mLoadMoreHandler = new Handler();
	private Handler mCancelHandler = new Handler();
	
	private Handler mHandler = new Handler();
	
	private HashMap<Integer, String> mImages = new HashMap<Integer, String>();
	private ArrayList<Integer> mRemovedShops = new ArrayList<Integer>();
	private boolean deleteStatus = false;
	
	private int maxId = -1;
	private int count = 6;
	
	// TODO re-init list in on resume
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.collection);
		
		ListView list = (ListView)findViewById(R.id.list);
		
        // add load more to list view
        mLoadMoreView = getLayoutInflater().inflate(R.layout.loadmore, null);
        mLoadMoreButton = (Button)mLoadMoreView.findViewById(R.id.loadMoreButton);
        list.addFooterView(mLoadMoreView);
        
        Button button = (Button)findViewById(R.id.cancel_collect);
        button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (mAdapterTemp == null){
					return;
				}
				mAdapterTemp.changeDeleteStatus();
				if (mAdapterTemp.isDeleteStatus()){
					removeListItemClickListener();
					((Button)v).setText("完  成");
					// set remove id to empty
					mRemovedShops.clear();
					deleteStatus = true;
				} else {
					addListItemClickListener();
					((Button)v).setText("取消收藏");
					// check removed items, and remove from db
					if (mRemovedShops.size() > 0){
						CollectionDAO dao = new CollectionDAO(CollectionActivity.this);
						for (int i=0; i < mRemovedShops.size(); i++){
							dao.delete(mRemovedShops.get(i));
						}
					}
					deleteStatus = false;
				}
				mCancelHandler.post(new Runnable(){
 	    		   public void run(){
 	    			   mAdapterTemp.notifyDataSetChanged();
 	    		   }
 	    	   });
			}
		});
        
        // init adapter and set scroll view to list.
        mAdapterTemp = new CollectionListItemsAdapter(this, R.drawable.logo);
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
	
    private void loadMoreData(){
    	// load more from local storage
    	CollectionDAO dao = new CollectionDAO(CollectionActivity.this);
    	ArrayList<ShopSummaryContent> shops = dao.get(maxId, count);
    	if (shops.size() == 0){
    		if (maxId == -1){
    			mLoadMoreHandler.post(new Runnable(){
    	     		   public void run(){
    	     			   Toast.makeText(CollectionActivity.this, "还没有收藏，赶快添加吧", Toast.LENGTH_SHORT).show();
    	     		   }
    	 	    	});
    	     		return;
    		} else {
    			mLoadMoreHandler.post(new Runnable(){
 	     		   public void run(){
 	     			   Toast.makeText(CollectionActivity.this, "没有更多数据了", Toast.LENGTH_SHORT).show();
 	     		   }
 	 	    	});
 	     		return;
    		}
    	}
    	for (int i=0; i < shops.size(); i++){
    		int p = mAdapterTemp.addItem(shops.get(i));
    		mImages.put(p, shops.get(i).getLogo());
    		if (i == shops.size() - 1){
    			// set max id
    			maxId = shops.get(i).getOrder();
    		}
    	}
    	
    	// load images
    	Iterator<Integer> it = mImages.keySet().iterator();
		final int width = (int)getResources().getDimension(R.dimen.list_image_width);
		final int height = (int)getResources().getDimension(R.dimen.list_image_height);
    	while (it.hasNext()){
    		final int position = (Integer)it.next();
    		final Bitmap bmp = ImageTool.getBitmapByUrl(mImages.get(position), width, height, CollectionActivity.this);
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
    
    public void addRemovedShop(int shopId){
    	mRemovedShops.add(shopId);
    }
    
    private void addListItemClickListener(){
    	ListView lv = (ListView)findViewById(R.id.list);
    	lv.setOnItemClickListener(new OnItemClickListener()
    	{
    	    @Override 
    	    public void onItemClick(AdapterView<?> arg0, View arg1,int position, long arg3)
    	    { 
    	    	try {
					//Toast.makeText(MainActivity.this, "" + position, Toast.LENGTH_SHORT).show();
					if (mAdapterTemp == null){
						Log.e(TAG, "list adapter is null, something happende");
						return;
					}
					ShopSummaryContent shop = mAdapterTemp.getShopSummaryAtPosition(position);
					if (shop == null){
						Log.e(TAG, "strange! shop can't be found in list adapter! at position: " + (position-1));
						return;
					}
					Intent intent = new Intent(CollectionActivity.this, ShopDetailActivity.class);
					intent.putExtra("shop_id", shop.getId());
					ActivityTool.startActivity(CollectionActivity.this, ShopDetailActivity.class, intent);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
    	    }
    	});
    }
    
    private void removeListItemClickListener(){
    	ListView lv = (ListView)findViewById(R.id.list);
    	lv.setOnItemClickListener(null);
    }
    
    Runnable initList = new Runnable(){
		@Override
		public void run() {
			loadMoreData();
			mHandler.post(new Runnable(){
				public void run(){
					mAdapterTemp.notifyDataSetChanged();
				}
			});
		}
    };
}