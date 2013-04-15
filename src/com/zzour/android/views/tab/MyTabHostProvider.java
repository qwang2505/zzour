package com.zzour.android.views.tab;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;

import com.zzour.andoird.base.BaseActivity;
import com.zzour.android.ContactActivity;
import com.zzour.android.MainActivity;
import com.zzour.android.MoreActivity;
import com.zzour.android.R;
import com.zzour.android.ShareActivity;

public class MyTabHostProvider extends TabHostProvider 
{
	private Tab homeTab;
	private Tab contactTab;
	private Tab shareTab;
	private Tab moreTab;
	
	private TabView tabView;
	private GradientDrawable gradientDrawable, transGradientDrawable;

	public MyTabHostProvider(BaseActivity context) {
		super(context);
	}

	@Override
	public TabView getTabHost(String category) 
	{
		tabView = new TabView(context);
		tabView.setOrientation(TabView.Orientation.BOTTOM);
		// what does it do with a xml?
		tabView.setBackgroundID(R.drawable.tab_background_gradient);
		
		gradientDrawable = new GradientDrawable(
	            GradientDrawable.Orientation.TOP_BOTTOM,
	            new int[] {0xFFB2DA1D, 0xFF85A315});
	    gradientDrawable.setCornerRadius(0f);
	    gradientDrawable.setDither(true);
	    
	    transGradientDrawable = new GradientDrawable(
	            GradientDrawable.Orientation.TOP_BOTTOM,
	            new int[] {0x00000000, 0x00000000});
	    transGradientDrawable.setCornerRadius(0f);
	    transGradientDrawable.setDither(true);

		homeTab = new Tab(context, category);
		homeTab.setIcon(R.drawable.home_sel);
		homeTab.setIconSelected(R.drawable.home_sel);
		homeTab.setBtnText("Home");
		homeTab.setBtnTextColor(Color.WHITE);
		homeTab.setSelectedBtnTextColor(Color.BLACK);
//		homeTab.setBtnColor(Color.parseColor("#00000000"));
//		homeTab.setSelectedBtnColor(Color.parseColor("#0000FF"));
		homeTab.setBtnGradient(transGradientDrawable);
		homeTab.setSelectedBtnGradient(gradientDrawable);
		homeTab.setIntent(new Intent(context, MainActivity.class), MainActivity.class);

		contactTab = new Tab(context, category);
		contactTab.setIcon(R.drawable.menu_sel);
		contactTab.setIconSelected(R.drawable.menu_sel);
		contactTab.setBtnText("Contact");
		contactTab.setBtnTextColor(Color.WHITE);
		contactTab.setSelectedBtnTextColor(Color.BLACK);
//		contactTab.setBtnColor(Color.parseColor("#00000000"));
//		contactTab.setSelectedBtnColor(Color.parseColor("#0000FF"));
		contactTab.setBtnGradient(transGradientDrawable);
		contactTab.setSelectedBtnGradient(gradientDrawable);
		contactTab.setIntent(new Intent(context, ContactActivity.class), ContactActivity.class);

		shareTab = new Tab(context, category);
		shareTab.setIcon(R.drawable.home_sel);
		shareTab.setIconSelected(R.drawable.home_sel);
		shareTab.setBtnText("Share");
		shareTab.setBtnTextColor(Color.WHITE);
		shareTab.setSelectedBtnTextColor(Color.BLACK);
//		shareTab.setBtnColor(Color.parseColor("#00000000"));
//		shareTab.setSelectedBtnColor(Color.parseColor("#0000FF"));
		shareTab.setBtnGradient(transGradientDrawable);
		shareTab.setSelectedBtnGradient(gradientDrawable);
		shareTab.setIntent(new Intent(context, ShareActivity.class), ShareActivity.class);
		
		moreTab = new Tab(context, category);
		moreTab.setIcon(R.drawable.more_sel);
		moreTab.setIconSelected(R.drawable.more_sel);
		moreTab.setBtnText("More");
		moreTab.setBtnTextColor(Color.WHITE);
		moreTab.setSelectedBtnTextColor(Color.BLACK);
//		moreTab.setBtnColor(Color.parseColor("#00000000"));
//		moreTab.setSelectedBtnColor(Color.parseColor("#0000FF"));
		moreTab.setBtnGradient(transGradientDrawable);
		moreTab.setSelectedBtnGradient(gradientDrawable);
		moreTab.setIntent(new Intent(context, MoreActivity.class), MoreActivity.class);

		tabView.addTab(homeTab);
		tabView.addTab(contactTab);
		tabView.addTab(shareTab);
		tabView.addTab(moreTab);

		return tabView;
	}
}