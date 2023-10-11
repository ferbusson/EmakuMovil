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

public class UserDayActivity extends Activity implements AnswerListener, OnItemClickListener {

	private String id_char;
	private TextView tv_totaldocsu;
	private TextView tv_totalrecu;
	private TextView tv_cajerou;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_user_day);
		Bundle extras = getIntent().getExtras();
		tv_totaldocsu =(TextView)findViewById(R.id.tv_totaldocsu);
		tv_totalrecu = (TextView)findViewById(R.id.tv_totalrecu);
		tv_cajerou = (TextView)findViewById(R.id.tv_dcajero);
		
		GlobalData.setLVlistdocsu((ListView) findViewById(R.id.lv_infodocsu));
		GlobalData.setLVinforecu((ListView) findViewById(R.id.lv_inforecu));
		GlobalData.getLVlistdocsu().setOnItemClickListener(this);
		
		ListTextValue list = (ListTextValue)GlobalData.getLVlistdocsu().getAdapter();
		if (list!=null) {
			list.notifyDataSetChanged();
		}

		if (extras!=null) {
			id_char = extras.getString("id_char");
			tv_cajerou.setText(extras.getString("usuario"));
			ProgressDialogManager.show(this, UserDayActivity.handler,getString(R.string.cargando));
			new SearchQuery(this,"MVSEL0007", new String[] {id_char}).start(); // consulta movimientos del dia por cajero
			new SearchQuery(this,"MVSEL0008", new String[] {id_char}).start(); // consulta recaudos del dia por cajero

		}

	}

	
	@Override
	public void arriveAnswerEvent(AnswerEvent e) {
		// TODO Auto-generated method stub
		System.out.println("llego la query " + e.getSqlCode());
		Document doc = e.getDocument();
		if (e.getSqlCode().equals("MVSEL0007")) {
			System.out.println("procesando MVSEL0007");
			final Element rootNode = doc.getRootElement();
			this.runOnUiThread(new Runnable() {
				public void run() {
					List<Element> listRows = rootNode.getChildren("row");
					DecimalFormat df = new DecimalFormat("##,###,###");
					double total = 0;
					GlobalData.getMyDLinfodocsu().clear();
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
						GlobalData.getMyDLinfodocsu().add(new fieldsPhotoTextValue(R.drawable.ic_action_documenth,codigo_tipo,descripcion,valor));
					}
					tv_totaldocsu.setText(df.format(total));
					ListTextValue listdocsu = new ListTextValue(
							UserDayActivity.this, R.layout.infofototextovalor,
							GlobalData.getMyDLinfodocsu(),UserDayActivity.this);
					listdocsu.notifyDataSetChanged();
					System.out.println("adicionando adaptador");
					GlobalData.getLVlistdocsu().setAdapter(listdocsu);
					System.out.println("adaptador adicionado");
					try {
						ProgressDialogManager.dismissCurrent();
					}
					catch(IllegalArgumentException e) {}
				}
			});
		}
		else if (e.getSqlCode().equals("MVSEL0008")) {
			final Element rootNode = doc.getRootElement();
			this.runOnUiThread(new Runnable() {
				public void run() {
					List<Element> listRows = rootNode.getChildren("row");
					DecimalFormat df = new DecimalFormat("##,###,###");
					double total = 0;
					GlobalData.getMyDLinforecdia().clear();
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
							GlobalData.getMyDLinforecdia().add(new fieldsPhotoTextValue(R.drawable.ic_action_moneyh,"",descripcion,valor));		
						}
						else {
							GlobalData.getMyDLinforecdia().add(new fieldsPhotoTextValue(R.drawable.ic_action_creditcardh,"",descripcion,valor));
						}
					}
					tv_totalrecu.setText(df.format(total));
					ListTextValue listrecaudou = new ListTextValue(
							UserDayActivity.this, R.layout.infofototextovalor,
							GlobalData.getMyDLinforecdia(),UserDayActivity.this);
					listrecaudou.notifyDataSetChanged();
					System.out.println("adicionando adaptador");
					GlobalData.getLVinforecu().setAdapter(listrecaudou);
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
		 if (arg0.getId()==R.id.lv_infodocsu) {
			 ListTextValue list = (ListTextValue)GlobalData.getLVlistdocsu().getAdapter();
		   	 ArrayList<fieldsPhotoTextValue> items = list.getItems();
			 Intent i = new Intent(this, DetailDayDocumentsActivity.class );
		   	 i.putExtra("usuario",tv_cajerou.getText());
		   	 i.putExtra("id_char", id_char);
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
