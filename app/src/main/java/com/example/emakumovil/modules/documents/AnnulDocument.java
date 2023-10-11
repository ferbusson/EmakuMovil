package com.example.emakumovil.modules.documents;

import java.util.ArrayList;
import java.util.List;

import org.jdom2.Document;
import org.jdom2.Element;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.example.emakumovil.R;
import com.example.emakumovil.components.AnswerEvent;
import com.example.emakumovil.components.AnswerListener;
import com.example.emakumovil.components.SearchQuery;
import com.example.emakumovil.components.recordsData;

public class AnnulDocument extends Activity
implements OnClickListener, AnswerListener, OnItemClickListener, TextWatcher  {

	private TextView tv_titulo;
	private ListView LVselected;
	private ArrayList<recordsData> items = new ArrayList<recordsData>();
	private ArrayList<recordsData> itemsDisplay;
	private LayoutInflater inflater;
	private LinearLayout progressLayout;
	private ImageButton ib_clean;
	private EditText et_query;
	private RecordsDataAdapter recordsDataAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_selected_data_dialog);
	    inflater = getLayoutInflater();
		tv_titulo = (TextView)findViewById(R.id.tv_titulo);
		tv_titulo.setText(R.string.buscar_documento);
    	ib_clean = (ImageButton)findViewById(R.id.ib_clean);
    	ib_clean.setOnClickListener(this);
    	et_query = (EditText)findViewById(R.id.et_query);
		et_query.addTextChangedListener(this);
    	progressLayout = (LinearLayout)findViewById(R.id.progressLayout);
    	InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(et_query, InputMethodManager.SHOW_FORCED);
    	items.clear();
		LVselected = (ListView) findViewById(R.id.lv_sprod);
		recordsDataAdapter = new RecordsDataAdapter(
				this, R.layout.recordsearch, items);
		LVselected.setAdapter(recordsDataAdapter);
		LVselected.setOnItemClickListener(this);
		LVselected.setTextFilterEnabled(true);
		new SearchQuery(this,"MVSEL0023").start(); // consulta listado de documentos

	}


	@Override
	public void arriveAnswerEvent(AnswerEvent e) {
		// TODO Auto-generated method stub
		Document doc = e.getDocument();
		System.out.println("llego una query "+e.getSqlCode());
		final Element rootNode = doc.getRootElement();
		this.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				progressLayout.setVisibility(View.GONE);
				List<Element> listRows = rootNode.getChildren("row");
				items.clear();
				for (int i = 0; i < listRows.size(); i++) {
					Element Erow = listRows.get(i);
					List<Element> Lcol = Erow.getChildren();
					String idRecord =  Lcol.get(0).getText();
					String descripcion = Lcol.get(1).getText();
					items.add(new recordsData(idRecord,null,descripcion,null,null,null,null));
				}

				recordsDataAdapter = new RecordsDataAdapter(
						AnnulDocument.this, R.layout.singlerecorddata,
						items);
				LVselected.setAdapter(recordsDataAdapter);
			}
		});
	}

	@Override
	public boolean containSqlCode(String sqlCode) {
		// TODO Auto-generated method stub
		return false;
	}


	@SuppressLint("DefaultLocale")
	private class RecordsDataAdapter extends ArrayAdapter<recordsData> implements Filterable {


		public RecordsDataAdapter(Context context, int textViewResourceId,
				ArrayList<recordsData> items) {
			super(context, textViewResourceId, items);
			AnnulDocument.this.items = items;
			Log.d("EMAKU ","EMAKU instanciando adaptador");
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View v = convertView;

			recordsData recData = items.get(position);
			if (recData != null) {
				v = inflater.inflate(R.layout.singlerecorddata, null);
				ImageView im_identify = (ImageView)v.findViewById(R.id.iv_icono);
				TextView tv_descripcion1 = (TextView) v
						.findViewById(R.id.tv_descripcion1);

				if (tv_descripcion1 != null) {
					im_identify.setImageResource(R.drawable.ic_action_documenth);
					tv_descripcion1.setText(recData.getDescripcion());
				}
			}
			return v;
		}

		@Override
		public int getCount() {
			return items.size();
		}


		@Override
		public long getItemId(int position) {
		    return position;
		}

		@Override
		@SuppressLint("DefaultLocale")
		public Filter getFilter() {
		    Filter filter = new Filter() {

		        @SuppressWarnings("unchecked")
		        @Override
		        protected void publishResults(CharSequence constraint,FilterResults results) {

		            items = (ArrayList<recordsData>) results.values; // tiene los valores filtrados
		            notifyDataSetChanged();  // notifica el cambio de valores
		        }

		        @SuppressLint("DefaultLocale")
				@Override
		        protected FilterResults performFiltering(CharSequence constraint) {
		            FilterResults results = new FilterResults();        // tiene el resultado del filtro
		            ArrayList<recordsData> FilteredArrList = new ArrayList<recordsData>();

		            if (itemsDisplay == null) {
		                itemsDisplay = new ArrayList<recordsData>(items); // graba los valores originales en items
		            }

		            /********
		             *
		             *  If constraint(CharSequence that is received) is null returns the mOriginalValues(Original) values
		             *  else does the Filtering and returns FilteredArrList(Filtered)
		             *
		             ********/
		            if (constraint == null || constraint.length() == 0) {

		                // set the Original result to return
		                results.count = itemsDisplay.size();
		                results.values = itemsDisplay;
		            }
		            else {
		                constraint = constraint.toString().toLowerCase();
		                for (int i = 0; i < itemsDisplay.size(); i++) {
		                    String data = itemsDisplay.get(i).getDescripcion();
		                    System.out.println("buscando "+constraint.toString()+" en "+data);
		                    if (data.toLowerCase().startsWith(constraint.toString())) {
		                    	System.out.println("encontrado");
		                        FilteredArrList.add(new recordsData(itemsDisplay.get(i).getId(),null,itemsDisplay.get(i).getDescripcion(),itemsDisplay.get(i).getDescripcion2(),null,null,null));
		                    }
		                }
		                // set the Filtered result to return
		                results.count = FilteredArrList.size();
		                results.values = FilteredArrList;
		            }
		            return results;
		        }
		    };
		    return filter;
		}

	}


	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub
	   	 Intent i = new Intent(this, DocumentDetailActivity.class );
	   	 i.putExtra("titulo",items.get(arg2).getDescripcion());
	   	 i.putExtra("codigo_tipo",items.get(arg2).getId());
	   	 System.out.println("codigo_tipo: "+items.get(arg2).getId());
	     this.startActivity(i);
	}

	@Override
	public void afterTextChanged(Editable s) {
		// TODO Auto-generated method stub
		//ArrayAdapter<RecordsDataAdapter> adapter = (ArrayAdapter<RecordsDataAdapter>)LVselected.getAdapter();
		recordsDataAdapter.getFilter().filter(s);
		//adapter.getFilter().filter(s);

	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		// TODO Auto-generated method stub

	}

    @Override
	public void onClick(View v) {
		et_query.setText("");
	}



}
