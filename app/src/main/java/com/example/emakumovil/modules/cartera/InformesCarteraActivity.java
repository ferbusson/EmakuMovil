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
import com.example.emakumovil.comunications.SocketConnector;
import com.example.emakumovil.comunications.SocketWriter;

public class InformesCarteraActivity extends Activity implements OnClickListener, DialogClickListener {

	private Button bt_desde;
	private Button bt_hasta;
	private Button bt_tercero;
	private Button bt_chistorico;
	private Button bt_cpendiente;
	private Button bt_cpendienter;
	private Button bt_cvencida;
	private Button bt_cvencidar;
	
	private SearchDataDialog buscarTercero;
	private String id_char;
	private String fdesde= new String();
	private String fhasta= new String();

	private DatePickerFragment desde;
	private DatePickerFragment hasta;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_informes_cartera);
		bt_desde = (Button)findViewById(R.id.bt_desde);
		bt_desde.setOnClickListener(this);
		bt_hasta = (Button)findViewById(R.id.bt_hasta);
		bt_hasta.setOnClickListener(this);
		bt_tercero = (Button)findViewById(R.id.bt_tercero);
		bt_tercero.setOnClickListener(this);
		bt_chistorico = (Button)findViewById(R.id.bt_chistorico);
		bt_chistorico.setOnClickListener(this);
		bt_cpendiente = (Button)findViewById(R.id.bt_cpendiente);
		bt_cpendiente.setOnClickListener(this);
		bt_cpendienter = (Button)findViewById(R.id.bt_cpendienter);
		bt_cpendienter.setOnClickListener(this);
		bt_cvencida = (Button)findViewById(R.id.bt_cvencida);
		bt_cvencida.setOnClickListener(this);
		bt_cvencidar = (Button)findViewById(R.id.bt_cvencidar);
		bt_cvencidar.setOnClickListener(this);
		
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
		else if (v.getId()==R.id.bt_chistorico) {
			sendReportRequest("CRE0068",fdesde,fhasta); 
		}
		else if (v.getId()==R.id.bt_cpendiente) {
			sendReportRequest("CRE0010",fdesde,fhasta);
		}
		else if (v.getId()==R.id.bt_cpendienter) {
			sendReportRequest("CRE0011",fdesde,fhasta);
		}
		else if (v.getId()==R.id.bt_cvencida) {
			sendReportRequest("CRE0012",fdesde,fhasta);
		}
		else if (v.getId()==R.id.bt_cvencidar) {
			sendReportRequest("CRE0013",fdesde,fhasta);
		}
	}
	
	private void sendReportRequest(final String idReport,final String desde,final String hasta) {
			if (!getString(R.string.desde).equals(desde)) {
				final Document transaction = new Document();
				Element raiz = new Element("REPORTREQUEST");
				Element driver = new Element("driver");
				driver.setText(idReport);
				Element id = new Element("id");
				id.setText("T7");
				transaction.setRootElement(raiz);
				raiz.addContent(driver);
				raiz.addContent(id);
				Element args = new Element("package");
				String sdesde = desde==null || desde.equals(getString(R.string.desde))?"":desde;
				String sid_char = id_char!=null && !"".equals(id_char)?id_char:"%";

				if (idReport.equals("CRE0068")) {
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
			    		Toast.makeText(InformesCarteraActivity.this,getString(R.string.error_finicial), Toast.LENGTH_LONG).show();
					}
				}
				else {
					args.addContent(new Element("field").setText("0"));
					args.addContent(new Element("field").setText(sdesde));
					args.addContent(new Element("field").setText(hasta));
					args.addContent(new Element("field").setText(""));
					args.addContent(new Element("field").setText("1305"));
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
	    		Toast.makeText(InformesCarteraActivity.this,getString(R.string.error_ffinal), Toast.LENGTH_LONG).show();
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
