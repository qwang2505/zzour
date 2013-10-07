package com.zzour.android.utils;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

import com.zzour.android.settings.LocalStorage;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.util.Log;

public class ImageTool {
	
	private static final String TAG = "ZZOUR";
	private static HashMap<Integer, Bitmap> mBitmapCache = new HashMap<Integer, Bitmap>();
	
	public static Bitmap cachedImage(String src, Context context){
		Bitmap bmp = LocalStorage.getImage(String.valueOf(src.hashCode()), context);
		return bmp;
	}
	
	public static Bitmap getBitmapByUrl(String src, int width, int height, Context context){
		// load image from memory
		int key = (new String("src" + src + width + height)).hashCode();
		if (mBitmapCache.containsKey(key)){
			Log.e("ZZOUR", "get image from memory cache");
			return mBitmapCache.get(key);
		}
		// load image from local storage by src as key
		Bitmap bmp = LocalStorage.getImage(String.valueOf(src.hashCode()), context);
		if (bmp == null){
			// if not in local storage, get from internet
			Log.e("ZZOUR", "download image from internet");
			bmp = getBitmapByUrl(src);
			// if can't get from internet, return
			if (bmp == null){
				return null;
			}
			LocalStorage.saveImage(String.valueOf(src.hashCode()), bmp, context);
		}
		// scale image
		bmp = scaleImage(bmp, width, height);
		mBitmapCache.put(key, bmp);
		return bmp;
	}
	
	public static Bitmap getBitmapByStream(int resourceId, InputStream input, int width, int height){
		// get key by resource id, width and height
		// do not need load image, so do not save to local storage
		int key = (new String("resource" + resourceId + width + height)).hashCode();
		if (mBitmapCache.containsKey(key)){
			return mBitmapCache.get(key);
		}
		Bitmap bmp = BitmapFactory.decodeStream(input);
		bmp = scaleImage(bmp, width, height);
		mBitmapCache.put(key, bmp);
		return bmp;
	}
	
	private static Bitmap scaleImage(Bitmap bmp, int width, int height){
    	int w = bmp.getWidth();
    	int h = bmp.getHeight();
    	float scaleW = ((float)width) / w;
    	float scaleH = ((float)height) / h;
    	Matrix m = new Matrix();
    	m.postScale(scaleW, scaleH);
    	Bitmap newBmp = Bitmap.createBitmap(bmp, 0, 0, w, h, m, true);
    	return newBmp;
    }
	
	private static Bitmap getBitmapByUrl(String src){
		try {
	        URL url = new URL(src);
	        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
	        connection.setDoInput(true);
	        connection.connect();
	        InputStream input = connection.getInputStream();
	        Bitmap bmp = BitmapFactory.decodeStream(input);
	        return bmp;
	    } catch (IOException e) {
	        e.printStackTrace();
	        Log.e(TAG, "get bitmap by url failed: " + src);
	        return null;
	    }
	}
}
