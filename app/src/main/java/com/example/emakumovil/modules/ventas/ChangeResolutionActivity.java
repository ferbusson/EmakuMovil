package com.example.emakumovil.modules.ventas;

import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.List;

import org.jdom2.Document;
import org.jdom2.Element;

import com.example.emakumovil.R;
import com.example.emakumovil.R.layout;
import com.example.emakumovil.R.menu;
import com.example.emakumovil.components.AnswerEvent;
import com.example.emakumovil.components.AnswerListener;
import com.example.emakumovil.components.DatePickerFragment;
import com.example.emakumovil.components.DialogClickEvent;
import com.example.emakumovil.components.DialogClickListener;
import com.example.emakumovil.components.SearchQuery;
import com.example.emakumovil.comunications.SocketConnector;
import com.example.emakumovil.comunications.SocketWriter;
import com.example.emakumovil.control.ClientHeaderValidator;

import android.os.Bundle;
import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class ChangeResolutionActivity extends Activity implements AnswerListener, OnClickListener, DialogClickListener {

	private TextView tv_documento;
	private TextView tv_resolucion;
	private TextView tv_fecha_resolucion;
	private TextView tv_numeracion_resolucion;
	private TextView tv_numero_actual_resolucion;
	
	private EditText et_resolucion;
	
	private Button bt_fecha;
	
	private EditText et_prefijo;
	private EditText et_desde;
	private EditText et_hasta;
	
	private String prefijo;
	
	private Button bt_guardar;
	
	private DatePickerFragment DPfecha;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_change_resolution);
		
		DPfecha = new DatePickerFragment(R.id.bt_fecha);
		DPfecha.addDialogClickListener(this);
		
		Bundle args =  getIntent().getExtras();
		
		
		if(args!=null){
			prefijo=args.getString("prefijo");
			System.out.println("el id_widget: "+args.getString(AppWidgetManager.EXTRA_APPWIDGET_ID));
			System.out.println("Se lanzo la query 80");
			new SearchQuery(this,"MVSEL0080", new String[] {prefijo}).start();
		}
		
		tv_documento=(TextView)findViewById(R.id.tv_documento);
		tv_resolucion=(TextView)findViewById(R.id.tv_resolucion);
		tv_fecha_resolucion=(TextView)findViewById(R.id.tv_fecha_resolucion);
		tv_numeracion_resolucion=(TextView)findViewById(R.id.tv_numeracion_resolucion);
		tv_numero_actual_resolucion=(TextView)findViewById(R.id.tv_numero_actual_resolucion);
		
		et_resolucion=(EditText) findViewById(R.id.et_resolucion);
		bt_fecha=(Button) findViewById(R.id.bt_fecha);
		bt_fecha.setOnClickListener(this);
		
		et_prefijo=(EditText) findViewById(R.id.et_prefijo);
		et_desde=(EditText) findViewById(R.id.et_desde);
		et_hasta=(EditText) findViewById(R.id.et_hasta);
		
		bt_guardar=(Button) findViewById(R.id.bt_guardar);
		bt_guardar.setOnClickListener(this);
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.change_resolution, menu);
		return true;
	}

	@Override
	public void arriveAnswerEvent(AnswerEvent e) {
		// TODO Auto-generated method stub
		Document doc = e.getDocument();
		final Element elm = doc.getRootElement();
		if (e.getSqlCode().equals("MVSEL0080")) {
			System.out.println("Llego la query 80");
			final List<Element> listRows = elm.getChildren("row");
			this.runOnUiThread(new Runnable() {
				public void run() {
					for (int i = 0; i < listRows.size(); i++) {
						Element Erow = listRows.get(i);
						List<Element> Lcol = Erow.getChildren();
						tv_documento.setText(Lcol.get(0).getText());
						tv_resolucion.setText(Lcol.get(1).getText());
						tv_fecha_resolucion.setText(Lcol.get(2).getText());
						tv_numeracion_resolucion.setText(Lcol.get(3).getText());
						tv_numero_actual_resolucion.setText(Lcol.get(4).getText());
					}
				}
			});
		}
	}

	@Override
	public boolean containSqlCode(String sqlCode) {
		// TODO Auto-generated method stub
		return false;
	}


	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if (v.getId()==R.id.bt_guardar) {
			sendTransaction();
		}
		else if(v.getId()==R.id.bt_fecha){
			DPfecha.show(getFragmentManager(),getString(R.string.fecha));
		}
	}

	private void sendTransaction() {
		final Document transaction = new Document();
		Element raiz = new Element("TRANSACTION");
		Element driver = new Element("driver");
		driver.setText("MVTR00020");
		Element id = new Element("id");
		id.setText("TMV7");
		transaction.setRootElement(raiz);
		raiz.addContent(driver);
		raiz.addContent(id);
		
		/*
		 * Resolucion
		 */
		
		Element res = new Element("package");
		Element resolucion = new Element("field").setText(et_resolucion.getText().toString());
		Element fecha = new Element("field").setText(bt_fecha.getText().toString());
		Element prefijo = new Element("field").setText(et_prefijo.getText().toString());
		Element desde = new Element("field").setText(et_desde.getText().toString());
		Element hasta = new Element("field").setText(et_hasta.getText().toString());
		Element codigo_tipo= new Element("field").setText(this.prefijo);
		
		res.addContent(resolucion);
		res.addContent(fecha);
		res.addContent(prefijo);
		res.addContent(desde);
		res.addContent(hasta);
		res.addContent(codigo_tipo);
		
		raiz.addContent(res);

		Thread t = new Thread() {
			public void run() {
				SocketChannel socket = SocketConnector.getSock();
				Log.d("EMAKU", "EMAKU: Socket: " + socket);
				SocketWriter.writing(socket, transaction);
			}
		};
		t.start();
	}


	@Override
	public void dialogClickEvent(DialogClickEvent e) {
		if (R.id.bt_fecha==e.getIdobject()) {
			bt_fecha.setText(e.getValue());
		}
		
	}


}
