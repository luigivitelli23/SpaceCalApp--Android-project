/*
 * Copyright (C) 2012 Evgeny Shishkin
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.medisa.myspacecal;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONException;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.devspark.sidenavigation.ISideNavigationCallback;
import com.devspark.sidenavigation.SideNavigationView;
import com.devspark.sidenavigation.SideNavigationView.Mode;
import com.devspark.sidenavigation.sample.R;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

/**
 * 
 * @author e.shishkin
 * 
 */
public class MainActivity extends SherlockActivity implements ISideNavigationCallback, OnClickListener {

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
    private SideNavigationView sideNavigationView;
    private String dateStart;
    private String dateEnd;
    private byte range=0;
    int indice=0;
    static public ArrayList<SatelliteIntegral> satellitiIntegral;
    static public Context ctx;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        sideNavigationView = (SideNavigationView) findViewById(R.id.side_navigation_view);
        sideNavigationView.setMenuItems(R.menu.side_navigation_menu);
        sideNavigationView.setMenuClickCallback(this);

        satellitiIntegral=new ArrayList<SatelliteIntegral>();
        
        
        _calendar = Calendar.getInstance(Locale.getDefault());
		month = _calendar.get(Calendar.MONTH);
		year = _calendar.get(Calendar.YEAR);

		ctx=this;

		prevMonth = (ImageView) this.findViewById(R.id.prevMonth);
		prevMonth.setOnClickListener(this);

		currentMonth = (Button) this.findViewById(R.id.currentMonth);
		currentMonth.setText(dateFormatter.format(dateTemplate, _calendar.getTime()));

		nextMonth = (ImageView) this.findViewById(R.id.nextMonth);
		nextMonth.setOnClickListener(this);

		calendarView = (GridView) this.findViewById(R.id.calendar);

		// Initialised
		adapter = new GridCellAdapter(getApplicationContext(), R.id.day_gridcell, month, year);
		adapter.notifyDataSetChanged();
		calendarView.setAdapter(adapter);
        

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getSupportMenuInflater().inflate(R.menu.main_menu, menu);
        menu.findItem(R.id.mode_left).setChecked(true);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                sideNavigationView.toggleMenu();
                break;
//            case R.id.mode_left:
//                item.setChecked(true);
//                sideNavigationView.setMode(Mode.LEFT);
//                break;
//            case R.id.mode_right:
//                item.setChecked(true);
//                sideNavigationView.setMode(Mode.RIGHT);
//                break;
//
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
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
    					dateStart=Funzioni.addZero(dpStart.getDayOfMonth(), 2)+"-"+Funzioni.addZero(dpStart.getMonth()+1, 2)+"-"+dpStart.getYear();
    					dateEnd=Funzioni.addZero(dpEnd.getDayOfMonth(), 2)+"-"+Funzioni.addZero(dpEnd.getMonth()+1, 2)+"-"+dpEnd.getYear();
    					Log.e(dateStart, dateEnd);
    					if (cbIntegralBox.isChecked()){
    	//#########################JSON
	    					AsyncHttpClient client = new AsyncHttpClient();
	    					client.get("http://199.180.196.10/json/integral?startdate="+dateStart+"&enddate="+dateEnd, new AsyncHttpResponseHandler() {
	    					    @Override
	    					    public void onSuccess(String response) {
	    					    	Log.e("SCARICATO", response+"");
	    					    	JSONArray jObject;
	    					        satellitiIntegral=new ArrayList<SatelliteIntegral>();
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
		    	    					adapter = new GridCellAdapter(getApplicationContext(), R.id.day_gridcell, month, year);
		    	    					adapter.notifyDataSetChanged();
		    	    					calendarView.setAdapter(adapter);

									} catch (JSONException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
	    					    }
	    					   }
	    					);
    					}
    					
    					dialog.dismiss();
  //###############################################
//    					Intent mioIntent= new Intent(ctx, MainActivity.class);
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
    
    
    
    
    
    
    
    @Override
    public void onBackPressed() {
        // hide menu if it shown
        if (sideNavigationView.isShown()) {
            sideNavigationView.hideMenu();
        } else {
            super.onBackPressed();
        }
    }

    /**
     * Start activity from SideNavigation.
     * 
     * @param title title of Activity
     * @param resId resource if of background image
     */
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



    @Override
	public void onClick(View v)
		{
			if (v == prevMonth)
				{
					if (month <= 1)
						{
							month = 11;
							year--;
						} else
						{
							month--;
						}

					adapter = new GridCellAdapter(getApplicationContext(), R.id.day_gridcell, month, year);
					_calendar.set(year, month, _calendar.get(Calendar.DAY_OF_MONTH));
					currentMonth.setText(dateFormatter.format(dateTemplate, _calendar.getTime()));

					adapter.notifyDataSetChanged();
					calendarView.setAdapter(adapter);
				}
			if (v == nextMonth)
				{
					if (month >= 11)
						{
							month = 0;
							year++;
						} else
						{
							month++;
						}

					adapter = new GridCellAdapter(getApplicationContext(), R.id.day_gridcell, month, year);
					_calendar.set(year, month, _calendar.get(Calendar.DAY_OF_MONTH));
					currentMonth.setText(dateFormatter.format(dateTemplate, _calendar.getTime()));
					adapter.notifyDataSetChanged();
					calendarView.setAdapter(adapter);
				}
		}

