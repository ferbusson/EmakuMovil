package com.example.emakumovil.modules.ventas;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.emakumovil.R;
import com.example.emakumovil.components.AnswerEvent;
import com.example.emakumovil.components.AnswerListener;
import com.example.emakumovil.components.DialogClickEvent;
import com.example.emakumovil.components.DialogClickListener;
import com.example.emakumovil.components.SearchDataDialog;
import com.example.emakumovil.components.SearchQuery;

import org.jdom2.Document;
import org.jdom2.Element;

import java.util.Iterator;

public class TiqueteActivity extends Activity implements View.OnClickListener, DialogClickListener, AnswerListener, View.OnFocusChangeListener {

    private ImageButton ib_buscar_destino;

    private String id_buscar_destino;

    private SearchDataDialog search;

    private EditText et_codigo_destino;

    private EditText et_codigo_punto;
    private EditText et_descripcion_punto;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tiquete);
        ib_buscar_destino = (ImageButton) findViewById(R.id.ib_buscar_destino);
        ib_buscar_destino.setOnClickListener(this);

    }
    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        if (v.getId() == R.id.ib_buscar_destino) {
            search = new SearchDataDialog("Buscar destino","MVSEL0081",null,R.id.ib_buscar_destino);
            search.addDialogClickListener(this);
            search.show(getFragmentManager(),"Buscar Destino");
        }
        else {
                Toast.makeText(this, R.string.error_id_char, Toast.LENGTH_LONG).show();
            }
        }

    @Override
    public void dialogClickEvent(DialogClickEvent e) {
        // TODO Auto-generated method stub
        if (e.getIdobject() == R.id.ib_buscar_destino) {
            if(e.getValue() != null){
                et_codigo_destino.setText(e.getValue());
                id_buscar_destino = e.getId();
                System.out.println("et_codigo_destino" + et_codigo_destino);
                System.out.println("id_buscar_destino" + id_buscar_destino);
                new SearchQuery(TiqueteActivity.this,"MVSEL0081",new String[]{id_buscar_destino}).start();
            }
        }
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {

    }

    @Override
    public void arriveAnswerEvent(AnswerEvent e) {
        Document doc = e.getDocument();
        final Element elm = doc.getRootElement();
        if (e.getSqlCode().equals("MVSEL0081")) {
            Iterator<Element> i = elm.getChildren("row").iterator();
            while (i.hasNext()) {
                System.out.println("iterator con datos");
                Element row = (Element) i.next();
                Iterator<Element> j = row.getChildren().iterator();

                final String codigo_punto = ((Element) j.next()).getValue();
                final String descripcion_punto = ((Element) j.next()).getValue();

                this.runOnUiThread(new Runnable() {
                    public void run() {
                        et_codigo_punto.setText(codigo_punto);
                        et_descripcion_punto.setText(descripcion_punto);

                        et_codigo_punto.setVisibility(View.VISIBLE);
                        et_descripcion_punto.setVisibility(View.VISIBLE);
                    }
                });
            }
        }
    }

    @Override
    public boolean containSqlCode(String sqlCode) {
        return false;
    }
}
