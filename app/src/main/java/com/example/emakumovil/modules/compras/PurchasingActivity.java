package com.example.emakumovil.modules.compras;

import java.nio.channels.SocketChannel;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.jdom2.Attribute;
import org.jdom2.Document;
import org.jdom2.Element;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.example.emakumovil.R;
import com.example.emakumovil.components.AnswerEvent;
import com.example.emakumovil.components.AnswerListener;
import com.example.emakumovil.components.DatePickerFragment;
import com.example.emakumovil.components.DialogClickEvent;
import com.example.emakumovil.components.DialogClickListener;
import com.example.emakumovil.components.GlobalData;
import com.example.emakumovil.components.SearchDataDialog;
import com.example.emakumovil.components.SearchQuery;
import com.example.emakumovil.components.SelectedDataDialog;
import com.example.emakumovil.communications.SocketConnector;
import com.example.emakumovil.communications.SocketWriter;
import com.example.emakumovil.control.ClientHeaderValidator;
import com.example.emakumovil.control.SuccessEvent;
import com.example.emakumovil.control.SuccessListener;
import com.example.emakumovil.misc.settings.ConfigFileHandler;

public class PurchasingActivity extends Activity implements OnClickListener,DialogClickListener, AnswerListener, SuccessListener {

	private TabHost tabs;
	
	
	private TextView descripcion1;
	private TextView descripcion2;
	private TextView descripcion3;
	private TextView descripcion4;
	
	private Button bt_fecha;
	private Button bt_fechar;
	private Button bt_bodega;
	private Button bt_transportadora;
	private Button bt_recibido;
	
	private ImageButton ib_excel;
	private Button bt_guardar;
	
	private String id_bodega;
	private String id_recibido;
	private String id_transportadora;
	private String id_tercero;
	private String id_direccion;
	private String id_telefono;
	
	private TextView tv_list_products;
	private TextView tv_stotal;
	private TextView tv_tdescuento;
	private TextView tv_giva;
	private TextView tv_gtotal;
	private double base;
	private double gtotal;
	
	private EditText et_dcredito;
	private EditText et_ncompra;
	private EditText et_nguia;
	private ImageButton ib_barcode_guia;
	private EditText et_peso;
	private EditText et_ncajas;
	
	private ListView lv_template;
	private DataAdapter adapter;
	
	private DatePickerFragment fecha;
	private DatePickerFragment fechar;

	private Locale locale = Locale.getDefault();
	private int tcant;
	
	private double tstotal;
	private double tdescuento;
	private double tviva;
	private double tretenciones;
	private double tcompra;
	
	private String numero;
	
	private SelectedDataDialog selected;
	private SearchDataDialog search;
	private PurchasingInformationDialog purchasingDialog;

	private DecimalFormat df = new DecimalFormat("##,###,##0.00");

	public PurchasingActivity() {
		System.out.println("se llamo al constructor..................................");
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_purchasing);

		System.out.println("se llamo a onCrete ++++++++++++++++++++++++++++++++++++++++");
		descripcion1=(TextView)findViewById(R.id.tv_descripcion1);
		descripcion2=(TextView)findViewById(R.id.tv_descripcion2);
		descripcion3=(TextView)findViewById(R.id.tv_descripcion3);
		descripcion4=(TextView)findViewById(R.id.tv_descripcion4);
	
		et_dcredito=(EditText)findViewById(R.id.et_dcredito);
		et_ncompra=(EditText)findViewById(R.id.et_ncompra);
		et_nguia=(EditText)findViewById(R.id.et_nguia);
		et_peso=(EditText)findViewById(R.id.et_peso);
		et_ncajas=(EditText)findViewById(R.id.et_ncajas);

		bt_bodega = (Button)findViewById(R.id.bt_bodega);
		bt_bodega.setOnClickListener(this);
	
		bt_transportadora = (Button)findViewById(R.id.bt_transportadora);
		bt_transportadora.setOnClickListener(this);
		bt_recibido = (Button)findViewById(R.id.bt_recibe);
		bt_recibido.setOnClickListener(this);
		
		ib_excel = (ImageButton)findViewById(R.id.ib_excel);
		ib_excel.setOnClickListener(this);
		bt_guardar = (Button)findViewById(R.id.bt_guardar);
		bt_guardar.setOnClickListener(this);
		
		bt_fecha = (Button)findViewById(R.id.bt_fecha);
		bt_fecha.setOnClickListener(this);
		bt_fechar = (Button)findViewById(R.id.bt_fechar);
		bt_fechar.setOnClickListener(this);
		
		tv_list_products = (TextView)findViewById(R.id.tv_list_products);
		tv_stotal = (TextView)findViewById(R.id.tv_stotal);
		tv_tdescuento = (TextView)findViewById(R.id.tv_tdescuento);
		tv_giva = (TextView)findViewById(R.id.tv_giva);
		tv_gtotal = (TextView)findViewById(R.id.tv_gtotal);

		fecha = new DatePickerFragment(R.id.bt_fecha);
		fecha.addDialogClickListener(this);

