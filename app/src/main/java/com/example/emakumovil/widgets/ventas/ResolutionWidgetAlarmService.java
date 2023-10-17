package com.example.emakumovil.widgets.ventas;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;

import androidx.core.app.NotificationCompat;

import com.example.emakumovil.R;

public class ResolutionWidgetAlarmService extends Service {
      
   private NotificationManager mManager;
 
    @Override
    public IBinder onBind(Intent arg0) {
       // TODO Auto-generated method stub
        return null;
    }
 
    @Override
    public void onCreate() {
       super.onCreate();
    }
 
   @Override
   public void onStart(Intent intent, int startId) {
	   Bundle b = intent.getExtras();
	   String resolucion = b.getString("resolucion");
       mManager = (NotificationManager) this.getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
       Intent intent1 = new Intent(this.getApplicationContext(),this.getClass());
       intent1.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP| Intent.FLAG_ACTIVITY_CLEAR_TOP);
       NotificationCompat.Builder notification = new NotificationCompat.Builder(this.getApplicationContext());
       notification.setSmallIcon(R.drawable.ic_launcher);
       notification.setContentTitle(resolucion);
       notification.setContentText(getString(R.string.fin_resolucion));
       String ringtone = ResolucionFacturacion.getRingtone(this.getApplicationContext());
       Uri alarmSound = null;
       if (ringtone==null || ringtone.equals("")) { 
    	   alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
       }
       else {
    	   alarmSound = Uri.parse(ringtone);
       }
       
       notification.setSound(alarmSound);
       mManager.notify(0, notification.build());
    }
 
    @Override
    public void onDestroy() {
        super.onDestroy();
    }

 
}
