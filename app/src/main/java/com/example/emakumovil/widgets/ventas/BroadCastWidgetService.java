package com.example.emakumovil.widgets.ventas;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.example.emakumovil.control.ClientHeaderValidator;
import com.example.emakumovil.control.UpdateCodeEvent;
import com.example.emakumovil.control.UpdateCodeListener;
import com.example.emakumovil.misc.settings.ConfigFileHandler;

public class BroadCastWidgetService extends Service implements Runnable, UpdateCodeListener {

	private static final String ACTION_UPDATE_RESOLUTION="com.example.emakumovil.widgets.ventas.UPDATE_RESOLUTION";
	private Thread t;
	
	public BroadCastWidgetService() {
		System.out.println("********* SERVICIO LANZADO *********************");
		ClientHeaderValidator.addUpdateCodeListener(this);
		t = new Thread(this);
		t.start();
	}
	
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	 public void onDestroyed(){
	        System.out.println("******** SERVICIO DESTRUIDO *******************");
	        super.onDestroy();
    }
	 
	@Override
	public void cathUpdateCodeEvent(UpdateCodeEvent e) {
		// TODO Auto-generated method stub
		Intent intent = new Intent(ACTION_UPDATE_RESOLUTION);
		System.out.println("Lanzando bradcast de consulta desde updatecode...");
		BroadCastWidgetService.this.getApplicationContext().sendBroadcast(intent);
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		System.out.println("comenzando ciclo ....");
		while (true) {
			try {
				if (ConfigFileHandler.getCurrentCompany().equals("")) {
					System.out.println("no existe configuracion inicial, cargando datos de conexion");
					ResolucionFacturacion.loadInfoConnecctions(BroadCastWidgetService.this.getApplicationContext());
				}
				Intent intent = new Intent(ACTION_UPDATE_RESOLUTION);
				System.out.println("Lanzando bradcast de consulta...");
				BroadCastWidgetService.this.getApplicationContext().sendBroadcast(intent);
				Thread.sleep(120000);
//				Thread.sleep(10000);
			}
			catch(InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

}
