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

public class DetailDayDocumentsActivity extends Activity implements AnswerListener {

	private TextView tv_cajerodd;
	private TextView tv_documentdd;
	private TextView tv_totaldocsdd;
    private SearchQuery squery;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_detail_day_documents);
		Bundle extras = getIntent().getExtras();
		
		tv_cajerodd = (TextView)findViewById(R.id.tv_cajerodd);
		tv_documentdd = (TextView)findViewById(R.id.tv_documentdd);
		tv_totaldocsdd = (TextView)findViewById(R.id.tv_totaldocsdd);
		GlobalData.setLVlistdocs((ListView) findViewById(R.id.lv_listdocsdd));

		if (extras!=null) {
			String codigo_tipo = extras.getString("codigo_tipo");
			ProgressDialogManager.show(this, DetailDayDocumentsActivity.handler,getString(R.string.cargando));
			String id_char = extras.getString("id_char");
			tv_cajerodd.setText(extras.getString("usuario"));
			tv_documentdd.setText(extras.getString("tdocumento"));
			squery = new SearchQuery(this,"MVSEL0009", new String[] {id_char,codigo_tipo}); // consulta movimientos de caja
			squery.start();
		}
	}

	@Override
	public void arriveAnswerEvent(AnswerEvent e) {
		// TODO Auto-generated method stub
		Document doc = e.getDocument();
		if (e.getSqlCode().equals("MVSEL0009")) {
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
					tv_totaldocsdd.setText(df.format(total));
					GlobalData.getLVlistdocs().setAdapter(new ListTextValue(
							DetailDayDocumentsActivity.this, R.layout.infofototextovalor,
							GlobalData.getMyDLlistdocs(),DetailDayDocumentsActivity.this));
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
