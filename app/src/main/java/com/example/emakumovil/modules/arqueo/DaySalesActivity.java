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

public class DaySalesActivity extends Activity implements AnswerListener, OnItemClickListener {

	private TextView tv_totaldocsdia;
	private TextView tv_totalrecdia;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_day_sales);
		tv_totaldocsdia = (TextView)findViewById(R.id.tv_totaldocsdia);
		tv_totalrecdia = (TextView)findViewById(R.id.tv_totalrecdia);
		GlobalData.setLVlistdia((ListView) findViewById(R.id.lv_ventasdia));
		GlobalData.setLVlistrecdia((ListView) findViewById(R.id.lv_inforecdia));

		ListTextValue list = (ListTextValue)GlobalData.getLVlistdia().getAdapter();
		if (list!=null) {
			list.notifyDataSetChanged();
		}

		GlobalData.getLVlistdia().setOnItemClickListener(this);
		ProgressDialogManager.show(this, DaySalesActivity.handler,getString(R.string.cargando));
		new SearchQuery(this,"MVSEL0005").start(); // consulta movimientos del dia
		new SearchQuery(this,"MVSEL0006").start(); // consulta recaudos del dia
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub
		 if (arg0.getId()==R.id.lv_ventasdia) {
			 ListTextValue list = (ListTextValue)GlobalData.getLVlistdia().getAdapter();
		   	 ArrayList<fieldsPhotoTextValue> items = list.getItems();
			 Intent i = new Intent(this, UserDayActivity.class );
			 i.putExtra("id_char",items.get(arg2).getCodigo_tipo());
		   	 i.putExtra("usuario",items.get(arg2).getText());
		     this.startActivity(i);
		}

	}

	@Override
	public void arriveAnswerEvent(AnswerEvent e) {
		// TODO Auto-generated method stub
		System.out.println("llego la query " + e.getSqlCode());
		Document doc = e.getDocument();
		if (e.getSqlCode().equals("MVSEL0005")) {
			System.out.println("procesando MVSEL0005");
			final Element rootNode = doc.getRootElement();
			this.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					List<Element> listRows = rootNode.getChildren("row");
					DecimalFormat df = new DecimalFormat("##,###,###");
					double total = 0;
					GlobalData.getMyDLinfodia().clear();
					for (int i = 0; i < listRows.size(); i++) {
						Element Erow = listRows.get(i);
						List<Element> Lcol = Erow.getChildren();
						String id_char = Lcol.get(0).getText();
						String descripcion = Lcol.get(1).getText();
						String tvalor = Lcol.get(2).getText();
						String valor = df.format(Double
								.parseDouble(tvalor));
						System.out.println("adicionando "+descripcion+" : "+valor);
						total+=Double.parseDouble(tvalor);
						GlobalData.getMyDLinfodia().add(new fieldsPhotoTextValue(R.drawable.ic_action_user,id_char,descripcion,valor));
					}
					tv_totaldocsdia.setText(df.format(total));
					ListTextValue listdocsdia = new ListTextValue(
							DaySalesActivity.this, R.layout.infofototextovalor,
							GlobalData.getMyDLinfodia(),DaySalesActivity.this);
					listdocsdia.notifyDataSetChanged();
					System.out.println("adicionando adaptador");
					GlobalData.getLVlistdia().setAdapter(listdocsdia);
					System.out.println("adaptador adicionado");
					try {
						ProgressDialogManager.dismissCurrent();
					}
					catch(IllegalArgumentException e){}
				}
			});
		}
		else if (e.getSqlCode().equals("MVSEL0006")) {
			final Element rootNode = doc.getRootElement();
			this.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					List<Element> listRows = rootNode.getChildren("row");
					DecimalFormat df = new DecimalFormat("##,###,###");
					double total = 0;
					GlobalData.getMyDLinforecdia().clear();
					for (int i = 0; i < listRows.size(); i++) {
						Element Erow = listRows.get(i);
						List<Element> Lcol = Erow.getChildren();
						String descripcion = Lcol.get(1).getText();
						String tvalor = Lcol.get(2).getText();
						String valor = df.format(Double
								.parseDouble(tvalor));
						total+=Double.parseDouble(tvalor);
						System.out.println("adicionando "+descripcion+" : "+valor);
						if (descripcion.equals("Efectivo")){
							GlobalData.getMyDLinforecdia().add(new fieldsPhotoTextValue(R.drawable.ic_action_moneyh,"",descripcion,valor));
						}
						else {
							GlobalData.getMyDLinforecdia().add(new fieldsPhotoTextValue(R.drawable.ic_action_creditcardh,"",descripcion,valor));
						}
					}
					tv_totalrecdia.setText(df.format(total));
					ListTextValue listrecaudodia = new ListTextValue(
							DaySalesActivity.this, R.layout.infofototextovalor,
							GlobalData.getMyDLinforecdia(),DaySalesActivity.this);
					listrecaudodia.notifyDataSetChanged();
					System.out.println("adicionando adaptador");
					GlobalData.getLVlistrecdia().setAdapter(listrecaudodia);
					System.out.println("adaptador adicionado");

				}
			});

		}

	}

	 @Override
	protected void onActivityResult(int requestCode, int resultCode,
             Intent data) {
		 System.out.println("que sera esto");
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


}