		fechar = new DatePickerFragment(R.id.bt_fechar);
		fechar.addDialogClickListener(this);

		lv_template = (ListView) findViewById(R.id.lv_template);
		
		ib_barcode_guia = (ImageButton) findViewById(R.id.ib_barcode_guia);
		ib_barcode_guia.setOnClickListener(this);
		
		adapter = new DataAdapter(
				PurchasingActivity.this, R.layout.listpurchasingtemplate,
				GlobalData.xls);
		lv_template.setAdapter(adapter);


		Bundle bundle = getIntent().getExtras();
    	if (bundle!=null) {
    		id_tercero = bundle.getString("id");
    		id_direccion = bundle.getString("id_direccion");
    		id_telefono = bundle.getString("id_telefono");
    		descripcion1.setText(bundle.getString("descripcion1"));
    		descripcion2.setText(bundle.getString("descripcion2"));
    		descripcion3.setText(bundle.getString("descripcion3"));
    		descripcion4.setText(bundle.getString("descripcion4"));
    	}
    	
		tabs=(TabHost)findViewById(android.R.id.tabhost);
		tabs.setup();
		Resources res = getResources();
		
		// Pestaña 1 
		
		TabHost.TabSpec spec1=tabs.newTabSpec(getString(R.string.imagen));
		spec1.setContent(R.id.sv_datos);
		spec1.setIndicator(getString(R.string.dgenerales),
		    res.getDrawable(android.R.drawable.ic_dialog_alert));
		tabs.addTab(spec1);

		// Pestaña 2 
		
		TabHost.TabSpec spec2=tabs.newTabSpec(getString(R.string.kardex));
		spec2.setContent(R.id.ly_productos);
		spec2.setIndicator(getString(R.string.productos),
		    res.getDrawable(android.R.drawable.ic_dialog_alert));
		tabs.addTab(spec2);


