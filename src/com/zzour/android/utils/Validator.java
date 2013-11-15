package com.zzour.android.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Validator {

	// TODO validate
	public static ValidateResult validUserName(String name){
		// not empty
		if (name == null || name.length() == 0){
			return new ValidateResult(false, "�û�������Ϊ��");
		}
		if (name.length() < 3 || name.length() > 15){
			return new ValidateResult(false, "�û���������3��15���ַ�");
		}
		return new ValidateResult(true, "");
	}
	
	public static ValidateResult validPassword(String password){
		// not empty
		if (password == null || password.length() == 0){
			return new ValidateResult(false, "���벻��Ϊ��");
		}
		// length between 6 to 20 characters
		if (password.length() > 20 || password.length() < 6){
			return new ValidateResult(false, "���벻����������ӦΪ6-20���ַ�������������");
		}
		return new ValidateResult(true, "");
	}
	
	public static ValidateResult validMailAddress(String mail){
		// not empty
		if (mail == null || mail.length() == 0){
			return new ValidateResult(false, "���䲻��Ϊ��");
		}
		String check = "^\\s*\\w+(?:\\.{0,1}[\\w-]+)*@[a-zA-Z0-9]+(?:[-.][a-zA-Z0-9]+)*\\.[a-zA-Z]+\\s*$";
		Pattern regex = Pattern.compile(check);
		Matcher matcher = regex.matcher(mail);
		boolean valid = matcher.matches();
		if (!valid){
			return new ValidateResult(false, "�����ʽ����ȷ");
		}
		return new ValidateResult(true, "");
	}
	
	public static class ValidateResult{
		private boolean success;
		private String msg;
		public ValidateResult(boolean suc, String message){
			this.success = suc;
			this.msg = message;
		}
		public boolean isSuccess() {
			return success;
		}
		public void setSuccess(boolean success) {
			this.success = success;
		}
		public String getMsg() {
			return msg;
		}
		public void setMsg(String msg) {
			this.msg = msg;
		}
	}
}
