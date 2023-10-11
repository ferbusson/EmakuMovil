package com.example.emakumovil.modules.arqueo;

import java.io.IOException;
import java.nio.channels.SocketChannel;
import java.text.DecimalFormat;
import java.util.Iterator;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import android.app.Activity;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TabHost;
import android.widget.TextView;

import com.example.emakumovil.R;
import com.example.emakumovil.components.AnswerEvent;
import com.example.emakumovil.components.AnswerListener;
import com.example.emakumovil.components.SearchQuery;
import com.example.emakumovil.communications.SocketConnector;
import com.example.emakumovil.communications.SocketWriter;

public class ChangeCashActivity extends Activity implements AnswerListener, OnClickListener {

	private TabHost tabs;
	private TextView tv_cajero;
	private TextView tv_fecha;
	private TextView tv_narqueo;
	
	private TextView tv_vpesos;
	private TextView tv_vtrm;
	private TextView tv_vdolares;
	private TextView tv_vpdolares;
	private TextView tv_vtcredito;
	private TextView tv_vtdebito;
	private TextView tv_vtregalo;
	private TextView tv_vfcredito;
	private TextView tv_vrecaudo;
	private TextView tv_vsistema;
	private TextView tv_vdiferencia;
	
	private EditTextDouble et_pcincuenta;
	private EditTextDouble et_pveinte;
	private EditTextDouble et_pdiez;
	private EditTextDouble et_pcinco;
	private EditTextDouble et_pdos;
	private EditTextDouble et_pmil;
	private EditTextDouble et_pmonedas;
	
	private EditTextDouble et_dcien;
	private EditTextDouble et_dcincuenta;
	private EditTextDouble et_dveinte;
	private EditTextDouble et_ddiez;
	private EditTextDouble et_dcinco;
	private EditTextDouble et_duno;
	private EditTextDouble et_dmonedas;
	
	private Button bt_modificar;
	
	private double pcincuenta;
	private double pveinte;
	private double pdiez;
	private double pcinco;
	private double pdos;
	private double pmil;
	private double pmonedas;
	
	private double dcien;
	private double dcincuenta;
	private double dveinte;
	private double ddiez;
	private double dcinco;
	private double duno;
	private double dmonedas;
	
	private double tcredito;
	private double tdebito;
	private double tregalo;
	private double fcredito;
	private double trm;
	
	private double vdolares;
	private double vsistema;
	
	private DecimalFormat df = new DecimalFormat("##,###,##0.00");

