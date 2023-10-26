package com.example.emakumovil.modules.ventas;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.Spanned;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.emakumovil.R;
import com.example.emakumovil.components.AnswerEvent;
import com.example.emakumovil.components.AnswerListener;
import com.example.emakumovil.components.DialogClickEvent;
import com.example.emakumovil.components.DialogClickListener;
import com.example.emakumovil.components.SearchDataDialog;
import com.example.emakumovil.components.SearchQuery;
import com.example.emakumovil.components.SelectedDataDialog;
import com.example.emakumovil.modules.terceros.PersonsActivity;

public class TikectActivity extends Activity implements View.OnClickListener, DialogClickListener, AnswerListener, View.OnFocusChangeListener {

    private ImageButton ib_buscar_destino;

    private String id_buscar_origen;

    private SearchDataDialog search;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sell_boarding_pass);
        ib_buscar_destino = findViewById(R.id.ib_buscar_destino);
        ib_buscar_destino.setOnClickListener(this);

    }
    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        if (v.getId() == R.id.ib_buscar_destino) {
            search = new SearchDataDialog("Buscar destino","MVSEL0061",null,R.id.ib_buscar_destino);
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
            //bt_tipo_tercero.setText(e.getValue());
            //id_tipo_tercero = e.getId();


        }
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {

    }

    @Override
    public void arriveAnswerEvent(AnswerEvent e) {

    }

    @Override
    public boolean containSqlCode(String sqlCode) {
        return false;
    }
}
