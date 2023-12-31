package com.example.emakumovil.modules.ventas;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
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
import android.widget.Toast;
import android.Manifest;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.emakumovil.Global;;
import com.example.emakumovil.R;
import com.example.emakumovil.communications.SocketConnector;
import com.example.emakumovil.communications.SocketWriter;
import com.example.emakumovil.components.AnswerEvent;
import com.example.emakumovil.components.AnswerListener;
import com.example.emakumovil.components.DialogClickEvent;
import com.example.emakumovil.components.DialogClickListener;
import com.example.emakumovil.components.SearchQuery;
import com.example.emakumovil.control.ClientHeaderValidator;
import com.example.emakumovil.control.SuccessEvent;
import com.example.emakumovil.control.SuccessListener;
import com.example.emakumovil.misc.settings.ConfigFileHandler;
import com.google.firebase.platforminfo.DefaultUserAgentPublisher;
import com.mazenrashed.printooth.Printooth;
import com.mazenrashed.printooth.data.printable.Printable;
import com.mazenrashed.printooth.data.printable.RawPrintable;
import com.mazenrashed.printooth.data.printable.TextPrintable;
import com.mazenrashed.printooth.data.printer.DefaultPrinter;
import com.mazenrashed.printooth.ui.ScanningActivity;
import com.mazenrashed.printooth.utilities.Printing;
import com.mazenrashed.printooth.utilities.PrintingCallback;

import org.jdom2.Attribute;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;

