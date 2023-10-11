package com.example.emakumovil.modules.inventario;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.emakumovil.R;
import com.example.emakumovil.components.DatePickerFragment;
import com.example.emakumovil.components.DialogClickEvent;
import com.example.emakumovil.components.DialogClickListener;
import com.example.emakumovil.components.ProgressDialogManager;
import com.example.emakumovil.components.SearchDataDialog;
import com.example.emakumovil.components.SelectedDataDialog;
import com.example.emakumovil.components.TimePickerFragment;

public class FitInventoryActivity extends Activity implements OnClickListener,DialogClickListener {

	private Button bt_fecha;
	private Button bt_hora;
	private Button bt_proveedor;
	private Button bt_marca;
	private Button bt_referencia;
	private Button bt_bodega;
	private Button bt_limpiar;
	private Button bt_consultar;
	private EditText et_desde;
	private EditText et_hasta;
	
	private String id_proveedor;
	private String id_marca;
	private String id_referencia;
	private String id_bodega;
	private SearchDataDialog search;
	private SelectedDataDialog selected;
	static final int DATE_DIALOG_ID = 0;
	static final int TIME_DIALOG_ID = 1;
	static final int SEARCH_DIALOG_ID = 2;
	private DatePickerFragment DPfecha;
	private TimePickerFragment hora;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_fit_inventory);
		bt_fecha = (Button) findViewById(R.id.bt_fecha);
		bt_fecha.setOnClickListener(this);
		DPfecha = new DatePickerFragment(R.id.bt_fecha);
		DPfecha.addDialogClickListener(this);
		bt_hora = (Button) findViewById(R.id.bt_hora);
		bt_hora.setOnClickListener(this);
		hora = new TimePickerFragment(R.id.bt_hora);
		hora.addDialogClickListener(this);
		bt_proveedor = (Button) findViewById(R.id.bt_proveedor);
		bt_proveedor.setOnClickListener(this);
		bt_marca = (Button) findViewById(R.id.bt_marca);
		bt_marca.setOnClickListener(this);
		bt_referencia = (Button) findViewById(R.id.bt_referencia);
		bt_referencia.setOnClickListener(this);
		bt_bodega = (Button) findViewById(R.id.bt_bodega);
		bt_bodega.setOnClickListener(this);
		bt_limpiar = (Button) findViewById(R.id.bt_limpiar);
		bt_limpiar.setOnClickListener(this);
		bt_consultar = (Button) findViewById(R.id.bt_consultar);
		bt_consultar.setOnClickListener(this);
		et_desde = (EditText) findViewById(R.id.et_desde);
		et_hasta = (EditText) findViewById(R.id.et_hasta);
	}


	final static Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			int total = msg.getData().getInt("total");
			ProgressDialogManager.currentDialog().setProgress(total);
			if (total <= 0) {
				try {
					ProgressDialogManager.dismissCurrent();
				} catch (IllegalArgumentException e) {
				}
			}
		}
	};
     
	public void onResume() {
		super.onResume();
		if (DPfecha!=null) {
			try {
				DPfecha.dismiss();
			}
			catch(NullPointerException npe) {}
		}
		if (hora!=null) {
			try {
				hora.dismiss();
			}
			catch(NullPointerException npe) {}
		}
	}


	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if (v.getId() == R.id.bt_fecha) {
			DPfecha.show(getFragmentManager(),getString(R.string.fecha));
		} 
		else if (v.getId()==R.id.bt_hora) {
			hora.show(getFragmentManager(),getString(R.string.hora));
		}
		else if (v.getId()==R.id.bt_proveedor) {
			search = new SearchDataDialog(getString(R.string.buscar_proveedor),"MVSEL0010",null,R.id.bt_proveedor);
			search.addDialogClickListener(this);
			search.show(getFragmentManager(),getString(R.string.buscar_proveedor));
		}
		else if (v.getId()==R.id.bt_marca) {
			selected = new SelectedDataDialog(R.id.bt_marca,getString(R.string.buscar_marca),"MVSEL0011",null);
			selected.addDialogClickListener(this);
			selected.show(getFragmentManager(),getString(R.string.buscar_marca));
		}
		else if (v.getId()==R.id.bt_referencia) {
			System.out.println("argumento: "+id_marca);
			selected = new SelectedDataDialog(R.id.bt_referencia,getString(R.string.buscar_referencia),"MVSEL0012",new String[]{id_marca});
			selected.addDialogClickListener(this);
			selected.show(getFragmentManager(),getString(R.string.buscar_referencia));
			
		}
		else if (v.getId()==R.id.bt_bodega) {
			selected = new SelectedDataDialog(R.id.bt_bodega,getString(R.string.buscar_bodega),"MVSEL0013",null);
			selected.addDialogClickListener(this);
			selected.show(getFragmentManager(),getString(R.string.buscar_referencia));
			
		}
		else if (v.getId()==R.id.bt_limpiar) {
			bt_fecha.setText(getString(R.string.fecha));
			bt_hora.setText(getString(R.string.hora));
			bt_proveedor.setText(getString(R.string.proveedor));
			bt_marca.setText(getString(R.string.marca));
			bt_referencia.setText(getString(R.string.referencia));
			bt_bodega.setText(getString(R.string.bodega));
			et_desde.setText("");
			et_hasta.setText("");
			id_proveedor="";
			id_marca="";
			id_referencia="";
			id_bodega="";
		}
		else if (v.getId()==R.id.bt_consultar) {
			if (!bt_fecha.getText().toString().equals(getString(R.string.fecha))) {
				if (!bt_hora.getText().toString().equals(getString(R.string.hora))) {
					if (!bt_bodega.getText().toString().equals(getString(R.string.bodega))) {
						if (!et_desde.getText().toString().equals("")) {
							if (!et_hasta.getText().toString().equals("")) {
								Intent i = new Intent(this,ListFitInventoryActivity.class);
							   	 i.putExtra("fecha", bt_fecha.getText().toString());
							   	 i.putExtra("hora", bt_hora.getText().toString());
							   	 i.putExtra("hora",bt_hora.getText().toString());
							   	 i.putExtra("id_proveedor",id_proveedor);
							   	 i.putExtra("id_marca",id_marca);
							   	 i.putExtra("id_referencia",id_referencia);
							   	 i.putExtra("id_bodega",id_bodega);
							   	 i.putExtra("bodega",bt_bodega.getText().toString());
							   	 i.putExtra("desde",et_desde.getText().toString());
							   	 i.putExtra("hasta",et_hasta.getText().toString());
								this.startActivity(i);
							}
							else {
			            		Toast.makeText(this,getString(R.string.error_rhasta), Toast.LENGTH_LONG).show();
							}
						}
						else {
		            		Toast.makeText(this,getString(R.string.error_rdesde), Toast.LENGTH_LONG).show();
						}
					}
					else {
	            		Toast.makeText(this,getString(R.string.error_bodega), Toast.LENGTH_LONG).show();
					}
				}
				else {
            		Toast.makeText(this,getString(R.string.error_hora), Toast.LENGTH_LONG).show();
				}
			}
			else {
        		Toast.makeText(this,getString(R.string.error_fecha), Toast.LENGTH_LONG).show();
			}
		}
	}

	 public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);
		System.out.println("se cerro");
	 }


	@Override
	public void dialogClickEvent(DialogClickEvent e) {
		// TODO Auto-generated method stub
		if (R.id.bt_proveedor==e.getIdobject()) {
			if (e.getValue()!=null) { 
				bt_proveedor.setText(e.getValue());
				id_proveedor = e.getId();
			}
		}
		else if (R.id.bt_marca==e.getIdobject()) {
			if (e.getValue()!=null) { 
				bt_marca.setText(e.getValue());
				bt_referencia.setText(getString(R.string.referencia));
				id_marca = e.getId();
			}
		}
		else if (R.id.bt_referencia==e.getIdobject()) {
			if (e.getValue()!=null) { 
				bt_referencia.setText(e.getValue());
				id_referencia = e.getId();
			}
		}
		else if (R.id.bt_bodega==e.getIdobject()) {
			if (e.getValue()!=null) { 
				bt_bodega.setText(e.getValue());
				id_bodega = e.getId();
			}
		}
		else if (R.id.bt_fecha==e.getIdobject()) {
			bt_fecha.setText(e.getValue());
		}
		else if (R.id.bt_hora==e.getIdobject()) {
			bt_hora.setText(e.getValue());
		}
		
	}

}
