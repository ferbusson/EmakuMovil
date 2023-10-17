package com.example.emakumovil.modules.cartera;

import java.nio.channels.SocketChannel;

import org.jdom2.Document;
import org.jdom2.Element;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.example.emakumovil.R;
import com.example.emakumovil.components.DatePickerFragment;
import com.example.emakumovil.components.DialogClickEvent;
import com.example.emakumovil.components.DialogClickListener;
import com.example.emakumovil.components.SearchDataDialog;
import com.example.emakumovil.communications.SocketConnector;
import com.example.emakumovil.communications.SocketWriter;

public class InformesCXPActivity extends Activity implements OnClickListener, DialogClickListener {

	private Button bt_desde;
	private Button bt_hasta;
	private Button bt_tercero;
	private Button bt_cxphistorico;
	private Button bt_cxppendiente;
	private Button bt_cxppendienter;
	private Button bt_cxpvencida;
	private Button bt_cxpvencidar;
	
	private SearchDataDialog buscarTercero;
	private String id_char;
	private String fdesde= new String();
	private String fhasta= new String();

	private DatePickerFragment desde;
	private DatePickerFragment hasta;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_informes_cxp);
		bt_desde = (Button)findViewById(R.id.bt_desde);
		bt_desde.setOnClickListener(this);
		bt_hasta = (Button)findViewById(R.id.bt_hasta);
		bt_hasta.setOnClickListener(this);
		bt_tercero = (Button)findViewById(R.id.bt_tercero);
		bt_tercero.setOnClickListener(this);
		bt_cxphistorico = (Button)findViewById(R.id.bt_cxphistorico);
		bt_cxphistorico.setOnClickListener(this);
		bt_cxppendiente = (Button)findViewById(R.id.bt_cxppendiente);
		bt_cxppendiente.setOnClickListener(this);
		bt_cxppendienter = (Button)findViewById(R.id.bt_cxppendienter);
		bt_cxppendienter.setOnClickListener(this);
		bt_cxpvencida = (Button)findViewById(R.id.bt_cxpvencida);
		bt_cxpvencida.setOnClickListener(this);
		bt_cxpvencidar = (Button)findViewById(R.id.bt_cxpvencidar);
		bt_cxpvencidar.setOnClickListener(this);
		
		desde = new DatePickerFragment(R.id.bt_desde);
		desde.addDialogClickListener(this);
		hasta = new DatePickerFragment(R.id.bt_hasta);
		hasta.addDialogClickListener(this);
		buscarTercero = new SearchDataDialog(getString(R.string.tercero),"MVSEL0035",null,R.id.bt_tercero);
		buscarTercero.addDialogClickListener(this);

	}

	public void onResume() {
		super.onResume();
		if (desde!=null) {
			try {
				desde.dismiss();
			}
			catch(NullPointerException npe) {}
		}
		if (hasta!=null) {
			try {
				hasta.dismiss();
			}
			catch(NullPointerException npe) {}
		}
		if (buscarTercero!=null) {
			try {
				buscarTercero.dismiss();
			}
			catch(NullPointerException npe) {}
		}
	}


	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if (v.getId() == R.id.bt_desde) {
			desde.show(getFragmentManager(),getString(R.string.fecha));
		} 
		else if (v.getId()==R.id.bt_hasta) {
			hasta.show(getFragmentManager(),getString(R.string.fecha));
		} 
		else if (v.getId()==R.id.bt_tercero) {
			buscarTercero.show(getFragmentManager(),getString(R.string.tercero));
		}
		else if (v.getId()==R.id.bt_cxphistorico) {
			sendReportRequest("CRE0069",fdesde,fhasta); 
		}
		else if (v.getId()==R.id.bt_cxppendiente) {
			sendReportRequest("CRE0032",fdesde,fhasta);
		}
		else if (v.getId()==R.id.bt_cxppendienter) {
			sendReportRequest("CRE0033",fdesde,fhasta);
		}
		else if (v.getId()==R.id.bt_cxpvencida) {
			sendReportRequest("CRE0034",fdesde,fhasta);
		}
		else if (v.getId()==R.id.bt_cxpvencidar) {
			sendReportRequest("CRE0035",fdesde,fhasta);
		}
	}

	private void sendReportRequest(final String idReport,final String desde,final String hasta) {
		if (!getString(R.string.desde).equals(desde)) {
			final Document transaction = new Document();
			Element raiz = new Element("REPORTREQUEST");
			Element driver = new Element("driver");
			driver.setText(idReport);
			Element id = new Element("id");
			id.setText("T8");
			transaction.setRootElement(raiz);
			raiz.addContent(driver);
			raiz.addContent(id);
			Element args = new Element("package");
			String sdesde = desde==null || desde.equals(getString(R.string.desde))?"":desde;
			String sid_char = id_char!=null && !"".equals(id_char)?id_char:"%";
			if (idReport.equals("CRE0069")) {
				if (!desde.equals("")) {
					args.addContent(new Element("field").setText(sdesde));
					args.addContent(new Element("field").setText(hasta));
					args.addContent(new Element("field").setText(sid_char.trim()));
					args.addContent(new Element("field").setText(sid_char.trim()));
					raiz.addContent(args);
					this.runOnUiThread(new Runnable() {
			            public void run() {
					        SocketChannel socket = SocketConnector.getSock();
					        Log.d("EMAKU","EMAKU: Socket: "+socket);
			            	SocketWriter.writing(socket, transaction);
			            }
					});
				}
				else {
		    		Toast.makeText(InformesCXPActivity.this,getString(R.string.error_finicial), Toast.LENGTH_LONG).show();
				}
			}
			else {
				args.addContent(new Element("field").setText("0"));
				args.addContent(new Element("field").setText(sdesde));
				args.addContent(new Element("field").setText(hasta));
				args.addContent(new Element("field").setText(""));
				args.addContent(new Element("field").setText("22"));
				args.addContent(new Element("field").setText(sid_char.trim()));
				args.addContent(new Element("field").setText(sid_char.trim()));
				raiz.addContent(args);
				this.runOnUiThread(new Runnable() {
		            public void run() {
				        SocketChannel socket = SocketConnector.getSock();
				        Log.d("EMAKU","EMAKU: Socket: "+socket);
		            	SocketWriter.writing(socket, transaction);
		            }
				});
			}
		}
		else {
    		Toast.makeText(InformesCXPActivity.this,getString(R.string.error_ffinal), Toast.LENGTH_LONG).show();
		}
	}


	@Override
	public void dialogClickEvent(DialogClickEvent e) {
		// TODO Auto-generated method stub
		if (R.id.bt_desde==e.getIdobject()){
			bt_desde.setText(e.getValue());
			fdesde = e.getValue();
		}
		else if (R.id.bt_hasta==e.getIdobject()) {
			bt_hasta.setText(e.getValue());
			fhasta=e.getValue();
		}
		else if (R.id.bt_tercero==e.getIdobject()) {
			bt_tercero.setText(e.getValue());
			id_char=e.getValue();
		}
	}

}
