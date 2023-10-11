package com.example.emakumovil.modules.inventario;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

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
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.example.emakumovil.R;
import com.example.emakumovil.components.AnswerEvent;
import com.example.emakumovil.components.AnswerListener;
import com.example.emakumovil.components.FieldsDDV;
import com.example.emakumovil.components.ProgressDialogManager;
import com.example.emakumovil.components.SearchQuery;

public class SearchCollectorActivity extends Activity implements OnClickListener, AnswerListener {

	private ImageButton ib_buscar;
	private EditText et_numero;
	private TextView tv_fecha;
	private TextView tv_total;
	private ArrayList<FieldsDDV> items = new ArrayList<FieldsDDV>();
	private ListView lv_listbarras;

    @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search_collector);
		ib_buscar = (ImageButton) findViewById(R.id.ib_buscar);
		ib_buscar.setOnClickListener(this);
		et_numero = (EditText) findViewById(R.id.et_numero);
		tv_fecha = (TextView) findViewById(R.id.tv_fecha);
		tv_total = (TextView) findViewById(R.id.tv_total);
		lv_listbarras = (ListView)findViewById(R.id.lv_listbarras);
	}


	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		new SearchQuery(SearchCollectorActivity.this,"MVSEL0015", new String[] {
				"DR",
				et_numero.getText().toString()}).start();
		new SearchQuery(SearchCollectorActivity.this,"MVSEL0016", new String[] {
				"DR",
				et_numero.getText().toString()}).start();
		ProgressDialogManager.show(this, SearchCollectorActivity.handler,getString(R.string.cargando));
	}

	@Override
	public void arriveAnswerEvent(AnswerEvent e) {
		// TODO Auto-generated method stub
		final Document doc = e.getDocument();
		if ("MVSEL0015".equals(e.getSqlCode())) {
		        this.runOnUiThread(new Runnable() {
		            public void run() {
		    			try {
							String fecha = doc.getRootElement().getChild("row").getChildText("col").trim().toString();
							tv_fecha.setText(fecha);
		    			}
						catch(NullPointerException NPEe) {
							tv_fecha.setText(getString(R.string.fecha));
						}
					}
		            });
		}
		else if ("MVSEL0016".equals(e.getSqlCode())) {
			final Element rootNode = doc.getRootElement();
			this.runOnUiThread(new Runnable() {
				public void run() {
					List<Element> listRows = rootNode.getChildren("row");
					DecimalFormat df = new DecimalFormat("##,###,###");
					double total = 0;
					items.clear();
					for (int i = 0; i < listRows.size(); i++) {
						Element Erow = (Element) listRows.get(i);
						List<Element> Lcol = Erow.getChildren();
						String descripcion1 = ((Element) Lcol.get(0)).getText();
						String descripcion2 = ((Element) Lcol.get(1)).getText();
						String tvalor = ((Element) Lcol.get(2)).getText();
						String valor = df.format(Double
								.parseDouble(tvalor));
						total+=Double.parseDouble(tvalor);
						items.add(new FieldsDDV(descripcion1,descripcion2,valor));
					}
					tv_total.setText(df.format(total));
					DataAdapter adapter = new DataAdapter(
							SearchCollectorActivity.this, R.layout.infofototextovalor,
							items);
					System.out.println("adicionando adaptador");
					lv_listbarras.setAdapter(adapter);
					System.out.println("adaptador adicionado");
					try {
						ProgressDialogManager.dismissCurrent();
					}
					catch(IllegalArgumentException e) {}
				}
			});
		}
	}

	private class DataAdapter extends ArrayAdapter<FieldsDDV> {

		private LayoutInflater vi;

		public DataAdapter(Context context, int textViewResourceId,
				ArrayList<FieldsDDV> items) {
			super(context, textViewResourceId, items);
			SearchCollectorActivity.this.items = items;
			Log.d("EMAKU ","EMAKU instanciando adaptador");
			vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View v = convertView;
			ViewHolder holder;
			
			if (v == null) {
				v = vi.inflate(R.layout.listicondatadatavalue, null);
				TextView tv_descripcion1 = (TextView) v
						.findViewById(R.id.tv_descripcion1);
				TextView tv_descripcion2 = (TextView) v
						.findViewById(R.id.tv_descripcion2);
				TextView tv_valor = (TextView) v
						.findViewById(R.id.tv_valor);
				holder = new ViewHolder(tv_descripcion1,tv_descripcion2,tv_valor);
				v.setTag(holder);
			}
			else {
				holder = (ViewHolder)v.getTag();
			}

			
			FieldsDDV myData = items.get(position);
			if (myData != null) {
				if (holder.tv_descripcion1 != null) {
					holder.tv_descripcion1.setText(myData.getDescripcion1());
				}
				if (holder.tv_descripcion2 != null) {
					holder.tv_descripcion2.setText(myData.getDescripcion2());
				}
				if (holder.tv_valor != null) {
					holder.tv_valor.setText(myData.getValor());
				}
			}
			return v;
		}

	}

	@Override
	public boolean containSqlCode(String sqlCode) {
		// TODO Auto-generated method stub
		return false;
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

     
 	private class ViewHolder {
 		public TextView tv_descripcion1;
 		public TextView tv_descripcion2;
 		public TextView tv_valor;
 		
 		public ViewHolder(TextView tv_descripcion1,TextView tv_descripcion2,TextView tv_valor) {
 			this.tv_descripcion1=tv_descripcion1;
 			this.tv_descripcion2=tv_descripcion2;
 			this.tv_valor=tv_valor;
 		}
 		
 	}
}
