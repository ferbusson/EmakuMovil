package com.example.emakumovil.widgets.ventas;

import java.util.Iterator;
import java.util.List;

import org.jdom2.Document;
import org.jdom2.Element;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.RingtonePreference;
import android.view.View;
import android.view.View.OnClickListener;

import com.example.emakumovil.R;
import com.example.emakumovil.components.AnswerEvent;
import com.example.emakumovil.components.AnswerListener;
import com.example.emakumovil.components.SearchQuery;
import com.example.emakumovil.components.TimePickerPreference;

public class ResolutionChooseWidgetFragment extends PreferenceFragment
		implements AnswerListener, OnClickListener {

	private String id_widget;
	private static final String ACTION_RESOLUTION_CHOOSE = "com.example.emakumovil.widgets.ventas.ID_RESOLUTION";

	private CheckBoxPreference activar_notificacion;
	private TimePickerPreference timepicker;
	private RingtonePreference ringtone;
	private ListPreference listresolutions;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle extras = getActivity().getIntent().getExtras();
		if (extras != null) {
			id_widget = extras.getString("id_widget");
		}

		addPreferencesFromResource(R.xml.resolution_widget_preferences);
		listresolutions = (ListPreference) findPreference("list_resoluciones");
		activar_notificacion = (CheckBoxPreference) findPreference("activa_notificacion");
		ringtone = (RingtonePreference) findPreference("ringtone");
		timepicker = (TimePickerPreference) findPreference("timepicker");
		loadStatusNotiffy();

		listresolutions
				.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {

					@Override
					public boolean onPreferenceChange(Preference preference,
							Object newValue) {
						// TODO Auto-generated method stub
						String textValue = newValue.toString();
						int index = listresolutions.findIndexOfValue(textValue);
						CharSequence[] entries = listresolutions.getEntries();
						CharSequence[] entryValues = listresolutions
								.getEntryValues();
						preference.setSummary(index >= 0 ? entries[index]
								: null);
						saveIdWidget(id_widget, entryValues[index].toString());
						Intent intent = new Intent(ACTION_RESOLUTION_CHOOSE);
						intent.putExtra("id_resolucion",
								entryValues[index].toString());
						intent.putExtra("id_widget", id_widget);
						getActivity().getApplicationContext().sendBroadcast(
								intent);
						return true;
					}
				});

		activar_notificacion
				.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {

					@Override
					public boolean onPreferenceChange(Preference preference,
							Object newValue) {
						// TODO Auto-generated method stub
						boolean estado = Boolean.parseBoolean(newValue
								.toString());
						timepicker.setEnabled(estado);
						ringtone.setEnabled(estado);
						return true;
					}
				});

		new SearchQuery(this, "MVSEL0079").start();

	}

	private void loadStatusNotiffy() {
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(getActivity()
						.getApplicationContext());
		boolean estado = prefs.getBoolean("activa_notificacion", false);
		timepicker.setEnabled(estado);
		ringtone.setEnabled(estado);
	}

	@Override
	public void arriveAnswerEvent(AnswerEvent e) {
		// TODO Auto-generated method stub

		Document doc = e.getDocument();
		if (doc!=null && e.getSqlCode().equals("MVSEL0079")) {
			Element elm = doc.getRootElement();
			List<Element> data = elm.getChildren("row");
			CharSequence[] entries = new CharSequence[data.size()];
			CharSequence[] entryvalues = new CharSequence[data.size()];
			for (int k = 0; k < data.size(); k++) {
				Element row = (Element) data.get(k);
				System.out.println("cols: " + row.getChildren().size());
				Iterator<Element> j = row.getChildren().iterator();
				entryvalues[k] = ((Element) j.next()).getValue();
				entries[k] = ((Element) j.next()).getValue();
			}
			listresolutions.setEntries(entries);
			listresolutions.setEntryValues(entryvalues);
		}
	}

	@Override
	public boolean containSqlCode(String sqlCode) {
		// TODO Auto-generated method stub
		return false;
	}

	private void saveIdWidget(String idWidget, String idResolucion) {
		SharedPreferences connections = getActivity().getSharedPreferences(
				"idWidgetResolution", Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = connections.edit();
		editor.putString(idResolucion, idWidget);
		editor.putString(idWidget, idResolucion);
		editor.commit();
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
	}

}
