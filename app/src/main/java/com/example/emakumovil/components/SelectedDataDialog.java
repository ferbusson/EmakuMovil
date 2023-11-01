package com.example.emakumovil.components;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.jdom2.Document;
import org.jdom2.Element;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
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
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.example.emakumovil.R;

@SuppressLint("ValidFragment")
public class SelectedDataDialog  extends DialogFragment implements OnClickListener, OnItemClickListener, AnswerListener,OnDismissListener, TextWatcher {

	private TextView tv_titulo;
	private ImageButton ib_clean;
	private EditText et_query;
	private ArrayList<recordsData> items = new ArrayList<recordsData>();
	private ArrayList<recordsData> itemsDisplay;
	private LinearLayout progressLayout;
	private LayoutInflater inflater;
	private ListView LVselected;
	private String descripcion;
	private String descripcion2;
	private String idRecord;
	private String query;
	private String[] args;
	private RecordsDataAdapter recordsDataAdapter;
	private Locale locale = Locale.getDefault();
	private long b;
	
	@SuppressLint("ValidFragment")
	public SelectedDataDialog(long b,String titulo,String query, String args[]) {
		Bundle bundle = new Bundle();
		bundle.putString("titulo",titulo);
		bundle.putString("query",query);
		setArguments(bundle);
		this.b=b;
		this.args=args;
	}

	@SuppressLint("ValidFragment")
	public SelectedDataDialog(String titulo,String query, String args[]) {
		Bundle bundle = new Bundle();
		bundle.putString("titulo",titulo);
		bundle.putString("query",query);
		setArguments(bundle);
		this.args=args;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
	    inflater = getActivity().getLayoutInflater();
	    Bundle bundle = getArguments();
	    View v = inflater.inflate(R.layout.activity_selected_data_dialog, null);
	    builder.setView(v)
	           .setNegativeButton(R.string.cancelar, new DialogInterface.OnClickListener() {
	               @Override
				public void onClick(DialogInterface dialog, int id) {
	               }
	           });

	    if (bundle!=null) {
	    	String titulo = bundle.getString("titulo");
	    	query = bundle.getString("query");
	    	tv_titulo = (TextView)v.findViewById(R.id.tv_titulo);
	    	tv_titulo.setText(titulo);
	    	ib_clean = (ImageButton)v.findViewById(R.id.ib_clean);
	    	ib_clean.setOnClickListener(this);
	    	et_query = (EditText)v.findViewById(R.id.et_query);
	    	progressLayout = (LinearLayout)v.findViewById(R.id.progressLayout);
	    	InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
	        imm.showSoftInput(et_query, InputMethodManager.SHOW_FORCED);
	    	items.clear();
			LVselected = (ListView) v.findViewById(R.id.lv_sprod);
			recordsDataAdapter = new RecordsDataAdapter(getActivity(),
					R.layout.recordsearch, items);
			LVselected.setAdapter(recordsDataAdapter);
			LVselected.setOnItemClickListener(this);
			LVselected.setTextFilterEnabled(true);
			et_query.addTextChangedListener(this);
			System.out.println("generalndo query");
			new SearchQuery(SelectedDataDialog.this,query, args).start();
	    }

		if (savedInstanceState!=null) {
			b=savedInstanceState.getLong("b");
		}
	    Dialog d = builder.create();
	    return d;
	}

	public void onSaveInstanceState(Bundle savedInstanceState) {
		super.onSaveInstanceState(savedInstanceState);
		savedInstanceState.putLong("b",b);
	}


