package com.example.emakumovil.modules.inventario;

import java.nio.channels.SocketChannel;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import org.jdom2.Attribute;
import org.jdom2.Document;
import org.jdom2.Element;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.example.emakumovil.R;
import com.example.emakumovil.components.AnswerEvent;
import com.example.emakumovil.components.AnswerListener;
import com.example.emakumovil.components.ProgressDialogManager;
import com.example.emakumovil.components.SearchQuery;
import com.example.emakumovil.communications.SocketConnector;
import com.example.emakumovil.communications.SocketWriter;
import com.example.emakumovil.control.ClientHeaderValidator;
import com.example.emakumovil.control.SuccessEvent;
import com.example.emakumovil.control.SuccessListener;
import com.example.emakumovil.misc.settings.ConfigFileHandler;

public class ListFitInventoryActivity extends Activity implements AnswerListener, OnClickListener, SuccessListener {

	private TextView tv_undfalta;
	private TextView tv_undsobra;
	private TextView tv_pfalta;
	private TextView tv_psobra;
	private TextView tv_undsdiff;
	private TextView tv_psdiff;
	
	private ListView lv_listajuste;
	
	private Button bt_guardar;
	
	private String fecha;
	private String id_proveedor;
	private String id_marca;
	private String id_referencia;
	private String id_bodega;
    private String desde;
    private String hasta;
    
	private int sobrantes;
	private int faltantes;
	private int undsdiff;
	private double psobrantes;
	private double pfaltantes;
	private double pdiff;
	private int cant;
	

