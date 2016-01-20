/*
 * Copyright (C) 2008 ZXing authors
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

package com.apputils.example.qr.view;

import java.util.Collection;
import java.util.HashSet;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.view.View;

import com.apputils.example.R;
import com.apputils.example.qr.camera.CameraManager;
import com.google.zxing.ResultPoint;

/**
 * This view is overlaid on top of the camera preview. It adds the viewfinder
 * rectangle and partial transparency outside it, as well as the laser scanner
 * animation and result points.
 */
public final class ViewfinderView extends View {

	private static final int[] SCANNER_ALPHA = { 0, 64, 128, 192, 255, 192, 128, 64 };
	private static final long ANIMATION_DELAY = 10L;
	private static final int OPAQUE = 0xFF;

	private final Paint paint;
	private Bitmap resultBitmap;
	private final int maskColor;
	private final int resultColor;
	private final int frameColor;
	private final int laserColor;
	// private final int resultPointColor;
	private int scannerAlpha;
	private Collection<ResultPoint> possibleResultPoints;
	private Collection<ResultPoint> lastPossibleResultPoints;
	private int slideTop;
	boolean isFirst;
	// 中间那条线每次刷新移动的距离
	private static final int SPEEN_DISTANCE = 5;

	// This constructor is used when the class is built from an XML resource.
	public ViewfinderView(Context context, AttributeSet attrs) {
		super(context, attrs);

		// Initialize these once for performance rather than calling them every
		// time in onDraw().
		paint = new Paint();
		Resources resources = getResources();
		maskColor = resources.getColor(R.color.viewfinder_mask);
		resultColor = resources.getColor(R.color.result_view);
		frameColor = resources.getColor(R.color.viewfinder_frame);
		laserColor = resources.getColor(R.color.viewfinder_laser);
		// resultPointColor =
		// resources.getColor(R.color.possible_result_points);
		scannerAlpha = 0;
		possibleResultPoints = new HashSet<ResultPoint>(5);
	}

	@Override
	public void onDraw(Canvas canvas) {
		Rect frame = CameraManager.get().getFramingRect();
		if (frame == null) {
			return;
		}
		
		// 初始化中间线滑动的最上边和最下边
		if (!isFirst) {
			isFirst = true;
			slideTop = frame.top;
		}
		
		int width = canvas.getWidth();
		int height = canvas.getHeight();

		// Draw the exterior (i.e. outside the framing rect) darkened
		paint.setColor(resultBitmap != null ? resultColor : maskColor);
		canvas.drawRect(0, 0, width, frame.top, paint);
		canvas.drawRect(0, frame.top, frame.left, frame.bottom + 1, paint);
		canvas.drawRect(frame.right + 1, frame.top, width, frame.bottom + 1, paint);
		canvas.drawRect(0, frame.bottom + 1, width, height, paint);

		if (resultBitmap != null) {
			// Draw the opaque result bitmap over the scanning rectangle
			paint.setAlpha(OPAQUE);
			canvas.drawBitmap(resultBitmap, frame.left, frame.top, paint);
		} else {

			// Draw a two pixel solid black border inside the framing rect
			// paint.setColor(frameColor);
			// canvas.drawRect(frame.left, frame.top, frame.right + 1, frame.top
			// + 2, paint);
			// canvas.drawRect(frame.left, frame.top + 2, frame.left + 2,
			// frame.bottom - 1, paint);
			// canvas.drawRect(frame.right - 1, frame.top, frame.right + 1,
			// frame.bottom - 1, paint);
			// canvas.drawRect(frame.left, frame.bottom - 1, frame.right + 1,
			// frame.bottom + 1, paint);
			paint.setColor(frameColor);
			int line = (frame.right - frame.left) / 8;
			int lineWid = 8;
			canvas.drawRect(frame.left, frame.top, frame.left + line, frame.top + lineWid, paint);
			canvas.drawRect(frame.right - line, frame.top, frame.right + 1, frame.top + lineWid, paint);

			canvas.drawRect(frame.left, frame.top + 2, frame.left + lineWid, frame.top + line, paint);
			canvas.drawRect(frame.left, frame.bottom - line, frame.left + lineWid, frame.bottom - 1, paint);

			canvas.drawRect(frame.right - lineWid + 1, frame.top, frame.right + 1, frame.top + line, paint);
			canvas.drawRect(frame.right - lineWid + 1, frame.bottom - line, frame.right + 1, frame.bottom - 1, paint);

			canvas.drawRect(frame.left, frame.bottom - lineWid + 1, frame.left + line, frame.bottom + 1, paint);
			canvas.drawRect(frame.right - line, frame.bottom - lineWid + 1, frame.right + 1, frame.bottom + 1, paint);
			
			slideTop += SPEEN_DISTANCE;
			if (slideTop >= frame.bottom) {
				slideTop = frame.top;
			}
			Rect lineRect = new Rect();
			lineRect.top = slideTop;
			lineRect.bottom = slideTop + 10;
			lineRect.left = frame.left;
			lineRect.right = frame.right;
			canvas.drawBitmap(((BitmapDrawable) (getResources().getDrawable(R.drawable.qrcode_scan_line))).getBitmap(),
					null, lineRect, paint);

			// Request another update at the animation interval, but only
			// repaint the laser line,
			// not the entire viewfinder mask.
			postInvalidateDelayed(ANIMATION_DELAY, frame.left, frame.top, frame.right, frame.bottom);
		}
	}

	public void drawViewfinder() {
		resultBitmap = null;
		invalidate();
	}

	/**
	 * Draw a bitmap with the result points highlighted instead of the live
	 * scanning display.
	 *
	 * @param barcode
	 *            An image of the decoded barcode.
	 */
	public void drawResultBitmap(Bitmap barcode) {
		resultBitmap = barcode;
		invalidate();
	}

	public void addPossibleResultPoint(ResultPoint point) {
		possibleResultPoints.add(point);
	}

}
