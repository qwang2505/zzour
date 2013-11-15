package com.zzour.android.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Validator {

	// TODO validate
	public static ValidateResult validUserName(String name){
		// not empty
		if (name == null || name.length() == 0){
			return new ValidateResult(false, "用户名不能为空");
		}
		if (name.length() < 3 || name.length() > 15){
			return new ValidateResult(false, "用户名必须是3到15个字符");
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
		String check = "^\\s*\\w+(?:\\.{0,1}[\\w-]+)*@[a-zA-Z0-9]+(?:[-.][a-zA-Z0-9]+)*\\.[a-zA-Z]+\\s*$";
		Pattern regex = Pattern.compile(check);
		Matcher matcher = regex.matcher(mail);
		boolean valid = matcher.matches();
		if (!valid){
			return new ValidateResult(false, "邮箱格式不正确");
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
