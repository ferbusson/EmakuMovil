package com.example.emakumovil.components;

import java.util.ArrayList;
import java.util.Vector;

import org.jdom2.Document;

import android.util.Log;

import com.example.emakumovil.transactions.TransactionServerException;
import com.example.emakumovil.transactions.TransactionServerResultSet;

public class SearchQuery extends Thread {

	private Vector<AnswerListener> answerListener = new Vector<AnswerListener>();
	private String sqlCode;
	private String[] args;

	// este constructor genera la query solo con la cadena args que se digita en el componente
	public SearchQuery(AnswerListener listener,String sqlCode,String[] args) {
		this.sqlCode=sqlCode;
		this.args=args;
		this.addAnswerListener(listener);
	}

	// este constructor genera la query con la lista de argumentos que lleguen el args
	public SearchQuery(AnswerListener listener, String sqlCode, ArrayList<String> args) {
		this.sqlCode=sqlCode;
		String[] str = new String[args.size()];
		for(int i = 0; i < args.size(); i++){
			str[i] = args.get(i);
		}
		this.args=str;
		this.addAnswerListener(listener);
	}
	public SearchQuery(AnswerListener listener,String sqlCode) {
		this.sqlCode=sqlCode;
		this.addAnswerListener(listener);
	}
	
	public void run() {
		Document doc = null;
		try {
			if (args!=null) {
				doc = TransactionServerResultSet.getResultSetST(sqlCode,args);// new String[]{XMLTField.getText()});
			}
			else {
				doc = TransactionServerResultSet.getResultSetST(sqlCode);
			}
			Log.d("EMAKU", "EMAKU Query desde Searching..");
		} catch (TransactionServerException e) {
			System.out.println("tiempo expirado..");
			e.printStackTrace();
		}
		AnswerEvent event = new AnswerEvent(this, sqlCode, doc);
		notificando(event);
	}
	
	private void notificando(AnswerEvent event) {
		for (AnswerListener l : answerListener) {
			System.out.println("Notificando a =>" + l);
			l.arriveAnswerEvent(event);
		}
	}
	
	public void addAnswerListener(AnswerListener listener) {
		answerListener.addElement(listener);
	}

	public void removeAnswerListener(AnswerListener listener) {
		answerListener.removeElement(listener);
	}


}
