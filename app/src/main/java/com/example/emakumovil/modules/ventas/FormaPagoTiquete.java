package com.example.emakumovil.modules.ventas;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.emakumovil.R;
import com.example.emakumovil.communications.SocketConnector;
import com.example.emakumovil.communications.SocketWriter;
import com.example.emakumovil.components.AnswerEvent;
import com.example.emakumovil.components.AnswerListener;
import com.example.emakumovil.components.DialogClickEvent;
import com.example.emakumovil.components.DialogClickListener;
import com.example.emakumovil.components.SearchQuery;

import org.jdom2.Attribute;
import org.jdom2.Document;
import org.jdom2.Element;

import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;

public class FormaPagoTiquete extends Activity implements View.OnClickListener, DialogClickListener,
        AnswerListener, View.OnFocusChangeListener {

    private TextView tv_detalle_origen;
    private TextView tv_detalle_destino;
    private TextView tv_detalles_bus;
    private TextView tv_titulo_puestos_seleccionados;
    private TextView tv_puestos_seleccionados;
    private TextView tv_total_compra;
    private EditText et_numero_id_cliente;
    private EditText editTextName;
    private EditText editTextLastName;
    private EditText editTextCash;
    private EditText editTextCreditCard;

    private EditText editTextAddress;
    private EditText editTextPhone;
    private EditText editTextEmail;
    private Button btnSubmit;

    private String id;
    private Integer id_activo;
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

    private Map<Integer,InfoPuestoVehiculo> puestos_seleccionados;

    private Spinner spinner_medio_de_pago;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forma_de_pago_tiquete);

        tv_detalle_origen = (TextView) findViewById(R.id.detalle_origen);
        tv_detalle_destino = (TextView) findViewById(R.id.detalle_destino);
        tv_detalles_bus = (TextView) findViewById(R.id.tv_detalles_bus);
        tv_titulo_puestos_seleccionados = (TextView)findViewById(R.id.tv_titulo_puestos_seleccionados);
        tv_puestos_seleccionados = (TextView) findViewById(R.id.tv_puestos_seleccionados);
        tv_total_compra = (TextView) findViewById(R.id.tv_total_compra);
        et_numero_id_cliente = (EditText) findViewById(R.id.et_numero_id_cliente);
        editTextName = (EditText) findViewById(R.id.editTextName);
        editTextLastName = (EditText) findViewById(R.id.editTextLastName);
        editTextAddress = (EditText) findViewById(R.id.editTextAddress);
        editTextPhone = (EditText) findViewById(R.id.editTextPhone);
        editTextEmail = (EditText) findViewById(R.id.editTextEmail);
        spinner_medio_de_pago = (Spinner) findViewById(R.id.spinner_medio_pago);
        editTextCash = (EditText) findViewById(R.id.editTexCash);
        editTextCreditCard = (EditText) findViewById(R.id.editTexCreditCard);
        btnSubmit = (Button) findViewById(R.id.btnSubmit);
        btnSubmit.setOnClickListener(this);

        // Inicia combo medios de pago
        String[] medios_de_pago = {"Efectivo",
                "Tarjeta",
                "Efectivo",
                "Crédito",
                "Bonos",
                "Efectivo",
                "Cortesía"};
        // Create an ArrayAdapter using the string array and a default spinner layout.
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.medios_de_pago_array,
                R.layout.spinner_medio_pago_format
        );
        // Specify the layout to use when the list of choices appears.
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner.
        spinner_medio_de_pago.setAdapter(adapter);

        spinner_medio_de_pago.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String medioPagoSeleccionado = (String) parent.getItemAtPosition(position);
                System.out.println("Medio seleccionado: " + medioPagoSeleccionado);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        // Termina combo medios de pago

        // Inside ActivityB in onCreate or another appropriate method
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            // aqui se almacenaran las puestos en formato integer para ser organizados luego
            ArrayList<Integer> cadena_puestos_seleccionados = new ArrayList<>();
            id_activo = bundle.getInt("id_activo");
            String origen = bundle.getString("origen");
            String destino = bundle.getString("destino");
            String detalles_bus = bundle.getString("detalles_bus");
            String valor_total = bundle.getString("valor_total");
            // des-parcel los puestos seleccionados que nos llegan en el bundle
            ParcelableMap parcelableMap = getIntent().getParcelableExtra("puestos_seleccionados");
            if(parcelableMap != null){
                puestos_seleccionados = parcelableMap.getMap();
                for(InfoPuestoVehiculo puesto : puestos_seleccionados.values()){
                    // obtenemos los puestos
                    cadena_puestos_seleccionados.add(puesto.getPuesto());
                }
            }

            // organizamos el array de numeros de puesto de menor a mayor
            Collections.sort(cadena_puestos_seleccionados);
            // StringBuilder permite cambiar la cadena sin instanciar un nuevo objeto cada vez
            // lo usamos para dar formato a la lista de puestos seleccionados
            StringBuilder cadenaPuestosFormateada = new StringBuilder();
            cadenaPuestosFormateada.append("[ ");
            for (int i = 0; i < cadena_puestos_seleccionados.size(); i++){
                cadenaPuestosFormateada.append(cadena_puestos_seleccionados.get(i));
                if (i < cadena_puestos_seleccionados.size() - 1)
                    cadenaPuestosFormateada.append(" ");
            }
            cadenaPuestosFormateada.append(" ]");
            String mostrar_texto = cadena_puestos_seleccionados.size() == 1 ? "Asiento" : "Asientos";

            tv_detalle_origen.setText(origen);
            tv_detalle_destino.setText(destino);
            tv_detalles_bus.setText(detalles_bus);
            tv_titulo_puestos_seleccionados.setText(mostrar_texto);
            tv_puestos_seleccionados.setText(cadenaPuestosFormateada);
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
        btnSubmit.setEnabled(false);
        sendTransaction();
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


    private void sendTransaction() {
        Document transaction = new Document();
        Element raiz = new Element("TRANSACTION");
        Element driver = new Element("driver");
        driver.setText("MVTR00021");
        Element id = new Element("id");
        id.setText("TMV21");
        transaction.setRootElement(raiz);
        raiz.addContent(driver);
        raiz.addContent(id);

        //prefijo tiquete
        Element package0 = new Element("package");
        Element field0 = new Element("field");
        field0.setText("T1");
        package0.addContent(field0);
        raiz.addContent(package0);

        // prefijo reserva, numero_reserva
        Element package1 = new Element("package");
        Element field11 = new Element("field");
        Element field12 = new Element("field");
        field11.setText("S1");
        package1.addContent(field11);
        package1.addContent(field12);
        raiz.addContent(package1);

        // prefijo valor bono, n bono
        Element package2 = new Element("package");
        Element field21 = new Element("field");
        Element field22 = new Element("field");
        field21.setText("0.0");
        package2.addContent(field21);
        package2.addContent(field22);
        raiz.addContent(package2);

        // datos personales
        Element package3 = new Element("package");
        Element field31 = new Element("field");
        Element field32 = new Element("field");
        Element field33 = new Element("field");
        Element field34 = new Element("field");
        Element field35 = new Element("field");
        Element field36 = new Element("field");
        Element field37 = new Element("field");

        // tipo identificacion
        field31.setText("13");
        // id_char (nro id cliente)
        field32.setText(et_numero_id_cliente.getText().toString());
        // nombre1
        field33.setText(editTextName.getText().toString());
        // nombre2
        field34.setText("");
        // apellido1
        field35.setText(editTextLastName.getText().toString());
        // apellido2
        field36.setText("");
        // razon social
        field37.setText("");

        package3.addContent(field31);
        package3.addContent(field32);
        package3.addContent(field33);
        package3.addContent(field34);
        package3.addContent(field35);
        package3.addContent(field36);
        package3.addContent(field37);

        raiz.addContent(package3);

        // direccion
        Element package4 = new Element("package");
        Element field41 = new Element("field");
        Element field42 = new Element("field");
        field41.setText(editTextAddress.getText().toString());
        field41.setText(et_numero_id_cliente.getText().toString());
        package4.addContent(field41);
        package4.addContent(field42);
        raiz.addContent(package4);
        // telefono
        Element package5 = new Element("package");
        Element field51 = new Element("field");
        Element field52 = new Element("field");
        field51.setText(editTextPhone.getText().toString());
        field51.setText(et_numero_id_cliente.getText().toString());
        package5.addContent(field51);
        package5.addContent(field52);
        raiz.addContent(package5);
        // email
        Element package6 = new Element("package");
        Element field61 = new Element("field");
        Element field62 = new Element("field");
        field61.setText(editTextEmail.getText().toString());
        field61.setText(et_numero_id_cliente.getText().toString());
        package6.addContent(field61);
        package6.addContent(field62);
        raiz.addContent(package6);
        // llave idpasajero
        Element package7 = new Element("package");
        Element field71 = new Element("field");
        Attribute field71_attrubute71 = new Attribute("attribute","key");
        Attribute field71_attrubute72 = new Attribute("name","idPasajero");
        field71.setAttribute(field71_attrubute71);
        field71.setAttribute(field71_attrubute72);
        field71.setText(et_numero_id_cliente.getText().toString());
        package7.addContent(field71);
        raiz.addContent(package7);

        // puestos seleccionados
        Element package8 = new Element("package");
        for(InfoPuestoVehiculo puesto : puestos_seleccionados.values()){
            // obtenemos los puestos
            Element subpackage = new Element("subpackage");

            Element id_ruta = new Element("field");
            id_ruta.setText(String.valueOf(puesto.getId_ruta()));
            Element id_punto_origen = new Element("field");
            id_punto_origen.setText(String.valueOf(puesto.getId_punto_origen()));
            Element id_punto_destino = new Element("field");
            id_punto_destino.setText(String.valueOf(puesto.getId_punto_destino()));
            Element id_horario = new Element("field");
            id_horario.setText(String.valueOf(puesto.getId_horario()));
            Element id_linea_servicio = new Element("field");
            id_linea_servicio.setText(String.valueOf(puesto.getId_linea()));
            Element id_activo = new Element("field");
            id_activo.setText(String.valueOf(puesto.getId_activo()));
            Element npuesto = new Element("field");
            npuesto.setText(String.valueOf(puesto.getPuesto()));
            Element tarifa = new Element("field");
            tarifa.setText(String.valueOf(puesto.getValor()));

            subpackage.addContent(id_ruta);
            subpackage.addContent(id_punto_origen);
            subpackage.addContent(id_punto_destino);
            subpackage.addContent(id_horario);
            subpackage.addContent(id_linea_servicio);
            subpackage.addContent(id_activo);
            subpackage.addContent(npuesto);
            subpackage.addContent(tarifa);

            package8.addContent(subpackage);

        }
        raiz.addContent(package8);

        // paquete en blanco
        Element pblanco1 = new Element("package");
        Element fblanco1 = new Element("field");
        pblanco1.addContent(fblanco1);
        raiz.addContent(pblanco1);

        // valores forma de pago
        Element package9 = new Element("package");
        Element efectivo = new Element("field");
        Element credito = new Element("field");
        Element tcreditcard = new Element("field");
        Element cambio = new Element("field");
        Element anticipo = new Element("field");
        Element total = new Element("field");

        efectivo.setText(editTextCash.getText().toString());
        credito.setText("0.0");
        tcreditcard.setText(editTextCash.getText().toString());
        cambio.setText("0.0");
        anticipo.setText("0.0");
        total.setText(editTextCash.getText().toString());
        package9.addContent(efectivo);
        package9.addContent(credito);
        package9.addContent(tcreditcard);
        package9.addContent(cambio);
        package9.addContent(anticipo);
        package9.addContent(total);
        raiz.addContent(package9);

        // tipo de tarjeta y valor en tarjeta
        Element package10 = new Element("package");
        Element idTipoTarjeta = new Element("field");
        Element valor_tarjeta = new Element("field");
        idTipoTarjeta.setText("NULL");
        valor_tarjeta.setText("0.0");
        package10.addContent(idTipoTarjeta);
        package10.addContent(valor_tarjeta);
        raiz.addContent(package10);

        // id_char cliente credito
        Element package11 = new Element("package");
        raiz.addContent(package11);

        // llave centrocosto
        Element package12 = new Element("package");
        Element field121 = new Element("field");
        Attribute field121_attrubute121 = new Attribute("attribute","key");
        Attribute field121_attrubute122 = new Attribute("name","centrocosto");
        field121.setAttribute(field121_attrubute121);
        field121.setAttribute(field121_attrubute122);
        field121.setText("2"); // pendiente traer esto desde la app
        package11.addContent(field121);
        raiz.addContent(package12);

        // valor pago a credito
        Element package13 = new Element("package");
        Element valor_credito = new Element("field");
        valor_credito.setText("0.0");
        package13.addContent(valor_credito);
        raiz.addContent(package13);

        // blanco 2
        raiz.addContent(pblanco1);

        // efectivo neto
        Element package14 = new Element("package");
        Element valor_efectivoneto = new Element("field");
        valor_efectivoneto.setText(editTextCash.getText().toString());
        package14.addContent(valor_efectivoneto);
        raiz.addContent(package14);

        // valor en tarjeta ******** reuso el mismo field que se uso antes tcreditcard *******
        Element package15 = new Element("package");
        package15.addContent(tcreditcard);
        raiz.addContent(package15);

        // llave id_activo
        Element package16 = new Element("package");
        Element field161 = new Element("field");
        Attribute field161_attrubute161 = new Attribute("attribute","key");
        Attribute field161_attrubute162 = new Attribute("name","idActivo");
        field161.setAttribute(field161_attrubute161);
        field161.setAttribute(field161_attrubute162);
        field161.setText(String.valueOf(id_activo));
        package16.addContent(field161);
        raiz.addContent(package16);

        // total neto
        Element package17 = new Element("package");
        Element total_neto = new Element("field");
        total_neto.setText(editTextCash.getText().toString()); // ******* pendiente poner total neto
        package17.addContent(total_neto);
        raiz.addContent(package17);

        // llave idTercero - id_char
        Element package18 = new Element("package");
        Element field181 = new Element("field");
        Attribute field181_attrubute181 = new Attribute("attribute","key");
        Attribute field181_attrubute182 = new Attribute("name","idTercero");
        field181.setAttribute(field181_attrubute181);
        field181.setAttribute(field181_attrubute182);
        field181.setText(et_numero_id_cliente.getText().toString());
        package18.addContent(field181);
        raiz.addContent(package18);

        // valor bono empresa
        Element package19 = new Element("package");
        Element valor_bono_empresa = new Element("field");
        valor_bono_empresa.setText("0.0");
        package19.addContent(valor_bono_empresa);
        raiz.addContent(package19);

        // valor bono propietario
        Element package20 = new Element("package");
        Element valor_bono_propietario = new Element("field");
        valor_bono_propietario.setText("0.0");
        package20.addContent(valor_bono_propietario);
        raiz.addContent(package20);

        // valor cortesia
        Element package21 = new Element("package");
        Element valor_cortesia = new Element("field");
        valor_cortesia.setText("0.0");
        package21.addContent(valor_cortesia);
        raiz.addContent(package21);


        SocketChannel socket = SocketConnector.getSock();
        Log.d("EMAKU","EMAKU: Socket: "+socket);
        SocketWriter.writing(socket, transaction);

    }

    @Override
    public boolean containSqlCode(String sqlCode) {
        return false;
    }

    @Override
    public void dialogClickEvent(DialogClickEvent e) {

    }
}
