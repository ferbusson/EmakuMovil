package com.example.emakumovil.modules.ventas;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.emakumovil.Global;
import com.example.emakumovil.MainActivity;
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

    private ImageButton ib_buscar_bus;
    private SearchDataDialog search;
    private EditText et_destino;
    private EditText et_numero_bus;

    private EditText et_descripcion_punto;
    private EditText et_descripcion_bus;

    private String system_user = Global.getSystem_user();
    private TextView tv_origen_value;
    private String id_punto_origen;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        String[] args = {system_user};
        super.onCreate(savedInstanceState);

        // hace que al dar clic en Tiquete se muestre la interfaz de tiquetes
        setContentView(R.layout.activity_tiquete);
        // consulta el punto origen basado en el usuario
        new SearchQuery(this,"MVSEL0082",args).start();
        // boton lupa buscar destino
        ib_buscar_destino = (ImageButton) findViewById(R.id.ib_buscar_destino);
        ib_buscar_bus = (ImageButton) findViewById(R.id.ib_buscar_bus);
        et_destino = (EditText) findViewById(R.id.et_destino);
        et_numero_bus = (EditText) findViewById(R.id.et_numero_bus);
        et_descripcion_punto = (EditText) findViewById(R.id.et_descripcion_punto);
        et_descripcion_bus = (EditText) findViewById(R.id.et_descripcion_bus);
        tv_origen_value = (TextView) findViewById(R.id.tv_origen_value);
        // se agrega listener al boton para ejecutar lo que se ponga en onClick
        ib_buscar_destino.setOnClickListener(this);
        ib_buscar_bus.setOnClickListener(this);

        System.out.println("Usuario: "+ system_user);
    }
    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.ib_buscar_destino) {
            System.out.println("Hice clic en la lupa");
            // se instancia el dialogo para buscar por palabra clave, el titulo se pone al componente
            search = new SearchDataDialog("Buscar destino", "MVSEL0081", null, R.id.ib_buscar_destino);
            // se adiciona listener
            search.addDialogClickListener(this);
            // se hace visible el dialogo, el tag solo es una marca
            search.show(getFragmentManager(), "Buscar punto destino");
        } else if (v.getId() == R.id.ib_buscar_bus) {
            System.out.println("Hice clic en la lupa buscar bus");
            // se instancia el dialogo para buscar por palabra clave, el titulo se pone al componente

            String[] args = {id_punto_origen,et_destino.getText().toString()};
            search = new SearchDataDialog("Buscar Bus", "MVSEL0083", args, R.id.ib_buscar_bus);
            // se adiciona listener
            search.addDialogClickListener(this);
            // se hace visible el dialogo, el tag solo es una marca
            search.show(getFragmentManager(), "Buscar Bus");
        }
    }
    @Override
    public void dialogClickEvent(DialogClickEvent e) {
        System.out.println("tiene que ser aqui0");
        System.out.println("tiene que ser aqui0"+e.getId());
        System.out.println("tiene que ser aqui0"+e.getValue());
        if (e.getIdobject() == R.id.ib_buscar_destino) {
            et_destino.setText((e.getId()));
            et_descripcion_punto.setText(e.getValue());
            et_descripcion_punto.setVisibility(View.VISIBLE);
        } else if (e.getIdobject() == R.id.ib_buscar_bus) {
            et_numero_bus.setText((e.getId()));
            et_descripcion_bus.setText(e.getValue());
            et_descripcion_bus.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onFocusChange(View arg0, boolean arg1) {
        System.out.println("tiene que ser aqui1");
        /*
        System.out.println("Di un enter");
        if (arg0.getId() == R.id.et_destino) {
            String codigo_punto = et_destino.getText().toString();
            new SearchQuery(TiqueteActivity.this, "MVSEL0081", new String[] { codigo_punto }).start();
        }*/
    }

    @Override
    public void arriveAnswerEvent(AnswerEvent e) {
        System.out.println("tiene que ser aqui2");
        Document doc = e.getDocument();
        final Element elm = doc.getRootElement();
        if (e.getSqlCode().equals("MVSEL0082")) {
            System.out.println("en arriveAnswerEvent");
            Iterator<Element> i = elm.getChildren("row").iterator();
            while (i.hasNext()) {
                System.out.println("iterator con datos");
                Element row = (Element) i.next();
                Iterator<Element> j = row.getChildren().iterator();
                id_punto_origen = (String) j.next().getValue();
                final String descripcion_punto = ((Element) j.next()).getValue();
                System.out.println("va el hilo para hacer visibles los et...");
                this.runOnUiThread(new Runnable() {
                    public void run() {
                        tv_origen_value.setText(descripcion_punto);
                        //tv_.setText(codigo_punto);
                    }
                });
            }
        }
    }

    public void onSaveInstanceState(Bundle savedInstanceState) {
        System.out.println("tiene que ser aqui3");
        //savedInstanceState.putString("et_destino", et_destino.getText().toString());
    }

    public void onRestoreInstanceState(Bundle savedInstanceState) {
        System.out.println("tiene que ser aqui4");
    }
    @Override
    public boolean containSqlCode(String sqlCode) {
        return false;
    }
}
