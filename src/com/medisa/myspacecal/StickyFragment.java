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

package com.medisa.myspacecal;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.TextView;

import com.devspark.sidenavigation.sample.R;

public class StickyFragment extends Fragment implements ObservableScrollView.Callbacks {
    private TextView mStickyView;
    private View mPlaceholderView;
    private ObservableScrollView mObservableScrollView;

    public StickyFragment() {
    }

    String[] nSatelliti;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater
                .inflate(R.layout.fragment_content, container, false);

        mObservableScrollView = (ObservableScrollView) rootView.findViewById(R.id.scroll_view);
        mObservableScrollView.setCallbacks(this);

        mStickyView = (TextView) rootView.findViewById(R.id.sticky);
        mStickyView.setText(R.string.sticky_item);
        mPlaceholderView = rootView.findViewById(R.id.placeholder);

        mObservableScrollView.removeAllViews();
        
        
        Intent mioIntent=getActivity().getIntent();
        
        nSatelliti=mioIntent.getStringExtra("satelliti").split(",");
        Log.e("EXTRA", mioIntent.getStringExtra("satelliti"));
        for (int i=0; i<nSatelliti.length; i++){
        	if (nSatelliti[i]!="null"){
        	}
        }

    	TextView a = new TextView(MainActivity.ctx);
        a.setTextAppearance(getActivity().getApplicationContext(), R.style.Item_Bottom);
        mObservableScrollView.addView(a);
        

    	TextView b = new TextView(MainActivity.ctx);
        b.setTextAppearance(getActivity().getApplicationContext(), R.style.Item_Bottom);
        mObservableScrollView.addView(b);
        
        
        
        mObservableScrollView.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        onScrollChanged();
                    }
                });

        return rootView;
    }

    @Override
    public void onScrollChanged() {
        mStickyView.setTranslationY(
                Math.max(0, mPlaceholderView.getTop() - mObservableScrollView.getScrollY()));
    }
}