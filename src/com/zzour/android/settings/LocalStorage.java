package com.zzour.android.settings;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;

public class LocalStorage {
	
	private static boolean mExternalStorageAvailable = false;
	private static boolean mExternalStorageWriteable = false;
	
	private static void checkExternalStorage(){
		String state = Environment.getExternalStorageState();

		if (Environment.MEDIA_MOUNTED.equals(state)) {
		    // We can read and write the media
		    mExternalStorageAvailable = mExternalStorageWriteable = true;
		} else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
		    // We can only read the media
		    mExternalStorageAvailable = true;
		    mExternalStorageWriteable = false;
		} else {
		    // Something else is wrong. It may be one of many other states, but all we need
		    //  to know is we can neither read nor write
		    mExternalStorageAvailable = mExternalStorageWriteable = false;
		}
	}
	
	private static File getExternalFilesDir(Context context){
		if (GlobalSettings.getApiLevel() >= 8){
			Log.d("ZZOUR", "api level greater than 8");
			return context.getExternalCacheDir();
		} else {
			Log.d("ZZOUR", "api level lower than 8");
			// create path
			File f = Environment.getDownloadCacheDirectory();
			// TODO test this
			File dir =  new File(f.getAbsolutePath(), "/zzour/files/");
			dir.mkdirs();
			Log.d("ZZOUR", "cache path " + dir.toString());
			return dir;
		}
	}
	
	public static void saveImage(String name, Bitmap bmp, Context context){
		// save bitmap file to local storage
		checkExternalStorage();
		if (!mExternalStorageWriteable){
			// not writable, return directly.
			return;
		}
		try{
			File f = getExternalFilesDir(context);
			File file = new File(f, name.replace("-", "a"));
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			// TODO all compress as png?
			bmp.compress(CompressFormat.PNG, 0 /*ignored for PNG*/, bos);
			byte[] bitmapdata = bos.toByteArray();
			OutputStream os = new FileOutputStream(file);
			os.write(bitmapdata);
			bos.close();
			os.close();
		} catch (IOException e){
			Log.e("ZZOUR", "Error write external storage.", e);
		}
	}
	
	public static Bitmap getImage(String name, Context context){
		checkExternalStorage();
		if (!mExternalStorageAvailable){
			return null;
		}
		try{
			File f = getExternalFilesDir(context);
			File file = new File(f, name.replace("-", "a"));
			if (!file.exists()){
				return null;
			}
			Bitmap bmp = BitmapFactory.decodeStream(new FileInputStream(file));
			return bmp;
		} catch (IOException e){
			Log.e("ZZOUR", "Error read external storage.", e);
		}
		return null;
	}
}
