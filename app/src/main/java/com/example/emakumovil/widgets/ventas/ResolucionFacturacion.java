package com.example.emakumovil.widgets.ventas;

import java.util.Calendar;
import java.util.Iterator;

import org.jdom2.Document;
import org.jdom2.Element;

import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.RemoteViews;

import com.Ostermiller.util.StringTokenizer;
import com.example.emakumovil.R;
import com.example.emakumovil.components.AnswerEvent;
import com.example.emakumovil.components.AnswerListener;
import com.example.emakumovil.components.SearchQuery;
import com.example.emakumovil.communications.SocketConnector;
import com.example.emakumovil.misc.settings.ConfigFileHandler;
import com.example.emakumovil.modules.ventas.ChangeResolutionActivity;

public class ResolucionFacturacion extends AppWidgetProvider implements
		AnswerListener {

	private RemoteViews remoteViews;
	private String resolucion = "Cargando...";
	private String prefix;
	private String id_widget;
	private String max = "";
	private String actual = "";
	private String cuota = "";
	private static Context context;
	private String id_resolucion = "FA";
	private static final String ACTION_RESOLUTION_CHOOSE = "com.example.emakumovil.widgets.ventas.ID_RESOLUTION";
	private static final String ACTION_UPDATE_RESOLUTION = "com.example.emakumovil.widgets.ventas.UPDATE_RESOLUTION";

	public ResolucionFacturacion() {
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		System.out.println("recibio: " + intent.getAction());
		final ComponentName appWidgets = new ComponentName(
				context.getPackageName(), getClass().getName());
		final AppWidgetManager appWidgetManager = AppWidgetManager
				.getInstance(context);
		final int ids[] = appWidgetManager.getAppWidgetIds(appWidgets);
		if (ids.length > 0) {
			System.out.println("llamando a update...");
			onUpdate(context, appWidgetManager, ids);
		}

		if (intent.getAction().equals(
				android.appwidget.AppWidgetManager.ACTION_APPWIDGET_ENABLED)) {
			onEnabled(context);
		} else if (intent
				.getAction()
				.equals(android.appwidget.AppWidgetManager.ACTION_APPWIDGET_OPTIONS_CHANGED)) {
			Intent configWidget = new Intent(context,
					ResolutionChooseWidgetActivity.class);
			id_widget = String.valueOf(ids[ids.length - 1]);
			configWidget.putExtra("id_widget", id_widget);
			configWidget.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(configWidget);

		} else if (intent.getAction().equals(ACTION_RESOLUTION_CHOOSE)) {
			System.out.println("parametro de resolucion actualizado");
			if (id_widget == null) {
				id_widget = String.valueOf(ids[0]);
			}
			updateQueryWidget(context, ids);
		} else if (intent.getAction().equals(ACTION_UPDATE_RESOLUTION)) {
			System.out.println("Actualizando Resolucion");
			updateQueryWidget(context, ids);
		}
		else if (intent.getAction()==null) {
	        Bundle extras = intent.getExtras();
	        if(extras!=null) {
	            int widgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
	            // do something for the widget that has appWidgetId = widgetId
	            System.out.println("***** SE HIZO CLICK AL WG "+widgetId);
	        }
	    }

	}

	@Override
	public void onDisabled(Context context) {
		context.stopService(new Intent(context, BroadCastWidgetService.class));
	}

	public void onEnabled(Context context) {
		ResolucionFacturacion.context = context;
		if (ConfigFileHandler.getCurrentCompany().equals("")) {
			loadInfoConnecctions(context);
		}
		IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_ON);
		filter.addAction(Intent.ACTION_SCREEN_OFF);
		BroadcastReceiver mReceiver = new PowerScreenWidget();
		context.getApplicationContext().registerReceiver(mReceiver, filter);

		context.startService(new Intent(context, BroadCastWidgetService.class));
		SharedPreferences connections = context.getSharedPreferences(
				"idWidgetResolution", Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = connections.edit();
		editor.clear();
		editor.commit();
		final ComponentName appWidgets = new ComponentName(
				context.getPackageName(), getClass().getName());
		final AppWidgetManager appWidgetManager = AppWidgetManager
				.getInstance(context);
		final int ids[] = appWidgetManager.getAppWidgetIds(appWidgets);

		Intent configWidget = new Intent(context,
				ResolutionChooseWidgetActivity.class);
		 
		
	    
		id_widget = String.valueOf(ids[ids.length - 1]);
		configWidget.putExtra("id_widget", id_widget);
		configWidget.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(configWidget);
	}

	private boolean isMyServiceRunning(Context context) {
		ActivityManager manager = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		for (RunningServiceInfo service : manager
				.getRunningServices(Integer.MAX_VALUE)) {
			if ("com.example.emakumovil.widgets.ventas.BroadCastWidgetService"
					.equals(service.service.getClassName())) {
				return true;
			}
		}
		return false;
	}

	public void onUpdate(Context context, AppWidgetManager appWidgetManager,
			int[] appWidgetIds) {
		ResolucionFacturacion.context = context;
		if (isMyServiceRunning(context)) {
			System.out.println("el servicio esta ok");
		} else {
			System.out.println("El puto servicio se murio y lo voy a levantar");
			context.startService(new Intent(context,
					BroadCastWidgetService.class));
		}
		
	}

	private PendingIntent pendingIntent;
	
	public void scheduleAlarm(String resolucion,Context c) {
		String time = getAlarmTime(c);
		if (!time.equals("")) {
			StringTokenizer stime = new StringTokenizer(time,":");
			String hora = stime.nextToken();
			String minuto = stime.nextToken();
			
			Calendar calendar = Calendar.getInstance();
			calendar.set(Calendar.HOUR_OF_DAY,Integer.parseInt(hora));
			calendar.set(Calendar.MINUTE, Integer.parseInt(minuto));
			calendar.set(Calendar.SECOND, 0);

			if (Calendar.getInstance().getTimeInMillis()<calendar.getTimeInMillis()) {
				Intent myIntent = new Intent(c, ResolutionWidgetAlarmReceiver.class);
				myIntent.putExtra("resolucion",resolucion);
			    pendingIntent = PendingIntent.getBroadcast(c, 0, myIntent,0);
			    AlarmManager alarmManager = (AlarmManager)c.getSystemService(Context.ALARM_SERVICE);
			    alarmManager.set(AlarmManager.RTC, calendar.getTimeInMillis(), pendingIntent);
			}
		}
	}

	public String getAlarmTime(Context c) {
		SharedPreferences pref = PreferenceManager
				.getDefaultSharedPreferences(c);
		return pref.getString("timepicker", "");
	}

	public static String getRingtone(Context c) {
		SharedPreferences pref = PreferenceManager
				.getDefaultSharedPreferences(c);
		return pref.getString("ringtone", "");
	}
	
	@Override
	public void arriveAnswerEvent(AnswerEvent e) {
		// TODO Auto-generated method stub
		Document doc = e.getDocument();
		if (doc != null) {
			if (context != null) {
				final Element elm = doc.getRootElement();
				if (e.getSqlCode().equals("MVSEL0078")) {
					Iterator<Element> i = elm.getChildren("row").iterator();
					while (i.hasNext()) {
						Element row = (Element) i.next();
						Iterator<Element> j = row.getChildren().iterator();
						resolucion = ((Element) j.next()).getValue();
						max = ((Element) j.next()).getValue();
						actual = ((Element) j.next()).getValue();
						cuota = ((Element) j.next()).getValue();
						prefix = ((Element) j.next()).getValue();
						remoteViews = new RemoteViews(context.getPackageName(),
								R.layout.resolucion_facturacion_layout);
						if (Float.parseFloat(cuota) > 80.0
								&& Float.parseFloat(cuota) < 90.0) {
							remoteViews.setTextColor(R.id.tv_actual,
									Color.YELLOW);
							remoteViews.setTextColor(R.id.tv_cuota,
									Color.YELLOW);
						} else if (Float.parseFloat(cuota) >= 90.0) {
							remoteViews.setTextColor(R.id.tv_actual, Color.RED);
							remoteViews.setTextColor(R.id.tv_cuota, Color.RED);
							scheduleAlarm(resolucion,context);
						}
						remoteViews.setTextViewText(R.id.tv_resolucion,
								resolucion);
						remoteViews.setTextViewText(R.id.tv_max,
								context.getString(R.string.max) + " " + max);
						remoteViews.setTextViewText(R.id.tv_actual,
								context.getString(R.string.actual) + " "
										+ actual);
						remoteViews.setTextViewText(R.id.tv_cuota,
								context.getString(R.string.cuota) + " " + cuota
										+ "%");
						remoteViews.setProgressBar(R.id.progressBar, 100,
								(int) Double.parseDouble(cuota), false);

						final AppWidgetManager appWidgetManager = AppWidgetManager
								.getInstance(context);
						String id_wg = getPrefix(context, prefix);
						
						Intent modifyResolution = new Intent(context,ChangeResolutionActivity.class);
						
						modifyResolution.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						modifyResolution.putExtra("prefijo",prefix);
						modifyResolution.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,id_wg);
						
						if (!id_wg.equals("")) {
						    PendingIntent modifyResolutionPendingIntent = PendingIntent.getActivity(context, Integer.parseInt(id_wg),modifyResolution, 0);
						    remoteViews.setOnClickPendingIntent(R.id.ly_widget, modifyResolutionPendingIntent);
						}
						
						if (!id_wg.equals("")) {
							appWidgetManager.updateAppWidget(
									Integer.parseInt(id_wg), remoteViews);
						} else {
							System.out
									.println("no hay prefijo asignado al widget");
						}
					}
				}
			} else {
				System.out.print("Contexto: {" + context + "} ");
			}
		} else {
			System.out.println("Systema sin conexion...");
		}
	}

	public String getPrefix(Context context, String id_widget) {
		SharedPreferences connections = context.getSharedPreferences(
				"idWidgetResolution", Context.MODE_PRIVATE);
		return connections.getString(id_widget, "");
	}

	@Override
	public boolean containSqlCode(String sqlCode) {
		// TODO Auto-generated method stub
		return false;
	}

	public static void loadInfoConnecctions(Context context) {
		SharedPreferences connections = context.getSharedPreferences(
				"lastConnectionEmaku", Context.MODE_PRIVATE);
		String empresa = connections.getString("empresa", "");
		String usuario = connections.getString("usuario", "");
		String passwd = connections.getString("passwd", "");
		ConfigFileHandler.setCurrentCompany(empresa);
		SocketConnector.setDatabase(empresa);
		SocketConnector.setUser(usuario);
		SocketConnector.setPassword(passwd);
	}

	private void updateQueryWidget(final Context context, final int[] ids) {
		// TODO Auto-generated method stub
		Thread t = new Thread() {
			public void run() {

				try {
					id_widget = String.valueOf(ids[ids.length - 1]);
					for (int z = 0; z < ids.length; z++) {
						String value = getPrefix(context,
								String.valueOf(ids[z]));
						if (value != null && !value.equals("")) {
							System.out
									.println("Lanzando query con retorno de prefijo ["
											+ value + "]...........");
							new SearchQuery(ResolucionFacturacion.this,
									"MVSEL0078", new String[] { value })
									.start();
						} else {
							System.out
									.println("Lanzando query con prefijo por defecto ----------------------------");
							new SearchQuery(ResolucionFacturacion.this,
									"MVSEL0078", new String[] { id_resolucion })
									.start();
						}
						Thread.sleep(10000);
					}
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		};
		if (ids.length > 0) {
			t.start();
		}
	}
}
