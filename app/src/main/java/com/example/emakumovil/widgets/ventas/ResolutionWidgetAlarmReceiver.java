package com.example.emakumovil.widgets.ventas;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

public class ResolutionWidgetAlarmReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		Bundle b = intent.getExtras();
		String resolucion = b.getString("resolucion");
		Intent alarmService = new Intent(context, ResolutionWidgetAlarmService.class);
		alarmService.putExtra("resolucion",resolucion);
	    context.getApplicationContext().startService(alarmService);
	}

}
