/**
 * 
 */
package com.threecats.colorselect;

/*
 *  Copyright 2011 3Cats Software <rumburake@gmail.com>
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  see NOTICE and LICENSE files in the top level project folder.
 */

import android.graphics.Color;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class ColorSelector extends DialogFragment {

    private static final String ARG_COLOR = "arg_color";

    public interface ColorSelectedListener {
		void onNewColor(int color);
	}

    public ColorSelector() {}

	public static ColorSelector newInstance(int startColor) {
        ColorSelector newFrag = new ColorSelector();

        Bundle args = new Bundle();
        args.putInt(ARG_COLOR, startColor);
        newFrag.setArguments(args);

        return newFrag;
    }

	View view;
	SaturationValueView viewSatVal;
	HueView viewHue;
	ImageView viewHueCursor;
	ImageView viewSatValCursor;
	ImageView viewOldColor;
	ImageView viewNewColor;

	float hueX = -1;
	float hueY = -1;
	float satValX = -1;
	float satValY = -1;

    private int startRgb = 0xFF000000;
    int rgb = 0xFF000000;
    float hsv[] = {0, 0, 0};

	void setHueCursor() {
		RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) viewHueCursor.getLayoutParams();
		layoutParams.leftMargin = -2 ;
		layoutParams.topMargin = (int) (hueY - viewHueCursor.getHeight() / 2);
		viewHueCursor.setLayoutParams(layoutParams);
	}
	
	void setSatValCursor() {
		RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) viewSatValCursor.getLayoutParams();
		layoutParams.leftMargin = (int) (satValX - viewSatValCursor.getWidth() / 2);
		layoutParams.topMargin = (int) (satValY - viewSatValCursor.getHeight() / 2);
		viewSatValCursor.setLayoutParams(layoutParams);
	}
	
	public void initColors() {
		Color.colorToHSV(rgb, hsv);

		viewOldColor.setBackgroundColor(0XFF000000 | startRgb);
		viewNewColor.setBackgroundColor(0XFF000000 | rgb);
		
		ViewTreeObserver vto = view.getViewTreeObserver(); 
		vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
			public void onGlobalLayout() {
				hueY = hsv[0] * viewHue.getMeasuredHeight() / 360.f;
				satValX = hsv[1] * viewSatVal.getMeasuredWidth();
				satValY = (1 - hsv[2]) * viewSatVal.getMeasuredHeight();
				viewSatVal.setHue(hsv[0]);
				setHueCursor();
				setSatValCursor();
				view.getViewTreeObserver().removeGlobalOnLayoutListener(this);
			}
		});

	}

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.color_select, container, false);

		getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);

		view = v.findViewById(R.id.All);
		viewSatVal = (SaturationValueView)v.findViewById(R.id.SatVal);
		viewHue = (HueView)v.findViewById(R.id.Hue);
		viewHueCursor = (ImageView)v.findViewById(R.id.HueCursor);
		viewSatValCursor = (ImageView)v.findViewById(R.id.SatValCursor);
		viewOldColor = (ImageView)v.findViewById(R.id.OldColor);
		viewNewColor = (ImageView)v.findViewById(R.id.NewColor);

        startRgb = getArguments().getInt(ARG_COLOR, 0xFF000000);
        rgb = startRgb;
        if (savedInstanceState != null) {
            rgb = savedInstanceState.getInt(ARG_COLOR, startRgb);
        }
        initColors();

		viewHue.setOnTouchListener(new View.OnTouchListener() {
			
			public boolean onTouch(View v, MotionEvent event) {
				int action = event.getAction();
				switch (action) {
				case MotionEvent.ACTION_DOWN:
				case MotionEvent.ACTION_UP:
				case MotionEvent.ACTION_MOVE:
					float y = event.getY();
					if (y < 0) {
						y = 0;
					}
					if (y > viewHue.getMeasuredHeight()) {
						y = viewHue.getMeasuredHeight() - 0.1f;
					}
					hueY = y;
					hueX = event.getX();
					
					hsv[0] = y * 360.f / viewHue.getMeasuredHeight();
					viewSatVal.setHue(hsv[0]);
					setHueCursor();

					rgb = Color.HSVToColor(hsv);
					viewNewColor.setBackgroundColor(rgb);
					
					return true;
				}
				return false;
			}
		});

		viewSatVal.setOnTouchListener(new View.OnTouchListener() {
			
			public boolean onTouch(View v, MotionEvent event) {
				int action = event.getAction();
				switch (action) {
				case MotionEvent.ACTION_DOWN:
				case MotionEvent.ACTION_UP:
				case MotionEvent.ACTION_MOVE:
					float y = event.getY();
					if (y < 0) {
						y = 0;
					}
					if (y > viewSatVal.getMeasuredHeight()) {
						y = viewSatVal.getMeasuredHeight() - 0.1f;
					}
					satValY = y;
					float x = event.getX();
					if (x < 0) {
						x = 0;
					}
					if (x > viewSatVal.getMeasuredWidth()) {
						x = viewSatVal.getMeasuredWidth() - 0.1f;
					}
					satValX = x;
					
					hsv[1] = x / viewSatVal.getMeasuredHeight();
					hsv[2] = 1 - y / viewSatVal.getMeasuredHeight();
					setSatValCursor();
					
					rgb = Color.HSVToColor(hsv);
					viewNewColor.setBackgroundColor(rgb);
					
					return true;
				}
				return false;
			}
		});
		
		viewOldColor.setOnTouchListener(new View.OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {
				viewOldColor.setBackgroundColor(0xFFFFFFFF);
				dismiss();
				return false;
			}
		});

		viewNewColor.setOnTouchListener(new View.OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {
				viewNewColor.setBackgroundColor(0xFFFFFFFF);
                ((ColorSelectedListener)getActivity()).onNewColor(rgb);
				dismiss();
				return false;
			}
		});

        return v;

	}

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt(ARG_COLOR, rgb);

        super.onSaveInstanceState(outState);
    }
}
