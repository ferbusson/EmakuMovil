package com.example.emakumovil.modules.cartera;

import java.io.IOException;
import java.nio.channels.SocketChannel;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.jdom2.Attribute;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.emakumovil.R;
import com.example.emakumovil.components.AnswerEvent;
import com.example.emakumovil.components.AnswerListener;
import com.example.emakumovil.components.DialogClickListener;
import com.example.emakumovil.components.GlobalData;
import com.example.emakumovil.components.ProgressDialogManager;
import com.example.emakumovil.components.SearchQuery;
import com.example.emakumovil.comunications.SocketConnector;
import com.example.emakumovil.comunications.SocketWriter;
import com.example.emakumovil.control.ClientHeaderValidator;
import com.example.emakumovil.control.SuccessEvent;
import com.example.emakumovil.control.SuccessListener;
import com.example.emakumovil.misc.settings.ConfigFileHandler;

public class ListCarteraActivity extends Activity implements AnswerListener, OnClickListener, DialogClickListener, SuccessListener {

	private TextView tv_descripcion1;
	private TextView tv_descripcion2;
	private TextView tv_descripcion3;
	private TextView tv_descripcion4;
	private TextView tv_saldo_ant;
	private TextView tv_abonos;
	private TextView tv_descuentos;
	private TextView tv_nuevo_saldo;
	private ListView lv_listdocs;
	private ImageButton ic_pago;
	private double total_cartera;
	private double abonos;
	private double descuento;
	private double nsaldo;
	private DecimalFormat df = new DecimalFormat("##,###,###");
	private String query;
	private String title;
	private int selectedItemClick;
	private PartialPaymentDialog partialpayment;
	private PaymentOnAccount paymentonaccount;
	private static PaymentIn paymentin;
	private DataAdapter adapter;
	private String id_char;
	private String id_tercero;
	private String fecha;
	
	private double efectivo;
	private double tarjetas;
	private double bancos;
	private String id_banco;
	
    private static final int CARTERA = 0;
    private static final int CXP = 1;
	private int type;

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
			setContentView(R.layout.activity_list_cartera);
			Calendar c = Calendar.getInstance();
			int year = c.get(Calendar.YEAR);
			int month = c.get(Calendar.MONTH);
			int day = c.get(Calendar.DAY_OF_MONTH);
	
			fecha = year
					+ "/"
					+ (month + 1 <= 9 ? "0" + (month + 1)
							: (month + 1)) + "/"
					+ (day <= 9 ? "0" + day : day);
	
