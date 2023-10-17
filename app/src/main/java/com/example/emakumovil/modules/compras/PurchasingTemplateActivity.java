package com.example.emakumovil.modules.compras;

import java.io.File;
import java.io.IOException;
import java.nio.channels.SocketChannel;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import jxl.Cell;
import jxl.CellType;
import jxl.NumberCell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;

import org.jdom2.Document;
import org.jdom2.Element;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.example.emakumovil.R;
import com.example.emakumovil.components.AnswerEvent;
import com.example.emakumovil.components.AnswerListener;
import com.example.emakumovil.components.SearchQuery;
import com.example.emakumovil.communications.SocketConnector;
import com.example.emakumovil.communications.SocketWriter;

public class PurchasingTemplateActivity extends Activity implements
		OnClickListener, AnswerListener {

	private ImageButton ib_excel;
	private ArrayList<TemplateXLS> xls = new ArrayList<TemplateXLS>();
	private ListView lv_template;
	private DecimalFormat df = new DecimalFormat("##,###,##0.00");
	private DataAdapter adapter;
	
	private TextView tv_descripcion1;
	private TextView tv_descripcion2;
	private TextView tv_descripcion3;
	private TextView tv_descripcion4;
	
	private TextView tv_list_products;
	private TextView tv_stotal;
	private TextView tv_descuento;
	private TextView tv_iva;
	private TextView tv_total;

	private Button bt_guardar;
	
	private boolean withBarcode;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_purchasing_template);
		ib_excel = (ImageButton) findViewById(R.id.ib_excel);
		
		tv_descripcion1 = (TextView)findViewById(R.id.tv_descripcion1);
		tv_descripcion2 = (TextView)findViewById(R.id.tv_descripcion2);
		tv_descripcion3 = (TextView)findViewById(R.id.tv_descripcion3);
		tv_descripcion4 = (TextView)findViewById(R.id.tv_descripcion4);
		lv_template = (ListView) findViewById(R.id.lv_template);
		tv_stotal = (TextView)findViewById(R.id.tv_stotal);
		tv_descuento = (TextView)findViewById(R.id.tv_tdescuento);
		tv_iva = (TextView)findViewById(R.id.tv_giva);
		tv_total = (TextView)findViewById(R.id.tv_gtotal);
		tv_list_products = (TextView)findViewById(R.id.tv_list_products);
		ib_excel.setOnClickListener(this);
		
		bt_guardar = (Button)findViewById(R.id.bt_guardar);
		bt_guardar.setOnClickListener(this);
		
		Bundle extras = getIntent().getExtras();
		if (extras!=null) {
			withBarcode = extras.getBoolean("withBarcode");
			if (!withBarcode) {
				setTitle(R.string.importar_plantilla_sin_barra);
			}
		}
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.template_with_barcode, menu);
		return true;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if (v.getId() == R.id.ib_excel) {
			Intent intent = new Intent("org.openintents.action.PICK_FILE");
			startActivityForResult(intent, 1);
		}
		else if (v.getId()==R.id.bt_guardar) {
			sendTransaction();
		}
	}

	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);
		Log.d("EMAKU", " ResultCode: " + resultCode + " requestCode: "
				+ requestCode);
		if (requestCode == 1) {
			if (resultCode == RESULT_OK) {
				Uri path = intent.getData();
				loadFileXLS(path);
			}
		}
	}

	public void loadFileXLS(Uri path) {
		File file = new File(path.getPath());
		try {
			Workbook workbook = Workbook.getWorkbook(file);
			Sheet sheet = workbook.getSheet(0);
			xls = new ArrayList<TemplateXLS>();
			
			double stotal = 0;
			double gdescuento = 0;
			double giva = 0;
			double gtotal = 0;
			int gcant = 0;
			
			int sinbarra = 0;
			if (!withBarcode) {
				sinbarra=1;
			}
			
			for (int i = 3; i < sheet.getRows(); i++) {
				Cell[] celdas = sheet.getRow(i);
				int cantidad = 0;
				double costo = 0;
				double iva = 0;
				double pdescuento = 0;

				if (celdas[0].getContents().equals("")) {
					break;
				}

				if (i==3) {
					new SearchQuery(this,"MVSEL0043",new String[] {celdas[0].getContents()}).start();					
				}

				if (celdas[9-sinbarra].getType() == CellType.NUMBER) {
					NumberCell nc = (NumberCell) celdas[9-sinbarra];
					cantidad = (int) nc.getValue();
				} else {
					cantidad = Integer.parseInt(celdas[9-sinbarra].getContents());
				}

				if (celdas[10-sinbarra].getType() == CellType.NUMBER) {
					NumberCell nc = (NumberCell) celdas[10-sinbarra];
					costo = (int) nc.getValue();
				} else {
					costo = Double.parseDouble(celdas[10-sinbarra].getContents());
				}

				if (celdas[11-sinbarra].getType() == CellType.NUMBER) {
					NumberCell nc = (NumberCell) celdas[11-sinbarra];
					iva = (int) nc.getValue();
				} else {
					iva = Double.parseDouble(celdas[11-sinbarra].getContents());
				}

				if (celdas[12-sinbarra].getType() == CellType.NUMBER) {
					NumberCell nc = (NumberCell) celdas[12-sinbarra];
					pdescuento = (int) nc.getValue();
				} else {
					pdescuento = Double.parseDouble(celdas[12-sinbarra].getContents());
				}

				double neto = Math.rint(cantidad*costo*100)/100;
				double descuento = Math.rint(neto * pdescuento)/100;
				double tiva = Math.rint((neto-descuento)*iva)/100;
				double total = neto-descuento+tiva;
			
				gcant+=cantidad;
				stotal+=neto;
				gdescuento+=descuento;
				giva+=tiva;
				gtotal+=total;
				
				if (withBarcode) {
					xls.add(new TemplateXLS(celdas[0].getContents(), celdas[1]
							.getContents(), celdas[2].getContents(), celdas[3]
							.getContents(), celdas[4].getContents(), celdas[5]
							.getContents(), celdas[6].getContents(), celdas[7]
							.getContents(), celdas[8].getContents(), cantidad,
							costo, iva, pdescuento,neto,descuento,tiva,total));
				}
				else {
					xls.add(new TemplateXLS(celdas[0].getContents(), celdas[1]
							.getContents(), celdas[2].getContents(), celdas[3]
							.getContents(),null,celdas[4].getContents(), celdas[5]
							.getContents(), celdas[6].getContents(), celdas[7]
							.getContents(), cantidad,costo, iva, pdescuento,neto,descuento,tiva,total));
				}
				adapter = new DataAdapter(
						PurchasingTemplateActivity.this, R.layout.listpurchasingtemplate,
						xls);
				lv_template.setAdapter(adapter);

			}

			tv_stotal.setText(df.format(stotal));
			tv_descuento.setText(df.format(gdescuento));
			tv_iva.setText(df.format(giva));
			tv_total.setText(df.format(gtotal));
			tv_list_products.setText(getString(R.string.lista_productos)+" "+gcant);
			
			workbook = null;
			file = null;
			System.gc();

		} catch (BiffException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private class DataAdapter extends ArrayAdapter<TemplateXLS> {

		private LayoutInflater vi;
		private ArrayList<TemplateXLS> items;
		
		public DataAdapter(Context context, int textViewResourceId,
				ArrayList<TemplateXLS> items) {
			super(context, textViewResourceId, items);
			Log.d("EMAKU ","EMAKU instanciando adaptador");
			vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			this.items=items;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View v = convertView;
			ViewHolder holder;
			TemplateXLS myData = items.get(position);
			if (v == null) {
				v = vi.inflate(R.layout.listpurchasingtemplate, null);

				TextView tv_barcode = (TextView) v
						.findViewById(R.id.tv_barcode);
				if (!withBarcode) {
					tv_barcode.setVisibility(View.GONE);
				}
				
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
					holder.tv_descripcion.setText(myData.getMarca()+" "+myData.getItem()+" "+myData.getDescripcion()+" Talla "+myData.getTalla()+" C.Color "+myData.getCcolor()+" "+myData.getDcolor());
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

 	public class TemplateXLS {
		private String nit;
		private String doc;
		private String item;
		private String descripcion;
		private String barcode;
		private String talla;
		private String ccolor;
		private String dcolor;
		private String marca;
		private int cantidad;
		private double costo;
		private double iva;
		private double pdescuento;
		private double neto;
		private double descuento;
		private double tiva;
		private double total;

		public TemplateXLS(String nit, String doc, String item,
				String descripcion, String barcode, String talla,
				String ccolor, String dcolor, String marca, int cantidad,
				double costo, double iva, double pdescuento,double neto,
				double descuento,double tiva,double total) {
			this.nit = nit;
			this.doc = doc;
			this.item = item;
			this.descripcion = descripcion;
			this.barcode = barcode;
			this.talla = talla;
			this.ccolor = ccolor;
			this.dcolor = dcolor;
			this.marca = marca;
			this.cantidad = cantidad;
			this.costo = costo;
			this.iva = iva;
			this.pdescuento = pdescuento;
			this.neto=neto;
			this.descuento = descuento;
			this.tiva=tiva;
			this.total=total;
			System.out.println("cargando registro...");
		}

		public String getNit() {
			return nit;
		}

		public String getDoc() {
			return doc;
		}

		public String getItem() {
			return item;
		}

		public String getDescripcion() {
			return descripcion;
		}

		public String getBarcode() {
			return barcode;
		}

		public String getTalla() {
			return talla;
		}

		public String getCcolor() {
			return ccolor;
		}

		public String getDcolor() {
			return dcolor;
		}

		public String getMarca() {
			return marca;
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

		
	}

	@Override
	public void arriveAnswerEvent(AnswerEvent e) {
		// TODO Auto-generated method stub
		if (e.getSqlCode().equals("MVSEL0043")) {
			try {
				Document doc = e.getDocument();
				final Element rootNode = doc.getRootElement();
				runOnUiThread(new Runnable() {
					public void run() {
						List<Element> listRows = rootNode.getChildren("row");
						for (int i = 0; i < listRows.size(); i++) {
							Element Erow = (Element) listRows.get(i);
							List<Element> Lcol = Erow.getChildren();
							tv_descripcion1.setText(((Element) Lcol.get(0)).getText());
							tv_descripcion2.setText(((Element) Lcol.get(1)).getText());
							tv_descripcion3.setText(((Element) Lcol.get(2)).getText());
							tv_descripcion4.setText(((Element) Lcol.get(3)).getText());
						}
					}
				});
			}
			catch(NullPointerException NPEe) {
				NPEe.printStackTrace();
			}
		}
	}
	
	private void sendTransaction() {
		Document transaction = new Document();
		Element raiz = new Element("TRANSACTION");
		Element driver = new Element("driver");
		if (withBarcode) {
			driver.setText("MVTR00014");
		}
		else {
			driver.setText("MVTR00015");
		}
		
		Element id = new Element("id");
		id.setText("TMV1");
		Element blank1 = new Element("package").addContent(new Element("field"));
		Element blank2 = new Element("package").addContent(new Element("field"));
		Element data = new Element("package");
		for (int i=0;i<xls.size();i++) {
			TemplateXLS sbpack = xls.get(i);
			Element subpackage = new Element("subpackage");
			
			Element field1 = new Element("field");
			field1.setText(sbpack.getMarca());
			subpackage.addContent(field1);
			
			Element field2 = new Element("field");
			field2.setText(sbpack.getCcolor());
			subpackage.addContent(field2);

			Element field3 = new Element("field");
			field3.setText(sbpack.getDcolor());
			subpackage.addContent(field3);
			
			Element field4 = new Element("field");
			field4.setText(sbpack.getTalla());
			subpackage.addContent(field4);
			
			Element field5 = new Element("field");
			field5.setText(sbpack.getItem());
			subpackage.addContent(field5);
			
			Element field6 = new Element("field");
			field6.setText(sbpack.getDescripcion());
			subpackage.addContent(field6);
			
			if (withBarcode) {
				Element field7 = new Element("field");
				field7.setText(sbpack.getBarcode());
				subpackage.addContent(field7);
			}
			
			Element field8 = new Element("field");
			field8.setText(String.valueOf(sbpack.getCantidad()));
			subpackage.addContent(field8);
			
			Element field9 = new Element("field");
			field9.setText(""+sbpack.getCosto());
			subpackage.addContent(field9);
			
			Element field10 = new Element("field");
			field10.setText(""+sbpack.getIva());
			subpackage.addContent(field10);
			
			Element field11 = new Element("field");
			field11.setText(sbpack.getNit());
			subpackage.addContent(field11);
			
			Element field12 = new Element("field");
			field12.setText(sbpack.getDoc());
			subpackage.addContent(field12);
			
			Element field13 = new Element("field");
			field13.setText(""+sbpack.getPdescuento());
			subpackage.addContent(field13);
			data.addContent(subpackage);

		}
		
		transaction.setRootElement(raiz);
		raiz.addContent(driver);
		raiz.addContent(id);
		raiz.addContent(blank1);
		raiz.addContent(data);
		raiz.addContent(blank2);
        SocketChannel socket = SocketConnector.getSock();
        Log.d("EMAKU","EMAKU: Socket: "+socket);
        SocketWriter.writing(socket, transaction);

	}


	@Override
	public boolean containSqlCode(String sqlCode) {
		// TODO Auto-generated method stub
		return false;
	}

}