	private ArrayList<AdjustPackage> items = new ArrayList<AdjustPackage>();


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_list_fit_inventory);
		Bundle extras = getIntent().getExtras();
		tv_undfalta = (TextView)findViewById(R.id.tv_undfalta);
		tv_undsobra = (TextView)findViewById(R.id.tv_undsobra);
		tv_pfalta = (TextView)findViewById(R.id.tv_pfalta);
		tv_psobra = (TextView)findViewById(R.id.tv_psobra);
		tv_undsdiff = (TextView)findViewById(R.id.tv_undsdiff);
		tv_psdiff = (TextView)findViewById(R.id.tv_psdiff);
		lv_listajuste = (ListView)findViewById(R.id.lv_listajuste);
		bt_guardar = (Button)findViewById(R.id.bt_guardar);
		bt_guardar.setOnClickListener(this);

		if (extras!=null) {
			fecha = extras.getString("fecha");
			String hora = extras.getString("hora");
			id_proveedor = extras.getString("id_proveedor");
			id_marca = extras.getString("id_marca");
			id_referencia = extras.getString("id_referencia");
			id_bodega = extras.getString("id_bodega");
			desde = extras.getString("desde");
			hasta = extras.getString("hasta");
			ClientHeaderValidator.addSuccessListener(this);
			ProgressDialogManager.show(this, ListFitInventoryActivity.handler,getString(R.string.cargando));
			new SearchQuery(this,"MVSEL0014", new String[] {id_proveedor,id_marca,id_referencia,id_bodega,fecha+" "+hora,desde,hasta}).start(); // Consulta registros para ajustar inventario
			
		}
	}


	@Override
	public void arriveAnswerEvent(AnswerEvent e) {
		// TODO Auto-generated method stub
		System.out.println("llego la query " + e.getSqlCode());
		Document doc = e.getDocument();
		if (e.getSqlCode().equals("MVSEL0014")) {
			final Element rootNode = doc.getRootElement();
			this.runOnUiThread(new Runnable() {
				public void run() {
					List<Element> listRows = rootNode.getChildren("row");
					items.clear();
					DecimalFormat df = new DecimalFormat("##,###,###");
					for (int i = 0; i < listRows.size(); i++) {
						Element Erow = (Element) listRows.get(i);
						List<Element> Lcol = Erow.getChildren();
						
						String id_prod_serv = ((Element) Lcol.get(0)).getText();
						String auxdif = ((Element) Lcol.get(1)).getText();
						String ponderado = ((Element) Lcol.get(2)).getText();
						String cta = ((Element) Lcol.get(3)).getText();
						String csobrante = ((Element) Lcol.get(4)).getText();
						String id_bodega = ((Element) Lcol.get(5)).getText();
						String barra = ((Element) Lcol.get(6)).getText();
						String descripcion = ((Element) Lcol.get(7)).getText();
						String ssistema = ((Element) Lcol.get(8)).getText();
						String sfisico = ((Element) Lcol.get(9)).getText();
						String diferencia = ((Element) Lcol.get(10)).getText();
						String pcosto = ((Element) Lcol.get(11)).getText();
						String referencia = ((Element) Lcol.get(12)).getText();
						String descripcion2 = ((Element) Lcol.get(13)).getText();
						String talla = ((Element) Lcol.get(14)).getText();
						String color = ((Element) Lcol.get(15)).getText();
						cant = cant + Integer.parseInt(diferencia);
						int diff = Integer.parseInt(diferencia);
						double costo = Double.parseDouble(pcosto);
						if (diff<0) {
							faltantes = faltantes + diff;
							pfaltantes = pfaltantes + (diff*costo);
						}
						else {
							sobrantes = sobrantes + diff;
							psobrantes = psobrantes + (diff*costo);
						}
						
						items.add(new AdjustPackage(
								id_prod_serv,
								auxdif,
								ponderado,
								cta,
								csobrante,
								id_bodega,
								barra,
								descripcion,
								ssistema,
								sfisico,
								diferencia,
								pcosto,
								referencia,
								descripcion2,
								talla,
								color));
					}
					tv_undfalta.setText(df.format(faltantes));
					tv_undsobra.setText(df.format(sobrantes));
					tv_pfalta.setText(df.format(pfaltantes));
					tv_psobra.setText(df.format(psobrantes));
					undsdiff = faltantes+sobrantes;
					pdiff = pfaltantes+psobrantes;
					tv_undsdiff.setText(df.format(faltantes+sobrantes));
					tv_psdiff.setText(df.format(pfaltantes+psobrantes));
					lv_listajuste.setAdapter(new AdjustPackageAdapter(
							ListFitInventoryActivity.this, R.layout.adjustablelistrecords,
							items));
					try {
						ProgressDialogManager.dismissCurrent();
					}
					catch(IllegalArgumentException e) {}
				}
			});
		}
		
	}

	@Override
	public boolean containSqlCode(String sqlCode) {
		// TODO Auto-generated method stub
		return false;
	}

	private class AdjustPackageAdapter extends ArrayAdapter<AdjustPackage> {

		private LayoutInflater vi;

		public AdjustPackageAdapter(Context context, int textViewResourceId,
				ArrayList<AdjustPackage> items) {
			super(context, textViewResourceId, items);
			ListFitInventoryActivity.this.items = items;
			Log.d("EMAKU ","EMAKU instanciando adaptador");
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View v = convertView;
			if (v == null) {
				vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			}

			AdjustPackage myData = items.get(position);
			if (myData != null) {
				v = vi.inflate(R.layout.adjustablelistrecords, null);
				TextView tv_barra = (TextView)v.findViewById(R.id.tv_barra);
				TextView tv_descripcion = (TextView)v.findViewById(R.id.tv_descripcion);
				TextView tv_ssistema = (TextView)v.findViewById(R.id.tv_ssistema);
				TextView tv_sfisico = (TextView)v.findViewById(R.id.tv_sfisico);
				TextView tv_diferencia = (TextView)v.findViewById(R.id.tv_diferencia);
				if (tv_barra != null) {
					tv_barra.setText(myData.getBarra());
				}
				if (tv_descripcion != null) {
					tv_descripcion.setText(myData.getDescripcion());
				}
				if (tv_ssistema != null) {
					tv_ssistema.setText(String.valueOf(myData.getSsistema()));
				}
				if (tv_sfisico != null) {
					tv_sfisico.setText(String.valueOf(myData.getSfisico()));
				}
				if (tv_diferencia != null) {
					tv_diferencia.setText(String.valueOf(myData.getDiferencia()));
				}
			}
			return v;
		}

	}

	private void sendTransaction() {
		Document transaction = new Document();
		Element raiz = new Element("TRANSACTION");
		Element driver = new Element("driver");
		driver.setText("MVTR00001");
		Element id = new Element("id");
		id.setText("TMV1");
		Element obs = new Element("package");
		Element data = new Element("package");
		for (int i=0;i<items.size();i++) {
			AdjustPackage sbpack = items.get(i);
			Element subpackage = new Element("subpackage");
			
			Element field1 = new Element("field");
			field1.setText(sbpack.getId_prod_serv());
			Attribute att1 = new Attribute("name","idProducto");
			field1.setAttribute(att1);
			subpackage.addContent(field1);
			
			Element field2 = new Element("field");
			field2.setText(sbpack.getAuxdif());
			Attribute att2 = new Attribute("name","cantidad");
			field2.setAttribute(att2);
			subpackage.addContent(field2);

			Element field3 = new Element("field");
			field3.setText(sbpack.getPcosto());
			Attribute att3 = new Attribute("name","pcosto");
			field3.setAttribute(att3);
			subpackage.addContent(field3);
			
			Element field4 = new Element("field");
			field4.setText(sbpack.getSsistema());
			subpackage.addContent(field4);
			
			Element field5 = new Element("field");
			field5.setText(sbpack.getSfisico());
			subpackage.addContent(field5);
			
			Element field6 = new Element("field");
			field6.setText(sbpack.getCta());
			subpackage.addContent(field6);
			
			Element field7 = new Element("field");
			field7.setText(sbpack.getCsobrante());
			subpackage.addContent(field7);

			Element field8 = new Element("field");
			field8.setText(sbpack.getId_bodega());
			Attribute att8 = new Attribute("name","bodegaAjuste");
			field8.setAttribute(att8);
			subpackage.addContent(field8);
			data.addContent(subpackage);
		}
		
		transaction.setRootElement(raiz);
		raiz.addContent(driver);
		raiz.addContent(id);
		raiz.addContent(obs);
		raiz.addContent(data);
        SocketChannel socket = SocketConnector.getSock();
        Log.d("EMAKU","EMAKU: Socket: "+socket);
        SocketWriter.writing(socket, transaction);

	}
	
	private void sendPrintJob(String ndocumento) {
		Document transaction = new Document();
		Element raiz = new Element("SERVERPRINTER");
		
		Element printerTemplate = new Element("printerTemplate");
		printerTemplate.setText("/graphics/TSAjusteInventarios.xml");
		
		Element jarFile = new Element("jarFile");
		jarFile.setText(ConfigFileHandler.getJarFile());

		Element jarDirectory = new Element("jarDirectory");
		jarDirectory.setText(ConfigFileHandler.getJarDirectory());

		Element numero1 = new Element("package");
		numero1.addContent(new Element("field").setText(ndocumento));
		
		Element numero2 = new Element("package");
		numero2.addContent(new Element("field").setText(ndocumento));
		
		Element efecha = new Element("package");
		efecha.addContent(new Element("field").setText(fecha));
		
		Element valores = new Element("package");
		valores.addContent(new Element("field").setText(String.valueOf(faltantes)));
		valores.addContent(new Element("field").setText(String.valueOf(sobrantes)));
		valores.addContent(new Element("field").setText(String.valueOf(pfaltantes)));
		valores.addContent(new Element("field").setText(String.valueOf(psobrantes)));
		valores.addContent(new Element("field").setText(String.valueOf(undsdiff)));
		valores.addContent(new Element("field").setText(String.valueOf(pdiff)));

		Element tcantidad = new Element("package");
		tcantidad.addContent(new Element("field").setText(String.valueOf(cant)));

		Element obs = new Element("package");
		
		Element data = new Element("package");
		for (int i=0;i<items.size();i++) {
			AdjustPackage sbpack = items.get(i);
			Element subpackage = new Element("subpackage");
			
			Element field1 = new Element("field");
			field1.setText(sbpack.getBarra());
			subpackage.addContent(field1);
			
			Element field2 = new Element("field");
			field2.setText(sbpack.getReferencia());
			subpackage.addContent(field2);

			Element field3 = new Element("field");
			field3.setText(sbpack.getDescripcion2());
			subpackage.addContent(field3);
			
			Element field4 = new Element("field");
			field4.setText(sbpack.getColor());
			subpackage.addContent(field4);
			
			Element field5 = new Element("field");
			field5.setText(sbpack.getTalla());
			subpackage.addContent(field5);
			
			Element field6 = new Element("field");
			field6.setText(sbpack.getSsistema());
			subpackage.addContent(field6);
			
			Element field7 = new Element("field");
			field7.setText(sbpack.getSfisico());
			subpackage.addContent(field7);

			Element field8 = new Element("field");
			field8.setText(sbpack.getDiferencia());
			subpackage.addContent(field8);
			data.addContent(subpackage);
		}
		
		transaction.setRootElement(raiz);
		raiz.addContent(printerTemplate);
		raiz.addContent(jarFile);
		raiz.addContent(jarDirectory);
		raiz.addContent(numero1);
		raiz.addContent(numero2);
		raiz.addContent(efecha);
		raiz.addContent(valores);
		raiz.addContent(tcantidad);
		raiz.addContent(obs);
		raiz.addContent(data);
        SocketChannel socket = SocketConnector.getSock();
        Log.d("EMAKU","EMAKU: Socket: "+socket);
        SocketWriter.writing(socket, transaction);

	}

	private class AdjustPackage {
	
		private String id_prod_serv;
		private String auxdif;
		private String cta;
		private String csobrante;
		private String id_bodega;
		private String barra;
		private String descripcion;
		private String ssistema;
		private String sfisico;
		private String diferencia;
		private String pcosto;
		private String referencia;
		private String descripcion2;
		private String talla;
		private String color;
		
		public AdjustPackage(String id_prod_serv,String auxdif,String ponderado,String cta,String csobrante,String id_bodega,String barra,
		String descripcion,String ssistema,String sfisico,String diferencia,String pcosto,String referencia,String descripcion2,String talla,String color) {
			this.id_prod_serv=id_prod_serv;
			this.auxdif=auxdif;
			this.cta=cta;
			this.csobrante=csobrante;
			this.id_bodega=id_bodega;
			this.barra=barra;
			this.descripcion=descripcion;
			this.ssistema=ssistema;
			this.sfisico=sfisico;
			this.diferencia=diferencia;
			this.pcosto=pcosto;
			this.referencia=referencia;
			this.descripcion2=descripcion2;
			this.talla=talla;
			this.color=color;
		}
		
		public String getId_prod_serv() {
			return id_prod_serv;
		}

		public String getAuxdif() {
			return auxdif;
		}

		public String getCta() {
			return cta;
		}

		public String getCsobrante() {
			return csobrante;
		}

		public String getId_bodega() {
			return id_bodega;
		}

		public String getBarra() {
			return barra;
		}

		public String getDescripcion() {
			return descripcion;
		}

		public String getSsistema() {
			return ssistema;
		}

		public String getSfisico() {
			return sfisico;
		}

		public String getDiferencia() {
			return diferencia;
		}

		public String getPcosto() {
			return pcosto;
		}

		public String getReferencia() {
			return referencia;
		}

		public String getDescripcion2() {
			return descripcion2;
		}

		public String getTalla() {
			return talla;
		}

		public String getColor() {
			return color;
		}


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

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		bt_guardar.setEnabled(false);
		sendTransaction();
	}

	@Override
	public void cathSuccesEvent(SuccessEvent e) {
		// TODO Auto-generated method stub
		sendPrintJob(e.getNdocument());
	}

}
