package com.drocode.android.touchlocation.overlay;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.graphics.drawable.Drawable;
import android.widget.TextView;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.OverlayItem;

public class MapsOverlay extends ItemizedOverlay<OverlayItem> {

	private List<OverlayItem> mOverlays = new ArrayList<OverlayItem>();
	private Context mContext;

	public MapsOverlay(Drawable marker, Context context) {
		super(boundCenterBottom(marker));
		mContext = context;
	}

	public void addItem(GeoPoint p, String title, String snippet) {
		OverlayItem newItem = new OverlayItem(p, title, snippet);
		mOverlays.add(newItem);
		populate();
	}

	@Override
	protected OverlayItem createItem(int i) {
		return mOverlays.get(i);
	}

	@Override
	public int size() {
		return mOverlays.size();
	}

	@Override
	protected boolean onTap(int index) {
		AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
		dialog.setTitle(mOverlays.get(index).getTitle());

		final TextView message = new TextView(mContext);
		message.setTextSize(14);
		message.setPadding(10, 10, 10, 10);
		message.setText(mOverlays.get(index).getSnippet());

		dialog.setView(message);
		dialog.setNeutralButton("Close", new OnClickListener() {

			@Override
			public void onClick(DialogInterface dlg, int which) {
				dlg.dismiss();
			}
		});

		dialog.show();
		return true;
	}
}