	// Inner Class
	public class GridCellAdapter extends BaseAdapter implements OnClickListener, OnLongClickListener
	{
		private static final String tag = "GridCellAdapter";
		private final Context _context;
		private final List<String> list;
		private final String[] weekdays = new String[] { "Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat" };
		private final String[] months = { "January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December" };
		private final int[] daysOfMonth = { 31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31 };
		private final int month, year;
		private int daysInMonth, prevMonthDays;
		private final int currentDayOfMonth;
		private Button gridcell;
		private TextView txtNumeroSatelliti;

		// Days in Current Month
		public GridCellAdapter(Context context, int textViewResourceId, int month, int year)
			{
				super();
				this._context = context;
				this.list = new ArrayList<String>();
				this.month = month;
				this.year = year;

				Log.d(tag, "Month: " + month + " " + "Year: " + year);
				Calendar calendar = Calendar.getInstance();
				currentDayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

				printMonth(month, year);
			}

		public String getItem(int position)
			{
				return list.get(position);
			}

		@Override
		public int getCount()
			{
				return list.size();
			}

		private void printMonth(int mm, int yy)
			{
				// The number of days to leave blank at
				// the start of this month.
				int trailingSpaces = 0;
				int leadSpaces = 0;
				int daysInPrevMonth = 0;
				int prevMonth = 0;
				int prevYear = 0;
				int nextMonth = 0;
				int nextYear = 0;
				indice=0;
				GregorianCalendar cal = new GregorianCalendar(yy, mm, currentDayOfMonth-1);

				// Days in Current Month
				daysInMonth = daysOfMonth[mm];
				int currentMonth = mm;
				if (currentMonth == 11)
					{
						prevMonth = 10;
						daysInPrevMonth = daysOfMonth[prevMonth];
						nextMonth = 0;
						prevYear = yy;
						nextYear = yy + 1;
					} else if (currentMonth == 0)
					{
						prevMonth = 11;
						prevYear = yy - 1;
						nextYear = yy;
						daysInPrevMonth = daysOfMonth[prevMonth];
						nextMonth = 1;
					} else
					{
						prevMonth = currentMonth - 1;
						nextMonth = currentMonth + 1;
						nextYear = yy;
						prevYear = yy;
						daysInPrevMonth = daysOfMonth[prevMonth];
					}

				// Compute how much to leave before before the first day of the
				// month.
				// getDay() returns 0 for Sunday.
				trailingSpaces = cal.get(Calendar.DAY_OF_WEEK); //- 1;

				if (cal.isLeapYear(cal.get(Calendar.YEAR)) && mm == 1)
					{
						++daysInMonth;
					}

				// Trailing Month days
				for (int i = 0; i < trailingSpaces; i++)
					{
					list.add(String.valueOf((daysInPrevMonth - trailingSpaces + 1) + i) + "-GREY" + "-" + months[prevMonth] + "-" + prevYear);
					}

				// Current Month Days
				for (int i = 1; i <= daysInMonth; i++)
					{
						list.add(String.valueOf(i) + "-WHITE" + "-" + months[mm] + "-" + yy);
					}

				// Leading Month days
				for (int i = 0; i < list.size() % 7; i++)
					{
						Log.d(tag, "NEXT MONTH:= " + months[nextMonth]);
						list.add(String.valueOf(i + 1) + "-GREY" + "-" + months[nextMonth] + "-" + nextYear);
					}
				
				
			}

		@Override
		public long getItemId(int position)
			{
				return position;
			}