		if (savedInstanceState==null) {
			GlobalData.xls.clear();
			GlobalData.data_retenciones.clear();
			ClientHeaderValidator.removeAllSuccessListener();
			ClientHeaderValidator.addSuccessListener(this);
		}
		else {
			  tretenciones=savedInstanceState.getDouble("tretenciones");
			  tcompra=savedInstanceState.getDouble("tcompra");
			  System.out.println("reasignando valor por onCreate a tcompra: "+tcompra);
		}

	}

	public void onSaveInstanceState(Bundle savedInstanceState) {
		super.onSaveInstanceState(savedInstanceState);
		
		savedInstanceState.putString("id_tercero",id_tercero);
		savedInstanceState.putString("id_direccion",id_direccion);
		savedInstanceState.putString("id_telefono",id_telefono);

		savedInstanceState.putString("fecha",bt_fecha.getText().toString());
		savedInstanceState.putString("bodega",bt_bodega.getText().toString());
		savedInstanceState.putString("id_bodega",id_bodega);
		savedInstanceState.putString("dcredito",et_dcredito.getText().toString());
		savedInstanceState.putString("ncompra",et_ncompra.getText().toString());
		savedInstanceState.putString("recibido",bt_recibido.getText().toString());
		savedInstanceState.putString("id_recibido",id_recibido);
		savedInstanceState.putString("transportadora",bt_transportadora.getText().toString());
		savedInstanceState.putString("id_transportadora",id_transportadora);
		savedInstanceState.putString("fechar",bt_fechar.getText().toString());
		savedInstanceState.putString("nguia",et_nguia.getText().toString());
		savedInstanceState.putString("peso",et_peso.getText().toString());
		savedInstanceState.putString("ncajas",et_ncajas.getText().toString());
		
		savedInstanceState.putString("cantidad",tv_list_products.getText().toString());
		savedInstanceState.putString("stotal",tv_stotal.getText().toString());
		savedInstanceState.putString("ttdescuento",tv_tdescuento.getText().toString());
		savedInstanceState.putString("giva",tv_giva.getText().toString());
		savedInstanceState.putString("tgtotal",tv_gtotal.getText().toString());
		
		savedInstanceState.putDouble("base",base);
		savedInstanceState.putDouble("gtotal",gtotal);
		savedInstanceState.putInt("tcant",tcant);
		savedInstanceState.putDouble("tstotal",tstotal);
		savedInstanceState.putDouble("tdescuento",tdescuento);
		savedInstanceState.putDouble("tviva",tviva);
		savedInstanceState.putDouble("tretenciones",tretenciones);
		savedInstanceState.putDouble("tcompra",tcompra);
		savedInstanceState.putString("numero",numero);
		System.out.println("guardando numero "+numero);
	}

	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState) {
	  super.onRestoreInstanceState(savedInstanceState);
	  id_tercero=savedInstanceState.getString("id_tercero");
	  id_direccion=savedInstanceState.getString("id_direccion");
	  id_telefono=savedInstanceState.getString("id_telefono");

	  bt_fecha.setText(savedInstanceState.getString("fecha"));
	  bt_bodega.setText(savedInstanceState.getString("bodega"));
	  id_bodega=savedInstanceState.getString("id_bodega");
	  et_dcredito.setText(savedInstanceState.getString("dcredito"));
	  et_ncompra.setText(savedInstanceState.getString("ncompra"));
	  bt_recibido.setText(savedInstanceState.getString("recibido"));
	  id_recibido=savedInstanceState.getString("id_recibido");
	  bt_transportadora.setText(savedInstanceState.getString("transportadora"));
	  id_transportadora=savedInstanceState.getString("id_transportadora");
	  bt_fechar.setText(savedInstanceState.getString("fechar"));
	  et_nguia.setText(savedInstanceState.getString("nguia"));
	  et_peso.setText(savedInstanceState.getString("peso"));
	  et_ncajas.setText(savedInstanceState.getString("ncajas"));
	  
	  tv_list_products.setText(savedInstanceState.getString("cantidad"));
	  tv_stotal.setText(savedInstanceState.getString("stotal"));
	  tv_tdescuento.setText(savedInstanceState.getString("ttdescuento"));
	  tv_giva.setText(savedInstanceState.getString("giva"));
	  tv_gtotal.setText(savedInstanceState.getString("tgtotal"));
	  
	  base=savedInstanceState.getDouble("base");
	  gtotal=savedInstanceState.getDouble("gtotal");

	  tcant=savedInstanceState.getInt("tcant");
	  tstotal=savedInstanceState.getDouble("tstotal");
	  tdescuento=savedInstanceState.getDouble("tdescuento");
	  tviva=savedInstanceState.getDouble("tviva");
	  tretenciones=savedInstanceState.getDouble("tretenciones");
	  tcompra=savedInstanceState.getDouble("tcompra");
	  if (savedInstanceState.getString("numero")!=null) {
		  numero=savedInstanceState.getString("numero");
	  }
	  else {
		  System.out.println("no se restauro porque al guardar fue nulo");
	  }
	  System.out.println("restaurando numero "+numero);

	}

	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.purchasing, menu);
		return true;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if (v.getId() == R.id.ib_barcode_guia) {
			Intent intent = new Intent("com.google.zxing.client.android.SCAN");
			intent.putExtra("SCAN_MODE", "PRODUCT_MODE");
			startActivityForResult(intent, 3);
		} else 	if (v.getId()==R.id.bt_fecha) {
			fecha.show(getFragmentManager(),getString(R.string.fecha));
		}
		else if (v.getId()==R.id.bt_fechar) {
			fechar.show(getFragmentManager(),getString(R.string.fecha));
		}
		else if (v.getId()==R.id.bt_bodega) {
			selected = new SelectedDataDialog(R.id.bt_bodega,getString(R.string.buscar_bodega),"MVSEL0013",null);
			selected.addDialogClickListener(this);
			selected.show(getFragmentManager(),getString(R.string.buscar_referencia));
			
		}
		else if (v.getId()==R.id.bt_transportadora) {
			selected = new SelectedDataDialog(R.id.bt_transportadora,getString(R.string.buscar_transportadora),"MVSEL0044",null);
			selected.addDialogClickListener(this);
			selected.show(getFragmentManager(),getString(R.string.buscar_transportadora));
			
		}
		else if(v.getId()==R.id.bt_recibe) {
			search = new SearchDataDialog(getString(R.string.buscar_empleado),"MVSEL0048",null,R.id.bt_recibe);
			search.addDialogClickListener(this);
			search.show(getFragmentManager(),getString(R.string.buscar_empleado));

		}
		else if (v.getId()==R.id.ib_excel) {
			selected = new SelectedDataDialog(R.id.ib_excel,getString(R.string.cargar_plantilla),"MVSEL0045",new String[]{id_tercero});
			selected.addDialogClickListener(this);
			selected.show(getFragmentManager(),getString(R.string.cargar_plantilla));
			
		}
		else if (v.getId()==R.id.bt_guardar) {
			PaymentValidation();
		}
		
	}

	@Override
	public void dialogClickEvent(DialogClickEvent e) {
		// TODO Auto-generated method stub
		if (R.id.bt_fecha==e.getIdobject()) {
			bt_fecha.setText(e.getValue());
		}
		else if (R.id.bt_fechar==e.getIdobject()) {
			bt_fechar.setText(e.getValue());
		}
		else if (R.id.bt_bodega==e.getIdobject()) {
			if (e.getValue()!=null) { 
				bt_bodega.setText(e.getValue());
				id_bodega = e.getId();
			}
		}
		else if (R.id.bt_transportadora==e.getIdobject()) {
			if (e.getValue()!=null) { 
				bt_transportadora.setText(e.getValue());
				id_transportadora = e.getId();
			}
		}
		else if (R.id.bt_recibe==e.getIdobject()) {
			if (e.getValue()!=null) { 
				bt_recibido.setText(e.getValue());
				id_recibido = e.getId();
			}
		}
		else if (R.id.ib_excel==e.getIdobject()) {
			new SearchQuery(PurchasingActivity.this,"MVSEL0046",new String[]{e.getId()}).start();
		}
		else if (R.id.bt_guardar==e.getIdobject()) {
			System.out.println("guardando compra...");
			tretenciones = e.getTretenciones();
			tcompra = e.getValor();
			System.out.println("obteniendo tcompra "+tcompra);

			sendTransaction(e.getValor(),e.getRetenciones());
		}
	}

	private void sendTransaction(double total_compra,Element retenciones) {
		Document transaction = new Document();
		Element raiz = new Element("TRANSACTION");
		Element driver = new Element("driver");
		driver.setText("MVTR00013");
		Element id = new Element("id");
		id.setText("TMV4");
		transaction.setRootElement(raiz);
		raiz.addContent(driver);
		raiz.addContent(id);
		
		Calendar c = Calendar.getInstance();
		int hourOfDay = c.get(Calendar.HOUR_OF_DAY);
		int minute = c.get(Calendar.MINUTE);
		String time_selected = " "+(hourOfDay<=9?"0"+hourOfDay:""+hourOfDay)+":"+(minute<=9?"0"+minute:""+minute);
		
		// Fecha
		Element pfecha = new Element("package");
		Element ffecha = new Element("field");
		Attribute afecha = new Attribute("attribute","date");
		ffecha.setAttribute(afecha);
		ffecha.setText(bt_fecha.getText().toString()+time_selected);
		pfecha.addContent(ffecha);
		raiz.addContent(pfecha);

		
		// ncompra y dcredito
		Element pinfo_documento = new Element("package");
		
		Element fncompra = new Element("field");
		fncompra.setText(et_ncompra.getText().toString());
		Attribute ancompra = new Attribute("attribute","finalKey");
		fncompra.setAttribute(ancompra);
		pinfo_documento.addContent(fncompra);
		
		Element fdcredito = new Element("field");
		fdcredito.setText(et_dcredito.getText().toString());
		Attribute adcredito = new Attribute("attribute","finalKey");
		fdcredito.setAttribute(adcredito);
		pinfo_documento.addContent(fdcredito);
		raiz.addContent(pinfo_documento);

		// Informacion de la transportadora
		
		Element ptransportadora = new Element("package");
		
		Element fid_transportadora = new Element("field");
		fid_transportadora.setText(id_transportadora);
		ptransportadora.addContent(fid_transportadora);

		Element fguia = new Element("field");
		fguia.setText(et_nguia.getText().toString());
		ptransportadora.addContent(fguia);
		
		Element ffechar = new Element("field");
		ffechar.setText(bt_fechar.getText().toString()+time_selected);
		ptransportadora.addContent(ffechar);

		Element fpeso = new Element("field");
		fpeso.setText(et_peso.getText().toString());
		ptransportadora.addContent(fpeso);

		Element fcajas = new Element("field");
		fcajas.setText(et_ncajas.getText().toString());
		ptransportadora.addContent(fcajas);

		Element frecibido = new Element("field");
		frecibido.setText(id_recibido);
		ptransportadora.addContent(frecibido);
		raiz.addContent(ptransportadora);
		
		// datos documento
		
		Element pdatos_documento = new Element("package");
		
		Element ftotal_compra = new Element("field");
		ftotal_compra.setText(String.valueOf(total_compra));
		pdatos_documento.addContent(ftotal_compra);
		raiz.addContent(pdatos_documento);

		// tercero
		
		Element ptercero = new Element("package");
		
		Element fid_tercero = new Element("field");
		Attribute a1tercero = new Attribute("attribute","key");
		Attribute a2tercero = new Attribute("name","idTercero");
		fid_tercero.setAttribute(a1tercero);
		fid_tercero.setAttribute(a2tercero);
		fid_tercero.setText(id_tercero);
		ptercero.addContent(fid_tercero);
		
		Element fid_direccion = new Element("field");
		fid_direccion.setText(id_direccion);
		ptercero.addContent(fid_direccion);

		Element fid_telefono = new Element("field");
		fid_telefono.setText(id_telefono);
		ptercero.addContent(fid_telefono);
		raiz.addContent(ptercero);

		// retenciones
		
		Element pretenciones = retenciones;
		raiz.addContent(pretenciones);
		
		
		// total compra 2205
		
		Element ptotal_2205 = new Element("package");
		
		Element ftotal_2205 = new Element("field");
		ftotal_2205.setText(String.valueOf(total_compra));
		ptotal_2205.addContent(ftotal_2205);
		raiz.addContent(ptotal_2205);
		
		// cxp
		
		Element pcxp = new Element("package");

		Element fdcredito2 = new Element("field");
		fdcredito2.setText(et_dcredito.getText().toString());
		pcxp.addContent(fdcredito2);

		Element fbase = new Element("field");
		fbase.setText(String.valueOf(base));
		pcxp.addContent(fbase);
		
		Element fpago = new Element("field");
		fpago.setText(String.valueOf(total_compra));
		pcxp.addContent(fpago);
		
		raiz.addContent(pcxp);
		
		// Bodega
		
		Element pbodega = new Element("package");
		
		Element fid_bodega = new Element("field");
		Attribute a1bodega = new Attribute("attribute","key");
		Attribute a2bodega = new Attribute("name","bodegaEntrante");
		fid_bodega.setAttribute(a1bodega);
		fid_bodega.setAttribute(a2bodega);
		fid_bodega.setText(id_bodega);
		pbodega.addContent(fid_bodega);
		
		raiz.addContent(pbodega);
		
		// Blanco 1
		
		Element pblanco1 = new Element("package");
		Element fblanco1 = new Element("field");
		pblanco1.addContent(fblanco1);
		
		raiz.addContent(pblanco1);
		
		// Productos
		
		Element pproductos = getProductos();
		raiz.addContent(pproductos);
		
		// Blanco2
		
		Element pblanco2 = new Element("package");
		Element fblanco2 = new Element("field");
		pblanco2.addContent(fblanco2);
		
		raiz.addContent(pblanco2);

		System.out.println("tcompra en guardar "+tcompra);
		
         SocketChannel socket = SocketConnector.getSock();
        Log.d("EMAKU","EMAKU: Socket: "+socket);
        SocketWriter.writing(socket, transaction);
	
	}

	private Element getProductos() {
		Element pack = new Element("package");
		for (int i=0;i<GlobalData.xls.size();i++) {
			TempoXLS prod = GlobalData.xls.get(i);
			Element spack = new Element("subpackage");
			
			Element id_prod_serv = new Element("field");
			Attribute aid_prod_serv = new Attribute("name","idProducto");
			id_prod_serv.setAttribute(aid_prod_serv);
			
			Element piva = new Element("field");
			Element vunitario = new Element("field");
			
			Element cantidad = new Element("field");
			Attribute acantidad = new Attribute("name","cantidad");
			cantidad.setAttribute(acantidad);
			
			Element pdescuento = new Element("field");
			Element neto = new Element("field");
			Element tiva = new Element("field");
			
			Element vdescuento = new Element("field");
			Attribute adescuento = new Attribute("name","descuentos");
			vdescuento.setAttribute(adescuento);
			
			Element factorc = new Element("field");
			Element pivac = new Element("field");
			
			Element uneto = new Element("field");
			Attribute auneto = new Attribute("name","pcosto");
			uneto.setAttribute(auneto);
			
			id_prod_serv.setText(String.valueOf(prod.getIdProdserv()));
			piva.setText(String.valueOf(prod.getIva()));
			vunitario.setText(String.valueOf(prod.getCosto()));
			cantidad.setText(String.valueOf(prod.getCantidad()));
			pdescuento.setText(String.valueOf(prod.getPdescuento()));
			neto.setText(String.valueOf(prod.getNeto()));
			tiva.setText(String.valueOf(prod.getTiva()));
			vdescuento.setText(String.valueOf(prod.getDescuento()));
			factorc.setText("1");
			pivac.setText(String.valueOf(prod.getIva()));
			uneto.setText(String.valueOf(prod.getUneto()));
			
			spack.addContent(id_prod_serv);
			spack.addContent(piva);
			spack.addContent(vunitario);
			spack.addContent(cantidad);
			spack.addContent(pdescuento);
			spack.addContent(neto);
			spack.addContent(tiva);
			spack.addContent(vdescuento);
			spack.addContent(factorc);
			spack.addContent(pivac);
			spack.addContent(uneto);
			pack.addContent(spack);
		}
		return pack;

	}
	
	private class DataAdapter extends ArrayAdapter<TempoXLS> {

		private LayoutInflater vi;
		private ArrayList<TempoXLS> items;
		
		public DataAdapter(Context context, int textViewResourceId,
				ArrayList<TempoXLS> items) {
			super(context, textViewResourceId, items);
			Log.d("EMAKU ","EMAKU instanciando adaptador");
			vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			this.items=items;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View v = convertView;
			ViewHolder holder;
			TempoXLS myData = items.get(position);
			if (v == null) {
				v = vi.inflate(R.layout.listpurchasingtemplate, null);

				TextView tv_barcode = (TextView) v
						.findViewById(R.id.tv_barcode);
				
				TextView tv_descripcion = (TextView) v
						.findViewById(R.id.tv_descripcion);
				
				TextView tv_vcant = (TextView) v
						.findViewById(R.id.tv_vcant);
				
				TextView tv_vcosto = (TextView) v
						.findViewById(R.id.tv_vcosto);
				
				TextView tv_viva = (TextView) v
						.findViewById(R.id.tv_viva);
				
				TextView tv_pdescuento = (TextView) v
						.findViewById(R.id.tv_vpdescuento);
						
				TextView tv_vneto = (TextView) v
						.findViewById(R.id.tv_vneto);

				TextView tv_vdescuento = (TextView) v
						.findViewById(R.id.tv_vdescuento);

				TextView tv_vtiva = (TextView) v
						.findViewById(R.id.tv_vtiva);

				TextView tv_vtotal = (TextView) v
						.findViewById(R.id.tv_vtotal);

				holder = new ViewHolder(tv_barcode,
										tv_descripcion,
										tv_vcant,
										tv_vcosto,
										tv_viva,
										tv_pdescuento,
										tv_vneto,
										tv_vdescuento,
										tv_vtiva,
										tv_vtotal);
				v.setTag(holder);
			}
			else {
				holder = (ViewHolder)v.getTag();
			}


			
			if (myData != null) {
				if (holder.tv_descripcion != null) {
					holder.tv_barcode.setText(myData.getBarcode());
					holder.tv_descripcion.setText(myData.getDescripcion());
					holder.tv_vcant.setText(String.valueOf(myData.getCantidad()));
					holder.tv_vcosto.setText(df.format(myData.getCosto()));
					holder.tv_viva.setText(df.format(myData.getIva()));
					holder.tv_vpdescuento.setText(df.format(myData.getPdescuento()));
					holder.tv_vneto.setText(df.format(myData.getNeto()));
					holder.tv_vdescuento.setText(df.format(myData.getDescuento()));
					holder.tv_vtiva.setText(df.format(myData.getTiva()));
					holder.tv_vtotal.setText(df.format(myData.getTotal()));
				}
			}
			return v;
		}

	}

 	private class ViewHolder {
 		public TextView tv_barcode;
 		public TextView tv_descripcion;
 		public TextView tv_vcant;
 		public TextView tv_vcosto;
 		public TextView tv_viva;
 		public TextView tv_vpdescuento;
 		public TextView tv_vneto;
 		public TextView tv_vdescuento;
 		public TextView tv_vtiva;
 		public TextView tv_vtotal;
 		
 		public ViewHolder(TextView tv_barcode,TextView tv_descripcion,TextView tv_vcant,TextView tv_vcosto,TextView tv_viva,TextView tv_vpdescuento,TextView tv_vneto,
 							TextView tv_vdescuento,TextView tv_vtiva,TextView tv_vtotal) {
 			this.tv_barcode=tv_barcode;
 			this.tv_descripcion=tv_descripcion;
 			this.tv_vcant=tv_vcant;
 			this.tv_vcosto=tv_vcosto;
 			this.tv_viva=tv_viva;
 			this.tv_vpdescuento=tv_vpdescuento;
 			this.tv_vneto=tv_vneto;
 			this.tv_vdescuento=tv_vdescuento;
 			this.tv_vtiva=tv_vtiva;
 			this.tv_vtotal=tv_vtotal;
 		}
 		
 	}

	private void PaymentValidation() {
		System.out.println("numero: "+this.numero);
		if (numero==null) {
			if (!bt_fecha.getText().equals(getString(R.string.fecha))) {
				if (!bt_bodega.getText().equals(getString(R.string.bodega))) {
					if (!et_dcredito.getText().toString().equals("")) {
						if (!et_ncompra.getText().toString().equals("")) {
							if (!bt_recibido.getText().equals(getString(R.string.recibido))) {
								if (!bt_transportadora.getText().equals(getString(R.string.transportadora))) {
									if (!bt_fechar.getText().equals(getString(R.string.fecha_recibido))) {
										if (!et_nguia.getText().toString().equals("")) {
											if (!et_peso.getText().toString().equals("")) {
												if (!et_ncajas.getText().toString().equals("")) {
													if (base!=0) {
														purchasingDialog = new PurchasingInformationDialog(R.id.bt_guardar,id_tercero,base,gtotal);
														purchasingDialog.show(getFragmentManager(),getString(R.string.forma_pago));
													}
													else {
														Toast.makeText(this,R.string.error_plantilla, Toast.LENGTH_LONG).show();
													}
												}
												else{
													Toast.makeText(this,R.string.error_cajas, Toast.LENGTH_LONG).show();
												}
											}
											else{
												Toast.makeText(this,R.string.error_peso, Toast.LENGTH_LONG).show();
											}
										}
										else{
											Toast.makeText(this,R.string.error_guia, Toast.LENGTH_LONG).show();
										}
									}
									else{
										Toast.makeText(this,R.string.error_fechar, Toast.LENGTH_LONG).show();
									}
								}
								else{
									Toast.makeText(this,R.string.error_transportadora, Toast.LENGTH_LONG).show();
								}
							}
							else{
								Toast.makeText(this,R.string.error_recibido, Toast.LENGTH_LONG).show();
							}
						}
						else{
							Toast.makeText(this,R.string.error_ncompra, Toast.LENGTH_LONG).show();
						}
					}
					else{
						Toast.makeText(this,R.string.error_dcredito, Toast.LENGTH_LONG).show();
					}
				}
				else{
					Toast.makeText(this,R.string.error_bodega, Toast.LENGTH_LONG).show();
				}
			}
			else{
				Toast.makeText(this,R.string.error_fecha, Toast.LENGTH_LONG).show();
			}
		}
		else {
			Toast.makeText(this,R.string.error_guardar, Toast.LENGTH_LONG).show();
		}
	}

 	public class TempoXLS {
		private String barcode;
		private String descripcion;
		private int cantidad;
		private double costo;
		private double iva;
		private double pdescuento;
		private double neto;
		private double descuento;
		private double tiva;
		private double total;
		private int id_prod_serv;
		private double uneto;
		
		public TempoXLS(String barcode, String descripcion,int cantidad,
				double costo, double iva, double pdescuento,double neto,
				double descuento,double tiva,double total,int id_prod_serv,double uneto) {
			this.barcode = barcode;
			this.descripcion = descripcion;
			this.cantidad = cantidad;
			this.costo = costo;
			this.iva = iva;
			this.pdescuento = pdescuento;
			this.neto=neto;
			this.descuento = descuento;
			this.tiva=tiva;
			this.total=total;
			this.id_prod_serv=id_prod_serv;
			this.uneto=uneto;
		}


		public String getBarcode() {
			return barcode;
		}

		public String getDescripcion() {
			return descripcion;
		}


		public int getCantidad() {
			return cantidad;
		}

		public double getCosto() {
			return costo;
		}

		public double getIva() {
			return iva;
		}

		public double getDescuento() {
			return descuento;
		}

		public double getPdescuento() {
			return pdescuento;
		}

		public double getNeto() {
			return neto;
		}

		public double getTiva() {
			return tiva;
		}

		public double getTotal() {
			return total;
		}

		public double getUneto() {
			return uneto;
		}

		public int getIdProdserv() {
			return id_prod_serv;
		}
		
	}

	@Override
	public void arriveAnswerEvent(AnswerEvent e) {
		// TODO Auto-generated method stub
		final Document doc = e.getDocument();
		if (e.getSqlCode().equals("MVSEL0046")) {
			final Element rootNode = doc.getRootElement();
			this.runOnUiThread(new Runnable() {
				public void run() {
					List<Element> listRows = rootNode.getChildren("row");
					GlobalData.xls.clear();
					int cantidad = 0;
					for (int i = 0; i < listRows.size(); i++) {
						double stotal = 0;
						double vdescuento = 0;
						double neto = 0;
						double viva = 0;
						double total = 0;
						Element Erow = (Element) listRows.get(i);
						List<Element> Lcol = Erow.getChildren();
						String codigo = ((Element) Lcol.get(0)).getText();
						String descripcion = ((Element) Lcol.get(1)).getText();
						cantidad = Integer.parseInt(((Element) Lcol.get(2)).getText());
						double pcosto = Double.parseDouble(((Element) Lcol.get(3)).getText());
						double iva = Double.parseDouble(((Element) Lcol.get(4)).getText());
						double descuento1 = Double.parseDouble(((Element) Lcol.get(5)).getText());
						int id_prod_serv = Integer.parseInt(((Element) Lcol.get(6)).getText());
						stotal = Math.rint(cantidad*pcosto);
						vdescuento = Math.rint(stotal*descuento1/100);
						neto = stotal-vdescuento;
						viva = Math.rint(neto*iva/100);
						total = neto+viva;
						double uneto = Math.rint(neto/cantidad*100)/100;
						tcant+=cantidad;
						tstotal+=stotal;
						base+=neto;
						tdescuento+=vdescuento;
						tviva+=viva;
						gtotal+=total;
						GlobalData.xls.add(new TempoXLS(codigo,descripcion,cantidad,pcosto,iva,descuento1,neto,vdescuento,viva,total,id_prod_serv,uneto));
					}
					tv_stotal.setText(df.format(tstotal));
					tv_tdescuento.setText(df.format(tdescuento));
					tv_giva.setText(df.format(tviva));
					tv_gtotal.setText(df.format(gtotal));
					tv_list_products.setText(getString(R.string.lista_productos)+" "+tcant);

				}
			});



		}
	}

	private void sendPrintJob(String numero) {
		System.out.println("Imprimiendo compra No. "+numero);
		System.out.println("tcompra al imprimir "+tcompra);
		Document transaction = new Document();
		Element raiz = new Element("SERVERPRINTER");
		
		Element printerTemplate = new Element("printerTemplate");
		printerTemplate.setText("/graphics/TSEntradaAlmacenResumida.xml");
		
		Element jarFile = new Element("jarFile");
		jarFile.setText(ConfigFileHandler.getJarFile());

		Element jarDirectory = new Element("jarDirectory");
		jarDirectory.setText(ConfigFileHandler.getJarDirectory());
		transaction.setRootElement(raiz);
		raiz.addContent(printerTemplate);
		raiz.addContent(jarFile);
		raiz.addContent(jarDirectory);

		/*
		 * Numero 
		 */
		
		Element numero1 = new Element("package");
		numero1.addContent(new Element("field").setText(numero)); 
		raiz.addContent(numero1);
		
		/*
		 * Cantidad 
		 */
		
		Element ecantidad = new Element("package");
		ecantidad.addContent(new Element("field").setText(String.valueOf(tcant))); 
		raiz.addContent(ecantidad);

		/*
		 * Guia 
		 */
		
		Element guia = new Element("package");
		guia.addContent(new Element("field").setText(bt_transportadora.getText().toString())); 
		guia.addContent(new Element("field").setText(et_nguia.getText().toString())); 
		guia.addContent(new Element("field").setText(bt_fechar.getText().toString())); 
		guia.addContent(new Element("field").setText(et_peso.getText().toString())); 
		guia.addContent(new Element("field").setText(et_ncajas.getText().toString())); 
		guia.addContent(new Element("field").setText(bt_recibido.getText().toString())); 
		raiz.addContent(guia);

		/*
		 * Tercero
		 */
		
		Element tercero = new Element("package");
		tercero.addContent(new Element("field").setText(descripcion2.getText().toString())); 
		tercero.addContent(new Element("field").setText(descripcion1.getText().toString())); 
		tercero.addContent(new Element("field").setText(descripcion3.getText().toString())); 
		tercero.addContent(new Element("field").setText(descripcion4.getText().toString())); 
		tercero.addContent(new Element("field").setText("")); 
		tercero.addContent(new Element("field").setText("")); 
		raiz.addContent(tercero);

		/*
		 * Numero Impreso
		 */
		
		Element numero2 = new Element("package");
		numero2.addContent(new Element("field").setText(numero)); 
		raiz.addContent(numero2);

		/*
		 * Factura Proveedor
		 */
		
		Element fprov = new Element("package");
		fprov.addContent(new Element("field").setText(et_ncompra.getText().toString())); 
		raiz.addContent(fprov);

		/*
		 * Fecha Documento
		 */

		Element fecha = new Element("package");
		fecha.addContent(new Element("field").setText(bt_fecha.getText().toString())); 
		raiz.addContent(fecha);
		
		/*
		 * Dias de credito
		 */

		Element dcredito = new Element("package");
		dcredito.addContent(new Element("field").setText(et_dcredito.getText().toString())); 
		raiz.addContent(dcredito);
		
		
		/*
		 * Vencimiento
		 */

	    SimpleDateFormat format = null;
		Calendar cal = Calendar.getInstance();
		System.out.println("dias de credito: {"+et_dcredito.getText()+"}");
		int dias = Integer.parseInt(et_dcredito.getText().toString());
		Date parsed = new Date();
		try {
		    format = new SimpleDateFormat("yyyy/MM/dd",locale);
		    parsed = format.parse(bt_fecha.getText().toString());
		}
		catch(ParseException pe) {
		    throw new IllegalArgumentException();
		}
		cal.setTime(parsed);
		cal.add(Calendar.DATE,dias);
		
		
		Element vencimiento = new Element("package");
		vencimiento.addContent(new Element("field").setText(format.format(cal.getTime()))); 
		raiz.addContent(vencimiento);
		
        
        /*
         * subtotales
         */
        
		Element subtotales = new Element("package");
		subtotales.addContent(new Element("field").setText(String.valueOf(tstotal))); 
		subtotales.addContent(new Element("field").setText(String.valueOf(tdescuento))); 
		subtotales.addContent(new Element("field").setText(String.valueOf(tviva))); 
		subtotales.addContent(new Element("field").setText(String.valueOf(gtotal))); 
		raiz.addContent(subtotales);

		/*
		 * retenciones
		 */
		
		Element retenciones = new Element("package");
		retenciones.addContent(new Element("field").setText(String.valueOf(tretenciones))); 
		raiz.addContent(retenciones);

		/*
		 * tcompra
		 */
		
		Element etcompra = new Element("package");
		etcompra.addContent(new Element("field").setText(String.valueOf(tcompra))); 
		raiz.addContent(etcompra);
	
        System.out.println("numero antes de solicitar impresion: "+this.numero);
		SocketChannel socket = SocketConnector.getSock();
        SocketWriter.writing(socket, transaction);
        System.out.println("numero despues de solicitar impresion: "+this.numero);
	}
	
	@Override
	public boolean containSqlCode(String sqlCode) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void cathSuccesEvent(SuccessEvent e) {
		// TODO Auto-generated method stub
		ClientHeaderValidator.removeSuccessListener(this);
		System.out.println("tcompra luego de guardar compra "+tcompra);
		if (tcompra==0) {
			System.out.println("se borro el valor tratare de restaurarlo del dialog");
			if (purchasingDialog!=null) {
				tretenciones = purchasingDialog.getRetencion();
				tcompra = purchasingDialog.getValor();
				System.out.println("tcompra luego de restaurar valores "+tcompra);
			}
			else {
				System.out.println("el dialogo era null y no se pudo restaurar");
			}
		}
		if (numero==null) {
			numero = e.getNdocument();
			sendPrintJob(numero);
		}
		else {
			System.out.println("El numero no era null y su valor es "+numero);
		}
	}
	
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);
		Log.d("EMAKU", " ResultCode: " + resultCode + " requestCode: "
				+ requestCode);
		if (requestCode == 3) {
			if (resultCode == RESULT_OK) {
				String contents = intent.getStringExtra("SCAN_RESULT");
				// Handle successful scan
				et_nguia.setText(contents);
			} else if (resultCode == RESULT_CANCELED) {
				// Handle cancel
				Toast toast = Toast.makeText(this, getString(R.string.ecancel),
						Toast.LENGTH_LONG);
				toast.setGravity(Gravity.TOP, 25, 400);
				toast.show();

			}
		}
	}

}
