package com.example.emakumovil.widgets.ventas;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class ResolutionChooseWidgetActivity extends Activity {
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle extras = getIntent().getExtras();
		if (extras!=null) {
			ResolutionChooseWidgetFragment rs = new ResolutionChooseWidgetFragment();
			rs.setArguments(extras);
			getFragmentManager().beginTransaction().
			replace(android.R.id.content,rs).commit();
		}
	}
}