			Bundle extras = getIntent().getExtras();
			tv_descripcion1 =(TextView)findViewById(R.id.tv_descripcion1);
			tv_descripcion2 =(TextView)findViewById(R.id.tv_descripcion2);
			tv_descripcion3 =(TextView)findViewById(R.id.tv_descripcion3);
			tv_descripcion4 =(TextView)findViewById(R.id.tv_descripcion4);
			tv_saldo_ant =(TextView)findViewById(R.id.tv_saldo_anterior);
			tv_descuentos =(TextView)findViewById(R.id.tv_descuentos);
			tv_abonos =(TextView)findViewById(R.id.tv_abono_cartera);
			tv_nuevo_saldo =(TextView)findViewById(R.id.tv_nuevo_saldo);
			lv_listdocs = (ListView)findViewById(R.id.lv_listdocs);
			ic_pago = (ImageButton)findViewById(R.id.ic_pago);
			ic_pago.setOnClickListener(this);
			if (extras!=null) {
				title = extras.getString("titulo");
				setTitle(title);
				type = extras.getInt("type");
				paymentin = new PaymentIn(type);
				paymentin.addDialogClickListener(this);
				id_tercero = extras.getString("id_tercero");
				id_char = extras.getString("descripcion1");
				tv_descripcion1.setText(id_char);
				tv_descripcion2.setText(extras.getString("descripcion2"));
				tv_descripcion3.setText(extras.getString("descripcion3"));
				tv_descripcion4.setText(extras.getString("descripcion4"));
				tv_saldo_ant.setText(extras.getString("valor"));
				tv_nuevo_saldo.setText(extras.getString("valor"));
				total_cartera = extras.getDouble("total_cartera");
				System.out.println("total cartera: "+total_cartera);
				query = extras.getString("query");
				if (savedInstanceState==null) {
					new SearchQuery(this,query, new String[] {
							id_char}).start();
					ProgressDialogManager.show(this, ListCarteraActivity.handler,getString(R.string.cargando));
				}
				else {
					adapter = new DataAdapter(
							ListCarteraActivity.this, R.layout.listdocumentscartera,
							GlobalData.items);
					lv_listdocs.setAdapter(adapter);
					adapter.notifyDataSetChanged();
					
					recalculate();
				}

				ClientHeaderValidator.addSuccessListener(this);

			}
			registerForContextMenu(lv_listdocs);

	}
	
	public void onSaveInstanceState(Bundle savedInstanceState) {
		super.onSaveInstanceState(savedInstanceState);
		savedInstanceState.putDouble("total_cartera",total_cartera);
		savedInstanceState.putDouble("abonos",abonos);
		savedInstanceState.putDouble("descuento",descuento);
		savedInstanceState.putDouble("nsaldo",nsaldo);
		savedInstanceState.putString("id_char",id_char);
	}
	
	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState) {
	  super.onRestoreInstanceState(savedInstanceState);
	  total_cartera=savedInstanceState.getDouble("total_cartera");
	  abonos=savedInstanceState.getDouble("abonos");
	  descuento=savedInstanceState.getDouble("descuento");
	  nsaldo=savedInstanceState.getDouble("nsaldo");
	  id_char=savedInstanceState.getString("id_char");
	}
	
	@Override
	public void arriveAnswerEvent(AnswerEvent e) {
		// TODO Auto-generated method stub
		Document doc = e.getDocument();
		final Element rootNode = doc.getRootElement();
		this.runOnUiThread(new Runnable() {
			public void run() {
				List<Element> listRows = rootNode.getChildren("row");
				GlobalData.items.clear();
				for (int i = 0; i < listRows.size(); i++) {
					Element Erow = (Element) listRows.get(i);
					List<Element> Lcol = Erow.getChildren();
					String ndocumento = ((Element) Lcol.get(0)).getText();
					String idCta = ((Element) Lcol.get(1)).getText();
					String charCta = ((Element) Lcol.get(2)).getText();
					String documento = ((Element) Lcol.get(3)).getText();
					double base = Double.parseDouble(((Element) Lcol.get(4)).getText());
					double tvalor = Double.parseDouble(((Element) Lcol.get(5)).getText());
					String valor = df.format(tvalor);
					System.out.println("string valor: "+valor+" double valor "+tvalor);
					GlobalData.items.add(new FieldsCartera(ndocumento,charCta,idCta,documento,base,valor,"0","0",valor,tvalor));
				}
				adapter = new DataAdapter(
						ListCarteraActivity.this, R.layout.listdocumentscartera,
						GlobalData.items);
				System.out.println("adicionando adaptador");
				lv_listdocs.setAdapter(adapter);
				System.out.println("adaptador adicionado");
				try {
					ProgressDialogManager.dismissCurrent();
				}
				catch(IllegalArgumentException e) {}
			}
		});
	}

	private class DataAdapter extends ArrayAdapter<FieldsCartera> {

		private LayoutInflater vi;

		public DataAdapter(Context context, int textViewResourceId,
				ArrayList<FieldsCartera> items) {
			super(context, textViewResourceId, items);
			GlobalData.items = items;
			Log.d("EMAKU ","EMAKU instanciando adaptador");
			vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View v = convertView;
			ViewHolder holder;
			FieldsCartera myData = GlobalData.items.get(position);
			CheckBox checkbox = null;
			if (v == null) {
				v = vi.inflate(R.layout.listdocumentscartera, null);
				TextView tv_descripcion1 = (TextView) v
						.findViewById(R.id.tv_descripcion);
				
				TextView tv_vtotal = (TextView) v
						.findViewById(R.id.tv_vtotal);
				
				TextView tv_vabono = (TextView) v
						.findViewById(R.id.tv_vabonos);
				
				TextView tv_vdescuento = (TextView) v
						.findViewById(R.id.tv_vdescuento);
				
				TextView tv_vsaldo = (TextView) v
						.findViewById(R.id.tv_vsaldo);
				
				checkbox = (CheckBox) v
						.findViewById(R.id.cb_pagar);
						
				holder = new ViewHolder(tv_descripcion1,tv_vtotal,tv_vabono,tv_vdescuento,tv_vsaldo,checkbox);
				v.setTag(holder);
			}
			else {
				holder = (ViewHolder)v.getTag();
				checkbox = holder.cb_pagar;
			}
			checkbox.setFocusable(false);
			new CheckBoxList(position,checkbox,holder);
			lv_listdocs.setDescendantFocusability(ViewGroup.FOCUS_BEFORE_DESCENDANTS);


			
			if (myData != null) {
				if (holder.tv_descripcion1 != null) {
					holder.tv_descripcion1.setText(myData.getDocumento());
					holder.tv_vtotal.setText(myData.getTotal());
					holder.tv_vabono.setText(myData.getAbono());
					holder.tv_vdescuento.setText(myData.getDescuento());
					holder.tv_vsaldo.setText(myData.getSaldo());
					holder.cb_pagar.setChecked(myData.isChecked());
				}
			}
			return v;
		}
		

	}

	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		System.out.println("hizo click");
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
	public boolean containSqlCode(String sqlCode) {
		// TODO Auto-generated method stub
		return false;
	}

 	private class ViewHolder {
 		public TextView tv_descripcion1;
 		public TextView tv_vsaldo;
 		public TextView tv_vabono;
 		public TextView tv_vdescuento;
 		public TextView tv_vtotal;
 		public CheckBox cb_pagar;
 		
 		public ViewHolder(TextView tv_descripcion1,TextView tv_vtotal,TextView tv_vabono,TextView tv_vdescuento,TextView tv_vsaldo,CheckBox cb_pagar) {
 			this.tv_descripcion1=tv_descripcion1;
 			this.tv_vsaldo=tv_vsaldo;
 			this.tv_vabono=tv_vabono;
 			this.tv_vdescuento=tv_vdescuento;
 			this.tv_vtotal=tv_vtotal;
 			this.cb_pagar=cb_pagar;
 		}
 		
 	}
 	
 	private class CheckBoxList implements OnClickListener {

 		private CheckBox checkbox;
 		private ViewHolder holder;
 		private int position;
 		
		public CheckBoxList(int position,CheckBox checkbox,ViewHolder holder) {
			// TODO Auto-generated constructor stub
			this.checkbox=checkbox;
			this.holder=holder;
			this.checkbox.setOnClickListener(this);
			this.position=position;
		}

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			CheckBox check = (CheckBox)v;
			System.out.println("position: "+position);
			if (check.isChecked()) {
				holder.tv_vabono.setText(GlobalData.items.get(position).getTotal());
				holder.tv_vsaldo.setText("0");
				GlobalData.items.get(position).setAbono(GlobalData.items.get(position).getTotal());
				GlobalData.items.get(position).setVabono(GlobalData.items.get(position).getDtotal());
				GlobalData.items.get(position).setSaldo("0");
				GlobalData.items.get(position).setChecked(true);
				abonos+=GlobalData.items.get(position).getDtotal();
			}
			else {
				holder.tv_vabono.setText("0");
				holder.tv_vsaldo.setText(GlobalData.items.get(position).getTotal());
				holder.tv_vdescuento.setText("0");
				GlobalData.items.get(position).setAbono("0");
				GlobalData.items.get(position).setSaldo(GlobalData.items.get(position).getTotal());
				GlobalData.items.get(position).setChecked(false);
				abonos-=GlobalData.items.get(position).getVabono();
				descuento-=GlobalData.items.get(position).getVdescuento();
				GlobalData.items.get(position).setVabono(0);
				GlobalData.items.get(position).setVdescuento(0);
				GlobalData.items.get(position).setDescuento("0");
			}
			tv_abonos.setText(df.format(abonos));
			tv_descuentos.setText(df.format(descuento));
			nsaldo = total_cartera - abonos-descuento;
			System.out.println("total_cartera: "+total_cartera+" abonos "+abonos+" saldo "+(total_cartera-abonos));
			tv_nuevo_saldo.setText(df.format(nsaldo));
		}
 		
  	}

 	/**
 	 * @author felipe
 	 *
 	 */
 	public class FieldsCartera {
 		
 		private String documento;
 		private String pdescuento="0";
 		private String ndocumento;
 		private String charCta;
 		private String idCta;
 		private double base;
 		private double vdescuento;
 		private double vabono;
 		private double vsaldo;
 		private String total;
 		private String abono;
 		private String saldo;
 		private String descuento;
		private double dtotal;
 		private boolean checked;

 		public FieldsCartera(String ndocumento,String charCta,String idCta,String documento,double base,String total,String abono,String descuento,String saldo,double dtotal) {
 			this.ndocumento=ndocumento;
 			this.documento=documento;
 			this.charCta=charCta;
 			this.idCta=idCta;
 			this.base=base;
 			this.total=total;
 			this.abono=abono;
 			this.descuento=descuento;
 			this.saldo=saldo;
 			this.dtotal=dtotal;
 		}

		public String getNdocumento() {
			return ndocumento;
		}

		public String getDocumento() {
			return documento;
		}

		public String getTotal() {
			return total;
		}

		public String getAbono() {
			return abono;
		}

		public String getDescuento() {
			return descuento;
		}

		public String getSaldo() {
			return saldo;
		}

		public double getDtotal() {
			return dtotal;
		}

		public void setAbono(String abono) {
			this.abono = abono;
		}

		public void setSaldo(String saldo) {
			this.saldo = saldo;
		}

		public boolean isChecked() {
			return checked;
		}

		public void setChecked(boolean checked) {
			this.checked = checked;
		}

		public double getBase() {
			return base;
		}

		public double getVdescuento() {
			return vdescuento;
		}

		public void setVdescuento(double vdescuento) {
			this.vdescuento = vdescuento;
		}

		public double getVabono() {
			return vabono;
		}

		public void setVabono(double vabono) {
			this.vabono = vabono;
		}
 
 		public void setDescuento(String descuento) {
			this.descuento = descuento;
		}

 		public void setPdescuento(String pdescuento) {
			this.pdescuento = pdescuento;
		}
 		
 		public String getPdescuento(){
 			return pdescuento;
 		}

		public double getVsaldo() {
			return vsaldo;
		}

		public void setVsaldo(double vsaldo) {
			this.vsaldo = vsaldo;
		}

		public String getCharCta() {
			return charCta;
		}

		public String getIdCta() {
			return idCta;
		}
 		
 	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,ContextMenuInfo menuInfo) {
	  super.onCreateContextMenu(menu, v, menuInfo);
	  System.out.println("oncreateContext");
	  getMenuInflater().inflate(R.menu.contextmenucartera,menu);
	  menu.setHeaderTitle(title);
	  AdapterContextMenuInfo info = (AdapterContextMenuInfo) menuInfo;
	  selectedItemClick = info.position;
	  
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
	 
	    switch (item.getItemId()) {
	        case R.id.it_abono_parcial:
	        	FieldsCartera cartera = GlobalData.items.get(selectedItemClick);
	        	String sdocumento = cartera.getDocumento();
	        	double base = cartera.getBase();
	        	double total = cartera.getDtotal();
	        	partialpayment = new PartialPaymentDialog(sdocumento,base,total) {
					public void onClick(DialogInterface arg0, int arg1) {
						super.onClick(arg0, arg1);
						// TODO Auto-generated method stub
						if (arg1==DialogInterface.BUTTON_NEGATIVE) {
						}
						else if (arg1==DialogInterface.BUTTON_POSITIVE) {
							partialPayment(selectedItemClick,partialpayment.getAbono(),partialpayment.getVtdesc(),partialpayment.getSaldo());
						}
					}
				};
				partialpayment.show(getFragmentManager(),getString(R.string.abono_parcial));

	            return true;
	        case R.id.it_abono_a_cuenta:
	        	paymentonaccount = new PaymentOnAccount(totalBase(),total_cartera) {
	        		public void onClick(DialogInterface arg0, int arg1) {
						super.onClick(arg0, arg1);
						// TODO Auto-generated method stub
						if (arg1==DialogInterface.BUTTON_NEGATIVE) {
						}
						else if (arg1==DialogInterface.BUTTON_POSITIVE) {
							paymentOnAccount(paymentonaccount.getAbono(),paymentonaccount.getPdesc1(),paymentonaccount.getPdesc2(),paymentonaccount.getPdesc3());
						}
					}
	        	};
	        	paymentonaccount.show(getFragmentManager(),getString(R.string.abono_a_cuenta));
	            return true;
	        default:
	        	return false;
	    }
	}

	private void paymentOnAccount(double abono_total,double pdesc1,double pdesc2,double pdesc3) {
		
		System.out.println("Valor Abono "+abono_total);
		descAccount(pdesc1,pdesc2,pdesc3);
		
		double total=0;
		double abono=0;
		double desc=0;
		
		for(int i=0;i<GlobalData.items.size();i++) {
			FieldsCartera cartera = GlobalData.items.get(i);
			total = cartera.getDtotal();
			desc = cartera.getVdescuento();
			abono = total-desc;
			if (abono>abono_total) {
				abono=abono_total;
			}
			else {
				abono_total-=abono;
			}
			System.out.println("asignando a setAbono "+abono);
			cartera.setAbono(df.format(Math.round(abono)));
			cartera.setVabono(Math.round(abono));
			cartera.setSaldo(df.format(Math.round(total-desc-abono)));
			cartera.setChecked(true);
			if (abono==abono_total) {
				break;
			}
		}
		adapter.notifyDataSetChanged();
		recalculate();

	}
	
	private void descAccount(double pdesc1,double pdesc2,double pdesc3) {
		
		if (pdesc1!=0) {

			double base = 0;
			double desc1 = 0;
			double desc2 = 0;
			double desc3 = 0;
			double tdesc = 0;
		
			for(int i=0;i<GlobalData.items.size();i++) {
				FieldsCartera cartera = GlobalData.items.get(i);
				base = cartera.getBase();
				desc1 = base*pdesc1/100;
				desc2 = desc1*pdesc2/100;
				desc3 = desc2*pdesc3/100;
				tdesc = desc1+desc2+desc3;
				cartera.setAbono("0");
				cartera.setVabono(0);
				cartera.setVdescuento(Math.round(tdesc));
				cartera.setDescuento(df.format(Math.round(tdesc)));
				cartera.setChecked(true);
			}
		}
	}
	
	private void partialPayment(int position,double abono,double descuento,double saldo) {
		FieldsCartera cartera = GlobalData.items.get(position);
		cartera.setVdescuento(descuento);
		cartera.setVabono(abono);
		cartera.setVsaldo(saldo);
		cartera.setChecked(true);
		cartera.setAbono(df.format(abono));
		cartera.setDescuento(df.format(descuento));
		cartera.setSaldo(df.format(saldo));
		adapter.notifyDataSetChanged();
		recalculate();
	}
	
	private void recalculate() {
		abonos = 0;
		descuento = 0;
		nsaldo = 0;
		for (int i=0;i<GlobalData.items.size();i++) {
			FieldsCartera cartera = GlobalData.items.get(i);
			abonos+=cartera.getVabono();
			descuento+=cartera.getVdescuento();
		}
		nsaldo = total_cartera-abonos-descuento;
		tv_abonos.setText(df.format(abonos));
		tv_descuentos.setText(df.format(descuento));
		tv_nuevo_saldo.setText(df.format(nsaldo));
	}
	
	private double totalBase() {
		double base = 0;
		for (int i=0;i<GlobalData.items.size();i++) {
			FieldsCartera cartera = GlobalData.items.get(i);
			base+=cartera.getBase();
		}
		return base;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if (abonos!=0) {
			System.out.println("nuevo saldo: "+nsaldo);
	    	PaymentIn.setTotales(total_cartera, abonos, descuento, nsaldo);
	    	paymentin.show(getFragmentManager(),getString(R.string.forma_pago));
		}
		else {
			Toast.makeText(this,R.string.error_pago, Toast.LENGTH_LONG).show();
		}
	}

	@Override
	public void dialogClickEvent(com.example.emakumovil.components.DialogClickEvent e) {
		// TODO Auto-generated method stub
		System.out.println("se cancela asi:");
		System.out.println("efectivo: "+e.getEfectivoneto());
		System.out.println("tarjetas: "+e.getTarjetas());
		System.out.println("bancos: "+e.getBancos());
		System.out.println("cambio: "+e.getCambio());
		System.out.println("id_tarjeta: "+e.getId_tarjeta());
		System.out.println("id_banco: "+e.getId_banco());
		efectivo = e.getEfectivoneto();
		tarjetas = e.getTarjetas();
		bancos = e.getBancos();
		id_banco = e.getId_banco();
		if (type==CARTERA) {
			sendTransactionCartera("MVTR00006",e.getEfectivoneto(),e.getTarjetas(),e.getBancos(),e.getCambio(),e.getId_banco(),e.getId_banco_tarjeta(),e.getId_tarjeta());
		}
		else if (type==CXP) {
			sendTransactionCxP("MVTR00007",e.getEfectivoneto(),e.getTarjetas(),e.getBancos(),e.getCambio(),e.getId_banco(),e.getId_banco_tarjeta(),e.getId_tarjeta());
		}
		new SearchQuery(this,query, new String[] {
				id_char}).start();

	}
	
	private void sendTransactionCartera(String id_transaction,double efectivoneto,double tarjeta,double consignacion,double cambio,
			String id_banco,String id_banco_tarjeta,String id_tarjeta) {
		Document transaction = new Document();
		Element raiz = new Element("TRANSACTION");
		Element driver = new Element("driver");
		driver.setText(id_transaction);
		Element id = new Element("id");
		id.setText("TMV2");
		transaction.setRootElement(raiz);
		raiz.addContent(driver);
		raiz.addContent(id);
		// Concepto..
		Element obs = new Element("package");
		Element field1 = new Element("field");
		field1.setText(getPagos());
		obs.addContent(field1);
		raiz.addContent(obs);
		
		// Datos documento
		
		Element datosdoc = new Element("package");
		Element field2 = new Element("field");
		field2.setText(String.valueOf(efectivoneto+cambio)); // Efectivo
		Element field3 = new Element("field");
		field3.setText(String.valueOf(tarjeta)); // Tarjetas
		Element field4 = new Element("field");
		field4.setText("0.0"); // cheque
		Element field5 = new Element("field");
		field5.setText(String.valueOf(consignacion)); // consignacion
		Element field6 = new Element("field");
		field6.setText(String.valueOf(cambio)); // cambio
		Element field7 = new Element("field");
		field7.setText(String.valueOf(abonos)); // total
		datosdoc.addContent(field2);
		datosdoc.addContent(field3);
		datosdoc.addContent(field4);
		datosdoc.addContent(field5);
		datosdoc.addContent(field6);
		datosdoc.addContent(field7);
		raiz.addContent(datosdoc);
		
		// Efectivo
		
		Element efectivo = new Element("package");
		Element field8 = new Element("field");
		field8.setText(String.valueOf(efectivoneto));
		efectivo.addContent(field8);
		raiz.addContent(efectivo);
		
		// Descuento
		
		Element descuento = new Element("package");
		Element field9 = new Element("field");
		field9.setText(String.valueOf(this.descuento));
		descuento.addContent(field9);
		raiz.addContent(descuento);
		
		// Tarjetas
		
		Element cta_banco = new Element("package");
		if (!"".equals(id_banco_tarjeta)) {
			Element field10 = new Element("field");
			Attribute key = new Attribute("attribute","key");
			Attribute name = new Attribute("name","cuenta");
			field10.setAttribute(key);
			field10.setAttribute(name);
			field10.setText(id_banco_tarjeta);
			cta_banco.addContent(field10);
		}
		
		raiz.addContent(cta_banco);
		
		// Valor Tarjeta
		
		Element vbanco = new Element("package");
		Element field11 = new Element("field");
		field11.setText(String.valueOf(tarjeta));
		vbanco.addContent(field11);
		raiz.addContent(vbanco);
		
		// Valor retencion Tarjeta1
		
		Element rtarjeta1 = new Element("package");
		raiz.addContent(rtarjeta1);
		
		// Valor retencion Tarjeta1
		
		Element rtarjeta2 = new Element("package");
		raiz.addContent(rtarjeta2);
		
		// Valor retencion Tarjeta1
		
		Element rtarjeta3 = new Element("package");
		raiz.addContent(rtarjeta3);
								
		// siete vacios
		Element vacio1 = new Element("package"); // removeKey
		Element vacio2 = new Element("package"); // removeKey
		Element vacio3 = new Element("package"); // cheques
		
		Element consignaciones = new Element("package"); // consignacion
		if (!"".equals(id_banco)) {
			Element sp1 = new Element("subpackage");
			Element sfield1 = new Element("field");
			sfield1.setText(id_banco);
			Element sfield2 = new Element("field");
			sfield2.setText("120"); // banco por defecto, campo requerido
			Element sfield3 = new Element("field");
			sfield3.setText(fecha); // fecha
			Element sfield4 = new Element("field");
			sfield4.setText(String.valueOf(consignacion));
			sp1.addContent(sfield1);
			sp1.addContent(sfield2);
			sp1.addContent(sfield3);
			sp1.addContent(sfield4);
			consignaciones.addContent(sp1);
		}
		
		
		Element vacio5 = new Element("package"); // rentencion documento
		Element vacio6 = new Element("package"); // rentencion documento
		Element vacio7 = new Element("package"); // rentencion documento
		raiz.addContent(vacio1); // removeKey
		raiz.addContent(vacio2); // removeKey
		raiz.addContent(vacio3); // cheques
		raiz.addContent(consignaciones); // consignacion
		raiz.addContent(vacio5); // retencion documento
		raiz.addContent(vacio6); // retencion documento
		raiz.addContent(vacio7); // retencion documento
		
		// tercero
		
		Element tercero = new Element("package");
		Element field12 = new Element("field");
		Attribute key2 = new Attribute("attribute","key");
		Attribute name2 = new Attribute("name","idTercero");
		field12.setAttribute(key2);
		field12.setAttribute(name2);
		field12.setText(id_char);
		tercero.addContent(field12);
		raiz.addContent(tercero);

		// tabla
		Element tabla = getElementPagosCartera();
		raiz.addContent(tabla);
		
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
	
	private void sendTransactionCxP(String id_transaction,double efectivoneto,double tarjeta,double consignacion,double cambio,
			String id_banco,String id_banco_tarjeta,String id_tarjeta) {
		Document transaction = new Document();
		Element raiz = new Element("TRANSACTION");
		Element driver = new Element("driver");
		driver.setText(id_transaction);
		Element id = new Element("id");
		id.setText("TMV3");
		transaction.setRootElement(raiz);
		raiz.addContent(driver);
		raiz.addContent(id);
		// Concepto..
		Element obs = new Element("package");
		Element field1 = new Element("field");
		field1.setText(getPagos());
		obs.addContent(field1);
		raiz.addContent(obs);
		
		// Datos documento
		
		Element datosdoc = new Element("package");
		Element field2 = new Element("field");
		field2.setText(String.valueOf(efectivoneto+cambio)); // Efectivo
		Element field3 = new Element("field");
		field3.setText(String.valueOf(tarjeta)); // Tarjetas
		Element field4 = new Element("field");
		field4.setText("0.0"); // cheque
		Element field5 = new Element("field");
		field5.setText(String.valueOf(consignacion)); // consignacion
		Element field6 = new Element("field");
		field6.setText(String.valueOf(cambio)); // cambio
		Element field7 = new Element("field");
		field7.setText(String.valueOf(abonos)); // total
		datosdoc.addContent(field2);
		datosdoc.addContent(field3);
		datosdoc.addContent(field4);
		datosdoc.addContent(field5);
		datosdoc.addContent(field6);
		datosdoc.addContent(field7);
		raiz.addContent(datosdoc);

		// Efectivo
		
		Element efectivo = new Element("package");
		Element field8 = new Element("field");
		field8.setText(String.valueOf(efectivoneto));
		efectivo.addContent(field8);
		raiz.addContent(efectivo);
		
		// Descuento
		
		Element descuento = new Element("package");
		Element field9 = new Element("field");
		field9.setText(String.valueOf(this.descuento));
		descuento.addContent(field9);
		raiz.addContent(descuento);
		
		// Tarjetas
		
		Element vbanco = new Element("package");
		raiz.addContent(vbanco);
		
		// Banco para cheque
		
		Element idbanco = new Element("package");
		raiz.addContent(idbanco);
		
		// Info cheque
								
		Element cheque = new Element("package");
		raiz.addContent(cheque);

		// Valor cheque
		
		Element vcheque = new Element("package");
		raiz.addContent(vcheque);

		// RemoveKey
		Element vacio1 = new Element("package"); 
		
		// Transferencia
		
		Element transferencia = new Element("package"); 
		if (!"".equals(id_banco)) {
			Element sp1 = new Element("subpackage");
			Element sfield1 = new Element("field");
			sfield1.setText(fecha); // fecha
			Element sfield2 = new Element("field");
			sfield2.setText(id_banco); // cuenta
			Element sfield3 = new Element("field");
			sfield3.setText("120"); // banco por defecto
			Element sfield4 = new Element("field");
			sfield4.setText(String.valueOf(consignacion));
			sp1.addContent(sfield1);
			sp1.addContent(sfield2);
			sp1.addContent(sfield3);
			sp1.addContent(sfield4);
			transferencia.addContent(sp1);
		}

		
		Element vacio2 = new Element("package"); // retencion1
		Element vacio3 = new Element("package"); // retencion2
		Element vacio4 = new Element("package"); // retencion3
		
		raiz.addContent(vacio1); // removeKey
		raiz.addContent(transferencia); // transferencia
		raiz.addContent(vacio2); // retencion1
		raiz.addContent(vacio3); // retencion2
		raiz.addContent(vacio4); // retencion3
		
		// tercero
		
		Element tercero = new Element("package");
		Element field10 = new Element("field");
		Attribute key2 = new Attribute("attribute","key");
		Attribute name2 = new Attribute("name","idTercero");
		field10.setAttribute(key2);
		field10.setAttribute(name2);
		field10.setText(id_tercero);
		tercero.addContent(field10);
		tercero.addContent(new Element("field").setText("NULL")); // direccion pendiente por enviar
		tercero.addContent(new Element("field").setText("NULL")); // telefono pendiente por enviar
		raiz.addContent(tercero);

		// tabla
		Element tabla = getElementPagosCxP();
		raiz.addContent(tabla);
		
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
	
	private Element getElementPagosCartera() {
		Element pack = new Element("package");
		for (int i=0;i<GlobalData.items.size();i++) {
			FieldsCartera cartera = GlobalData.items.get(i);
			double abono = cartera.getVabono();
			double descuento = cartera.getVdescuento();
			if (abono>0 || descuento>0) {
				System.out.println("abonando..");
				Element spack = new Element("subpackage");
				Element ndocumento = new Element("field");
				Element total = new Element("field");
				Element vabono = new Element("field");
				Element pdesc = new Element("field");
				Element vdesc = new Element("field");
				Element char_cta = new Element("field");
				Element id_cta = new Element("field");
				
				ndocumento.setText(cartera.getNdocumento());
				total.setText(String.valueOf(cartera.getVabono()));
				vabono.setText(String.valueOf(cartera.getVabono()));
				pdesc.setText(cartera.getPdescuento()); 
				vdesc.setText(String.valueOf(cartera.getVdescuento()));
				char_cta.setText(cartera.getCharCta());
				id_cta.setText(cartera.getIdCta());
				
				spack.addContent(ndocumento);
				spack.addContent(total);
				spack.addContent(vabono);
				spack.addContent(pdesc);
				spack.addContent(vdesc);
				spack.addContent(char_cta);
				spack.addContent(id_cta);
				pack.addContent(spack);
			}
		}
		return pack;
	}
	
	private Element getElementPagosCxP() {
		Element pack = new Element("package");
		for (int i=0;i<GlobalData.items.size();i++) {
			FieldsCartera cartera = GlobalData.items.get(i);
			double abono = cartera.getVabono();
			double descuento = cartera.getVdescuento();
			if (abono>0 || descuento>0) {
				Element spack = new Element("subpackage");
				Element ndocumento = new Element("field");
				Element total = new Element("field");
				Element vabono = new Element("field");
				Element pdesc = new Element("field");
				Element vdesc1 = new Element("field");
				Element vdesc2 = new Element("field");
				Element vdesc3 = new Element("field");
				Element char_cta = new Element("field");
				Element id_cta = new Element("field");
				
				ndocumento.setText(cartera.getNdocumento());
				total.setText(String.valueOf(cartera.getVabono()));
				vabono.setText(String.valueOf(cartera.getVabono()));
				pdesc.setText(cartera.getPdescuento()); 
				vdesc1.setText(String.valueOf(cartera.getVdescuento()));
				vdesc2.setText("0.0");
				vdesc3.setText("0.0");
				char_cta.setText(cartera.getCharCta());
				id_cta.setText(cartera.getIdCta());
				
				spack.addContent(ndocumento);
				spack.addContent(total);
				spack.addContent(vabono);
				spack.addContent(pdesc);
				spack.addContent(vdesc1);
				spack.addContent(vdesc2);
				spack.addContent(vdesc3);
				spack.addContent(char_cta);
				spack.addContent(id_cta);
				pack.addContent(spack);
			}
		}
		return pack;
	}

	private String getPagos() {
		String pagos = "Abono(s) de la(s) siguiente(s) factura(s)\n";
		
		for (int i=0;i<GlobalData.items.size();i++) {
			FieldsCartera cartera = GlobalData.items.get(i);
			double abono = cartera.getVabono();
			double descuento = cartera.getVdescuento();
			if (abono>0 || descuento>0) {
				pagos+=cartera.getDocumento()+" Total: "+cartera.getTotal()+" abono: "+cartera.getAbono()+" desc: "+cartera.getDescuento()+" saldo "+cartera.getSaldo()+"\n";
			}
		}
		return pagos;
	}

	private void sendPrintJobCartera(String ndocumento) {
		Document transaction = new Document();
		Element raiz = new Element("SERVERPRINTER");
		
		Element printerTemplate = new Element("printerTemplate");
		printerTemplate.setText("/graphics/TSComprobanteIngreso.xml");
		
		Element jarFile = new Element("jarFile");
		jarFile.setText(ConfigFileHandler.getJarFile());

		Element jarDirectory = new Element("jarDirectory");
		jarDirectory.setText(ConfigFileHandler.getJarDirectory());
		transaction.setRootElement(raiz);
		raiz.addContent(printerTemplate);
		raiz.addContent(jarFile);
		raiz.addContent(jarDirectory);

		/*
		 * Numero Barra
		 */
		
		Element numero1 = new Element("package");
		numero1.addContent(new Element("field").setText(ndocumento)); 
		raiz.addContent(numero1);
		
		/*
		 * Numero Impreso
		 */
		
		Element numero2 = new Element("package");
		numero2.addContent(new Element("field").setText(ndocumento)); 
		raiz.addContent(numero2);
		
		/*
		 * Fecha
		 */
		
		Element efecha = new Element("package");
		efecha.addContent(new Element("field").setText(fecha)); 
		raiz.addContent(efecha);

		/*
		 * Tercero
		 */
		
		Element etercero = new Element("package");
		etercero.addContent(new Element("field").setText(tv_descripcion2.getText().toString())); 
		etercero.addContent(new Element("field"));
		etercero.addContent(new Element("field"));
		etercero.addContent(new Element("field"));
		etercero.addContent(new Element("field"));
		etercero.addContent(new Element("field"));
		raiz.addContent(etercero);
		
		/*
		 * Valor Numero
		 */
		Element valor1 = new Element("package");
		valor1.addContent(new Element("field").setText(String.valueOf(abonos))); 
		raiz.addContent(valor1);

		/*
		 * Valor en Letras
		 */
		
		Element valor2 = new Element("package");
		valor2.addContent(new Element("field").setText(String.valueOf(abonos))); 
		raiz.addContent(valor2);
		
		/*
		 * Concepto
		 */
		
		Element concepto = new Element("package");
		concepto.addContent(new Element("field").setText(getPagos())); 
		raiz.addContent(concepto);
		
		/*
		 * Registro contable
		 */
		
		Element contabilidad = new Element("package");
		raiz.addContent(contabilidad);
		
		/*
		 * Forma de pago
		 */
		
		Element pago = new Element("package");
		pago.addContent(new Element("field").setText(efectivo>0?"X":"")); 
		pago.addContent(new Element("field").setText("")); 
		pago.addContent(new Element("field").setText(bancos>0?"X":"")); 
		pago.addContent(new Element("field").setText(tarjetas>0?"X":"")); 
		raiz.addContent(pago);

        SocketChannel socket = SocketConnector.getSock();
        SocketWriter.writing(socket, transaction);

	}
	
	private void sendPrintJobCxP(String ndocumento) {
		Document transaction = new Document();
		Element raiz = new Element("SERVERPRINTER");
		
		Element printerTemplate = new Element("printerTemplate");
		printerTemplate.setText("/graphics/TSComprobanteEgreso.xml");
		
		Element jarFile = new Element("jarFile");
		jarFile.setText(ConfigFileHandler.getJarFile());

		Element jarDirectory = new Element("jarDirectory");
		jarDirectory.setText(ConfigFileHandler.getJarDirectory());
		transaction.setRootElement(raiz);
		raiz.addContent(printerTemplate);
		raiz.addContent(jarFile);
		raiz.addContent(jarDirectory);

		/*
		 * Numero Barra
		 */
		
		Element numero1 = new Element("package");
		numero1.addContent(new Element("field").setText(ndocumento)); 
		raiz.addContent(numero1);
		
		/*
		 * Fecha
		 */
		
		Element efecha = new Element("package");
		efecha.addContent(new Element("field").setText(fecha)); 
		efecha.addContent(new Element("field")); 
		efecha.addContent(new Element("field")); 
		efecha.addContent(new Element("field")); 
		efecha.addContent(new Element("field")); 
		efecha.addContent(new Element("field")); 
		raiz.addContent(efecha);

		/*
		 * Tercero
		 */
		
		Element etercero = new Element("package");
		etercero.addContent(new Element("field"));
		etercero.addContent(new Element("field").setText(tv_descripcion1.getText().toString())); 
		etercero.addContent(new Element("field").setText(tv_descripcion2.getText().toString()));
		etercero.addContent(new Element("field"));
		etercero.addContent(new Element("field"));
		etercero.addContent(new Element("field"));
		raiz.addContent(etercero);

		/*
		 * Cuenta Bancaria
		 */
		
		Element cta_banco = new Element("package");
		cta_banco.addContent(new Element("field").setText(String.valueOf(id_banco))); 
		raiz.addContent(cta_banco);

		/*
		 * Numero Impreso
		 */
		
		Element numero2 = new Element("package");
		numero2.addContent(new Element("field").setText(ndocumento)); 
		raiz.addContent(numero2);
		

		
		/*
		 * Valor Numero
		 */
		Element valor1 = new Element("package");
		valor1.addContent(new Element("field").setText(String.valueOf(abonos))); 
		raiz.addContent(valor1);

		/*
		 * Valor en Letras
		 */
		
		Element valor2 = new Element("package");
		valor2.addContent(new Element("field").setText(String.valueOf(abonos))); 
		raiz.addContent(valor2);
		
		/*
		 * Concepto
		 */
		
		Element concepto = new Element("package");
		concepto.addContent(new Element("field").setText(getPagos())); 
		raiz.addContent(concepto);
		

		/*
		 * Numero Tres
		 */
		
		Element numero3 = new Element("package");
		numero3.addContent(new Element("field").setText(ndocumento)); 
		raiz.addContent(numero3);
		

		/*
		 * Registro contable
		 */
		
		Element contabilidad = new Element("package");
		raiz.addContent(contabilidad);
		
		/*
		 * Forma de pago
		 */
		
		Element pago = new Element("package");
		pago.addContent(new Element("field").setText(efectivo>0?"X":"")); 
		pago.addContent(new Element("field").setText("")); 
		pago.addContent(new Element("field").setText(bancos>0?"X":"")); 
		pago.addContent(new Element("field").setText(tarjetas>0?"X":"")); 
		raiz.addContent(pago);

        SocketChannel socket = SocketConnector.getSock();
        SocketWriter.writing(socket, transaction);

	}
	
	@Override
	public void cathSuccesEvent(SuccessEvent e) {
		// TODO Auto-generated method stub
		if (type==CARTERA) {
			sendPrintJobCartera(e.getNdocument());
		}
		else if (type==CXP) {
			sendPrintJobCxP(e.getNdocument());
		}
	}

}
