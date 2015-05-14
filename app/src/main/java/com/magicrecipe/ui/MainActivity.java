package com.magicrecipe.ui;

import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.magicrecipe.constants.Constants;
import com.magicrecipe.database.RecipeDB;
import com.magicrecipe.tabs.HistoryFragment;
import com.magicrecipe.tabs.SlidingTabLayout;
import com.magicrecipe.tabs.ViewPagerAdapter;
import com.puppy.magicrecipe.R;

public class MainActivity extends ActionBarActivity {
	Toolbar toolbar;
	ViewPager pager;
	ViewPagerAdapter viewPagerAdapter;
	SlidingTabLayout tabs;
	CharSequence Titles[]={"History","Search","Favorites"};
	int Numboftabs =3;


	private AdView mAdView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// Creating The Toolbar and setting it as the Toolbar for the activity

		toolbar = (Toolbar) findViewById(R.id.tool_bar);
		toolbar.setTitle("");
		toolbar.setLogo(R.drawable.ic_launcher);
		toolbar.inflateMenu(R.menu.menu_main);
		setSupportActionBar(toolbar);


		// Creating The ViewPagerAdapter and Passing Fragment Manager, Titles fot the Tabs and Number Of Tabs.
		viewPagerAdapter =  new ViewPagerAdapter(getSupportFragmentManager(),Titles,Numboftabs);

		// Assigning ViewPager View and setting the adapter
		pager = (ViewPager) findViewById(R.id.pager);
		pager.setAdapter(viewPagerAdapter);
		pager.setCurrentItem(1);

		// Assiging the Sliding Tab Layout View
		tabs = (SlidingTabLayout) findViewById(R.id.tabs);
		tabs.setContext(this);
		tabs.setDistributeEvenly(true); // To make the Tabs Fixed set this true, This makes the tabs Space Evenly in Available width

		// Setting Custom Color for the Scroll bar indicator of the Tab View
		tabs.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
			@Override
			public int getIndicatorColor(int position) {
				return getResources().getColor(R.color.tabsScrollColor);
			}
		});

		// Setting the ViewPager For the SlidingTabsLayout
		tabs.setViewPager(pager);


//
		mAdView = (AdView) findViewById(R.id.adView);
//		AdRequest adRequest = new AdRequest.Builder().addTestDevice(
//				AdRequest.DEVICE_ID_EMULATOR).build();
		AdRequest adRequest = new AdRequest.Builder().build();
		mAdView.loadAd(adRequest);
	}

	/** Called when leaving the activity */
	@Override
	public void onPause() {
		if (mAdView != null) {
			mAdView.pause();
		}
		super.onPause();
	}

	/** Called when returning to the activity */
	@Override
	public void onResume() {
		super.onResume();
		if (mAdView != null) {
			mAdView.resume();
		}
	}

	/** Called before the activity is destroyed */
	@Override
	public void onDestroy() {
		if (mAdView != null) {
			mAdView.destroy();
		}
		super.onDestroy();
	}
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		// TODO Auto-generated method stub
		super.onConfigurationChanged(newConfig);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the tool bar if it is present.
		getMenuInflater().inflate(R.menu.menu_main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent intent = new Intent(Constants.LIST_UPDATE_NOTIFIER);
		switch (item.getItemId()) {
			case R.id.clear_history:
				RecipeDB.getInstance(this).clearHistory();
				intent.putExtra(Constants.UPDATE_NOTIFIER_KEY, Constants.MENU_CLEAR_HISTORY);
				LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
				return true;
			case R.id.clear_favorites:
				RecipeDB.getInstance(this).clearFavorites();
				intent.putExtra(Constants.UPDATE_NOTIFIER_KEY, Constants.MENU_CLEAR_FAVORITES);
				LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}
}
