package com.example.emakumovil.components;


import java.util.ArrayList;
import java.util.List;

import org.jdom.Document;
import org.jdom.Element;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.example.emakumovil.R;
import com.example.emakumovil.modules.compras.PurchasingActivity;

public class SearchPersonActivity extends Activity implements OnClickListener, OnItemClickListener, AnswerListener {

	private ImageButton ib_search;
	private EditText et_query;
	private ArrayList<recordsData> items = new ArrayList<recordsData>();
	private LayoutInflater inflater;
	private ListView LVrecords;
	private String descripcion1;
	private String descripcion2;
	private String descripcion3;
	private String descripcion4;
	private String idRecord;
	private LinearLayout progressLayout;
	private String query;
	
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search_person);
	    
    	ib_search = (ImageButton)findViewById(R.id.ib_buscar);
    	ib_search.setOnClickListener(this);
    	et_query = (EditText)findViewById(R.id.et_query);
    	progressLayout = (LinearLayout)findViewById(R.id.progressLayout);
		progressLayout.setVisibility(View.GONE);	
    	items.clear();
		LVrecords = (ListView)findViewById(R.id.lv_records);
		LVrecords.setAdapter(new RecordsDataAdapter(
				SearchPersonActivity.this, R.layout.recordsearch,
				items));
		LVrecords.setOnItemClickListener(this);
		
	    Bundle bundle = getIntent().getExtras();
	    if (bundle!=null) {
	    	query = bundle.getString("query");
	    	setTitle(bundle.getInt("titulo"));
	    	
	    }

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		System.out.println("generalndo query");
		String arg = et_query.getText().toString();
		new SearchQuery(SearchPersonActivity.this,query, new String[] {arg}).start();
		progressLayout.setVisibility(View.VISIBLE);	
	}

	@Override
	public void arriveAnswerEvent(AnswerEvent e) {
		// TODO Auto-generated method stub
		Document doc = e.getDocument();
		System.out.println("llego una query");
		if (e.getSqlCode().equals("MVSEL0010") || e.getSqlCode().equals("MVSEL0035")) {
			final Element rootNode = doc.getRootElement();
			this.runOnUiThread(new Runnable() {
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
						String id2 = ((Element) Lcol.get(5)).getText();
						String id3 = ((Element) Lcol.get(6)).getText();
						items.add(new recordsData(id,codigo,descripcion,descripcion2,descripcion3,id2,id3));
						progressLayout.setVisibility(View.GONE);	
					}
					LVrecords.setAdapter(new RecordsDataAdapter(
							SearchPersonActivity.this, R.layout.recordsearch,
							items));
				}
			});
		}
	}

	@Override
	public boolean containSqlCode(String sqlCode) {
		// TODO Auto-generated method stub
		return false;
	}

	private class RecordsDataAdapter extends ArrayAdapter<recordsData> {


		public RecordsDataAdapter(Context context, int textViewResourceId,
				ArrayList<recordsData> items) {
			super(context, textViewResourceId, items);
			SearchPersonActivity.this.items = items;
			Log.d("EMAKU ","EMAKU instanciando adaptador");
			inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View v = null;
			recordsData recData = items.get(position);
			if (recData != null) {
				if (query.equals("MVSEL0010") || query.equals("MVSEL0035")) {
					v = inflater.inflate(R.layout.recordsearch, null);
					TextView tv_descripcion1 = (TextView) v
							.findViewById(R.id.tv_descripcion1);
					TextView tv_descripcion2 = (TextView) v
							.findViewById(R.id.tv_descripcion2);
					TextView tv_descripcion3 = (TextView) v
							.findViewById(R.id.tv_descripcion3);
					TextView tv_descripcion4 = (TextView) v
							.findViewById(R.id.tv_descripcion4);

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
				}
			}
			return v;
		}

	}

	public String getDescripcion1() {
		return descripcion1;
	}
	
	public String getDescripcion2() {
		return descripcion2;
	}

	public String getDescripcion3() {
		return descripcion3;
	}

	public String getDescripcion4() {
		return descripcion4;
	}

	public String getIdRecord() {
		return idRecord;
	}


	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub
		descripcion1 = items.get(arg2).getCodigo();
		descripcion2 = items.get(arg2).getDescripcion();
		descripcion3 = items.get(arg2).getDescripcion2();
		descripcion4 = items.get(arg2).getDescripcion3();
		idRecord = items.get(arg2).getId();
		Intent i = new Intent(this,PurchasingActivity.class);
		i.putExtra("id",items.get(arg2).getId());
		i.putExtra("descripcion1",descripcion1);
		i.putExtra("descripcion2",descripcion2);
		i.putExtra("descripcion3",descripcion3);
		i.putExtra("descripcion4",descripcion4);
		i.putExtra("id_direccion",items.get(arg2).getId2());
		i.putExtra("id_telefono",items.get(arg2).getId3());
		this.startActivity(i);

	}
}
