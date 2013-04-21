package com.medisa.myspacecal;

/*
 * Copyright 2012 Roman Nurik + Nick Butcher
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.util.ArrayList;
import java.util.Calendar;

import org.json.JSONArray;
import org.json.JSONException;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Dialog;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.GridView;
import android.widget.ImageView;

import com.devspark.sidenavigation.ISideNavigationCallback;
import com.devspark.sidenavigation.SideNavigationView;
import com.devspark.sidenavigation.SideNavigationView.Mode;
import com.devspark.sidenavigation.sample.R;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.medisa.myspacecal.MainActivity.GridCellAdapter;

/**
 * Demo activity for quick return + sticky views in scroll containers.
 */
public class Range extends FragmentActivity implements ActionBar.TabListener, ISideNavigationCallback, OnClickListener {
    ViewPager mPager;
    public static final String EXTRA_TITLE = "com.devspark.sidenavigation.sample.extra.MTGOBJECT";
    public static final String EXTRA_RESOURCE_ID = "com.devspark.sidenavigation.sample.extra.RESOURCE_ID";
    public static final String EXTRA_MODE = "com.devspark.sidenavigation.sample.extra.MODE";
    private static final String tag = "Main";
	private Button selectedDayMonthYearButton;
	private Button currentMonth;
	private ImageView prevMonth;
	private ImageView nextMonth;
	private GridView calendarView;
	private GridCellAdapter adapter;
	private Calendar _calendar;
	private int month, year;
	private final DateFormat dateFormatter = new DateFormat();
	private static final String dateTemplate = "MMMM yyyy";
    private String dateStart;
    private String dateEnd;
    private byte range=0;
    Context ctx;
    ArrayList<SatelliteIntegral> satellitiIntegral;
    
