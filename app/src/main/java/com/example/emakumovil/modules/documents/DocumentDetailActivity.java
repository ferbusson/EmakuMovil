package com.example.emakumovil.modules.documents;

import java.io.IOException;
import java.nio.channels.SocketChannel;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TextView;

import com.example.emakumovil.R;
import com.example.emakumovil.components.AnswerEvent;
import com.example.emakumovil.components.AnswerListener;
import com.example.emakumovil.components.FieldsDDV;
import com.example.emakumovil.components.GlobalData;
import com.example.emakumovil.components.SearchQuery;
import com.example.emakumovil.communications.SocketConnector;
import com.example.emakumovil.communications.SocketWriter;

public class DocumentDetailActivity extends Activity implements OnClickListener, AnswerListener {

	private TextView tv_titulo;
	private TextView tv_fecha;
	private TextView tv_valor;
	private TextView tv_tercero;
	private TextView tv_nitcc;
	private TextView tv_estado;
	
	private TextView tv_sdebe;
	private TextView tv_shaber;
	private TextView tv_gtotal;

	private ListView lv_contabilizacion;
	private ListView lv_productos;
	
	private LinearLayout ly_tcontab;
	private LinearLayout ly_tprod;
	
	private Button bt_anular;
	
	private EditText et_numero;
	
	private String codigo_tipo;
	
	private TabHost tabs;
	
