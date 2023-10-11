package com.example.emakumovil.modules.cartera;

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
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.emakumovil.R;
import com.example.emakumovil.components.AnswerEvent;
import com.example.emakumovil.components.AnswerListener;
import com.example.emakumovil.components.DialogClickEvent;
import com.example.emakumovil.components.DialogClickListener;
import com.example.emakumovil.components.GlobalData;
import com.example.emakumovil.components.SearchQuery;
import com.example.emakumovil.components.recordsData;

@SuppressLint("ValidFragment")
public class PaymentIn extends DialogFragment 
implements android.content.DialogInterface.OnClickListener,OnClickListener, OnItemClickListener, AnswerListener,
OnFocusChangeListener {

	private LayoutInflater inflater;
	private ImageButton im_efectivo;
	private ImageButton im_tarjetas;
	private ImageButton im_consignacion;
	private LinearLayout ly_totales;
	private LinearLayout ly_tarjetas;
	private LinearLayout ly_bancos;
	private LinearLayout ly_etarjetas;
	
	private ListView lv_tarjetas;
	private ListView lv_bancos;
	private TextView tv_total_cartera;
	private TextView tv_descuentos;
	private TextView tv_abono_cartera;
	private TextView tv_nsaldo;
	private TextView tv_cambio;
	private TextView tv_tarjetas;
	private TextView tv_bancos;
	private TextView tv_consignacion;
	
	private EditText et_efectivo;
	private EditText et_tarjetas;
	private EditText et_consignacion;
	
	private ArrayList<recordsData> itemstc = new ArrayList<recordsData>();
	private ArrayList<recordsData> itemsbc = new ArrayList<recordsData>();

	private DecimalFormat df = new DecimalFormat("##,###,###");

	private static double total_cartera;
	private static double descuento;
	private static double abono;
	private static double nsaldo;
	
	private double efectivo;
	private double tarjetas;
	private double consignacion;
	private double cambio;
	
	private String id_banco;
	private String id_banco_tarjeta;
	private String id_tarjeta;
	
    private Button positiveButton;
    
    private static final int CXP = 1;
    private int type;
	
	public PaymentIn(int type) {
		this.type=type;
	}
	
	public static void setTotales(double total_cartera,double abono,double descuento,double nsaldo) {
		PaymentIn.total_cartera=total_cartera;
		PaymentIn.descuento=descuento;
		PaymentIn.abono=abono;
		PaymentIn.nsaldo=nsaldo;
	}
	
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
	    inflater = getActivity().getLayoutInflater();
	    View v = inflater.inflate(R.layout.recaudo, null);
	    builder.setView(v)
	           .setNegativeButton(R.string.cancelar, this).setPositiveButton(R.string.aceptar,this);
	    cleanValues();
	    ly_totales = (LinearLayout)v.findViewById(R.id.ly_totales);
	    ly_tarjetas = (LinearLayout)v.findViewById(R.id.ly_tarjetas);
	    ly_etarjetas = (LinearLayout)v.findViewById(R.id.ly_etarjetas);
	    
	    
		ly_bancos = (LinearLayout)v.findViewById(R.id.ly_bancos);
	    im_efectivo = (ImageButton)v.findViewById(R.id.im_efectivo);
	    im_tarjetas = (ImageButton)v.findViewById(R.id.im_tarjetas);
	    im_consignacion = (ImageButton)v.findViewById(R.id.im_consignacion);
	    im_efectivo.setOnClickListener(this);
	    im_tarjetas.setOnClickListener(this);
	    im_consignacion.setOnClickListener(this);
	    
	    tv_total_cartera = (TextView)v.findViewById(R.id.tv_saldo_anterior);
	    tv_total_cartera.setText(df.format(total_cartera));
	    tv_descuentos = (TextView)v.findViewById(R.id.tv_descuentos);
	    tv_descuentos.setText(df.format(descuento));
	    tv_abono_cartera = (TextView)v.findViewById(R.id.tv_abono_cartera);
	    tv_abono_cartera.setText(df.format(abono));
	    tv_nsaldo = (TextView)v.findViewById(R.id.tv_nuevo_saldo);
	    tv_nsaldo.setText(df.format(nsaldo));
	    tv_cambio = (TextView)v.findViewById(R.id.tv_cambio);
	    tv_tarjetas = (TextView)v.findViewById(R.id.tv_tarjetas);
	    tv_bancos = (TextView)v.findViewById(R.id.tv_bancos);
	    tv_consignacion = (TextView)v.findViewById(R.id.tv_consignacion);
	    
	    if (type==CXP) {
	    	ly_etarjetas.setVisibility(View.GONE);
	    	tv_tarjetas.setVisibility(View.GONE);
	    	tv_consignacion.setText(R.string.bancos);
	    }

	    et_efectivo = (EditText)v.findViewById(R.id.et_efectivo);
	    et_efectivo.setOnFocusChangeListener(this);
	    et_tarjetas = (EditText)v.findViewById(R.id.et_tarjetas);
	    et_tarjetas.setOnFocusChangeListener(this);
	    et_consignacion = (EditText)v.findViewById(R.id.et_consignacion);
		et_consignacion.setOnFocusChangeListener(this);
		
		lv_tarjetas = (ListView) v.findViewById(R.id.lv_tarjetas);
		lv_tarjetas.setAdapter(new RecordsDataAdapter(
				getActivity(), R.layout.recordsearch,
				itemstc));
		lv_tarjetas.setOnItemClickListener(this);

		lv_bancos = (ListView) v.findViewById(R.id.lv_bancos);
		lv_bancos.setAdapter(new RecordsDataAdapter(
				getActivity(), R.layout.recordsearch,
				itemsbc));
		lv_bancos.setOnItemClickListener(this);

		new SearchQuery(PaymentIn.this,"MVSEL0021").start();
		new SearchQuery(PaymentIn.this,"MVSEL0022").start();
	    
		if (savedInstanceState!=null) {
			System.out.println("recuperando totales almacenados");
			System.out.println("efectivo: "+efectivo+" tarjetas "+tarjetas+" consignacion "+consignacion);
			total_cartera = savedInstanceState.getDouble("total_cartera");
			abono = savedInstanceState.getDouble("abono");
			descuento = savedInstanceState.getDouble("descuento");
			nsaldo = savedInstanceState.getDouble("nsaldo");
			efectivo = savedInstanceState.getDouble("efectivo");
			tarjetas = savedInstanceState.getDouble("tarjetas");
			consignacion = savedInstanceState.getDouble("consignacion");
			cambio = savedInstanceState.getDouble("cambio");
			id_banco = savedInstanceState.getString("id_banco");
			id_tarjeta = savedInstanceState.getString("id_tarjeta");
			et_efectivo.setText(df.format(efectivo));
			et_consignacion.setText(df.format(consignacion));
			et_tarjetas.setText(df.format(tarjetas));
			tv_bancos.setText(savedInstanceState.getString("bancos"));
			tv_tarjetas.setText(savedInstanceState.getString("tarjetas"));
		}
	    Dialog d = builder.create();
	    return d;

	}

	public void onSaveInstanceState(Bundle savedInstanceState) {
		super.onSaveInstanceState(savedInstanceState);
		savedInstanceState.putDouble("total_cartera",total_cartera);
		savedInstanceState.putDouble("abono",abono);
		savedInstanceState.putDouble("descuento",descuento);
		savedInstanceState.putDouble("nsaldo",nsaldo);
		savedInstanceState.putDouble("efectivo",efectivo);
		savedInstanceState.putDouble("tarjetas",tarjetas);
		savedInstanceState.putDouble("consignacion",consignacion);
		savedInstanceState.putDouble("cambio",cambio);
		savedInstanceState.putString("id_banco",id_banco);
		savedInstanceState.putString("id_tarjeta",id_tarjeta);
		savedInstanceState.putString("bancos",tv_bancos.getText().toString());
		savedInstanceState.putString("tarjetas",tv_tarjetas.getText().toString());
		
	}

	private void cleanValues() {
		efectivo=0;
		cambio=0;
		tarjetas=0;
		consignacion=0;
		id_tarjeta="";
		id_banco="";
		id_banco_tarjeta="";
	}


	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		Animation expandir=AnimationUtils.loadAnimation(PaymentIn.this.getActivity(), R.animator.expandir);
		Animation ocultar=AnimationUtils.loadAnimation(PaymentIn.this.getActivity(), R.animator.ocultar);
	    int height = ly_totales.getHeight();
	    int width = ly_totales.getWidth();
		if (R.id.im_efectivo==v.getId()) {
			efectivo=abono;
			et_efectivo.setText(df.format(efectivo));

		}
		else if (R.id.im_tarjetas==v.getId()) {
			    ly_tarjetas.setLayoutParams(new LayoutParams(width,height));
				ly_totales.startAnimation(ocultar);		
				ly_totales.setVisibility(View.GONE);
				ly_tarjetas.startAnimation(expandir);		
				ly_tarjetas.setVisibility(View.VISIBLE);			
				im_consignacion.setEnabled(false);
				im_tarjetas.setEnabled(false);
		}
		else if (R.id.im_consignacion==v.getId()) {
		    ly_bancos.setLayoutParams(new LayoutParams(width,height));
			ly_totales.startAnimation(ocultar);		
			ly_totales.setVisibility(View.GONE);
			ly_bancos.startAnimation(expandir);		
			ly_bancos.setVisibility(View.VISIBLE);			
			im_consignacion.setEnabled(false);
			im_tarjetas.setEnabled(false);
		}
		cambio=efectivo+tarjetas+consignacion-abono;
		tv_cambio.setText(df.format(cambio));
	}

	@Override
	public void arriveAnswerEvent(AnswerEvent e) {
		// TODO Auto-generated method stub
		final AnswerEvent ev = e;
		Document doc = e.getDocument();
		System.out.println("llego una query "+e.getSqlCode());
		final Element rootNode = doc.getRootElement();
		getActivity().runOnUiThread(new Runnable() {
			public void run() {
				List<Element> listRows = rootNode.getChildren("row");
				if (ev.getSqlCode().equals("MVSEL0021")) {
					itemstc.clear();
					for (int i = 0; i < listRows.size(); i++) {
						Element Erow = (Element) listRows.get(i);
						List<Element> Lcol = Erow.getChildren();
						String idtarjeta =  ((Element) Lcol.get(0)).getText();
						String idbancotarjeta =  ((Element) Lcol.get(1)).getText();
						String descripcion = ((Element) Lcol.get(2)).getText();
						itemstc.add(new recordsData(idtarjeta,idbancotarjeta,descripcion,null,null,null,null));
					}
					lv_tarjetas.setAdapter(new RecordsDataAdapter(
							getActivity(), R.layout.singlerecorddata,
							itemstc));
				}
				else {
					itemsbc.clear();
					for (int i = 0; i < listRows.size(); i++) {
						Element Erow = (Element) listRows.get(i);
						List<Element> Lcol = Erow.getChildren();
						String idRecord =  ((Element) Lcol.get(0)).getText();
						String descripcion = ((Element) Lcol.get(1)).getText();
						itemsbc.add(new recordsData(idRecord,null,descripcion,null,null,null,null));
					}
					lv_bancos.setAdapter(new RecordsDataAdapter(
							getActivity(), R.layout.singlerecorddata,
							itemsbc));
				}
			}
		});

	}

	@Override
	public boolean containSqlCode(String sqlCode) {
		// TODO Auto-generated method stub
		return false;
	}
	
	private class RecordsDataAdapter extends ArrayAdapter<recordsData> implements Filterable {

		private ArrayList<recordsData> items = new ArrayList<recordsData>();

		public RecordsDataAdapter(Context context, int textViewResourceId,
				ArrayList<recordsData> items) {
			super(context, textViewResourceId, items);
			this.items = items;
			Log.d("EMAKU ","EMAKU instanciando adaptador");
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View v = convertView;

			recordsData recData = items.get(position);
			if (recData != null) {
				v = inflater.inflate(R.layout.singlerecorddata, null);
				ImageView iv_icono = (ImageView)v.findViewById(R.id.iv_icono);
				TextView tv_descripcion1 = (TextView) v
						.findViewById(R.id.tv_descripcion1);

				if (tv_descripcion1 != null) {
					iv_icono.setImageResource(R.drawable.ic_action_creditcardh);
					tv_descripcion1.setText(recData.getDescripcion());
				}
			}
			return v;
		}
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub
		Animation expandir=AnimationUtils.loadAnimation(PaymentIn.this.getActivity(), R.animator.expandir);
		Animation ocultar=AnimationUtils.loadAnimation(PaymentIn.this.getActivity(), R.animator.ocultar);
		ly_totales.startAnimation(expandir);		
		ly_totales.setVisibility(View.VISIBLE);
		
		im_consignacion.setEnabled(true);
		im_tarjetas.setEnabled(true);
		ListView view = (ListView)arg1.getParent();
		if (view.getId()==R.id.lv_tarjetas) {
			ly_tarjetas.startAnimation(ocultar);		
			ly_tarjetas.setVisibility(View.GONE);		
				String value = et_tarjetas.getText().toString();
			if (value.equals("")) {
				tarjetas = abono-efectivo-consignacion;
				if (tarjetas>0) {
					et_tarjetas.setText(df.format(tarjetas));
					tv_tarjetas.setText(getString(R.string.tarjeta)+" "+itemstc.get(arg2).getDescripcion());
					id_tarjeta = itemstc.get(arg2).getId();
					id_banco_tarjeta = itemstc.get(arg2).getCodigo();
                    System.out.println("id_tarjeta: "+id_tarjeta+" id_banco_tarjeta "+id_banco_tarjeta+" id_banco "+id_banco);
				}
				else {
					Toast.makeText(PaymentIn.this.getActivity(),R.string.error_valorok, Toast.LENGTH_LONG).show();
					tarjetas=0;
				}
			}
			else {
				tv_tarjetas.setText(getString(R.string.tarjeta)+" "+itemstc.get(arg2).getDescripcion());
				id_tarjeta = itemstc.get(arg2).getId();
				id_banco_tarjeta = itemstc.get(arg2).getCodigo();
                System.out.println("id_tarjeta: "+id_tarjeta+" id_banco_tarjeta "+id_banco_tarjeta+" id_banco "+id_banco);
			}
		}
		else if (view.getId()==R.id.lv_bancos) {
			ly_bancos.startAnimation(ocultar);		
			ly_bancos.setVisibility(View.GONE);		
			String value = et_consignacion.getText().toString();
			if (value.equals("")) {
				consignacion = abono-efectivo-tarjetas;
				if (consignacion>0) {
					et_consignacion.setText(df.format(consignacion));
					tv_bancos.setText(getString(R.string.banco)+" "+itemsbc.get(arg2).getDescripcion());
					id_banco = itemsbc.get(arg2).getId();
				}
				else {
					Toast.makeText(PaymentIn.this.getActivity(),R.string.error_valorok, Toast.LENGTH_LONG).show();
					consignacion=0;
				}
			}
			else {
				tv_bancos.setText(getString(R.string.banco)+" "+itemsbc.get(arg2).getDescripcion());
				id_banco = itemsbc.get(arg2).getId();
			}
		}
		cambio=efectivo+tarjetas+consignacion-abono;
		tv_cambio.setText(df.format(cambio));

	}

	@Override
	public void onFocusChange(View view, boolean arg1) {
		// TODO Auto-generated method stub
		if (view.getId()==R.id.et_efectivo) {
			efectivo = getDoubleValue((EditText)view);
		}
		else if (view.getId()==R.id.et_tarjetas) {
			tarjetas = getDoubleValue((EditText)view);
			if (tarjetas+consignacion>abono) {
				tarjetas = 0;
				et_tarjetas.setText("");
				Toast.makeText(PaymentIn.this.getActivity(),R.string.error_tarjetasbancos, Toast.LENGTH_LONG).show();
			}
			
		}
		else if (view.getId()==R.id.et_consignacion) {
			consignacion = getDoubleValue((EditText)view);
			if (tarjetas+consignacion>abono) {
				consignacion = 0;
				et_consignacion.setText("");
				Toast.makeText(PaymentIn.this.getActivity(),R.string.error_tarjetasbancos, Toast.LENGTH_LONG).show();
			}
		}
		
		cambio=efectivo+tarjetas+consignacion-abono;
		tv_cambio.setText(df.format(cambio));
	}
	
	private double getDoubleValue(EditText text) {
		try {
			String sval = text.getText().toString().replace(".","");
			double val = Double.parseDouble(sval);
			text.setText(df.format(val));
			return val;
		}
		catch(NumberFormatException e) {
			text.setText("");
			return 0;
		}
	}
	
	public void onClick(DialogInterface arg0, int arg1) {
		// TODO Auto-generated method stub
		if (arg1==DialogInterface.BUTTON_NEGATIVE) {
			System.out.println("cancelo");
		}
		else if (arg1==DialogInterface.BUTTON_POSITIVE) {
			System.out.println("acepto");
		}
	}		

	@Override
	public void onStart()
	{
	    super.onStart();    
	    AlertDialog d = (AlertDialog)getDialog();
	    if(d != null)
	    {
		    positiveButton = (Button) d.getButton(Dialog.BUTTON_POSITIVE);
	        positiveButton.setOnClickListener(new View.OnClickListener()
	                {
	                    @Override
	                    public void onClick(View v)
	                    {
	                    	efectivo = getDoubleValue(et_efectivo);
	                    	tarjetas = getDoubleValue(et_tarjetas);
	                    	consignacion = getDoubleValue(et_consignacion);
	                		cambio=efectivo+tarjetas+consignacion-abono;

	                    	
	                    	if (efectivo+tarjetas+consignacion>=abono) {
	                            dismiss();
	                            System.out.println("id_tarjeta: "+id_tarjeta+" id_banco_tarjeta "+id_banco_tarjeta+" id_banco "+id_banco);
	                            DialogClickEvent event = new DialogClickEvent(this,efectivo-cambio,cambio,tarjetas,consignacion,id_tarjeta,id_banco_tarjeta,id_banco);
	                            notificando(event);
	                    	}
	                    	else {
	                    		PaymentIn.this.getActivity().runOnUiThread(new Runnable() {
	                    		    public void run() {
	    	        					Toast.makeText(PaymentIn.this.getActivity(),R.string.error_fpago, Toast.LENGTH_LONG).show();
	                    		    }
	                    		});
	                    	}
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


}
