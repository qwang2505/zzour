package com.zzour.android;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Timer;
import java.util.TimerTask;

import com.zzour.android.R;
import com.zzour.android.utils.ImageTool;
import com.zzour.android.views.HorizontalImageScrollView;
import com.zzour.android.views.adapters.ListItemsAdapter;

import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.AdapterView.OnItemSelectedListener;

public class MainActivity extends Activity {
	
	final static String TAG = "ZZOUR";
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
	private Handler mHandler = null;
	
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
        String text = new String("牛肉面");
        searchText.setText(text);
        
        this.initScrollImageView();
        
        mHandler = new Handler();
        new Thread(initList).start();
    }
    
    private void initScrollImageView(){
        // TODO get images and set to HorizontalImageScrollView
        mImageScrollView = (HorizontalImageScrollView)findViewById(R.id.image_scroll);
        // get device size, and calculate image size.
        DisplayMetrics metrics = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int width = metrics.widthPixels - 10;
        int height = 160;
        
        ArrayList<Bitmap> items = new ArrayList<Bitmap>();
        Bitmap bmp = BitmapFactory.decodeStream(getResources().openRawResource(R.drawable.scroll_image_1));
    	Bitmap newBmp = ImageTool.scaleImage(bmp, width, height);
    	items.add(newBmp);
    	bmp = BitmapFactory.decodeStream(getResources().openRawResource(R.drawable.scroll_image_2));
    	newBmp = ImageTool.scaleImage(bmp, width, height);
    	items.add(newBmp);
    	mImageScrollView.setFeatureItems(items);
    	mScreenWidth = metrics.widthPixels;
    	// start auto scrolling
    	this.startAutoScrolling();
    }
    
    public void startAutoScrolling(){
        if (mScrollTimer == null) {
            mScrollTimer =   new Timer();
            final Runnable Timer_Tick = new Runnable() {
                public void run() {
                    moveScrollView();
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

    private void moveScrollView(){
    	if (mCurrentImageIndex >= mMaxImageIndex){
    		mCurrentImageIndex = 0;
    	}else{
    		mCurrentImageIndex++;
    	}
    	
    	int scrollPos = mCurrentImageIndex * mScreenWidth;
    	mImageScrollView.smoothScrollTo(scrollPos, 0);
    }
    
    Runnable updateList = new Runnable(){
    	public void run(){
    		ListView list = (ListView)findViewById(R.id.list);
    		list.setAdapter(mAdapterTemp);
    	}
    };
    
    Runnable initList = new Runnable(){

		@Override
		public void run() {
			initList();
			mHandler.post(updateList);
		}
    	
    };
    
    private void initList(){
    	ListView list = (ListView)findViewById(R.id.list);
    	
    	String[] titles = new String[]{
    			"first item",
    			"second item",
    			"third item",
    			"i hope you not bored",
    			"but i am",
    			"we need more",
    			"ok last one"
    	};
    	String[] descs = new String[]{
    			"i don't know what to say, i hate this complicated example, but... deal with it.",
    			"i don't know what to say, i hate this complicated example, but... deal with it.",
    			"i don't know what to say, i hate this complicated example, but... deal with it.",
    			"i don't know what to say, i hate this complicated example, but... deal with it.",
    			"i don't know what to say, i hate this complicated example, but... deal with it.",
    			"i don't know what to say, i hate this complicated example, but... deal with it.",
    			"i don't know what to say, i hate this complicated example, but... deal with it."
    	};
    	String[] images = new String[]{
    			"http://www.zzour.com/data/files/store_8/other/store_logo.jpg",
    			"http://www.zzour.com/data/files/store_14/other/store_logo.jpg",
    			"http://www.zzour.com/data/files/store_10/other/store_logo.jpg",
    			"http://www.zzour.com/data/files/store_7/other/store_logo.jpg",
    			"http://www.zzour.com/data/files/store_15/other/store_logo.jpg",
    			"http://www.zzour.com/data/files/store_11/other/store_logo.jpg",
    			"http://www.zzour.com/data/files/store_16/other/store_logo.jpg",
    	};
    	
    	mAdapterTemp = new ListItemsAdapter(this);
    	for (int i=0; i < titles.length; i++){
    		Bitmap b = ImageTool.getBitmapByUrl(images[i], 100, 100);
    		if (b == null){
    			continue;
    		}
    		mAdapterTemp.addItem(titles[i], descs[i], b);
    	}
    	//list.setAdapter(adapter);
    }
}
