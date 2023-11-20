package com.example.emakumovil.modules.ventas;

import static androidx.core.content.ContextCompat.startActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TableLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.emakumovil.R;
import com.example.emakumovil.components.AnswerEvent;
import com.example.emakumovil.components.AnswerListener;
import com.example.emakumovil.components.DialogClickEvent;
import com.example.emakumovil.components.DialogClickListener;
import com.example.emakumovil.components.SearchQuery;

import org.jdom2.Document;
import org.jdom2.Element;

import java.util.Iterator;

public class FormaPagoTiquete extends Activity implements View.OnClickListener, DialogClickListener,
        AnswerListener, View.OnFocusChangeListener {

    private TextView tv_detalle_origen;
    private TextView tv_detalle_destino;
    private TextView tv_detalles_bus;
    private TextView tv_total_compra;
    private EditText et_numero_id_cliente;
    private EditText editTextName;
    private EditText editTextLastName;

    private EditText editTextAddress;
    private EditText editTextPhone;
    private EditText editTextEmail;
    private String id;
    private String id_char;
    private String nombre1;
    private String nombre2;
    private String apellido1;
    private String apellido2;
    private String razon_social;
    private String id_direccion;
    private String departamento;
    private String ciudad;
    private String direccion;
    private String id_telefono;
    private String numero;
    private String id_email;
    private String email;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forma_de_pago_tiquete);
        tv_detalle_origen = (TextView) findViewById(R.id.detalle_origen);
        tv_detalle_destino = (TextView) findViewById(R.id.detalle_destino);
        tv_detalles_bus = (TextView) findViewById(R.id.tv_detalles_bus);
        tv_total_compra = (TextView) findViewById(R.id.tv_total_compra);
        et_numero_id_cliente = (EditText) findViewById(R.id.et_numero_id_cliente);
        editTextName = (EditText) findViewById(R.id.editTextName);
        editTextLastName = (EditText) findViewById(R.id.editTextLastName);
        editTextAddress = (EditText) findViewById(R.id.editTextAddress);
        editTextPhone = (EditText) findViewById(R.id.editTextPhone);
        editTextEmail = (EditText) findViewById(R.id.editTextEmail);
        // Inside ActivityB in onCreate or another appropriate method
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            String origen = bundle.getString("origen");
            String destino = bundle.getString("destino");
            String detalles_bus = bundle.getString("detalles_bus");
            String valor_total = bundle.getString("valor_total");

            System.out.println(detalles_bus);
            System.out.println(valor_total);

            tv_detalle_origen.setText(origen);
            tv_detalle_destino.setText(destino);
            tv_detalles_bus.setText(detalles_bus);
            tv_total_compra.setText(valor_total);

            setupEditTextNumeroIDClienteListener();
        }
    }

    private void setupEditTextNumeroIDClienteListener(){
        et_numero_id_cliente.setOnKeyListener((view,keyCode,keyEvent)->{
                    if((keyEvent.getAction() == KeyEvent.ACTION_DOWN ) &&
                            (keyCode == keyEvent.KEYCODE_ENTER || keyCode == KeyEvent.KEYCODE_DPAD_CENTER)){
                        String[] idDigitado = {et_numero_id_cliente.getText().toString()};
                        new SearchQuery(this,"MVSEL0085",idDigitado).start();
                        return true;
                    }
                    return false;
                });
    }
    @Override
    public void onClick(View v) {

    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {

    }

    @Override
    public void arriveAnswerEvent(AnswerEvent e) {
        Document doc = e.getDocument();
        final Element elm = doc.getRootElement();
        if (e.getSqlCode().equals("MVSEL0085")) {
            Iterator<Element> i = elm.getChildren("row").iterator();
            while (i.hasNext()) {
                Element row = (Element) i.next();
                Iterator<Element> j = row.getChildren().iterator();
                id = ((Element) j.next()).getValue();
                id_char = ((Element) j.next()).getValue();
                nombre1 = ((Element) j.next()).getValue();
                nombre2 = ((Element) j.next()).getValue();
                apellido1 = ((Element) j.next()).getValue();
                apellido2 = ((Element) j.next()).getValue();
                razon_social = ((Element) j.next()).getValue();
                id_direccion = ((Element) j.next()).getValue();
                departamento = ((Element) j.next()).getValue();
                ciudad = ((Element) j.next()).getValue();
                direccion = ((Element) j.next()).getValue();
                id_telefono = ((Element) j.next()).getValue();
                numero = ((Element) j.next()).getValue();
                id_email = ((Element) j.next()).getValue();
                email = ((Element) j.next()).getValue();

                this.runOnUiThread(new Runnable() {
                    public void run() {
                        editTextName.setText(nombre1 + " " + nombre2);
                        editTextLastName.setText(apellido1 + " " + apellido2);
                        editTextAddress.setText(direccion + " " + departamento + " " + ciudad);
                        editTextPhone.setText(numero);
                        editTextEmail.setText(email);
                    }
                });
            }
        }
    }

    @Override
    public boolean containSqlCode(String sqlCode) {
        return false;
    }

    @Override
    public void dialogClickEvent(DialogClickEvent e) {

    }
}
