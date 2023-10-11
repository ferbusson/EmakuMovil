package com.example.emakumovil.components;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.emakumovil.R;

public class ListTextValue extends ArrayAdapter<fieldsPhotoTextValue> {

	private ArrayList<fieldsPhotoTextValue> items;

	private LayoutInflater vi;
	private Activity main;

	public ListTextValue(Context context, int textViewResourceId,
			ArrayList<fieldsPhotoTextValue> items,Activity main) {
		super(context, textViewResourceId, items);
		this.items = items;
		this.main=main;
		Log.d("EMAKU ","EMAKU instanciando adaptador");
	}

	public ArrayList<fieldsPhotoTextValue> getItems() {
		return items;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v = convertView;
		if (v == null) {
			vi = (LayoutInflater) main.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		fieldsPhotoTextValue myData = items.get(position);
		if (myData != null) {
			v = vi.inflate(R.layout.infofototextovalor, null);
			TextView tv_descripcion = (TextView) v.findViewById(R.id.tv_descripcion);
			TextView tv_valor = (TextView) v.findViewById(R.id.tv_valor);
			ImageView im_identify = (ImageView)v.findViewById(R.id.im_identify);
			if (im_identify!=null) {
				System.out.println("foto "+myData.getPhoto());
				im_identify.setImageResource(myData.getPhoto());
			}
			if (tv_descripcion != null) {
				tv_descripcion.setText(myData.getText());
			}
			if (tv_valor != null) {
				tv_valor.setText(myData.getValue());
			}
		}
		return v;
	}

}
