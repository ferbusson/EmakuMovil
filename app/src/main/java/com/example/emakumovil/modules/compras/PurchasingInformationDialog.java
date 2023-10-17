package com.example.emakumovil.modules.compras;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import org.jdom2.Document;
import org.jdom2.Element;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TextView;

import com.example.emakumovil.R;
import com.example.emakumovil.components.AnswerEvent;
import com.example.emakumovil.components.AnswerListener;
import com.example.emakumovil.components.DialogClickEvent;
import com.example.emakumovil.components.DialogClickListener;
import com.example.emakumovil.components.GlobalData;
import com.example.emakumovil.components.SearchQuery;

@SuppressLint("ValidFragment")
public class PurchasingInformationDialog   extends DialogFragment implements OnClickListener, View.OnClickListener, AnswerListener  {

	private String id;
	private double base;
	private double total;
	private double total_retenciones;
	
	private TextView tv_vtotal;
	private TextView tv_vtotal_retenciones;
	private TextView tv_vrete_fuente;
	private TextView tv_vrete_iva;
	private TextView tv_vrete_ica;
	private TextView tv_vvalor_neto_compra;
	private TextView tv_viva_regimen_simplificado;
	private TextView tv_vtotal_factura_compra;
	
	private TabHost tabs;
	private LayoutInflater inflater;
	
	private LinearLayout ly_totales;
	private LinearLayout ly_retenciones;
	
	private DecimalFormat df = new DecimalFormat("##,###,##0.00");
	private DataAdapter adapter;
	private ListView lv_retenciones;

	private double rfuente = 0;
	private double riva = 0;
	private double rica = 0;
	private double rsimplificado = 0;
	private double total_compra = 0;
	
	private Button positiveButton;
	private long b;
	
	public PurchasingInformationDialog() {}
	
	public PurchasingInformationDialog(long b,String id,double base,double total) {
		this.b=b;
		this.id=id;
		this.base=base;
		this.total=total;
	}

	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
	    inflater = getActivity().getLayoutInflater();
	    View v = inflater.inflate(R.layout.purchasing_information_dialog, null);
	    builder.setView(v)
	           .setNegativeButton(R.string.cancelar, this).setPositiveButton(R.string.aceptar,this);      
	    
	    ly_totales = (LinearLayout)v.findViewById(R.id.ly_totales);
	    ly_retenciones = (LinearLayout)v.findViewById(R.id.ly_retenciones);
	    lv_retenciones = (ListView)v.findViewById(R.id.lv_rentenciones);
	    
	    tv_vtotal = (TextView)v.findViewById(R.id.tv_vtotal);
	    tv_vtotal.setText(df.format(total));
	    
	    tv_vtotal_retenciones = (TextView)v.findViewById(R.id.tv_vtotal_retenciones);
	    tv_vrete_fuente = (TextView)v.findViewById(R.id.tv_vrete_fuente);
	    tv_vrete_iva = (TextView)v.findViewById(R.id.tv_vrete_iva);
	    tv_vrete_ica = (TextView)v.findViewById(R.id.tv_vrete_ica);
	    tv_vvalor_neto_compra = (TextView)v.findViewById(R.id.tv_vvalor_neto_compra);
	    tv_viva_regimen_simplificado = (TextView)v.findViewById(R.id.tv_viva_regimen_simplificado);
	    tv_vtotal_factura_compra = (TextView)v.findViewById(R.id.tv_vtotal_factura_compra);
	    
		tabs=(TabHost)v.findViewById(android.R.id.tabhost);
		tabs.setup();
		Resources res = getResources();
		

    		// Pestaña 1 
		
		TabHost.TabSpec spec1=tabs.newTabSpec(getString(R.string.totales));
		spec1.setContent(R.id.ly_totales);
		spec1.setIndicator(getString(R.string.totales),
		    res.getDrawable(android.R.drawable.ic_dialog_alert));
		tabs.addTab(spec1);

