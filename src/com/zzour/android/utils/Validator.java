package com.zzour.android.utils;

public class Validator {

	// TODO validate
	public static ValidateResult validUserName(String name){
		// not empty
		if (name == null || name.length() == 0){
			return new ValidateResult(false, "�û�������Ϊ��");
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