	private DecimalFormat df = new DecimalFormat("#,###,###,##0.00");
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_document_detail);
		Resources res = getResources();
		 
		tabs=(TabHost)findViewById(android.R.id.tabhost);
		tabs.setup();
		 
		// Pestaña 1 
		
		TabHost.TabSpec spec1=tabs.newTabSpec(getString(R.string.productos));
		spec1.setContent(R.id.ly_productos);
		spec1.setIndicator(getString(R.string.productos),
		    res.getDrawable(android.R.drawable.ic_dialog_alert));
		tabs.addTab(spec1);
		
	
		
		// Pestaña 2
		
		TabHost.TabSpec spec2=tabs.newTabSpec(getString(R.string.contabilizacion));
		spec2.setContent(R.id.ly_contabilizacion);
		spec2.setIndicator(getString(R.string.contabilizacion),
		    res.getDrawable(android.R.drawable.ic_dialog_map));
		tabs.addTab(spec2);
		 
		tabs.setCurrentTab(0);	
		
		ly_tcontab = (LinearLayout)findViewById(R.id.ly_tcontab);
		ly_tprod = (LinearLayout)findViewById(R.id.ly_tproductos);
	
		tabs.getTabWidget().getChildAt(0).setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
            		tabs.setCurrentTab(0);
            		ly_tcontab.setVisibility(View.GONE);
            		ly_tprod.setVisibility(View.VISIBLE);
            }});
		
		tabs.getTabWidget().getChildAt(1).setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
            		tabs.setCurrentTab(1);
            		ly_tcontab.setVisibility(View.VISIBLE);
            		ly_tprod.setVisibility(View.GONE);
            }});
				
		
		tv_titulo =(TextView)findViewById(R.id.tv_titulo);
		tv_fecha =(TextView)findViewById(R.id.tv_fecha);
		tv_valor =(TextView)findViewById(R.id.tv_valor);
		tv_tercero =(TextView)findViewById(R.id.tv_tercero);
		tv_nitcc =(TextView)findViewById(R.id.tv_nitcc);
		tv_estado =(TextView)findViewById(R.id.tv_estado);

		tv_sdebe =(TextView)findViewById(R.id.tv_debe);
		tv_shaber =(TextView)findViewById(R.id.tv_haber);
		tv_gtotal =(TextView)findViewById(R.id.tv_gtotal);

		et_numero = (EditText)findViewById(R.id.et_numero);
		et_numero.setOnClickListener(this);
		
		lv_contabilizacion = (ListView)findViewById(R.id.lv_contabilizacion);
		lv_productos = (ListView)findViewById(R.id.lv_productos);
	
		bt_anular = (Button)findViewById(R.id.bt_anular);
		bt_anular.setOnClickListener(this);
		
		Bundle extras = getIntent().getExtras();
		
		if (extras!=null) {
			String title = extras.getString("titulo");
			codigo_tipo = extras.getString("codigo_tipo");
			tv_titulo.setText(title);
		}
	}


	@Override
	public void arriveAnswerEvent(AnswerEvent e) {
		// TODO Auto-generated method stub
		Document doc = e.getDocument();
		System.out.println("llego la "+e.getSqlCode());
		final Element elm = doc.getRootElement();
		if (e.getSqlCode().equals("MVSEL0025")) {
			Iterator<Element> i = elm.getChildren("row").iterator();
			while (i.hasNext()) {
				System.out.println("iterator con datos");
				Element row = (Element) i.next();
				Iterator<Element> j = row.getChildren().iterator();
				final String fecha = ((Element) j.next()).getValue();
				final String valor = ((Element) j.next()).getValue();
				final String tercero = ((Element) j.next()).getValue();
				final String id_char = ((Element) j.next()).getValue();
				final String estado = ((Element) j.next()).getValue();
				
		        this.runOnUiThread(new Runnable() {
		            public void run() {
		            	tv_fecha.setText(getString(R.string.fecha)+": "+fecha);
						tv_valor.setText(getString(R.string.valor)+" "+valor);
						tv_tercero.setText(getString(R.string.tercero)+": "+tercero);
						tv_nitcc.setText(getString(R.string.nitcc)+" "+id_char);
						tv_estado.setText(getString(R.string.estado)+" "+estado);
		            }
		        });
			}
		}
		else if (e.getSqlCode().equals("MVSEL0026")) {
			this.runOnUiThread(new Runnable() {
				public void run() {
					List<Element> listRows = elm.getChildren("row");
					double tdebe = 0;
					double thaber = 0;
					GlobalData.items_contab.clear();
					for (int i = 0; i < listRows.size(); i++) {
						Element Erow = (Element) listRows.get(i);
						List<Element> Lcol = Erow.getChildren();
						String descripcion1 = ((Element) Lcol.get(0)).getText();
						String descripcion2 = ((Element) Lcol.get(1)).getText();
						double debe = Double
								.parseDouble(((Element) Lcol.get(2)).getText());
						double haber = Double
								.parseDouble(((Element) Lcol.get(3)).getText());
						tdebe+=debe;
						thaber+=haber;
						GlobalData.items_contab.add(new FieldsDDV(descripcion1,descripcion2,debe,haber));
					}
					
					tv_sdebe.setText(df.format(tdebe));
					tv_shaber.setText(df.format(thaber));
					
					//tv_total.setText(df.format(total));
					DataAdapterContab adapter = new DataAdapterContab(
							DocumentDetailActivity.this, R.layout.contabrecorddata,
							GlobalData.items_contab);
					System.out.println("adicionando adaptador");
					lv_contabilizacion.setAdapter(adapter);
					System.out.println("adaptador adicionado");
				}
			});
		
		}
		else if (e.getSqlCode().equals("MVSEL0027")) {
			this.runOnUiThread(new Runnable() {
				public void run() {
					List<Element> listRows = elm.getChildren("row");
					double gtotal = 0;
					GlobalData.items_prod.clear();
					for (int i = 0; i < listRows.size(); i++) {
						Element Erow = (Element) listRows.get(i);
						List<Element> Lcol = Erow.getChildren();
						String descripcion1 = ((Element) Lcol.get(0)).getText();
						String descripcion2 = ((Element) Lcol.get(1)).getText();
						int cantidad = Integer
								.parseInt(((Element) Lcol.get(2)).getText());
						double pventa = Double
								.parseDouble(((Element) Lcol.get(3)).getText());
						double total = Double
								.parseDouble(((Element) Lcol.get(4)).getText());
						gtotal+=total;
						GlobalData.items_prod.add(new FieldsDDV(descripcion1,descripcion2,cantidad,pventa,total));
					}
					
					tv_gtotal.setText(df.format(gtotal));
					
					//tv_total.setText(df.format(total));
					DataAdapterProd adapter = new DataAdapterProd(
							DocumentDetailActivity.this, R.layout.prodrecorddata,
							GlobalData.items_prod);
					System.out.println("adicionando adaptador");
					lv_productos.setAdapter(adapter);
					System.out.println("adaptador adicionado");
				}
			});
		
		
		}
		
	}

	private class DataAdapterContab extends ArrayAdapter<FieldsDDV> {

		private LayoutInflater vi;

		public DataAdapterContab(Context context, int textViewResourceId,
				ArrayList<FieldsDDV> items) {
			super(context, textViewResourceId, items);
			GlobalData.items_contab = items;
			Log.d("EMAKU ","EMAKU instanciando adaptador");
			vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View v = convertView;
			ViewHolderContab holder;
			
			if (v == null) {
				v = vi.inflate(R.layout.contabrecorddata, null);
				TextView tv_descripcion1 = (TextView) v
						.findViewById(R.id.tv_descripcion1);
				TextView tv_descripcion2 = (TextView) v
						.findViewById(R.id.tv_descripcion2);
				TextView tv_debe = (TextView) v
						.findViewById(R.id.tv_debe);
				TextView tv_haber = (TextView) v
						.findViewById(R.id.tv_haber);
				holder = new ViewHolderContab(tv_descripcion1,tv_descripcion2,tv_debe,tv_haber);
				v.setTag(holder);
			}
			else {
				holder = (ViewHolderContab)v.getTag();
			}

			
			FieldsDDV myData = GlobalData.items_contab.get(position);
			if (myData != null) {
				if (holder.tv_descripcion1 != null) {
					holder.tv_descripcion1.setText(myData.getDescripcion1());
				}
				if (holder.tv_descripcion2 != null) {
					holder.tv_descripcion2.setText(myData.getDescripcion2());
				}
				if (holder.tv_debe != null) {
					holder.tv_debe.setText(df.format(myData.getDebe()));
				}
				if (holder.tv_haber != null) {
					holder.tv_haber.setText(df.format(myData.getHaber()));
				}
			}
			return v;
		}

	}

	private class DataAdapterProd extends ArrayAdapter<FieldsDDV> {

		private LayoutInflater vi;

		public DataAdapterProd(Context context, int textViewResourceId,
				ArrayList<FieldsDDV> items) {
			super(context, textViewResourceId, items);
			GlobalData.items_prod = items;
			Log.d("EMAKU ","EMAKU instanciando adaptador");
			vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View v = convertView;
			ViewHolderProd holder;
			
			if (v == null) {
				v = vi.inflate(R.layout.prodrecorddata, null);
				TextView tv_descripcion1 = (TextView) v
						.findViewById(R.id.tv_descripcion1);
				TextView tv_descripcion2 = (TextView) v
						.findViewById(R.id.tv_descripcion2);
				TextView tv_cantidad = (TextView) v
						.findViewById(R.id.tv_cantidad);
				TextView tv_pventa = (TextView) v
						.findViewById(R.id.tv_pventa);
				TextView tv_total = (TextView) v
						.findViewById(R.id.tv_total);
				holder = new ViewHolderProd(tv_descripcion1,tv_descripcion2,tv_cantidad,tv_pventa,tv_total);
				v.setTag(holder);
			}
			else {
				holder = (ViewHolderProd)v.getTag();
			}

			
			FieldsDDV myData = GlobalData.items_prod.get(position);
			if (myData != null) {
				if (holder.tv_descripcion1 != null) {
					holder.tv_descripcion1.setText(myData.getDescripcion1());
				}
				if (holder.tv_descripcion2 != null) {
					holder.tv_descripcion2.setText(myData.getDescripcion2());
				}
				if (holder.tv_cantidad != null) {
					holder.tv_cantidad.setText(df.format(myData.getCantidad()));
				}
				if (holder.tv_pventa != null) {
					holder.tv_pventa.setText(df.format(myData.getPventa()));
				}
				if (holder.tv_total != null) {
					holder.tv_total.setText(df.format(myData.getTotal()));
				}
			}
			return v;
		}

	}

	private class ViewHolderContab {
 		public TextView tv_descripcion1;
 		public TextView tv_descripcion2;
 		public TextView tv_debe;
 		public TextView tv_haber;
 		
 		public ViewHolderContab(TextView tv_descripcion1,TextView tv_descripcion2,TextView tv_debe,TextView tv_haber) {
 			this.tv_descripcion1=tv_descripcion1;
 			this.tv_descripcion2=tv_descripcion2;
 			this.tv_debe=tv_debe;
 			this.tv_haber=tv_haber;
 		}
 		
 	}

	private class ViewHolderProd {
 		public TextView tv_descripcion1;
 		public TextView tv_descripcion2;
 		public TextView tv_cantidad;
 		public TextView tv_pventa;
 		public TextView tv_total;
 		
 		public ViewHolderProd(TextView tv_descripcion1,TextView tv_descripcion2,TextView tv_cantidad,TextView tv_pventa,TextView tv_total) {
 			this.tv_descripcion1=tv_descripcion1;
 			this.tv_descripcion2=tv_descripcion2;
 			this.tv_cantidad=tv_cantidad;
 			this.tv_pventa=tv_pventa;
 			this.tv_total=tv_total;
 		}
 		
 	}

	@Override
	public boolean containSqlCode(String sqlCode) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		if (arg0 instanceof EditText) {
			EditText et = (EditText)arg0;
			new SearchQuery(this,"MVSEL0025",new String[]{codigo_tipo,et.getText().toString()}).start(); // consulta listado de documentos
			new SearchQuery(this,"MVSEL0026",new String[]{codigo_tipo,et.getText().toString()}).start(); // consulta contabilizacion de documentos
			new SearchQuery(this,"MVSEL0027",new String[]{codigo_tipo,et.getText().toString()}).start(); // consulta productos de documentos
		}
		else {
			sendPackage();
		}
	}
		
	private void sendPackage() {
		Document transaction = new Document();
		Element raiz = new Element("TRANSACTION");
		Element driver = new Element("driver");
		driver.setText("MVTR00003");
		Element id = new Element("id");
		id.setText("TMV3");
		transaction.setRootElement(raiz);
		raiz.addContent(driver);
		raiz.addContent(id);

		// Numero..
		Element num = new Element("package");
		Element field1 = new Element("field");
		field1.setText(et_numero.getText().toString());
		num.addContent(field1);
		raiz.addContent(num);

		// Tipo ..
		Element tipo = new Element("package");
		Element field2 = new Element("field");
		field2.setText(codigo_tipo);
		tipo.addContent(field2);
		raiz.addContent(tipo);

		// Blanco ..
		Element blanco = new Element("package");
		Element field3 = new Element("field");
		blanco.addContent(field3);
		raiz.addContent(blanco);
		
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

}
