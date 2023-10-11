package com.example.emakumovil.modules.arqueo;

import java.text.DecimalFormat;
import java.util.List;

import org.jdom2.Document;
import org.jdom2.Element;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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

public class DetailDocumentsActivity extends Activity implements AnswerListener {

	private TextView tv_cajero;
	private TextView tv_fecha;
	private TextView tv_narqueo;
	private TextView tv_document;
	private TextView tv_totaldocs;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_detail_documents);
		Bundle extras = getIntent().getExtras();
		tv_cajero = (TextView)findViewById(R.id.tv_cajero);
		tv_fecha = (TextView)findViewById(R.id.tv_fecha);
		tv_narqueo = (TextView)findViewById(R.id.tv_narqueo);
		tv_document = (TextView)findViewById(R.id.tv_document);
		tv_totaldocs = (TextView)findViewById(R.id.tv_totaldocs);
		GlobalData.setLVlistdocs((ListView) findViewById(R.id.lv_listdocs));

		if (extras!=null) {
			String codigo_tipo = extras.getString("codigo_tipo");
			ProgressDialogManager.show(this, DetailDocumentsActivity.handler,getString(R.string.cargando));
			String ndocumento = extras.getString("ndocumento");
			tv_cajero.setText(extras.getString("usuario"));
			tv_fecha.setText(extras.getString("fecha"));
			tv_narqueo.setText(extras.getString("narqueo"));
			tv_document.setText(extras.getString("tdocumento"));
			new SearchQuery(this,"MVSEL0004", new String[] {ndocumento,codigo_tipo}).start(); // consulta movimientos de caja
		}
	}


	@Override
	public void arriveAnswerEvent(AnswerEvent e) {
		// TODO Auto-generated method stub
		System.out.println("llego la query " + e.getSqlCode());
		Document doc = e.getDocument();
		if (e.getSqlCode().equals("MVSEL0004")) {
			System.out.println("procesando MVSEL0004");
			final Element rootNode = doc.getRootElement();
			this.runOnUiThread(new Runnable() {
				public void run() {
					List<Element> listRows = rootNode.getChildren("row");
					DecimalFormat df = new DecimalFormat("##,###,###");
					double total = 0;
					GlobalData.getMyDLlistdocs().clear();
					for (int i = 0; i < listRows.size(); i++) {
						Element Erow = (Element) listRows.get(i);
						List<Element> Lcol = Erow.getChildren();
						String descripcion = ((Element) Lcol.get(0)).getText();
						String tvalor = ((Element) Lcol.get(1)).getText();
						String valor = df.format(Double
								.parseDouble(tvalor));
						System.out.println("adicionando "+descripcion+" : "+valor);
						total+=Double.parseDouble(tvalor);
						GlobalData.getMyDLlistdocs().add(new fieldsPhotoTextValue(R.drawable.ic_action_documenth,"",descripcion,valor));
					}
					tv_totaldocs.setText(df.format(total));
					GlobalData.getLVlistdocs().setAdapter(new ListTextValue(
							DetailDocumentsActivity.this, R.layout.infofototextovalor,
							GlobalData.getMyDLlistdocs(),DetailDocumentsActivity.this));
					try {
						ProgressDialogManager.dismissCurrent();
					}
					catch(IllegalArgumentException e) {}
				}
			});
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


}
