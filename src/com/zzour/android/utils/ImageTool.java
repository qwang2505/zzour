package com.zzour.android.utils;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;

public class ImageTool {

	public static Bitmap getBitmapByUrl(String src){
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
	
	public static Bitmap getBitmapByUrl(String src, int width, int height){
		Bitmap bmp = getBitmapByUrl(src);
		if (bmp == null){
			return null;
		}
		return scaleImage(bmp, width, height);
	}
	
	public static Bitmap getBitmapByStream(InputStream input, int width, int height){
		Bitmap bmp = BitmapFactory.decodeStream(input);
		return scaleImage(bmp, width, height);
	}
	
	public static Bitmap scaleImage(Bitmap bmp, int width, int height){
    	int w = bmp.getWidth();
    	int h = bmp.getHeight();
    	float scaleW = ((float)width) / w;
    	float scaleH = ((float)height) / h;
    	Matrix m = new Matrix();
    	m.postScale(scaleW, scaleH);
    	Bitmap newBmp = Bitmap.createBitmap(bmp, 0, 0, w, h, m, true);
    	return newBmp;
    }
}
