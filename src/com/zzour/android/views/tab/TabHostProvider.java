package com.zzour.android.views.tab;

import com.zzour.andoird.base.BaseActivity;

import android.app.Activity;

// Actually, i don't know why this exists...
public abstract class TabHostProvider {
	public BaseActivity context;

	public TabHostProvider(BaseActivity context) {
		this.context = context;
	}

	// get tab host by tab name? what's category?
	// I think the category is a tab name.
	public abstract TabView getTabHost(String category);
}