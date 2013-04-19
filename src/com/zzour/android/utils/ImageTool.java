package com.zzour.android.utils;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;

public class ImageTool {
	
	private static final String TAG = "ZZOUR";
	private static HashMap<Integer, Bitmap> mBitmapCache = new HashMap<Integer, Bitmap>();
	
	public static Bitmap getBitmapByUrl(String src, int width, int height){
		int key = (new String("src" + src + width + height)).hashCode();
		if (mBitmapCache.containsKey(key)){
			return mBitmapCache.get(key);
		}
		Bitmap bmp = getBitmapByUrl(src);
		if (bmp == null){
			return null;
		}
		bmp = scaleImage(bmp, width, height);
		mBitmapCache.put(key, bmp);
		return bmp;
	}
	
	public static Bitmap getBitmapByStream(int resourceId, InputStream input, int width, int height){
		// get key by resource id, width and height
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
	        return null;
	    }
	}
}
