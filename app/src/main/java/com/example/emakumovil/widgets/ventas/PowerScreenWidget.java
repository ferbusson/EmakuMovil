package com.example.emakumovil.widgets.ventas;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class PowerScreenWidget extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
            System.out.println("pantalla apagada bajando servicio..");
    		if (isMyServiceRunning(context)) {
    			System.out.println("bajando service..");
    			context.getApplicationContext().stopService(new Intent(context, BroadCastWidgetService.class));
    		}
        } else if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
            System.out.println("pantalla prendida, levantando servicio");
    		if (isMyServiceRunning(context)) {
    			System.out.println("running");
    			context.getApplicationContext().startService(new Intent(context, BroadCastWidgetService.class));
    		}
        } 
	}

	private boolean isMyServiceRunning(Context context) {
	    ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
	    for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
	        if ("com.example.emakumovil.widgets.ventas.BroadCastWidgetService".equals(service.service.getClassName())) {
	            return true;
	        }
	    }
	    return false;
	}

}