    private SideNavigationView sideNavigationView;
    @Override
    public boolean onOptionsItemSelected(android.view.MenuItem item) {
    	// TODO Auto-generated method stub
    	switch (item.getItemId()) {
        case android.R.id.home:
            sideNavigationView.toggleMenu();
            break;
//        case R.id.mode_left:
//            item.setChecked(true);
//            sideNavigationView.setMode(Mode.LEFT);
//            break;
//        case R.id.mode_right:
//            item.setChecked(true);
//            sideNavigationView.setMode(Mode.RIGHT);
//            break;
//
        default:
            return super.onOptionsItemSelected(item);
    }
    	return super.onOptionsItemSelected(item);
    }
    @Override
    public void onBackPressed() {
        // hide menu if it shown
        if (sideNavigationView.isShown()) {
            sideNavigationView.hideMenu();
        } else {
            super.onBackPressed();
        }
    }
    @SuppressLint("NewApi")
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_range);
        sideNavigationView = (SideNavigationView) findViewById(R.id.side_navigation_view);
        sideNavigationView.setMenuItems(R.menu.side_navigation_menu);
        sideNavigationView.setMenuClickCallback(this);
        
        PagerAdapter adapter = new FragmentStatePagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                switch (position) {
                    case 0:
                        return new StickyFragment();  
                }
                return null;
            }

            @Override
            public int getCount() {
                return 1;
            }

            @Override
            public CharSequence getPageTitle(int position) {
                switch (position) {
                    case 0:
                        return getString(R.string.sticky_item);
                }
                return null;
            }
        };

        mPager = (ViewPager) findViewById(R.id.pager);
        mPager.setAdapter(adapter);
        mPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @SuppressLint("NewApi")
			@Override
            public void onPageSelected(int position) {
                getActionBar().setHomeButtonEnabled(true);
            }
        });

        mPager.setPageMargin(getResources().getDimensionPixelSize(R.dimen.page_margin));

        getActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);

        for (int position = 0; position < adapter.getCount(); position++) {
            getActionBar().addTab(getActionBar().newTab()
                    .setText(adapter.getPageTitle(position))
                    .setTabListener(this));
        }

        getActionBar().setDisplayShowHomeEnabled(true);
        getActionBar().setDisplayShowTitleEnabled(true);
        getActionBar().setHomeButtonEnabled(true);
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        mPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
	}
	@Override
    public void onSideNavigationItemClick(int itemId) {
        switch (itemId) {
            case R.id.side_navigation_menu_calendario:   
            	range=0;
            	Intent i = new Intent(this, MainActivity.class);
            	startActivity(i);
                break;
            case R.id.side_navigation_menu_date_range:
            	// custom dialog
    			final Dialog dialog = new Dialog(this);
    			dialog.setContentView(R.layout.custom_range_date);
    			dialog.setTitle("Data range");
     
    			// set the custom dialog components - text, image and button
    			final DatePicker dpStart =(DatePicker)dialog.findViewById(R.id.dpStart);
    			final DatePicker dpEnd =(DatePicker)dialog.findViewById(R.id.dpEnd);
    			Button btnSalva =(Button)dialog.findViewById(R.id.btnSalva);
    			final CheckBox cbIntegralBox=(CheckBox)dialog.findViewById(R.id.cbIntegral);
    			// if button is clicked, close the custom dialog
    			btnSalva.setOnClickListener(new OnClickListener() {
    				@Override
    				public void onClick(View v) {
    					dateStart=Funzioni.addZero(dpStart.getDayOfMonth(), 2)+"-"+Funzioni.addZero(dpStart.getMonth(), 2)+"-"+dpStart.getYear();
    					dateEnd=Funzioni.addZero(dpEnd.getDayOfMonth(), 2)+"-"+Funzioni.addZero(dpEnd.getMonth(), 2)+"-"+dpEnd.getYear();
    					Log.e(dateStart, dateEnd);
    					if (cbIntegralBox.isChecked()){
    	//#########################JSON
	    					AsyncHttpClient client = new AsyncHttpClient();
	    					client.get("http://199.180.196.10/json/integral?startdate="+dateStart+"&enddate="+dateEnd, new AsyncHttpResponseHandler() {
	    					    @Override
	    					    public void onSuccess(String response) {
	    					    	Log.e("SCARICATO", response);
	    					    	JSONArray jObject;
									try {
										jObject = new JSONArray(response);
		    					        for (int i = 0; i < jObject.length(); i++) {
		    					             JSONArray menuObject = jObject.getJSONArray(i);
		    					             String dataStart= menuObject.getString(0);
		    					             String dataEnd= menuObject.getString(1);
		    					             String raj2000= menuObject.getString(2);
		    					             String decj2000= menuObject.getString(3);
		    					             String target= menuObject.getString(4);
		    					             Log.e("", dataStart+" "+dataEnd+" "+raj2000+" "+decj2000+" "+target);
		    					             satellitiIntegral.add(new SatelliteIntegral(dataStart, dataEnd, raj2000, decj2000, target));
		    					        }
									} catch (JSONException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
	    					    }
	    					   }
	    					);
    					}
    			
    					
    					dialog.dismiss();
//    					Intent mioIntent= new Intent(ctx, Range.class);
//    					startActivity(mioIntent);
    				}
    				
    			});
    			dialog.show();
                break;
            case R.id.side_navigation_menu_item3:
                invokeActivity(getString(R.string.title3), R.drawable.ic_android3);
                break;

            case R.id.side_navigation_menu_item4:
                invokeActivity(getString(R.string.title4), R.drawable.ic_android4);
                break;

            case R.id.side_navigation_menu_item5:
                invokeActivity(getString(R.string.title5), R.drawable.ic_android5);
                break;

            default:
                return;
        }
    }
	
	private void invokeActivity(String title, int resId) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra(EXTRA_TITLE, title);
        intent.putExtra(EXTRA_RESOURCE_ID, resId);
        intent.putExtra(EXTRA_MODE, sideNavigationView.getMode() == Mode.LEFT ? 0 : 1);

        // all of the other activities on top of it will be closed and this
        // Intent will be delivered to the (now on top) old activity as a
        // new Intent.
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        startActivity(intent);
        // no animation of transition
        overridePendingTransition(0, 0);
    }
}