public class FormaPagoTiquete extends Activity implements View.OnClickListener, DialogClickListener,
        AnswerListener, View.OnFocusChangeListener, SuccessListener, PrintingCallback {

    Printing printing;
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
    private Button btn_print;
    private Button btn_print_image;
    private Button btn_print_text;

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
    private static final int REQUEST_ENABLE_BT = 1;
    private static final int MY_BLUETOOTH_PERMISSION_REQUEST_SCANN = 123;
    private static final int MY_BLUETOOTH_PERMISSION_REQUEST_CONNECT = 456;

    private Map<Integer, InfoPuestoVehiculo> puestos_seleccionados;

    private Spinner spinner_medio_de_pago;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // POS printer setup
        Printooth.INSTANCE.init(this);

        setContentView(R.layout.forma_de_pago_tiquete);

        tv_detalle_origen = (TextView) findViewById(R.id.detalle_origen);
        tv_detalle_destino = (TextView) findViewById(R.id.detalle_destino);
        tv_detalles_bus = (TextView) findViewById(R.id.tv_detalles_bus);
        tv_titulo_puestos_seleccionados = (TextView) findViewById(R.id.tv_titulo_puestos_seleccionados);
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

        btn_print = (Button) findViewById(R.id.btn_print);
        btn_print.setOnClickListener(this);
        btn_print_image = (Button) findViewById(R.id.btn_print_image);
        btn_print_image.setOnClickListener(this);
        btn_print_text = (Button) findViewById(R.id.btn_print_text);
        btn_print_text.setOnClickListener(this);


        if (printing != null)
            printing.setPrintingCallback(this);

        btn_print.setOnClickListener(
                view -> {
                    BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                    if (bluetoothAdapter != null && !bluetoothAdapter.isEnabled()) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(this);
                        builder.setMessage("Bluetooth is currently off. Do you want to turn it on?")
                                .setCancelable(false)
                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                                        // Check if permission is granted
                                        if (ContextCompat.checkSelfPermission(FormaPagoTiquete.this, Manifest.permission.BLUETOOTH_SCAN)
                                                != PackageManager.PERMISSION_GRANTED) {
                                            // Permission not granted, request it
                                            System.out.println("Permission not granted, request it - scann");
                                            ActivityCompat.requestPermissions(FormaPagoTiquete.this,
                                                    new String[]{Manifest.permission.BLUETOOTH_SCAN}, MY_BLUETOOTH_PERMISSION_REQUEST_SCANN);
                                            System.out.println("He request it - scann");
                                        } if (ContextCompat.checkSelfPermission(FormaPagoTiquete.this, Manifest.permission.BLUETOOTH_CONNECT)
                                                != PackageManager.PERMISSION_GRANTED) {
                                            // Permission not granted, request it
                                            System.out.println("Permission not granted, request it - connect");
                                            ActivityCompat.requestPermissions(FormaPagoTiquete.this,
                                                    new String[]{Manifest.permission.BLUETOOTH_CONNECT}, MY_BLUETOOTH_PERMISSION_REQUEST_CONNECT);
                                            System.out.println("He request it - connect");
                                        } else {
                                            if (Printooth.INSTANCE.hasPairedPrinter()) {
                                                Printooth.INSTANCE.removeCurrentPrinter();
                                                System.out.println("has paired printer");
                                            } else {
                                                startActivityForResult(new Intent(FormaPagoTiquete.this,
                                                                ScanningActivity.class),
                                                        ScanningActivity.SCANNING_FOR_PRINTER);
                                                System.out.println("has paired printer else");
                                                changePairAndunpair();
                                            }
                                        }
                                        startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
                                    }
                                })
                                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                    }
                                });
                        AlertDialog alert = builder.create();
                        alert.show();
                    }


                });

        btn_print_image.setOnClickListener(view ->{
            if(!Printooth.INSTANCE.hasPairedPrinter())
                startActivityForResult(new Intent(FormaPagoTiquete.this,ScanningActivity.class),ScanningActivity.SCANNING_FOR_PRINTER);
            else
                printImages();
        });

        btn_print_text.setOnClickListener(view ->{
            if(!Printooth.INSTANCE.hasPairedPrinter())
                startActivityForResult(new Intent(FormaPagoTiquete.this,ScanningActivity.class),ScanningActivity.SCANNING_FOR_PRINTER);
            else
                printText();
        });

        changePairAndunpair();

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
        ClientHeaderValidator.addSuccessListener(this);
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
        sendPrintJob("574118");
    }

    private void changePairAndunpair() {
        if(Printooth.INSTANCE.hasPairedPrinter()){
            btn_print.setText(new StringBuilder("Unpair ").append(
                    Printooth.INSTANCE.getPairedPrinter().getName().toString()));
        } else {
            btn_print.setText("Pair with printer");
        }
    }

    private void printText() {
        System.out.println("Entre a imprimir texto");
        ArrayList<Printable> printables = new ArrayList<>();
        printables.add(new RawPrintable.Builder(new byte[]{27,100,4}).build());
        // add text
        printables.add(new TextPrintable.Builder()
                .setText("hello mi amor! : e")
                .setCharacterCode(DefaultPrinter.Companion.getCHARCODE_PC1252())
                .setNewLinesAfter(1).build());

        // custom text
        printables.add(new TextPrintable.Builder()
                .setText("hello mi amor!")
                .setLineSpacing(DefaultPrinter.Companion.getLINE_SPACING_60())
                .setAlignment(DefaultPrinter.Companion.getALIGNMENT_CENTER())
                .setEmphasizedMode(DefaultPrinter.Companion.getEMPHASIZED_MODE_BOLD())
                .setUnderlined(DefaultPrinter.Companion.getUNDERLINED_MODE_ON())
                .setCharacterCode(DefaultPrinter.Companion.getCHARCODE_PC1252())
                .setNewLinesAfter(1).build());

        printing.print(printables);


    }

    private void printImages() {

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
                        editTextName.setText(nombre1);
                        editTextName.setVisibility(View.VISIBLE);
                        editTextLastName.setText(apellido1);
                        editTextLastName.setVisibility(View.VISIBLE);
                        editTextAddress.setText(direccion + " " + departamento + " " + ciudad);
                        editTextAddress.setVisibility(View.VISIBLE);
                        editTextPhone.setText(numero);
                        editTextPhone.setVisibility(View.VISIBLE);
                        editTextEmail.setText(email);
                        editTextEmail.setVisibility(View.VISIBLE);
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
        field42.setText(et_numero_id_cliente.getText().toString());
        package4.addContent(field41);
        package4.addContent(field42);
        raiz.addContent(package4);
        // telefono
        Element package5 = new Element("package");
        Element field51 = new Element("field");
        Element field52 = new Element("field");
        field51.setText(editTextPhone.getText().toString());
        field52.setText(et_numero_id_cliente.getText().toString());
        package5.addContent(field51);
        package5.addContent(field52);
        raiz.addContent(package5);
        // email
        Element package6 = new Element("package");
        Element field61 = new Element("field");
        Element field62 = new Element("field");
        field61.setText(editTextEmail.getText().toString());
        field62.setText(et_numero_id_cliente.getText().toString());
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
        tcreditcard.setText(editTextCreditCard.getText().toString());
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
        package12.addContent(field121);
        raiz.addContent(package12);

        // valor pago a credito
        Element package13 = new Element("package");
        Element valor_credito = new Element("field");
        valor_credito.setText("0.0");
        package13.addContent(valor_credito);
        raiz.addContent(package13);

        // blanco 2
        Element pblanco2 = new Element("package");
        Element fblanco2 = new Element("field");
        pblanco2.addContent(fblanco2);
        raiz.addContent(pblanco2);


        // efectivo neto
        Element package14 = new Element("package");
        Element valor_efectivoneto = new Element("field");
        valor_efectivoneto.setText(editTextCash.getText().toString());
        package14.addContent(valor_efectivoneto);
        raiz.addContent(package14);

        // valor en tarjeta
        Element package15 = new Element("package");
        Element valor_en_tarjeta = new Element("field");
        valor_en_tarjeta.setText(editTextCreditCard.getText().toString());
        package15.addContent(valor_en_tarjeta);
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

        // Create an XMLOutputter with desired format
        XMLOutputter xmlOutputter = new XMLOutputter(Format.getPrettyFormat());

        try {
            // Output the XML content of the Document to a String
            String xmlString = xmlOutputter.outputString(transaction);

            // Print the XML content
            System.out.println(xmlString);
        } catch (Exception e) {
            e.printStackTrace();
        }


        SocketChannel socket = SocketConnector.getSock();
        Log.d("EMAKU","EMAKU: Socket: "+socket);
        SocketWriter.writing(socket, transaction);

    }

    private void sendPrintJob(String ndocumento) {
        Document transaction = new Document();
        Element raiz = new Element("SERVERPRINTER");

        Element printerTemplate = new Element("printerTemplate");
        printerTemplate.setText("/graphics/TSFacturacionTiquete.xml");

        Element jarFile = new Element("jarFile");
        jarFile.setText(ConfigFileHandler.getJarFile());

        Element jarDirectory = new Element("jarDirectory");
        jarDirectory.setText(ConfigFileHandler.getJarDirectory());
        // numero
        Element numero1 = new Element("package");
        numero1.addContent(new Element("field").setText(ndocumento));
        // prefijo
        Element prefijo = new Element("package");
        prefijo.addContent(new Element("field").setText("T1"));
        // sucursal
        Element sucursal  = new Element("package");
        sucursal.addContent(new Element("field").setText(sucursal.getTextTrim()));
        // origen
        Element origen  = new Element("package");
        origen.addContent(new Element("field").setText(tv_detalle_origen.getText().toString()));
        // destino
        Element destino  = new Element("package");
        destino.addContent(new Element("field").setText(tv_detalle_destino.getText().toString()));
        // fecha
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH); // Note: Month starts from 0 (January is 0)
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        String currentDate = year + "-" + (month + 1) + "-" + day; // Creating a date string

        Element fecha  = new Element("package");
        fecha.addContent(new Element("field").setText(currentDate));
        // hora
        // Get the current time
        Calendar calendar2 = Calendar.getInstance();
        int hour = calendar2.get(Calendar.HOUR_OF_DAY); // 24-hour format
        int minute = calendar2.get(Calendar.MINUTE);
        int second = calendar2.get(Calendar.SECOND);
        // Display or use the current time
        String currentTime = hour + ":" + minute + ":" + second;

        Element hora  = new Element("package");
        hora.addContent(new Element("field").setText(currentTime));

        // fecha ******* pendiente tener la fecha de programacion del bus
        Element fecha_viaje  = new Element("package");
        fecha_viaje.addContent(new Element("field").setText(currentDate));

        // pasajero
        Element pasajero  = new Element("package");
        pasajero.addContent(new Element("field").setText(
                editTextName.getText().toString()
                + " " + editTextLastName.getText().toString()));
        // cc
        Element cc  = new Element("package");
        cc.addContent(new Element("field").setText(et_numero_id_cliente.getText().toString()));
        // linea ******* pendiente
        Element linea  = new Element("package");
        linea.addContent(new Element("field").setText("Microbuses"));
        // hora del viaje ******* pendiente
        Element hora_viaje  = new Element("package");
        hora_viaje.addContent(new Element("field").setText(currentTime));
        // placa *******pendiente
        Element placa  = new Element("package");
        placa.addContent(new Element("field").setText("DOD-228"));
        // norden ******* pendiente
        Element norden  = new Element("package");
        norden.addContent(new Element("field").setText(tv_detalles_bus.getText().toString()));
        // valor
        Element valor  = new Element("package");
        valor.addContent(new Element("field").setText(tv_total_compra.getText().toString()));

        // TABLA
        Element tabla_puestos = new Element("package");
        for(InfoPuestoVehiculo puesto : puestos_seleccionados.values()){
            // obtenemos los puestos
            Element subpackage = new Element("subpackage");

            Element puesto_imp = new Element("field");
            puesto_imp.setText(String.valueOf(puesto.getPuesto()));
            Element valor_puesto_imp = new Element("field");
            valor_puesto_imp.setText(String.valueOf(puesto.getValor()));

            subpackage.addContent(puesto_imp);
            subpackage.addContent(valor_puesto_imp);

            tabla_puestos.addContent(subpackage);

        }

        // linea
        Element linea1  = new Element("package");

        // linea
        Element linea2  = new Element("package");

        // total numero de puestos
        Element total  = new Element("package");
        total.addContent(new Element("field").setText(String.valueOf(puestos_seleccionados.size())));
        // forma de pago ******* pendiente
        Element forma  = new Element("package");
        forma.addContent(new Element("field").setText("Efectivo"));

        // efectivo
        Element efectivo  = new Element("package");
        efectivo.addContent(new Element("field").setText(editTextCash.getText().toString()));
        // tarjeras
        Element tarjeras  = new Element("package");
        tarjeras.addContent(new Element("field").setText(editTextCreditCard.getText().toString()));
        // cambio
        Element cambio  = new Element("package");
        cambio.addContent(new Element("field").setText("0.0"));
        // linea3
        Element linea3  = new Element("package");
        // elaboro
        Element elaboro  = new Element("package");
        elaboro.addContent(new Element("field").setText(Global.getSystem_user() + " - " +
                "Movil App"));
        // field
        Element field  = new Element("package");

        transaction.setRootElement(raiz);
        raiz.addContent(printerTemplate);
        raiz.addContent(jarFile);
        raiz.addContent(jarDirectory);
        raiz.addContent(numero1);
        raiz.addContent(prefijo);
        raiz.addContent(sucursal);
        raiz.addContent(origen);
        raiz.addContent(destino);
        raiz.addContent(fecha);
        raiz.addContent(hora);
        raiz.addContent(fecha_viaje);
        raiz.addContent(pasajero);
        raiz.addContent(cc);
        raiz.addContent(linea);
        raiz.addContent(hora_viaje);
        raiz.addContent(placa);
        raiz.addContent(norden);
        raiz.addContent(valor);
        raiz.addContent(tabla_puestos);
        raiz.addContent(linea1);
        raiz.addContent(linea2);
        raiz.addContent(total);
        raiz.addContent(forma);
        raiz.addContent(efectivo);
        raiz.addContent(tarjeras);
        raiz.addContent(cambio);
        raiz.addContent(linea3);
        raiz.addContent(elaboro);
        raiz.addContent(field);

        // Create an XMLOutputter with desired format
        XMLOutputter xmlOutputter = new XMLOutputter(Format.getPrettyFormat());

        try {
            // Output the XML content of the Document to a String
            String xmlString = xmlOutputter.outputString(transaction);

            // Print the XML content
            System.out.println(xmlString);
        } catch (Exception e) {
            e.printStackTrace();
        }


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

    @Override
    public void cathSuccesEvent(SuccessEvent e) {

        FormaPagoTiquete.this.runOnUiThread(new Runnable() {
            public void run() {
                Toast.makeText(FormaPagoTiquete.this,
                        "Felicidades ya tienes tu tiquete imprimiendo",
                        Toast.LENGTH_LONG).show();
            }});
        sendPrintJob(e.getNdocument());
    }

    @Override
    public void connectingWithPrinter() {

    }

    @Override
    public void connectionFailed(@NonNull String s) {

    }

    @Override
    public void onError(@NonNull String s) {

    }

    @Override
    public void onMessage(@NonNull String s) {

    }

    @Override
    public void printingOrderSentSuccessfully() {

    }

    @Override
    public void disconnected() {

    }
}
