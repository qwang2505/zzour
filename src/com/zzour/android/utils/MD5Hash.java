package com.zzour.android.utils;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5Hash {
	
	private static String salt = "diordnaruozz";
	
	public static String md5(String input){
		String md5 = null;
        if(null == input) return null;
        try {
		    //Create MessageDigest object for MD5
		    MessageDigest digest = MessageDigest.getInstance("MD5");
		    //Update input string in message digest
		    //input += salt;
		    digest.update(input.getBytes(), 0, input.length());
		    //Converts message digest value in base 16 (hex) 
		    md5 = new BigInteger(1, digest.digest()).toString(16);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return md5;
	}
	
	public static String md5For8Bit(String input){
		String md5 = null;
        if(null == input) return null;
        try {
		    //Create MessageDigest object for MD5
		    MessageDigest digest = MessageDigest.getInstance("MD5");
		    digest.update(input.getBytes(), 0, input.length());
		    //Converts message digest value in base 16 (hex) 
		    md5 = new BigInteger(1, digest.digest()).toString(16);
		    md5 = md5.substring(8, 24);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return md5;
	}
}