		// Pestaña 2 
		
		TabHost.TabSpec spec2=tabs.newTabSpec(getString(R.string.retenciones));
		spec2.setContent(R.id.hs_retenciones);
		spec2.setIndicator(getString(R.string.retenciones),
		    res.getDrawable(android.R.drawable.ic_dialog_alert));
		tabs.addTab(spec2);

		tabs.getTabWidget().getChildAt(1).setOnClickListener(this);

		if (savedInstanceState!=null) {
			id=savedInstanceState.getString("id_tercero");
			base=savedInstanceState.getDouble("base");
			total=savedInstanceState.getDouble("total");
			total_retenciones=savedInstanceState.getDouble("total_retenciones");

			tv_vtotal.setText(savedInstanceState.getString("vtotal"));
			tv_vtotal_retenciones.setText(savedInstanceState.getString("vtotal_retenciones"));
			tv_vrete_fuente.setText(savedInstanceState.getString("vrete_fuente"));
			tv_vrete_iva.setText(savedInstanceState.getString("vrete_iva"));
			tv_vrete_ica.setText(savedInstanceState.getString("vrete_ica"));
			tv_vvalor_neto_compra.setText(savedInstanceState.getString("vvalor_neto_compra"));
			tv_viva_regimen_simplificado.setText(savedInstanceState.getString("viva_regimen_simplificado"));
			tv_vtotal_factura_compra.setText(savedInstanceState.getString("vtotal_factura_compra"));
			
			rfuente=savedInstanceState.getDouble("rfuente");
			riva=savedInstanceState.getDouble("riva");
			rica=savedInstanceState.getDouble("rica");
			rsimplificado=savedInstanceState.getDouble("rsimplificado");
			total_compra=savedInstanceState.getDouble("total_compra");
			b=savedInstanceState.getLong("b");
			
		}
		else {
			new SearchQuery(PurchasingInformationDialog.this,"MVSEL0047",new String[]{id,String.valueOf(base)}).start();
		}

		adapter = new DataAdapter(
				getActivity(), R.layout.list_purchasing_deductions,
				GlobalData.data_retenciones);
		lv_retenciones.setAdapter(adapter);