	private String ndocumento;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_change_cash);
		Bundle extras = getIntent().getExtras();
	
		tv_cajero = (TextView)findViewById(R.id.tv_cajero);
		tv_fecha = (TextView)findViewById(R.id.tv_fecha);
		tv_narqueo = (TextView)findViewById(R.id.tv_narqueo);

		et_pcincuenta = new EditTextDouble((EditText)findViewById(R.id.et_pcincuenta));
		et_pveinte = new EditTextDouble((EditText)findViewById(R.id.et_pveinte));
		et_pdiez = new EditTextDouble((EditText)findViewById(R.id.et_pdiez));
		et_pcinco = new EditTextDouble((EditText)findViewById(R.id.et_pcinco));
		et_pdos = new EditTextDouble((EditText)findViewById(R.id.et_pdos));
		et_pmil = new EditTextDouble((EditText)findViewById(R.id.et_pmil));
		et_pmonedas = new EditTextDouble((EditText)findViewById(R.id.et_pmonedas));
		
		et_dcien = new EditTextDouble((EditText)findViewById(R.id.et_dcien));
		et_dcincuenta = new EditTextDouble((EditText)findViewById(R.id.et_dcincuenta));
		et_dveinte = new EditTextDouble((EditText)findViewById(R.id.et_dveinte));
		et_ddiez = new EditTextDouble((EditText)findViewById(R.id.et_ddiez));
		et_dcinco = new EditTextDouble((EditText)findViewById(R.id.et_dcinco));
		et_duno = new EditTextDouble((EditText)findViewById(R.id.et_duno));
		et_dmonedas = new EditTextDouble((EditText)findViewById(R.id.et_dmonedas));
		
		bt_modificar = (Button)findViewById(R.id.bt_modificar);
		bt_modificar.setOnClickListener(this);

		tv_vpesos = (TextView)findViewById(R.id.tv_vpesos);
		tv_vtrm = (TextView)findViewById(R.id.tv_vtrm);
		tv_vdolares = (TextView)findViewById(R.id.tv_vdolares);
		tv_vpdolares = (TextView)findViewById(R.id.tv_vpdolares);
		
		tv_vtcredito = (TextView)findViewById(R.id.tv_vtcredito);
		tv_vtdebito = (TextView)findViewById(R.id.tv_vtdebito);
		tv_vtregalo = (TextView)findViewById(R.id.tv_vtregalo);

		tv_vfcredito = (TextView)findViewById(R.id.tv_vfcredito);
		
		tv_vrecaudo = (TextView)findViewById(R.id.tv_vrecaudo);
		tv_vsistema = (TextView)findViewById(R.id.tv_vsistema);
		tv_vdiferencia = (TextView)findViewById(R.id.tv_vdiferencia);
		

		tabs=(TabHost)findViewById(android.R.id.tabhost);
		tabs.setup();
		Resources res = getResources();

		// Pestaña 1 
		
		TabHost.TabSpec spec1=tabs.newTabSpec(getString(R.string.totales));
		spec1.setContent(R.id.ly_totales);
		spec1.setIndicator(getString(R.string.totales),
		    res.getDrawable(android.R.drawable.ic_dialog_alert));
		tabs.addTab(spec1);

		// Pestaña 2 
		
		TabHost.TabSpec spec2=tabs.newTabSpec(getString(R.string.pesos));
		spec2.setContent(R.id.ly_pesos);
		spec2.setIndicator(getString(R.string.pesos),
		    res.getDrawable(android.R.drawable.ic_dialog_alert));
		tabs.addTab(spec2);

		// Pestaña 3 
		
		TabHost.TabSpec spec3=tabs.newTabSpec(getString(R.string.dolares));
		spec3.setContent(R.id.ly_dolares);
		spec3.setIndicator(getString(R.string.dolares),
		    res.getDrawable(android.R.drawable.ic_dialog_alert));
		tabs.addTab(spec3);

		tabs.getTabWidget().getChildAt(0).setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
            		tabs.setCurrentTab(0);
            		calcular();
            }});

		tabs.getTabWidget().getChildAt(1).setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
            		tabs.setCurrentTab(1);
            		calcular();
            }});

		tabs.getTabWidget().getChildAt(2).setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
            		tabs.setCurrentTab(2);
            		calcular();
            }});

		if (extras!=null) {
			ndocumento = extras.getString("ndocumento");
			vsistema = extras.getDouble("vsistema");
			tv_cajero.setText(extras.getString("usuario"));
			tv_fecha.setText(extras.getString("fecha"));
			tv_narqueo.setText(extras.getString("narqueo"));
			new SearchQuery(this,"MVSEL0034", new String[] {ndocumento}).start(); // consulta movimientos de caja
		}
	}


	@Override
	public void arriveAnswerEvent(AnswerEvent e) {
		// TODO Auto-generated method stub
		Document doc = e.getDocument();
		Element elm = doc.getRootElement();
		if (e.getSqlCode()=="MVSEL0034") {
			Iterator<Element> i = elm.getChildren("row").iterator();
			while (i.hasNext()) {
				System.out.println("iterator con datos");
				Element row = (Element) i.next();
				Iterator<Element> j = row.getChildren().iterator();
				pcincuenta = Double.parseDouble(((Element) j.next()).getValue());
				pveinte = Double.parseDouble(((Element) j.next()).getValue());
				pdiez = Double.parseDouble(((Element) j.next()).getValue());
				pcinco = Double.parseDouble(((Element) j.next()).getValue());
				pdos = Double.parseDouble(((Element) j.next()).getValue());
				pmil = Double.parseDouble(((Element) j.next()).getValue());
				pmonedas = Double.parseDouble(((Element) j.next()).getValue());
				
				dcien = Double.parseDouble(((Element) j.next()).getValue());
				dcincuenta = Double.parseDouble(((Element) j.next()).getValue());
				dveinte = Double.parseDouble(((Element) j.next()).getValue());
				ddiez = Double.parseDouble(((Element) j.next()).getValue());
				dcinco = Double.parseDouble(((Element) j.next()).getValue());
				duno = Double.parseDouble(((Element) j.next()).getValue());
				dmonedas = Double.parseDouble(((Element) j.next()).getValue());
				
				tcredito = Double.parseDouble(((Element) j.next()).getValue());
				tdebito = Double.parseDouble(((Element) j.next()).getValue());
				tregalo = Double.parseDouble(((Element) j.next()).getValue());
				fcredito = Double.parseDouble(((Element) j.next()).getValue());
				trm = Double.parseDouble(((Element) j.next()).getValue());
				
				this.runOnUiThread(new Runnable() {
		            public void run() {
		            	et_pcincuenta.setText(df.format(pcincuenta));
		            	et_pcincuenta.setValue(pcincuenta);
		            	et_pveinte.setText(df.format(pveinte));
		            	et_pveinte.setValue(pveinte);
		            	et_pdiez.setText(df.format(pdiez));
		            	et_pdiez.setValue(pdiez);
		            	et_pcinco.setText(df.format(pcinco));
		            	et_pcinco.setValue(pcinco);
		            	et_pdos.setText(df.format(pdos));
		            	et_pdos.setValue(pdos);
		            	et_pmil.setText(df.format(pmil));
		            	et_pmil.setValue(pmil);
		            	et_pmonedas.setText(df.format(pmonedas));
		            	et_pmonedas.setValue(pmonedas);
		            	
		            	et_dcien.setText(df.format(dcien));
		            	et_dcien.setValue(dcien);
		            	et_dcincuenta.setText(df.format(dcincuenta));
		            	et_dcincuenta.setValue(dcincuenta);
		            	et_dveinte.setText(df.format(dveinte));
		            	et_dveinte.setValue(dveinte);
		            	et_ddiez.setText(df.format(ddiez));
		            	et_ddiez.setValue(ddiez);
		            	et_dcinco.setText(df.format(dcinco));
		            	et_dcinco.setValue(dcinco);
		            	et_duno.setText(df.format(duno));
		            	et_duno.setValue(duno);
		            	et_dmonedas.setText(df.format(dmonedas));
		            	et_dmonedas.setValue(dmonedas);
		            	
		            	double vpesos = pcincuenta+pveinte+pdiez+pcinco+pdos+pmil+pmonedas;
		            	tv_vpesos.setText(df.format(vpesos));
		            	tv_vtrm.setText(df.format(trm));
		            	vdolares = dcien+dcincuenta+dveinte+ddiez+dcinco+duno+dmonedas;
		            	tv_vdolares.setText(df.format(vdolares));
		            	tv_vpdolares.setText(df.format(vdolares*trm));
		            	tv_vtcredito.setText(df.format(tcredito));
		            	tv_vtdebito.setText(df.format(tdebito));
		            	tv_vtregalo.setText(df.format(tregalo));
		            	tv_vfcredito.setText(df.format(fcredito));
		            	double vrecaudo = vpesos+(vdolares*trm)+tcredito+tdebito+tregalo+fcredito;
		            	tv_vrecaudo.setText(df.format(vrecaudo));
		            	tv_vsistema.setText(df.format(vsistema));
		            	tv_vdiferencia.setText(df.format(vrecaudo-vsistema));
		            }
		        });	
			}
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
		calcular();
		if (v instanceof Button) {
			sendPackage();
		}
	}

	private void calcular() {
		double pcincuenta = et_pcincuenta.getValue();
		double pveinte = et_pveinte.getValue();
		double pdiez = et_pdiez.getValue();
		double pcinco = et_pcinco.getValue();
		double pdos = et_pdos.getValue();
		double pmil = et_pmil.getValue();
		double pmonedas = et_pmonedas.getValue();
		
		double dcien = et_dcien.getValue();
		double dcincuenta = et_dcincuenta.getValue();
		double dveinte = et_dveinte.getValue();
		double ddiez = et_ddiez.getValue();
		double dcinco = et_dcinco.getValue();
		double duno = et_duno.getValue();
		double dmonedas = et_dmonedas.getValue();
		
		double tpesos=pcincuenta+pveinte+pdiez+pcinco+pdos+pmil+pmonedas;
		vdolares=dcien+dcincuenta+dveinte+ddiez+dcinco+duno+dmonedas;
		double tpdolares=vdolares*trm;
		
    	tv_vpesos.setText(df.format(tpesos));
    	tv_vdolares.setText(df.format(vdolares));
    	tv_vpdolares.setText(df.format(tpdolares));
    	
    	double vrecaudo = tpesos+tpdolares+tcredito+tdebito+tregalo+fcredito;
    	
    	tv_vrecaudo.setText(df.format(vrecaudo));
    	tv_vsistema.setText(df.format(vsistema));
    	tv_vdiferencia.setText(df.format(vrecaudo-vsistema));

	}
	
	private void sendPackage() {
		Document transaction = new Document();
		Element raiz = new Element("TRANSACTION");
		Element driver = new Element("driver");
		driver.setText("MVTR00004");
		Element id = new Element("id");
		id.setText("TMV4");
		transaction.setRootElement(raiz);
		raiz.addContent(driver);
		raiz.addContent(id);

		Element num = new Element("package");
		num.addContent(new Element("field").setText(String.valueOf(vdolares)));
		num.addContent(new Element("field").setText(String.valueOf(et_pcincuenta.getValue())));
		num.addContent(new Element("field").setText(String.valueOf(et_pveinte.getValue())));
		num.addContent(new Element("field").setText(String.valueOf(et_pdiez.getValue())));
		num.addContent(new Element("field").setText(String.valueOf(et_pcinco.getValue())));
		num.addContent(new Element("field").setText(String.valueOf(et_pdos.getValue())));
		num.addContent(new Element("field").setText(String.valueOf(et_pmil.getValue())));
		num.addContent(new Element("field").setText(String.valueOf(et_pmonedas.getValue())));
		num.addContent(new Element("field").setText(String.valueOf(et_dcien.getValue())));
		num.addContent(new Element("field").setText(String.valueOf(et_dcincuenta.getValue())));
		num.addContent(new Element("field").setText(String.valueOf(et_dveinte.getValue())));
		num.addContent(new Element("field").setText(String.valueOf(et_ddiez.getValue())));
		num.addContent(new Element("field").setText(String.valueOf(et_dcinco.getValue())));
		num.addContent(new Element("field").setText(String.valueOf(et_duno.getValue())));
		num.addContent(new Element("field").setText(String.valueOf(et_dmonedas.getValue())));
		num.addContent(new Element("field").setText(ndocumento));
		raiz.addContent(num);

		
		XMLOutputter out = new XMLOutputter();
        out.setFormat(Format.getPrettyFormat());
        try {
			out.output(raiz,System.out);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        SocketChannel socket = SocketConnector.getSock();
        Log.d("EMAKU","EMAKU: Socket: "+socket);
        SocketWriter.writing(socket, transaction);

	}

	
	class EditTextDouble implements OnFocusChangeListener {

		private double value;
		private EditText et;
		private DecimalFormat df = new DecimalFormat("##,###,##0.00");

		public EditTextDouble(EditText et) {
			this.et=et;
			this.et.setOnFocusChangeListener(this);
		}
		
		void setValue(double value) {
			this.value=value;
		}
		
		double getValue() {
			return value;
		}
		
		EditText getEditText() {
			return et;
		}
		
		void setText(String text) {
			et.setText(text);
		}

		public void onFocusChange(View v, boolean hasFocus) {
			// TODO Auto-generated method stub
			EditText e = (EditText)v;
			try {
				setValue(Double.valueOf(e.getText().toString()));
				setText(df.format(value));
			}
			catch(NumberFormatException nfee) {
				nfee.printStackTrace();
			}
			calcular();
		}
		
	}

}

