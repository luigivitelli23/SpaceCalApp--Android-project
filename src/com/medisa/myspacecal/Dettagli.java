package com.medisa.myspacecal;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.devspark.sidenavigation.sample.R;

public class Dettagli extends Activity {

	ListView lstDettagliScrollView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_dettagli);
		Intent intent= getIntent();
		String[] satellitiTag=intent.getStringExtra("satelliti").split(",");
		
		lstDettagliScrollView=(ListView) findViewById(R.id.lstDettagli);
		
		ArrayList<String> satelliti = new ArrayList<String>();
        for (int i=1; i<satellitiTag.length; i++){
        	Log.e("NUMERI", satellitiTag[i]);
        	if (satellitiTag[i]!=null){
        		satelliti.add("Data start: "+ MainActivity.satellitiIntegral.get(Integer.valueOf(satellitiTag[i])).getDataStart()+
        						"\nData end: "+ MainActivity.satellitiIntegral.get(Integer.valueOf(satellitiTag[i])).getDataEnd()+
        						"\nDecj2000: "+ MainActivity.satellitiIntegral.get(Integer.valueOf(satellitiTag[i])).getDecj2000()+
        						"\nRaj2000: "+ MainActivity.satellitiIntegral.get(Integer.valueOf(satellitiTag[i])).getRaj2000()+
        						"\nTarget: "+ MainActivity.satellitiIntegral.get(Integer.valueOf(satellitiTag[i])).getTarget());
        	}
        }
        // This is the array adapter, it takes the context of the activity as a first // parameter, the type of list view as a second parameter and your array as a third parameter
        ArrayAdapter<String> arrayAdapter =      
        new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, satelliti);
        lstDettagliScrollView.setAdapter(arrayAdapter); 
		
	}

//	@Override
//	public boolean onCreateOptionsMenu(Menu menu) {
//		// Inflate the menu; this adds items to the action bar if it is present.
//		getMenuInflater().inflate(R.menu.dettagli, menu);
//		return true;
//	}

}
