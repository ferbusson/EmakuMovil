package com.example.emakumovil.modules.arqueo;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.jdom2.Document;
import org.jdom2.Element;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.emakumovil.R;
import com.example.emakumovil.components.AnswerEvent;
import com.example.emakumovil.components.AnswerListener;
import com.example.emakumovil.components.DatePickerFragment;
import com.example.emakumovil.components.DialogClickEvent;
import com.example.emakumovil.components.DialogClickListener;
import com.example.emakumovil.components.GlobalData;
import com.example.emakumovil.components.ProgressDialogManager;
import com.example.emakumovil.components.SearchQuery;
import com.example.emakumovil.components.fieldsAQ;

public class QueryCashActivity extends Activity implements OnClickListener,
		OnItemClickListener, AnswerListener,DialogClickListener {

	private Button bt_desde;
	private Button bt_hasta;
	private ImageButton bt_aceptaraq;
	static final int DATE_DIALOG_ID = 0;
	private ArrayList<fieldsAQ> items;
	private int selectedItemClick;
	private ListView lv_cuadre;
	DecimalFormat df = new DecimalFormat("##,###,###");
	private DatePickerFragment desde;
	private DatePickerFragment hasta;
	
	@SuppressLint("SimpleDateFormat")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_query_cash);
		bt_desde = (Button) findViewById(R.id.bt_desde);
		bt_hasta = (Button) findViewById(R.id.bt_hasta);
		bt_aceptaraq = (ImageButton) findViewById(R.id.bt_aceptaraq);

		bt_desde.setOnClickListener(this);
		bt_hasta.setOnClickListener(this); 
		bt_aceptaraq.setOnClickListener(this);

		desde = new DatePickerFragment(R.id.bt_desde);
		desde.addDialogClickListener(this);
		hasta = new DatePickerFragment(R.id.bt_hasta);
		hasta.addDialogClickListener(this);

		GlobalData.items_aq.clear();
		lv_cuadre = (ListView) findViewById(R.id.lv_arqueo);
		
		lv_cuadre.setAdapter(new MyListAdapter(
				QueryCashActivity.this, R.layout.infoarqueolayout,
				GlobalData.items_aq));
		lv_cuadre.setOnItemClickListener(this);
		registerForContextMenu(lv_cuadre);

	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		if (arg0.getId() == R.id.bt_desde) {
			desde.show(getFragmentManager(),getString(R.string.desde));
		} 
		else if (arg0.getId() == R.id.bt_hasta) {
			hasta.show(getFragmentManager(),getString(R.string.hasta));
		} 
		else if (arg0.getId() == R.id.bt_aceptaraq) {
			if (!getString(R.string.hasta).equals(bt_hasta.getText().toString())) {
				if (!getString(R.string.hasta).equals(bt_hasta.getText().toString())) {
					ProgressDialogManager.show(this, QueryCashActivity.handler,getString(R.string.cargando));
					new SearchQuery(QueryCashActivity.this,"MVSEL0001", new String[] {
							bt_desde.getText().toString(),
							bt_hasta.getText().toString() }).start();
				}
				else {
		    		Toast.makeText(QueryCashActivity.this,getString(R.string.error_finicial), Toast.LENGTH_LONG).show();
				}
			}
			else {
	    		Toast.makeText(QueryCashActivity.this,getString(R.string.error_ffinal), Toast.LENGTH_LONG).show();
			}
		}
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
	}

	@Override
	public void arriveAnswerEvent(AnswerEvent e) {
		// TODO Auto-generated method stub
		System.out.println("llego la query " + e.getSqlCode());
		Document doc = e.getDocument();
		if (e.getSqlCode().equals("MVSEL0001")) {
			final Element rootNode = doc.getRootElement();
			this.runOnUiThread(new Runnable() {
				public void run() {
					List<Element> listRows = rootNode.getChildren("row");
					String lastdate=null;
					boolean repeat= false;
					GlobalData.items_aq.clear();
					for (int i = 0; i < listRows.size(); i++) {
						Element Erow = (Element) listRows.get(i);
						List<Element> Lcol = Erow.getChildren();
						// fecha numero cajero total recaudo diferencia
						SimpleDateFormat dateformat = new SimpleDateFormat("EEE dd MMM yyyy",Locale.US);
						SimpleDateFormat dateformatxml = new SimpleDateFormat("yyyy-MM-dd",Locale.US);
						//String fecha = dateformat.format(((Element) Lcol.get(0)).getText());
						Date fechaok;
						try {
							fechaok = dateformatxml.parse(((Element) Lcol.get(0)).getText());
						} catch (ParseException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							fechaok = new Date();
						}
						String fecha = dateformat.format(fechaok);
						System.out.println("fecha desde la bd "+((Element) Lcol.get(0)).getText());
						System.out.println("fecha convertida "+fecha);
						String numero = ((Element) Lcol.get(1)).getText();
						String cajero = ((Element) Lcol.get(2)).getText();
						double total = Double.parseDouble(((Element) Lcol.get(3)).getText());
						String recaudo = df.format(Double
								.parseDouble(((Element) Lcol.get(4)).getText()));
						String diferencia = df.format(Double
								.parseDouble(((Element) Lcol.get(5)).getText()));
						double diffn = Double.parseDouble(((Element) Lcol
								.get(5)).getText());
						String gtotal = df.format(Double
								.parseDouble(((Element) Lcol.get(6)).getText()));
						String ndocumento = ((Element) Lcol.get(7)).getText();
						if (lastdate==null || !lastdate.equals(fecha)) {
							lastdate=fecha;
							i--;
							repeat=true;
						}
						else {
							repeat=false;
						}
						GlobalData.items_aq.add(new fieldsAQ(cajero,numero, total,
								recaudo, diferencia, diffn, fecha,gtotal,repeat,ndocumento));
					}
					lv_cuadre.setAdapter(new MyListAdapter(
							QueryCashActivity.this, R.layout.infoarqueolayout,
							GlobalData.items_aq));
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

	private class MyListAdapter extends ArrayAdapter<fieldsAQ> {

		private LayoutInflater vi;

		public MyListAdapter(Context context, int textViewResourceId,
				ArrayList<fieldsAQ> items) {
			super(context, textViewResourceId, items);
			QueryCashActivity.this.items = items;
			Log.d("EMAKU ","EMAKU instanciando adaptador");
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View v = convertView;
			if (v == null) {
				vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			}

			fieldsAQ myData = items.get(position);
			if (myData != null) {
				if (!myData.isRepeat()) {
					v = vi.inflate(R.layout.infoarqueolayout, null);
					TextView tv_nombre = (TextView) v
							.findViewById(R.id.tv_nombre);
					TextView tv_numeroaq = (TextView) v
							.findViewById(R.id.tv_numeroaq);
					TextView tv_totales = (TextView) v
							.findViewById(R.id.tv_totales);
					TextView tv_recaudo = (TextView) v
							.findViewById(R.id.tv_recaudo);
					TextView tv_diff = (TextView) v.findViewById(R.id.tv_diff);
					ImageView im_status = (ImageView)v.findViewById(R.id.im_status);

					if (tv_nombre != null) {
						tv_nombre.setText(myData.getUsuario());
					}
					if (tv_numeroaq != null) {
						tv_numeroaq.setText(myData.getNarqueo());
					}
					if (tv_totales != null) {
						tv_totales.setText(df.format(myData.getValor()));
					}
					if (tv_recaudo != null) {
						tv_recaudo.setText(String.valueOf(myData.getRecaudo()));
					}
					if (tv_diff != null) {
						tv_diff.setText(String.valueOf(myData.getDiff()));
						double diffn = myData.getDiffn();
						if (Math.abs(diffn) > 2000) {
							im_status.setImageResource(R.drawable.ic_action_dontlike);
							//tv_diff.setTextColor(Color.RED);
						} else {
							im_status.setImageResource(R.drawable.ic_action_like);
							//tv_diff.setTextColor(Color.GREEN);
						}
					}
				} else {
					v = vi.inflate(R.layout.fechaarqueo, null);
					TextView tv_fechaaq = (TextView) v
							.findViewById(R.id.tv_fechaaq);
					TextView tv_totalaq = (TextView) v
							.findViewById(R.id.tv_totalaq);
					if (tv_fechaaq != null) {
						tv_fechaaq.setText(myData.getFecha());
					}
					if (tv_totalaq != null) {
						tv_totalaq.setText(myData.getTotal());
					}
				}
			}
			return v;
		}
		
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub
		 if (arg0.getId()==R.id.lv_arqueo) {
		   	 Intent i = new Intent(this, DetailCashActivity.class );
		   	 String ndocumento = items.get(arg2).getNdocumento();
		   	 i.putExtra("ndocumento", ndocumento);
		   	 i.putExtra("usuario",items.get(arg2).getUsuario());
		   	 i.putExtra("fecha",items.get(arg2).getFecha());
		   	 i.putExtra("narqueo",items.get(arg2).getNarqueo());
		     this.startActivity(i);

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
 	public void onCreateContextMenu(ContextMenu menu, View v,ContextMenuInfo menuInfo) {
 	  super.onCreateContextMenu(menu, v, menuInfo);
 	  getMenuInflater().inflate(R.menu.contextmenuarqueo,menu);
 	  menu.setHeaderTitle(getString(R.string.ccaja));
 	  AdapterContextMenuInfo info = (AdapterContextMenuInfo) menuInfo;
 	  selectedItemClick = info.position;
 	  
 	}

 	@Override
 	public boolean onContextItemSelected(MenuItem item) {
 	 
 	    switch (item.getItemId()) {
 	        case R.id.it_modificar_arqueo:
 			   	 Intent i = new Intent(this, ChangeCashActivity.class );
 			   	 String ndocumento = items.get(selectedItemClick).getNdocumento();
 			   	 i.putExtra("ndocumento", ndocumento);
 			   	 i.putExtra("vsistema",items.get(selectedItemClick).getValor());
 			   	 i.putExtra("usuario",items.get(selectedItemClick).getUsuario());
 			   	 i.putExtra("fecha",items.get(selectedItemClick).getFecha());
 			   	 i.putExtra("narqueo",items.get(selectedItemClick).getNarqueo());

 			     this.startActivity(i);
 			     return true;
 	        default:
 	        	return false;
 	    }
 	}

	@Override
	public void dialogClickEvent(DialogClickEvent e) {
		// TODO Auto-generated method stub
		if (R.id.bt_desde==e.getIdobject()){
			bt_desde.setText(e.getValue());
		}
		else if (R.id.bt_hasta==e.getIdobject()) {
			bt_hasta.setText(e.getValue());
		}
	}
}
