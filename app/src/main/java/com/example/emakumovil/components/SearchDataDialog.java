package com.example.emakumovil.components;


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
import android.content.DialogInterface.OnDismissListener;
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
import android.widget.Toast;

import com.example.emakumovil.R;

@SuppressLint("ValidFragment")
public class SearchDataDialog extends DialogFragment implements OnClickListener, OnItemClickListener, AnswerListener,OnDismissListener {

	private TextView tv_titulo;
	private ImageButton ib_search;
	private EditText et_query;
	private ArrayList<recordsData> items = new ArrayList<recordsData>();
	private LayoutInflater inflater;
	private ListView LVrecords;
	private String descripcion;
	private String idRecord;
	private LinearLayout progressLayout;
	private long b;
	private String query;
	
	public SearchDataDialog(String titulo,String query, String args[],long b) {
		Bundle bundle = new Bundle();
		bundle.putString("titulo",titulo);
		bundle.putString("query",query);
		setArguments(bundle);
		this.b=b;
	}

	public SearchDataDialog(String titulo,String query, String args[]) {
		Bundle bundle = new Bundle();
		bundle.putString("titulo",titulo);
		bundle.putString("query",query);
		setArguments(bundle);
	}
	
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
	    inflater = getActivity().getLayoutInflater();
	    Bundle args = getArguments();
	    View v = inflater.inflate(R.layout.search_data, null);
	    builder.setView(v)
	           .setNegativeButton(R.string.cancelar, new DialogInterface.OnClickListener() {
	               public void onClick(DialogInterface dialog, int id) {
	               }
	           });      
	    
	    if (args!=null) {
	    	String titulo = args.getString("titulo");
	    	query = args.getString("query");
	    	tv_titulo = (TextView)v.findViewById(R.id.tv_titulo);
	    	tv_titulo.setText(titulo);
	    	ib_search = (ImageButton)v.findViewById(R.id.ib_search);
	    	ib_search.setOnClickListener(this);
	    	et_query = (EditText)v.findViewById(R.id.et_query);
	    	progressLayout = (LinearLayout)v.findViewById(R.id.progressLayout);
			getActivity().runOnUiThread(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					progressLayout.setVisibility(View.GONE);	
					
				}
			});
	    	InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
	        imm.showSoftInput(et_query, InputMethodManager.SHOW_FORCED);
	    	items.clear();
			LVrecords = (ListView) v.findViewById(R.id.lv_records);
			LVrecords.setAdapter(new RecordsDataAdapter(
					getActivity(), R.layout.recordsearch,
					items));
			LVrecords.setOnItemClickListener(this);
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
		System.out.println("generando query");
		String arg = et_query.getText().toString();
		if (arg.length()>3) {
			new SearchQuery(SearchDataDialog.this,query, new String[] {arg}).start();
			getActivity().runOnUiThread(new Runnable() {
	
				@Override
				public void run() {
					// TODO Auto-generated method stub
					progressLayout.setVisibility(View.VISIBLE);	
					
				}
			});
		}
		else {
			Toast.makeText(this.getActivity(), R.string.error_minchar, Toast.LENGTH_LONG).show();
		}
	}

	@Override
	public void arriveAnswerEvent(AnswerEvent e) {
		// TODO Auto-generated method stub
		Document doc = e.getDocument();
		System.out.println("llego una query");
		if (e.getSqlCode().equals("MVSEL0010") || e.getSqlCode().equals("MVSEL0035") || e.getSqlCode().equals("MVSEL0048")
				|| query.equals("MVSEL0061") ) {
			final Element rootNode = doc.getRootElement();
			getActivity().runOnUiThread(new Runnable() {
				public void run() {
					List<Element> listRows = rootNode.getChildren("row");
					items.clear();
					for (int i = 0; i < listRows.size(); i++) {
						Element Erow = (Element) listRows.get(i);
						List<Element> Lcol = Erow.getChildren();
						String id = ((Element) Lcol.get(0)).getText(); // id
						String codigo = ((Element) Lcol.get(1)).getText(); // nombres
						String descripcion = ((Element) Lcol.get(2)).getText(); // id_char
						String descripcion2 = ((Element) Lcol.get(3)).getText(); // direccion
						String descripcion3 = ((Element) Lcol.get(4)).getText(); // telefono
						String id2 = ((Element) Lcol.get(5)).getText(); // id_direccion
						String id3 = ((Element) Lcol.get(6)).getText(); // id_telefono
						items.add(new recordsData(id,codigo,descripcion,descripcion2,descripcion3,id2,id3));
					}
					LVrecords.setAdapter(new RecordsDataAdapter(
							getActivity(), R.layout.recordsearch,
							items));
				}
			});
		} else if (e.getSqlCode().equals("MVSEL0081")) { // consulta punto destino
			final Element rootNode = doc.getRootElement();
			getActivity().runOnUiThread(new Runnable() {
				public void run() {
					List<Element> listRows = rootNode.getChildren("row");
					items.clear();
					for (int i = 0; i < listRows.size(); i++) {
						Element Erow = (Element) listRows.get(i);
						List<Element> Lcol = Erow.getChildren();
						String codigo = ((Element) Lcol.get(0)).getText(); // codigo punto
						String descripcion = ((Element) Lcol.get(1)).getText(); // descripcion punto

						items.add(new recordsData(codigo,descripcion));
					}
					LVrecords.setAdapter(new RecordsDataAdapter(
							getActivity(), R.layout.recordsearch,
							items));
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

	private class RecordsDataAdapter extends ArrayAdapter<recordsData> {


		public RecordsDataAdapter(Context context, int textViewResourceId,
				ArrayList<recordsData> items) {
			super(context, textViewResourceId, items);
			SearchDataDialog.this.items = items;
			Log.d("EMAKU ","EMAKU instanciando adaptador");
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View v = convertView;

			recordsData recData = items.get(position);
			if (recData != null) {
				if (query.equals("MVSEL0010") || query.equals("MVSEL0035") || query.equals("MVSEL0048")
						|| query.equals("MVSEL0061")  || query.equals("MVSEL0081") ) {
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

	public String getDescripcion() {
		return descripcion;
	}
	
	public String getIdRecord() {
		return idRecord;
	}


	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub
		System.out.println("onItemClick");
		idRecord = items.get(arg2).getId();
		descripcion = items.get(arg2).getDescripcion();

		System.out.println("descripcion: " + descripcion);
		System.out.println("idRecord" + idRecord);

        DialogClickEvent event = new DialogClickEvent(this,idRecord,descripcion,b);
        notificando(event);

		dismiss();
	}

	private void notificando(DialogClickEvent event) {
		GlobalData.dialogClickListener.dialogClickEvent(event);
	}

	public void addDialogClickListener(DialogClickListener l) {
		System.out.println("se adiciono un oyente");
		GlobalData.dialogClickListener = l;
	}

}
