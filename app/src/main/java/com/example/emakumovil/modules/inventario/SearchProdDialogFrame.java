package com.example.emakumovil.modules.inventario;

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
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

import com.example.emakumovil.R;
import com.example.emakumovil.components.AnswerEvent;
import com.example.emakumovil.components.AnswerListener;
import com.example.emakumovil.components.FieldsDDV;
import com.example.emakumovil.components.GlobalData;
import com.example.emakumovil.components.SearchQuery;

@SuppressLint("ValidFragment")
public class SearchProdDialogFrame extends DialogFragment implements OnClickListener, AnswerListener, OnItemClickListener {

	private LayoutInflater inflater;
	private TextView tv_titulo;
	private EditText et_query;
	private ImageButton ib_buscar;
	private DataAdapterProd adapter;
	private ListView lv_sprod;
	private ProgressBar progress1;
	private String barra;
	
	public SearchProdDialogFrame(String titulo,String query) {
		Bundle bundle = new Bundle();
		bundle.putString("titulo",titulo);
		bundle.putString("query",query);
		setArguments(bundle);
	}
	
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
	    inflater = getActivity().getLayoutInflater();
	    Bundle bundle = getArguments();
	    View v = inflater.inflate(R.layout.activity_search_data_dialog, null);
	    builder.setView(v)
        .setNegativeButton(R.string.cancelar, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
            }
        });      
	    if (bundle!=null) {
	    	String titulo = bundle.getString("titulo");
	    	tv_titulo = (TextView)v.findViewById(R.id.tv_titulo);
	    	tv_titulo.setText(titulo);
	    }
	    
    	et_query = (EditText)v.findViewById(R.id.et_query);
    	lv_sprod = (ListView)v.findViewById(R.id.lv_sprod);
    	lv_sprod.setOnItemClickListener(this);
    	ib_buscar = (ImageButton)v.findViewById(R.id.ib_buscar);
    	progress1 = (ProgressBar)v.findViewById(R.id.progressBar1);
		progress1.setVisibility(View.INVISIBLE);
    	ib_buscar.setOnClickListener(this);
    	GlobalData.items_sprod.clear();
		adapter = new DataAdapterProd(
		getActivity(), R.layout.sprodrecorddata,
		GlobalData.items_sprod);
		lv_sprod.setAdapter(adapter);
		adapter.notifyDataSetChanged();
	    Dialog d = builder.create();
	    return d;

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		new SearchQuery(SearchProdDialogFrame.this,"MVSEL0033",new String[]{et_query.getText().toString()}).start(); 
		GlobalData.items_sprod.clear();
		adapter.notifyDataSetChanged();
		progress1.setVisibility(View.VISIBLE);

	}

	@Override
	public void arriveAnswerEvent(AnswerEvent e) {
		// TODO Auto-generated method stub
		Document doc = e.getDocument();
		final Element elm = doc.getRootElement();
		if (e.getSqlCode()=="MVSEL0033") {
			getActivity().runOnUiThread(new Runnable() {
				public void run() {
					progress1.setVisibility(View.GONE);
					List<Element> listRows = elm.getChildren("row");
					System.out.println("cargando kardex");
					for (int i = 0; i < listRows.size(); i++) {
						Element Erow = (Element) listRows.get(i);
						List<Element> Lcol = Erow.getChildren();
						String descripcion1 = ((Element) Lcol.get(0)).getText();
						String descripcion2 = ((Element) Lcol.get(1)).getText();
						GlobalData.items_sprod.add(new FieldsDDV(descripcion1,descripcion2,""));
					}
					adapter.notifyDataSetChanged();
				}
			});
		}
	}

	@Override
	public boolean containSqlCode(String sqlCode) {
		// TODO Auto-generated method stub
		return false;
	}
	
	private class DataAdapterProd extends ArrayAdapter<FieldsDDV> {

		private LayoutInflater vi;

		public DataAdapterProd(Context context, int textViewResourceId,
				ArrayList<FieldsDDV> items) {
			super(context, textViewResourceId, items);
			GlobalData.items_sprod = items;
			Log.d("EMAKU ","EMAKU instanciando adaptador");
			vi = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View v = convertView;
			ViewHolderProd holder;
			
			if (v == null) {
				v = vi.inflate(R.layout.sprodrecorddata, null);
				TextView tv_descripcion1 = (TextView) v
						.findViewById(R.id.tv_descripcion1);
				TextView tv_descripcion2 = (TextView) v
						.findViewById(R.id.tv_descripcion2);
				
				holder = new ViewHolderProd(tv_descripcion1,
						tv_descripcion2);
				
				v.setTag(holder);
			}
			else {
				holder = (ViewHolderProd)v.getTag();
			}

			
			FieldsDDV myData = GlobalData.items_sprod.get(position);
			if (myData != null) {
				if (holder.tv_descripcion1 != null) {
					holder.tv_descripcion1.setText(myData.getDescripcion1());
				}
				if (holder.tv_descripcion2 != null) {
					holder.tv_descripcion2.setText(myData.getDescripcion2());
				}
			}
			return v;
		}

	}


	private class ViewHolderProd {
 		public TextView tv_descripcion1;
 		public TextView tv_descripcion2;
 		
 		public ViewHolderProd(TextView tv_descripcion1,TextView tv_descripcion2) {
 			this.tv_descripcion1=tv_descripcion1;
 			this.tv_descripcion2=tv_descripcion2;
 		}
 		
 	}

	public String getBarra() {
		return barra;
	}
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub
		barra = GlobalData.items_sprod.get(arg2).getDescripcion1();
		System.out.println("barra: "+barra);
		dismiss();
	}

}
