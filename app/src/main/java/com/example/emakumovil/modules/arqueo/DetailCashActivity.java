package com.example.emakumovil.modules.arqueo;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import org.jdom2.Document;
import org.jdom2.Element;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.example.emakumovil.R;
import com.example.emakumovil.components.AnswerEvent;
import com.example.emakumovil.components.AnswerListener;
import com.example.emakumovil.components.GlobalData;
import com.example.emakumovil.components.ListTextValue;
import com.example.emakumovil.components.ProgressDialogManager;
import com.example.emakumovil.components.SearchQuery;
import com.example.emakumovil.components.fieldsPhotoTextValue;

public class DetailCashActivity extends Activity implements AnswerListener, OnItemClickListener {

	private TextView tv_totaldocs;
	private TextView tv_totalrec;
	private TextView tv_cajero;
	private TextView tv_fecha;
	private TextView tv_narqueo;
	private String ndocumento;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_detail_cash);
		Bundle extras = getIntent().getExtras();
		tv_totaldocs =(TextView)findViewById(R.id.tv_totaldocs);
		tv_totalrec = (TextView)findViewById(R.id.tv_totalrec);
		tv_cajero = (TextView)findViewById(R.id.tv_cajero);
		tv_fecha = (TextView)findViewById(R.id.tv_fecha);
		tv_narqueo = (TextView)findViewById(R.id.tv_narqueo);
		
		GlobalData.setLVinfodocs((ListView) findViewById(R.id.lv_infodocs));
		GlobalData.setLVinforec((ListView) findViewById(R.id.lv_inforec));
		
		System.out.println("cambiando adaptador");
		ListTextValue list = (ListTextValue)GlobalData.getLVinfodocs().getAdapter();
		if (list!=null) {
			list.notifyDataSetChanged();
			System.out.println("adaptador cambiado");
		}
		
		GlobalData.getLVinfodocs().setOnItemClickListener(this);
		GlobalData.getLVinforec().setOnItemClickListener(this);

		if (extras!=null) {
			ndocumento = extras.getString("ndocumento");
			tv_cajero.setText(extras.getString("usuario"));
			tv_fecha.setText(extras.getString("fecha"));
			tv_narqueo.setText(extras.getString("narqueo"));
			ProgressDialogManager.show(this, DetailCashActivity.handler, getString(R.string.cargando));
			new SearchQuery(this,"MVSEL0002", new String[] {ndocumento}).start(); // consulta movimientos de caja
			new SearchQuery(this,"MVSEL0003", new String[] {ndocumento}).start(); // consulta recaudos de caja

		}
		
		
	}
	
	 protected void onActivityResult(int requestCode, int resultCode,
             Intent data) {
		 System.out.println("que sera esto");
     }
	
	protected void onSaveInstanceState(Bundle frozenState) {
		System.out.println("back");
	}
	

	@Override
	public void arriveAnswerEvent(AnswerEvent e) {
		// TODO Auto-generated method stub
		System.out.println("llego la query " + e.getSqlCode());
		Document doc = e.getDocument();
		if (e.getSqlCode().equals("MVSEL0002")) {
			System.out.println("procesando MVSEL0002");
			final Element rootNode = doc.getRootElement();
			this.runOnUiThread(new Runnable() {
				public void run() {
					List<Element> listRows = rootNode.getChildren("row");
					DecimalFormat df = new DecimalFormat("##,###,###");
					double total = 0;
					GlobalData.getMyDLinfodocs().clear();
					for (int i = 0; i < listRows.size(); i++) {
						Element Erow = (Element) listRows.get(i);
						List<Element> Lcol = Erow.getChildren();
						String codigo_tipo = ((Element) Lcol.get(0)).getText();
						String descripcion = ((Element) Lcol.get(1)).getText();
						String tvalor = ((Element) Lcol.get(2)).getText();
						String valor = df.format(Double
								.parseDouble(tvalor));
						System.out.println("adicionando "+descripcion+" : "+valor);
						total+=Double.parseDouble(tvalor);
						GlobalData.getMyDLinfodocs().add(new fieldsPhotoTextValue(R.drawable.ic_action_documenth,codigo_tipo,descripcion,valor));
					}
					tv_totaldocs.setText(df.format(total));
					ListTextValue listdocs = new ListTextValue(
							DetailCashActivity.this, R.layout.infofototextovalor,
							GlobalData.getMyDLinfodocs(),DetailCashActivity.this);
					listdocs.notifyDataSetChanged();
					System.out.println("adicionando adaptador");
					GlobalData.getLVinfodocs().setAdapter(listdocs);
					System.out.println("adaptador adicionado");
					try {
						ProgressDialogManager.dismissCurrent();
					}
					catch(IllegalArgumentException e) {}
				}
			});
		}
		else if (e.getSqlCode().equals("MVSEL0003")) {
			final Element rootNode = doc.getRootElement();
			this.runOnUiThread(new Runnable() {
				public void run() {
					List<Element> listRows = rootNode.getChildren("row");
					DecimalFormat df = new DecimalFormat("##,###,###");
					double total = 0;
					GlobalData.getMyDLinforec().clear();
					for (int i = 0; i < listRows.size(); i++) {
						Element Erow = (Element) listRows.get(i);
						List<Element> Lcol = Erow.getChildren();
						String descripcion = ((Element) Lcol.get(1)).getText();
						String tvalor = ((Element) Lcol.get(2)).getText();
						String valor = df.format(Double
								.parseDouble(tvalor));
						total+=Double.parseDouble(tvalor);
						System.out.println("adicionando "+descripcion+" : "+valor);
						if (descripcion.equals("Efectivo")) {
							GlobalData.getMyDLinforec().add(new fieldsPhotoTextValue(R.drawable.ic_action_moneyh,"",descripcion,valor));		
						}
						else {
							GlobalData.getMyDLinforec().add(new fieldsPhotoTextValue(R.drawable.ic_action_creditcardh,"",descripcion,valor));
						}
					}
					tv_totalrec.setText(df.format(total));
					ListTextValue listrecaudo = new ListTextValue(
							DetailCashActivity.this, R.layout.infofototextovalor,
							GlobalData.getMyDLinforec(),DetailCashActivity.this);
					listrecaudo.notifyDataSetChanged();
					System.out.println("adicionando adaptador");
					GlobalData.getLVinforec().setAdapter(listrecaudo);
					System.out.println("adaptador adicionado");

				}
			});
			
		}
	}

	@Override
	public boolean containSqlCode(String sqlCode) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub
		 if (arg0.getId()==R.id.lv_infodocs) {
			 ListTextValue list = (ListTextValue)GlobalData.getLVinfodocs().getAdapter();
		   	 ArrayList<fieldsPhotoTextValue> items = list.getItems();
			 Intent i = new Intent(this, DetailDocumentsActivity.class );
		   	 //i.putExtra("ndocumento", ndocumento);
		   	 i.putExtra("usuario",tv_cajero.getText());
		   	 i.putExtra("fecha",tv_fecha.getText());
		   	 i.putExtra("narqueo",tv_narqueo.getText());
		   	 i.putExtra("ndocumento",ndocumento);
		   	 i.putExtra("tdocumento", items.get(arg2).getText());
		   	 i.putExtra("codigo_tipo", items.get(arg2).getCodigo_tipo());
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
	
}
