package com.zzour.android.network.api.results;

import com.zzour.android.models.UserAccount;

public class UserAccountResult extends ApiResult {
	private UserAccount account = null;

	public UserAccount getAccount() {
		return account;
	}

	public void setAccount(UserAccount account) {
		this.account = account;
	}
}