		Dialog d = builder.create();
	    return d;
	}

	public void onSaveInstanceState(Bundle savedInstanceState) {
		super.onSaveInstanceState(savedInstanceState);
		
		savedInstanceState.putString("id_tercero",id);
		savedInstanceState.putDouble("base",base);
		savedInstanceState.putDouble("total",total);
		savedInstanceState.putDouble("total_retenciones",total_retenciones);
		
		savedInstanceState.putString("vtotal",tv_vtotal.getText().toString());
		savedInstanceState.putString("vtotal_retenciones",tv_vtotal_retenciones.getText().toString());
		savedInstanceState.putString("vrete_fuente",tv_vrete_fuente.getText().toString());
		savedInstanceState.putString("vrete_iva",tv_vrete_iva.getText().toString());
		savedInstanceState.putString("vrete_ica",tv_vrete_ica.getText().toString());
		savedInstanceState.putString("vvalor_neto_compra",tv_vvalor_neto_compra.getText().toString());
		savedInstanceState.putString("viva_regimen_simplificado",tv_viva_regimen_simplificado.getText().toString());
		savedInstanceState.putString("vtotal_factura_compra",tv_vtotal_factura_compra.getText().toString());

		savedInstanceState.putDouble("rfuente",rfuente);
		savedInstanceState.putDouble("riva",riva);
		savedInstanceState.putDouble("rica",rica);
		savedInstanceState.putDouble("rsimplificado",rsimplificado);
		savedInstanceState.putDouble("total_compra",total_compra);

		savedInstanceState.putLong("b",b);
	}
	

	  @Override
	public void onClick(DialogInterface dialog, int which) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
	    int height = ly_totales.getHeight();
	    int width = ly_totales.getWidth();
	    FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(width,height);
	    ly_retenciones.setLayoutParams(lp);
	    tabs.setCurrentTab(1);
	}

	@Override
	public void arriveAnswerEvent(AnswerEvent e) {
		// TODO Auto-generated method stub
		final Document doc = e.getDocument();
		if ("MVSEL0047".equals(e.getSqlCode())) {
			System.out.println("llego una query MVSEL0047");
			final Element rootNode = doc.getRootElement();
				List<Element> listRows = rootNode.getChildren("row");
				GlobalData.data_retenciones.clear();
				rfuente = 0;
				riva = 0;
				rica = 0;
				rsimplificado = 0;
				
				for (int i = 0; i < listRows.size(); i++) {
					System.out.println("cargando.........");
					Element Erow = (Element) listRows.get(i);
					List<Element> Lcol = Erow.getChildren();
					String cuenta = ((Element) Lcol.get(0)).getText();
					String nombre = ((Element) Lcol.get(1)).getText();
					double base = Double.parseDouble(((Element) Lcol.get(2)).getText());
					double porcentaje = Double.parseDouble(((Element) Lcol.get(3)).getText());
					double valor = Double.parseDouble(((Element) Lcol.get(4)).getText());
					String id_cta = ((Element) Lcol.get(5)).getText();
					GlobalData.data_retenciones.add(new Retenciones(cuenta,nombre,base,porcentaje,valor,id_cta));
					// Sumatoria retenciones
					if (cuenta.substring(0,4).equals("2365")) {
						rfuente+=valor;
					}
					else if (cuenta.substring(0,4).equals("2367")) {
						riva+=valor;
					}
					else if (cuenta.substring(0,4).equals("2368")) {
						rica+=valor;
					}
					else if (cuenta.substring(0,4).equals("2408")) {
						rsimplificado+=valor;
					}
				}
				getActivity().runOnUiThread(new Runnable() {
        		    public void run() {
						tv_vrete_fuente.setText(df.format(rfuente));
						tv_vrete_iva.setText(df.format(riva));
						tv_vrete_ica.setText(df.format(rica));
						tv_viva_regimen_simplificado.setText(df.format(rsimplificado));
						total_retenciones = rfuente+riva+rica;
						tv_vtotal_retenciones.setText(df.format(total_retenciones));
						double total_menos_retenciones = total-total_retenciones;
						tv_vvalor_neto_compra.setText(df.format(total_menos_retenciones));
						total_compra =total_menos_retenciones+rsimplificado;
						tv_vtotal_factura_compra.setText(df.format(total_compra));
        		    }});

		}
	}

	@Override
	public boolean containSqlCode(String sqlCode) {
		// TODO Auto-generated method stub
		return false;
	}

	private class DataAdapter extends ArrayAdapter<Retenciones> {

		private ArrayList<Retenciones> items;
		
		public DataAdapter(Context context, int textViewResourceId,
				ArrayList<Retenciones> items) {
			super(context, textViewResourceId, items);
			Log.d("EMAKU ","EMAKU instanciando adaptador");
			this.items=items;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View v = convertView;
			ViewHolder holder;
			Retenciones myData = items.get(position);
			if (v == null) {
				v = inflater.inflate(R.layout.list_purchasing_deductions, null);

				TextView tv_cuenta = (TextView) v
						.findViewById(R.id.tv_cuenta);
				
				TextView tv_nombre = (TextView) v
						.findViewById(R.id.tv_nombre);
				
				TextView tv_base = (TextView) v
						.findViewById(R.id.tv_base);
				
				TextView tv_porcentaje = (TextView) v
						.findViewById(R.id.tv_porcentaje);
				
				TextView tv_valor = (TextView) v
						.findViewById(R.id.tv_valor);
				
				holder = new ViewHolder(tv_cuenta,tv_nombre,tv_base,tv_porcentaje,tv_valor);
				v.setTag(holder);
			}
			else {
				holder = (ViewHolder)v.getTag();
			}


			
			if (myData != null) {
				if (holder.tv_nombre != null) {
					holder.tv_cuenta.setText(myData.getCuenta());
					holder.tv_nombre.setText(myData.getNombre());
					holder.tv_base.setText(df.format(myData.getBase()));
					holder.tv_porcentaje.setText(df.format(myData.getPorcentaje()));
					holder.tv_valor.setText(df.format(myData.getValor()));
				}
			}
			return v;
		}

	}

 	private class ViewHolder {
 		public TextView tv_cuenta;
 		public TextView tv_nombre;
 		public TextView tv_base;
 		public TextView tv_porcentaje;
 		public TextView tv_valor;
 		
 		public ViewHolder(TextView tv_cuenta,TextView tv_nombre,TextView tv_base,TextView tv_porcentaje,TextView tv_valor) {
 			this.tv_cuenta=tv_cuenta;
 			this.tv_nombre=tv_nombre;
 			this.tv_base=tv_base;
 			this.tv_porcentaje=tv_porcentaje;
 			this.tv_valor=tv_valor;
 		}
 		
 	}

	private Element getRetenciones() {
		Element pack = new Element("package");
		for (int i=0;i<GlobalData.data_retenciones.size();i++) {
			Retenciones ret = GlobalData.data_retenciones.get(i);
			Element spack = new Element("subpackage");
			Element cta = new Element("field");
			Element valor = new Element("field");
			Element id_cta = new Element("field");
			Element base = new Element("field");
			Element porcentaje = new Element("field");
			
			cta.setText(ret.getCuenta());
			valor.setText(String.valueOf(ret.getValor()));
			id_cta.setText(String.valueOf(ret.getId_cta()));
			base.setText(String.valueOf(ret.getBase())); 
			porcentaje.setText(String.valueOf(ret.getPorcentaje())); 
			
			spack.addContent(cta);
			spack.addContent(valor);
			spack.addContent(id_cta);
			spack.addContent(base);
			spack.addContent(porcentaje);
			pack.addContent(spack);
		}
		return pack;
	}

 	public class Retenciones {
		private String cuenta;
		private String nombre;
		private double base;
		private double porcentaje;
		private double valor;
		private String id_cta;
		
		public Retenciones(String cuenta, String nombre,double base, double porcentaje, double valor,String id_cta) {
			this.cuenta=cuenta;
			this.nombre=nombre;
			this.base=base;
			this.porcentaje=porcentaje;
			this.valor=valor;
			this.id_cta=id_cta;
		}

		public String getCuenta() {
			return cuenta;
		}

		public String getNombre() {
			return nombre;
		}

		public double getBase() {
			return base;
		}

		public double getPorcentaje() {
			return porcentaje;
		}

		public double getValor() {
			return valor;
		}
		public String getId_cta() {
			return id_cta;
		}
	}

	@Override
	public void onStart() {
	    super.onStart();    
	    AlertDialog d = (AlertDialog)getDialog();
	    if(d != null) {
		    positiveButton = (Button) d.getButton(Dialog.BUTTON_POSITIVE);
	        positiveButton.setOnClickListener(new View.OnClickListener() {
	            @Override
	            public void onClick(View v) {
	                    dismiss();
	                    System.out.println("total_retenciones:"+total_retenciones);
	                    DialogClickEvent event = new DialogClickEvent(this,b,getRetenciones(),total_compra,total_retenciones);
	                    notificando(event);
	            }
	        });
	    }
	}

	private void notificando(DialogClickEvent event) {
		GlobalData.dialogClickListener.dialogClickEvent(event);
	}

	public void addDialogClickListener(DialogClickListener l) {
		System.out.println("se adiciono un oyente");
		GlobalData.dialogClickListener = l;
	}

	public double getRetencion() {
		return total_retenciones;
	}
	
	public double getValor() {
		return total_compra;
	}

}
