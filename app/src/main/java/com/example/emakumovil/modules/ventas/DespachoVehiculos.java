package com.example.emakumovil.modules.ventas;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TextView;

import com.example.emakumovil.R;
import com.example.emakumovil.components.AnswerEvent;
import com.example.emakumovil.components.AnswerListener;
import com.example.emakumovil.components.DialogClickEvent;
import com.example.emakumovil.components.DialogClickListener;
import com.example.emakumovil.components.SearchQuery;

public class DespachoVehiculos extends Activity implements View.OnClickListener, DialogClickListener,
        AnswerListener, View.OnFocusChangeListener {

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // hace que al dar clic en menu Tiquete se muestre la interfaz de tiquetes
        setContentView(R.layout.activity_despacho_vahiculo);
        // consulta el punto origen basado en el usuario
        //new SearchQuery(this,"MVSEL0082",args).start();
        // boton lupa buscar destino


    }

    @Override
    public void onClick(View v) {

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

    @Override
    public void dialogClickEvent(DialogClickEvent e) {

    }
}