     @Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		System.out.println("limpiando");
		et_query.setText("");
	}

	@Override
	public void arriveAnswerEvent(AnswerEvent e) {
		// TODO Auto-generated method stub
		Document doc = e.getDocument();
		System.out.println("llego una query "+e.getSqlCode());
		if (e.getSqlCode().equals("MVSEL0011") || e.getSqlCode().equals("MVSEL0040") || query.equals("MVSEL0041") || 
				query.equals("MVSEL0042")  || query.equals("MVSEL0044") || query.equals("MVSEL0045")|| 
				query.equals("MVSEL0049") || query.equals("MVSEL0050")|| query.equals("MVSEL0051")
				|| query.equals("MVSEL0052")|| query.equals("MVSEL0053")|| query.equals("MVSEL0054")
				|| query.equals("MVSEL0055")|| query.equals("MVSEL0056")|| query.equals("MVSEL0057")
				|| query.equals("MVSEL0058")|| query.equals("MVSEL0059")|| query.equals("MVSEL0060")
				|| query.equals("MVSEL0081") || query.equals("MVSEL0083")) {
			final Element rootNode = doc.getRootElement();
			getActivity().runOnUiThread(new Runnable() {
				@Override
				public void run() {
					List<Element> listRows = rootNode.getChildren("row");
					items.clear();
					for (int i = 0; i < listRows.size(); i++) {
						Element Erow = listRows.get(i);
						List<Element> Lcol = Erow.getChildren();
						String idRecord =  Lcol.get(0).getText();
						String descripcion = Lcol.get(1).getText();
						items.add(new recordsData(idRecord,null,descripcion,null,null,null,null));
					}
					System.out.println("items adicionados, preparando adaptador");
					recordsDataAdapter = new RecordsDataAdapter(
							getActivity(), R.layout.singlerecorddata,
							items);
					LVselected.setAdapter(recordsDataAdapter);
				}
			});
		}
		else if (e.getSqlCode().equals("MVSEL0012") || e.getSqlCode().equals("MVSEL0013")) {
			final Element rootNode = doc.getRootElement();
			getActivity().runOnUiThread(new Runnable() {
				@Override
				public void run() {
					List<Element> listRows = rootNode.getChildren("row");
					items.clear();
					for (int i = 0; i < listRows.size(); i++) {
						Element Erow = listRows.get(i);
						List<Element> Lcol = Erow.getChildren();
						String id = Lcol.get(0).getText();
						String descripcion = Lcol.get(1).getText();
						String descripcion2 = Lcol.get(2).getText();
						items.add(new recordsData(id,null,descripcion,descripcion2,null,null,null));
					}
					System.out.println("lvrecords: "+LVselected);
					RecordsDataAdapter adapter = new RecordsDataAdapter(getActivity(), R.layout.pairrecorddata,items);
					LVselected.setAdapter(adapter);
//					adapter.notifyDataSetChanged();
				}
			});
		}
		getActivity().runOnUiThread(new Runnable() {

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

	private class RecordsDataAdapter extends ArrayAdapter<recordsData> implements Filterable {


		public RecordsDataAdapter(Context context, int textViewResourceId,
				ArrayList<recordsData> items) {
			super(context, textViewResourceId, items);
			SelectedDataDialog.this.items = items;
			Log.d("EMAKU ","EMAKU instanciando adaptador");
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View v = convertView;

			recordsData recData = items.get(position);
			if (recData != null) {
				if (query.equals("MVSEL0011") || query.equals("MVSEL0040") || query.equals("MVSEL0041") || 
						query.equals("MVSEL0042")|| query.equals("MVSEL0044") || query.equals("MVSEL0045")|| 
						query.equals("MVSEL0049") || query.equals("MVSEL0050")|| query.equals("MVSEL0051")
						|| query.equals("MVSEL0052")|| query.equals("MVSEL0053")|| query.equals("MVSEL0054")
						|| query.equals("MVSEL0055")|| query.equals("MVSEL0056")|| query.equals("MVSEL0057")
						|| query.equals("MVSEL0058")|| query.equals("MVSEL0059")|| query.equals("MVSEL0060")
						|| query.equals("MVSEL0081") || query.equals("MVSEL0083")) {
					v = inflater.inflate(R.layout.singlerecorddata, null);
					TextView tv_descripcion1 = (TextView) v
							.findViewById(R.id.tv_descripcion1);

					if (tv_descripcion1 != null) {
						tv_descripcion1.setText(recData.getDescripcion());
					}
				}
				else if (query.equals("MVSEL0012") || query.equals("MVSEL0013")) {
					v = inflater.inflate(R.layout.pairrecorddata, null);
					TextView tv_descripcion1 = (TextView) v
							.findViewById(R.id.tv_descripcion1);
					TextView tv_descripcion2 = (TextView) v
							.findViewById(R.id.tv_descripcion2);

					if (tv_descripcion1 != null) {
						tv_descripcion1.setText(recData.getDescripcion());
					}
					if (tv_descripcion2 != null) {
						tv_descripcion2.setText(recData.getDescripcion2());
					}
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
		public Filter getFilter() {
		    Filter filter = new Filter() {

		        @SuppressWarnings("unchecked")
		        @Override
		        protected void publishResults(CharSequence constraint,FilterResults results) {

		            items = (ArrayList<recordsData>) results.values; // tiene los valores filtrados
		            notifyDataSetChanged();  // notifica el cambio de valores
		        }

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
		                constraint = constraint.toString().toLowerCase(locale);
		                for (int i = 0; i < itemsDisplay.size(); i++) {
		                    String data = itemsDisplay.get(i).getDescripcion();
		                    System.out.println("buscando "+constraint.toString()+" en "+data);
		                    if (data.toLowerCase(locale).startsWith(constraint.toString())) {
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

	public String getDescripcion() {
		return descripcion;
	}

	public String getDescripcion2() {
		return descripcion2;
	}

	public String getIdRecord() {
		System.out.println("idRecord "+idRecord);
		return idRecord;
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub
		descripcion = items.get(arg2).getDescripcion();
		descripcion2 = items.get(arg2).getDescripcion2();
		idRecord = items.get(arg2).getId();
        DialogClickEvent event = null;
        		
		event = new DialogClickEvent(this,idRecord,descripcion,b);
        notificando(event);
		dismiss();
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

	private void notificando(DialogClickEvent event) {
		GlobalData.dialogClickListener.dialogClickEvent(event);
	}

	public void addDialogClickListener(DialogClickListener l) {
		System.out.println("se adiciono un oyente");
		GlobalData.dialogClickListener = l;
	}

}
