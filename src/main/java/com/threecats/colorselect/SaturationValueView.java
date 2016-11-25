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

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ComposeShader;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.Shader;
import android.graphics.Shader.TileMode;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;

public class SaturationValueView extends View {
	
	Paint paint;
	Shader valueShader, saturationShader, composeShader;
	int hueColorRgb;

	/**
	 * @param context
	 */
	public SaturationValueView(Context context) {
		super(context);
	}

	/**
	 * @param context
	 * @param attrs
	 */
	public SaturationValueView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	/**
	 * @param context
	 * @param attrs
	 * @param defStyle
	 */
	public SaturationValueView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	/* (non-Javadoc)
	 * @see android.view.View#onDraw(android.graphics.Canvas)
	 */
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		canvas.drawRect(0, 0, getMeasuredWidth(), getMeasuredHeight(), paint);
	}
	
	void setHue(float hue) {
		float hsv[] = {hue, 1, 1};
		hueColorRgb = Color.HSVToColor(hsv);

		setupShader();

		invalidate();
	}

	void setupShader() {
		if (null == paint) {
			paint = new Paint();
		}
		valueShader = new LinearGradient(0, 0, 0, getMeasuredHeight(), 0xffffffff, 0xff000000, TileMode.CLAMP);
		saturationShader = new LinearGradient(0, 0, getMeasuredWidth(), 0, 0xffffffff, hueColorRgb, TileMode.CLAMP);
		composeShader = new ComposeShader(saturationShader, valueShader, PorterDuff.Mode.MULTIPLY);

		paint.setShader(composeShader);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
			setLayerType(LAYER_TYPE_SOFTWARE, null);

		setupShader();
	}
}
