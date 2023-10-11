package com.example.emakumovil.modules.cartera;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import org.jdom2.Document;
import org.jdom2.Element;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.example.emakumovil.R;
import com.example.emakumovil.components.AnswerEvent;
import com.example.emakumovil.components.AnswerListener;
import com.example.emakumovil.components.SearchQuery;
import com.example.emakumovil.components.recordsData;

public class CarteraCxPActivity extends Activity implements OnClickListener, OnItemClickListener, AnswerListener {

	private TextView tv_titulo;
	private ImageButton ib_search;
	private EditText et_query;
	private ArrayList<recordsData> items = new ArrayList<recordsData>();
	private ListView LVrecords;
	private String query;
	private String listQuery;
	private LinearLayout progressLayout;
	private LayoutInflater vi;
	private DecimalFormat df;
	private String titulo;
	
	private int type;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_cartera_cxp);
		Bundle extras = getIntent().getExtras();
		tv_titulo =(TextView)findViewById(R.id.tv_titulo);
		df = new DecimalFormat("##,###,###");
		if (extras!=null) {
			type = extras.getInt("type");
			query = extras.getString("query");
			listQuery = extras.getString("listQuery");
			titulo = extras.getString("titulo");
			tv_titulo.setText(extras.getString("titulo"));
	    	ib_search = (ImageButton)findViewById(R.id.ib_search);
	    	ib_search.setOnClickListener(this);
	    	et_query = (EditText)findViewById(R.id.et_query);
	    	progressLayout = (LinearLayout)findViewById(R.id.progressLayout);
			progressLayout.setVisibility(View.GONE);	
	    	InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
	        imm.showSoftInput(et_query, InputMethodManager.SHOW_FORCED);
	    	items.clear();
			LVrecords = (ListView)findViewById(R.id.lv_records);
			LVrecords.setAdapter(new CarteraDataAdapter(
					this, R.layout.activity_cartera_cxp,
					items));
			LVrecords.setOnItemClickListener(this);
			vi = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		String arg = et_query.getText().toString();
		new SearchQuery(this,query, new String[] {arg}).start();
		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				progressLayout.setVisibility(View.VISIBLE);	
				
			}
		});
		
	}

	private class CarteraDataAdapter extends ArrayAdapter<recordsData> {


		public CarteraDataAdapter(Context context, int textViewResourceId,
				ArrayList<recordsData> items) {
			super(context, textViewResourceId, items);
			CarteraCxPActivity.this.items = items;
			Log.d("EMAKU ","EMAKU instanciando adaptador");
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View v = convertView;
			if (v == null) {
				v = vi.inflate(R.layout.carterasearch, null);
			}

			recordsData recData = items.get(position);
			if (recData != null) {
				TextView tv_descripcion1 = (TextView) v
						.findViewById(R.id.tv_descripcion1);
				TextView tv_descripcion2 = (TextView) v
						.findViewById(R.id.tv_descripcion2);
				TextView tv_descripcion3 = (TextView) v
						.findViewById(R.id.tv_descripcion3);
				TextView tv_descripcion4 = (TextView) v
						.findViewById(R.id.tv_descripcion4);
				TextView tv_valor = (TextView) v
						.findViewById(R.id.tv_valor);

				if (tv_descripcion1 != null) {
					tv_descripcion1.setText(recData.getCodigo());
				}
				if (tv_descripcion2 != null) {
					tv_descripcion2.setText(recData.getDescripcion());
				}
				if (tv_descripcion3 != null) {
					tv_descripcion3.setText(recData.getDescripcion2());
				}
				if (tv_descripcion4 != null) {
					tv_descripcion4.setText(recData.getDescripcion3());
				}
				if (tv_valor != null) {
					tv_valor.setText(recData.getValor());
				}
			}
			return v;
		}

	}

	@Override
	public void arriveAnswerEvent(AnswerEvent e) {
		// TODO Auto-generated method stub
		Document doc = e.getDocument();
		System.out.println("llego una query");
		if (e.getSqlCode().equals(query)) {
			try {
				final Element rootNode = doc.getRootElement();
				runOnUiThread(new Runnable() {
					public void run() {
						List<Element> listRows = rootNode.getChildren("row");
						items.clear();
						for (int i = 0; i < listRows.size(); i++) {
							Element Erow = (Element) listRows.get(i);
							List<Element> Lcol = Erow.getChildren();
							String id = ((Element) Lcol.get(0)).getText();
							String codigo = ((Element) Lcol.get(1)).getText();
							String descripcion = ((Element) Lcol.get(2)).getText();
							String descripcion2 = ((Element) Lcol.get(3)).getText();
							String descripcion3 = ((Element) Lcol.get(4)).getText();
							double total_cartera = Double.parseDouble(((Element) Lcol.get(5)).getText());
							String svalor = df.format(total_cartera);
							
							items.add(new recordsData(id,codigo,descripcion,descripcion2,descripcion3,svalor,total_cartera));
						}
						LVrecords.setAdapter(new CarteraDataAdapter(
								CarteraCxPActivity.this, R.layout.carterasearch,
								items));
					}
				});
			}
			catch (NullPointerException npee) {
				npee.printStackTrace();
			}
		}
		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				progressLayout.setVisibility(View.GONE);	
				
			}
		});
		
	}

	@Override
	public boolean containSqlCode(String sqlCode) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub
	   	 Intent i = new Intent(this, ListCarteraActivity.class );
	   	 i.putExtra("type",type);
	   	 i.putExtra("id_tercero",items.get(arg2).getId());
	   	 i.putExtra("descripcion1",items.get(arg2).getCodigo());
	   	 i.putExtra("descripcion2",items.get(arg2).getDescripcion());
	   	 i.putExtra("descripcion3",items.get(arg2).getDescripcion2());
	   	 i.putExtra("descripcion4",items.get(arg2).getDescripcion3());
	   	 i.putExtra("valor",items.get(arg2).getValor());
	   	 i.putExtra("total_cartera",items.get(arg2).getDvalor());
	   	 i.putExtra("query",listQuery);
	   	 i.putExtra("titulo",titulo);
	     this.startActivity(i);

	}
	
}
