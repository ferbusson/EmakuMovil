package com.example.emakumovil.modules.ventas;

import java.nio.channels.SocketChannel;
import java.util.Iterator;

import org.jdom2.Document;
import org.jdom2.Element;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.example.emakumovil.R;
import com.example.emakumovil.components.AnswerEvent;
import com.example.emakumovil.components.AnswerListener;
import com.example.emakumovil.components.SearchQuery;
import com.example.emakumovil.comunications.SocketConnector;
import com.example.emakumovil.comunications.SocketWriter;

public class TRMActivity extends Activity implements AnswerListener,OnClickListener {

	private Button bt_actualizar;
	private EditText et_trm;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_trm);
		bt_actualizar = (Button)findViewById(R.id.bt_actualizar);
		bt_actualizar.setOnClickListener(this);
		et_trm = (EditText)findViewById(R.id.et_trm);
		new SearchQuery(this,"MVSEL0077").start(); // consulta la trm actual
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.trm, menu);
		return true;
	}

	@Override
	public void arriveAnswerEvent(AnswerEvent e) {
		// TODO Auto-generated method stub
		Document doc = e.getDocument();
		final Element elm = doc.getRootElement();
		 if (e.getSqlCode().equals("MVSEL0077")) {
				Iterator<Element> i = elm.getChildren("row").iterator();
				while (i.hasNext()) {
					System.out.println("iterator con datos");
					Element row = (Element) i.next();
					Iterator<Element> j = row.getChildren().iterator();
					final String strm = ((Element) j.next()).getValue();
					this.runOnUiThread(new Runnable() {
						public void run() {
							et_trm.setText(strm);
						}
					});
				}
		}		
	}

	@Override
	public boolean containSqlCode(String sqlCode) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if (v.getId()==R.id.bt_actualizar) {
			sendTransaction();
		}
	}

	private void sendTransaction() {
		Document transaction = new Document();
		Element raiz = new Element("TRANSACTION");
		Element driver = new Element("driver");
		driver.setText("MVTR00018");
		Element id = new Element("id");
		id.setText("TMV5");
		transaction.setRootElement(raiz);
		raiz.addContent(driver);
		raiz.addContent(id);
		
		/*
		 * TRM
		 */
		
		Element etrm = new Element("package");
		Element ftrm = new Element("field").setText(et_trm.getText().toString());
		etrm.addContent(ftrm);
		raiz.addContent(etrm);

		SocketChannel socket = SocketConnector.getSock();
		Log.d("EMAKU", "EMAKU: Socket: " + socket);
		SocketWriter.writing(socket, transaction);

	}
}
