package com.zzour.android.views.tab;

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
		// tab view to hold all tabs
		// TODO can this be reused?
		tabView = new TabView(context);
		tabView.setOrientation(TabView.Orientation.BOTTOM);
		// what does it do with a xml? in resource is a defined shape.
		tabView.setBackgroundID(R.drawable.tab_background_gradient);
		
		// new gradient drawable
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

	    // new home tab
	    // TODO where use category? also set button text bellow
		homeTab = new Tab(context, category);
		// home sel is a white icon with transparent background
		homeTab.setIcon(R.drawable.home_sel);
		// set icon selected, it is the same with icon.
		homeTab.setIconSelected(R.drawable.home_sel);
		// set button text? I don't this has been shown
		homeTab.setBtnText(this.context.getResources().getString(R.string.order_tab_text));
		// set button text color
		homeTab.setBtnTextColor(Color.WHITE);
		// set selected button text color
		homeTab.setSelectedBtnTextColor(Color.BLACK);
		// set button gradient, use gradient for button background
		homeTab.setBtnGradient(transGradientDrawable);
		// set selected gradient button background
		homeTab.setSelectedBtnGradient(gradientDrawable);
		// set intent
		homeTab.setIntent(new Intent(context, MainActivity.class), MainActivity.class);

		// it's all the same
		contactTab = new Tab(context, category);
		contactTab.setIcon(R.drawable.menu_sel);
		contactTab.setIconSelected(R.drawable.menu_sel);
		contactTab.setBtnText(this.context.getResources().getString(R.string.store_tab_text));
		contactTab.setBtnTextColor(Color.WHITE);
		contactTab.setSelectedBtnTextColor(Color.BLACK);
		contactTab.setBtnGradient(transGradientDrawable);
		contactTab.setSelectedBtnGradient(gradientDrawable);
		contactTab.setIntent(new Intent(context, ContactActivity.class), ContactActivity.class);

		shareTab = new Tab(context, category);
		shareTab.setIcon(R.drawable.home_sel);
		shareTab.setIconSelected(R.drawable.home_sel);
		shareTab.setBtnText(this.context.getResources().getString(R.string.my_order_tab_text));
		shareTab.setBtnTextColor(Color.WHITE);
		shareTab.setSelectedBtnTextColor(Color.BLACK);
		shareTab.setBtnGradient(transGradientDrawable);
		shareTab.setSelectedBtnGradient(gradientDrawable);
		shareTab.setIntent(new Intent(context, ShareActivity.class), ShareActivity.class);
		
		moreTab = new Tab(context, category);
		moreTab.setIcon(R.drawable.more_sel);
		moreTab.setIconSelected(R.drawable.more_sel);
		moreTab.setBtnText(this.context.getResources().getString(R.string.more_tab_text));
		moreTab.setBtnTextColor(Color.WHITE);
		moreTab.setSelectedBtnTextColor(Color.BLACK);
		moreTab.setBtnGradient(transGradientDrawable);
		moreTab.setSelectedBtnGradient(gradientDrawable);
		moreTab.setIntent(new Intent(context, MoreActivity.class), MoreActivity.class);

		// add tabs to tab view.
		tabView.addTab(homeTab);
		tabView.addTab(contactTab);
		tabView.addTab(shareTab);
		tabView.addTab(moreTab);

		return tabView;
	}
}