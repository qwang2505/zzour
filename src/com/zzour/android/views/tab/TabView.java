package com.zzour.android.views.tab;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;

public class TabView {
	private final List<Tab> tabSet = new ArrayList<Tab>();
	private int mHeaderHeight;
	private final Context context;
	private View currentView;

	public enum Orientation {
		TOP, BOTTOM
	};

	private Orientation orientation;
	private int backgroundID;
	private int selectedTabId = 0;

	public TabView(Context context) {
		this.context = context;
	}

	public void addTab(Tab tab) {
		tab.preferedHeight = mHeaderHeight;
		tabSet.add(tab);
	}

	public View render(int selectedTabId) {
		this.selectedTabId = selectedTabId;
		switch (orientation) {
		case TOP:
			return renderTOP();

		case BOTTOM:
			return renderBOTTOM();

		}
		return null;
	}

	public View renderBOTTOM() {
		// get tab size
		int tabsize = tabSet.size();

		// new table layout
		TableLayout table = new TableLayout(context);
		// set layout params for table layout
		table.setLayoutParams(new FrameLayout.LayoutParams(
				TableRow.LayoutParams.MATCH_PARENT,TableRow.LayoutParams.MATCH_PARENT));

		// this is the content row
		TableRow rowTop = new TableRow(context);

		TableLayout.LayoutParams rowTopParams = new TableLayout.LayoutParams(
				TableLayout.LayoutParams.MATCH_PARENT,
				TableLayout.LayoutParams.MATCH_PARENT);
		rowTopParams.weight = 1;

		TableRow.LayoutParams spanParams = new TableRow.LayoutParams(
				TableRow.LayoutParams.MATCH_PARENT,
				TableRow.LayoutParams.MATCH_PARENT);
		spanParams.span = tabsize;
		spanParams.weight = 1;

		rowTop.addView(currentView, spanParams);

		// this is the real tab menu
		TableRow rowBottom = new TableRow(context);
		rowBottom.setBackgroundResource(backgroundID);
		TableLayout.LayoutParams rowBottomParams = new TableLayout.LayoutParams(
				TableLayout.LayoutParams.MATCH_PARENT,
				TableLayout.LayoutParams.WRAP_CONTENT);
		
		for (int i = 0; i < tabsize; i++) {
			Tab tab = tabSet.get(i);
			if (i == selectedTabId)
				tab.setSelected(true);
			final View view = tab.getView();
			TableRow.LayoutParams colParams = new TableRow.LayoutParams();
			colParams.weight = 1;
			rowBottom.addView(view, colParams);
		}

		table.addView(rowTop, rowTopParams);
		table.addView(rowBottom, rowBottomParams);

		return table;
	}

	public View renderTOP() {
		int tabsize = tabSet.size();

		FrameLayout.LayoutParams pTable = new FrameLayout.LayoutParams(
				TableRow.LayoutParams.MATCH_PARENT,
				TableRow.LayoutParams.MATCH_PARENT);

		TableLayout table = new TableLayout(context);
		table.setLayoutParams(pTable);

		TableRow rowContent = new TableRow(context);
		TableRow.LayoutParams pRowContent = new TableRow.LayoutParams();
		pRowContent.span = tabsize;
		pRowContent.width = TableRow.LayoutParams.MATCH_PARENT;
		pRowContent.height = TableRow.LayoutParams.WRAP_CONTENT;
		pRowContent.weight = 1;

		ViewGroup.LayoutParams scrollerParams = new ViewGroup.LayoutParams(
				ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.MATCH_PARENT);

		ScrollView scroller = new ScrollView(context);
		scroller.setLayoutParams(scrollerParams);

		scroller.addView(currentView, scrollerParams);
		rowContent.addView(scroller, pRowContent);

		TableRow rowTabs = new TableRow(context);
		rowTabs.setBackgroundResource(backgroundID);

		for (int i = 0; i < tabsize; i++) {
			Tab tab = tabSet.get(i);
			if (i == selectedTabId)
				tab.setSelected(true);
			View view = tab.getView();
			TableRow.LayoutParams pCol = new TableRow.LayoutParams();
			pCol.weight = 1;
			rowTabs.addView(view, pCol);
		}

		TableRow.LayoutParams pRowTabs = new TableRow.LayoutParams();
		pRowTabs.height = TableRow.LayoutParams.WRAP_CONTENT;
		pRowTabs.weight = 1;

		table.addView(rowTabs, pRowTabs);
		table.addView(rowContent);

		return table;
	}

	public void setCurrentView(View currentView) {
		this.currentView = currentView;
	}

	public void setCurrentView(int resourceViewID) {
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(resourceViewID, null);
		setCurrentView(view);
	}

	// more like set position, bottom of screen or bottom
	public void setOrientation(Orientation orientation) {
		this.orientation = orientation;
	}

	public void setBackgroundID(int backgroundID) {
		// background resource id.
		this.backgroundID = backgroundID;
	}

	public Tab getTab(String tag) {
		for (int i = 0; i < tabSet.size(); i++) {
			Tab t = tabSet.get(i);
			if (tag.equals(t.getTag())) {
				return t;
			}
		}
		throw new IllegalArgumentException("Tab \"" + tag + "\" not found");
	}
}