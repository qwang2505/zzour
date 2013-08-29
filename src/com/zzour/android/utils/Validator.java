package com.zzour.android.utils;

public class Validator {

	// TODO validate
	public static ValidateResult validUserName(String name){
		// not empty
		if (name == null || name.length() == 0){
			return new ValidateResult(false, "用户名不能为空");
		}
		return new ValidateResult(true, "");
	}
	
	public static ValidateResult validPassword(String password){
		// not empty
		if (password == null || password.length() == 0){
			return new ValidateResult(false, "密码不能为空");
		}
		// length between 6 to 20 characters
		if (password.length() > 20 || password.length() < 6){
			return new ValidateResult(false, "密码不符合条件，应为6-20个字符，请重新输入");
		}
		return new ValidateResult(true, "");
	}
	
	public static ValidateResult validMailAddress(String mail){
		// not empty
		if (mail == null || mail.length() == 0){
			return new ValidateResult(false, "邮箱不能为空");
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