		@SuppressLint("ResourceAsColor")
		@Override
		public View getView(int position, View convertView, ViewGroup parent)
			{
				Log.d(tag, "getView ...");
				View row = convertView;
				if (row == null)
					{
						// ROW INFLATION
						Log.d(tag, "Starting XML Row Inflation ... ");
						LayoutInflater inflater = (LayoutInflater) _context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
						row = inflater.inflate(R.layout.day_gridcell, parent, false);

						Log.d(tag, "Successfully completed XML Row Inflation!");
					}

				// Get a reference to the Day gridcell
				gridcell = (Button) row.findViewById(R.id.day_gridcell);
				
//				gridcell.setOnClickListener(this);
				gridcell.setOnLongClickListener(this);
				
				txtNumeroSatelliti = (TextView) row.findViewById(R.id.txtNumeroSatelliti);
				txtNumeroSatelliti.setOnClickListener(this);
				// ACCOUNT FOR SPACING

				Log.d(tag, "Current Day: " + currentDayOfMonth);
				String[] day_color = list.get(position).split("-");
				gridcell.setText(day_color[0]);
				gridcell.setTag(day_color[0] + "-" + day_color[2] + "-" + day_color[3]);
				
				if (day_color[1].equals("GREY"))
					{
						gridcell.setTextColor(Color.LTGRAY);
						txtNumeroSatelliti.setText("5");
					}
				if (day_color[1].equals("WHITE"))
					{
						gridcell.setTextColor(Color.WHITE);
	//###########################NUMERO SATELLITI DA JSON
						Log.e("LO CHIAMO", "LO CHIAMO");

				        Log.e("size arraylist", satellitiIntegral.size()+"");
//						for (int i=0; i<Funzioni.satellitiIntegral.size(); i++){
						try{
							if (satellitiIntegral.size()>0){
								int n=0;
								Log.e("CONFRONTO DATE"+indice, year+"-"+Funzioni.addZero(month+1, 2)+"-"+Funzioni.addZero(Integer.parseInt(day_color[0]),2)+"-----"+satellitiIntegral.get(indice).getDataStart().split(" ")[0]);
								
								while (string2Date(year+"-"+Funzioni.addZero(month+1, 2)+"-"+Funzioni.addZero(Integer.parseInt(day_color[0]), 2)).getMonth()!=string2Date(satellitiIntegral.get(indice).getDataStart().split(" ")[0]).getMonth()){
									indice+=1;
								}
								Log.e(""+string2Date(year+"-"+Funzioni.addZero(month+1, 2)+"-"+Funzioni.addZero(Integer.parseInt(day_color[0]), 2)), ""+(string2Date(satellitiIntegral.get(indice).getDataStart().split(" ")[0])));
								if (string2Date(year+"-"+Funzioni.addZero(month+1, 2)+"-"+Funzioni.addZero(Integer.parseInt(day_color[0]), 2)).compareTo(string2Date(satellitiIntegral.get(indice).getDataStart().split(" ")[0]))==0){
									Log.e("CONFRONTO DATE", year+"-"+Funzioni.addZero(month+1, 2)+"-"+Funzioni.addZero(Integer.parseInt(day_color[0]),2)+"-----"+satellitiIntegral.get(indice).getDataStart().split(" ")[0]);
									Log.e(Funzioni.addZero(month+1, 2)+"", ""+satellitiIntegral.get(indice).getDataStart().split(" ")[0].split("-")[1]);
									
									while (string2Date(year+"-"+Funzioni.addZero(month+1, 2)+"-"+Funzioni.addZero(Integer.parseInt(day_color[0]), 2)).compareTo(string2Date(satellitiIntegral.get(indice).getDataStart().split(" ")[0]))==0){
										txtNumeroSatelliti.setTag(txtNumeroSatelliti.getTag()+","+indice);
										n+=1;
										indice+=1;
										Log.e("UGUALI", "UGUALI");
									}
									switch (n) {
									case 0:
										txtNumeroSatelliti.setBackgroundColor(Color.BLACK);
										break;
									case 1:
										txtNumeroSatelliti.setBackgroundColor(Color.GRAY);
										break;
									case 2:
										txtNumeroSatelliti.setBackgroundColor(Color.GREEN);
										break;
									case 3:
										txtNumeroSatelliti.setBackgroundColor(Color.YELLOW);
										break;
									default:
										txtNumeroSatelliti.setBackgroundColor(Color.RED);
										break;
									}
								}
							}
	//					}
						}catch(Exception e){
							Log.e("ERRORE", e.getMessage());
						}
						txtNumeroSatelliti.setText("5");
					}
				if (position == currentDayOfMonth)
					{
						gridcell.setTextColor(Color.BLUE);
	//###########################NUMERO SATELLITI DA JSON
						txtNumeroSatelliti.setText("5");
					}

				return row;
			}
		@SuppressLint("SimpleDateFormat")
		public Date string2Date(String a){
			String dateString = a;
		    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		    Date convertedDate = new Date();
		    try {
		        convertedDate = dateFormat.parse(dateString);
		    } catch (ParseException e) {
		        // TODO Auto-generated catch block
		        e.printStackTrace();
		    }
		    convertedDate.setMonth(convertedDate.getMonth()+1);
		    return convertedDate;
		}
		@Override
		public void onClick(View view)
			{
//				String date_month_year = (String) view.getTag();
//				Toast.makeText(getApplicationContext(), date_month_year, Toast.LENGTH_LONG).show();
				Intent mioIntent = new Intent(ctx, Dettagli.class);
				mioIntent.putExtra("satelliti",view.getTag().toString());
				startActivity(mioIntent);
			}

		@Override
		public boolean onLongClick(View v) {
			// TODO Auto-generated method stub
			Log.e("LONG CLICK", "LONG CLICK");
			return false;
		}
	}
}

