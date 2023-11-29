package com.example.emakumovil.modules.ventas;

import static com.example.emakumovil.R.drawable.*;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.icu.text.IDNA;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.example.emakumovil.Global;
import com.example.emakumovil.R;
import com.example.emakumovil.components.AnswerEvent;
import com.example.emakumovil.components.AnswerListener;
import com.example.emakumovil.components.DialogClickEvent;
import com.example.emakumovil.components.DialogClickListener;
import com.example.emakumovil.components.SearchQuery;
import com.example.emakumovil.components.SelectedDataDialog;

import org.jdom2.Document;
import org.jdom2.Element;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class TiqueteActivity extends Activity implements View.OnClickListener, DialogClickListener,
        AnswerListener, View.OnFocusChangeListener {
    // cadena que almacena id_punto_origen
    private String id_punto_origen;
    private String id_punto_destino;
    // etiqueta que recibe descripcion punto origen
    private TextView tv_origen_value;
    // lupa buscar destino
    //private ImageButton ib_buscar_destino;
    //lupa buscar bus
    //private ImageButton ib_buscar_bus;
    private SelectedDataDialog selected;
    // etiqueta que recibe descripcion destino
    private EditText et_destino;
    // etiqueta que recibe numero bus
    private EditText et_numero_bus;
    // etiqueta que recibe descripcion punto
    private EditText et_descripcion_punto;
    // etiqueta que recibe descripcion bus
    private TextView et_descripcion_bus;
    // scrollview que recibe el plano del bus
    private TableLayout tl_plano_bus;
    private LinearLayout ll_buscar_bus;
    private LinearLayout ll_valor_unitario;
    private LinearLayout ll_cantidad_puestos;
    private LinearLayout ll_total_venta;
    private Button bt_continuar;
    private TextView tv_selecciona_asiento;
    // recibe nombre de usuario de la sesion
    private String system_user = Global.getSystem_user();

    private EditText et_valor_unitario;
    private EditText et_cantidad_puestos;
    private EditText et_total_venta;

    // constantes plano del bus
    private final int VIEW = 0;
    private final int CONFIG = 1;
    private final int SALES = 3;
    private final int VENDIDO_TAQUILLA=9;
    private final int VENDIDO_REVERTIDA=10;
    private final int VENDIDO_FUTURA=12;
    private final int RESERVA=11;
    // variables plano del bus
    private int puestos_vendidos=0;
    private boolean orientation = true;
    private int mode=3;
    private int puestos = 0;
    private int rowsp1 = 0;
    private int colsp1 = 0;
    private int rowsp2 = 0;
    private int colsp2 = 0;
    private int floors = 0;
    private int type = 4;
    private int seatConfig = 1;
    private int idActivo = -1;
    private Map<Integer,Map> pisos_vehiculos = new HashMap<Integer,Map>();
    private Map<Integer,InfoPuestoVehiculo> puestos_seleccionados_tiquete = new HashMap<Integer,InfoPuestoVehiculo>();
    private double valor_unitario = 0;
    private int cantidad_clicks = 0;
    private double total_venta = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        String[] args = {system_user};
        super.onCreate(savedInstanceState);
        // hace que al dar clic en menu Tiquete se muestre la interfaz de tiquetes
        setContentView(R.layout.activity_tiquete);
        // consulta el punto origen basado en el usuario
        new SearchQuery(this,"MVSEL0082",args).start();
        // boton lupa buscar destino
        //ib_buscar_destino = (ImageButton) findViewById(R.id.ib_buscar_destino);
        //ib_buscar_bus = (ImageButton) findViewById(R.id.ib_buscar_bus);
        et_destino = (EditText) findViewById(R.id.et_destino);
        et_numero_bus = (EditText) findViewById(R.id.et_numero_bus);
        //et_descripcion_punto = (EditText) findViewById(R.id.et_descripcion_punto);
        et_descripcion_bus = (TextView) findViewById(R.id.et_descripcion_bus);
        tv_origen_value = (TextView) findViewById(R.id.tv_origen_value);
        tl_plano_bus = (TableLayout) findViewById(R.id.tl_plano_bus);
        tv_selecciona_asiento = (TextView) findViewById(R.id.tv_selecciona_asiento);
        et_valor_unitario = (EditText) findViewById(R.id.et_valor_unitario);
        et_cantidad_puestos = (EditText) findViewById(R.id.et_cantidad_puestos);
        et_total_venta = (EditText) findViewById(R.id.et_total_venta);
        bt_continuar = (Button) findViewById(R.id.bt_continuar);
        ll_valor_unitario = (LinearLayout) findViewById(R.id.ll_valor_unitario);
        ll_cantidad_puestos = (LinearLayout) findViewById(R.id.ll_cantidad_puestos);
        ll_total_venta = (LinearLayout) findViewById(R.id.ll_total_venta);
        ll_buscar_bus = (LinearLayout) findViewById(R.id.ll_buscar_bus);

        // se agrega listener al boton para ejecutar lo que se ponga en onClick
        et_destino.setOnClickListener(this);
        et_numero_bus.setOnClickListener(this);
        //ib_buscar_bus.setOnClickListener(this);
        //ib_buscar_destino.setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.et_destino) {
            // se instancia el dialogo para buscar por palabra clave, el titulo se pone al componente
            // consulta punto destino a partir de punto origen
            selected = new SelectedDataDialog(R.id.et_destino,"Seleccione Destino",
                    "MVSEL0081", new String[] {id_punto_origen});
            // se adiciona listener
            selected.addDialogClickListener(this);
            // se hace visible el dialogo, el tag solo es una marca
            selected.show(getFragmentManager(), "Buscar punto destino");
        } else if (v.getId() == R.id.et_numero_bus) {
            // se instancia el dialogo para buscar por palabra clave, el titulo se pone al componente
            String[] args_buscar_bus = {id_punto_origen,id_punto_destino};
            selected = new SelectedDataDialog(R.id.et_numero_bus,"Vehículos disponibles",
                    "MVSEL0083", args_buscar_bus);
            // se adiciona listener
            selected.addDialogClickListener(this);
            // se hace visible el dialogo, el tag solo es una marca
            selected.show(getFragmentManager(), "Buscar Bus");
        }
    }

    @Override
    public void dialogClickEvent(DialogClickEvent e) {
        // aqui llegan los datos al hacer clic en los selectedDataDialog y SearchDataDialog
        System.out.println("tiene que ser aqui0");
        if (e.getIdobject() == R.id.et_destino) {
            id_punto_destino = e.getId();
            et_destino.setText((e.getValue()));
            ll_buscar_bus.setVisibility(View.VISIBLE);
            et_numero_bus.requestFocus();

            System.out.println("id_punto_destino: " + id_punto_destino);
            System.out.println("descripcion destino: " + et_destino.getText().toString());
        } else
            // llega info luego de hacer clic en la lista de buses
            if (e.getIdobject() == R.id.et_numero_bus) {
            et_numero_bus.setText((e.getId()));
            et_descripcion_bus.setText(e.getValue());
            et_descripcion_bus.setVisibility(View.VISIBLE);
            et_numero_bus.setSelection(et_numero_bus.getText().length());
            String[] args = {id_punto_origen,id_punto_destino,
                    et_numero_bus.getText().toString()};
            // se ejecuta query para pintar el plano del bus
            new SearchQuery(this,"MVSEL0084",args).start();
            et_numero_bus.clearFocus();
        }
    }
    public void openFormaPago(View view) {
        Intent intent = new Intent(this,FormaPagoTiquete.class);
        //Create a bundle to pass data
        Bundle bundle = new Bundle();
        String origen = tv_origen_value.getText().toString();
        String destino = et_destino.getText().toString();
        String detalles_bus = et_descripcion_bus.getText().toString();
        String valor_total = et_total_venta.getText().toString();

        bundle.putInt("id_activo",idActivo);
        bundle.putString("origen",origen);
        bundle.putString("destino",destino);
        bundle.putString("detalles_bus",detalles_bus);
        bundle.putString("valor_total",valor_total);

        // el hashmap de los puestos seleccionados no se puede pasar como hashmap en el bundle
        // por esta razon usamos Parcelable
        ParcelableMap parcelableMap = new ParcelableMap(puestos_seleccionados_tiquete);
        intent.putExtra("puestos_seleccionados",parcelableMap);
        intent.putExtras(bundle);
        startActivity(intent);
    }
    @Override
    public void onFocusChange(View arg0, boolean arg1) {
    }

    // sentencias ejecutadas directamente en la vista
    @Override
    public void arriveAnswerEvent(AnswerEvent e) {
        Document doc = e.getDocument();
        final Element elm = doc.getRootElement();
        // consulta descripcion de punto origen
        if (e.getSqlCode().equals("MVSEL0082")) {
            System.out.println("en arriveAnswerEvent");
            Iterator<Element> i = elm.getChildren("row").iterator();
            while (i.hasNext()) {
                Element row = (Element) i.next();
                Iterator<Element> j = row.getChildren().iterator();
                id_punto_origen = (String) j.next().getValue();
                final String descripcion_punto = ((Element) j.next()).getValue();
                this.runOnUiThread(new Runnable() {
                    public void run() {
                        tv_origen_value.setText(descripcion_punto);
                    }
                });
            }
        } else if (e.getSqlCode().equals("MVSEL0084")) { // consulta distribucion del bus
            // recepcion de datos de la query
            procesaQueryDistribucionVehiculo(elm.getChildren("row").iterator());
            if (colsp1!=0) {
                if (orientation) {
                    //Bus Parado
                    //paintFloorVertical(floors);
                    System.out.println("Pintando bus vertical");
                    //capturamos localmente las filas y columnas del bus
                    int numRows = rowsp1;
                    int numCols = colsp1;
                    this.runOnUiThread(new Runnable() {
                        public void run() {

                            // limpia bus
                            tl_plano_bus.removeAllViews();

                            Context appContext = getApplicationContext();
                            Drawable front_of_bus = ContextCompat.getDrawable(appContext,bus_frente_160x70_v);
                            Drawable back_of_bus = ContextCompat.getDrawable(appContext,bus_trasera_160x70_v);
                            Drawable seat_v = ContextCompat.getDrawable(appContext, seat_free);
                            Drawable conductor_v = ContextCompat.getDrawable(appContext, driver01);
                            Drawable grada_v = ContextCompat.getDrawable(appContext, stairs);
                            Drawable water_v = ContextCompat.getDrawable(appContext, ico_water_v);
                            Drawable tele_v = ContextCompat.getDrawable(appContext, ico_tele_v);
                            Drawable panel = ContextCompat.getDrawable(appContext, ico_panel);
                            Drawable puesto_facturado_v = ContextCompat.getDrawable(appContext, seat_busy);
                            Drawable puesto_reservado_v = ContextCompat.getDrawable(appContext, seat_reserved);
                            Drawable pasillo_v = ContextCompat.getDrawable(appContext, hallway);
                            

                    Map<Integer,PuestoVehiculo> filas_vehiculo = pisos_vehiculos.get(1);
                    float density = getResources().getDisplayMetrics().density;
                    String texto_asiento = "--";
                    TableRow tableRowFront = new TableRow(appContext);
                    TextView panelFrontal = new TextView(appContext);
                    TableRow.LayoutParams tableRowFront_params = new TableRow.LayoutParams(
                            TableLayout.LayoutParams.MATCH_PARENT,
                            TableLayout.LayoutParams.MATCH_PARENT
                    );
                    panelFrontal.setLayoutParams(tableRowFront_params);
                    tableRowFront_params.weight = 1;
                    panelFrontal.setLayoutParams(tableRowFront_params);
                    panelFrontal.setBackground(front_of_bus);
                    tableRowFront.addView(panelFrontal);
                    tl_plano_bus.addView(tableRowFront);
                    //recorremos las filas
                    for(int r = 0; r < numRows; r ++){
                        System.out.println("r: " + r);
                        PuestoVehiculo fila_vehiculo = filas_vehiculo.get(r+1);

                        //creamos variable de tipo TableRow
                        TableRow tableRow = new TableRow(appContext);
                        TableRow.LayoutParams layoutParams = new TableRow.LayoutParams(
                                TableLayout.LayoutParams.WRAP_CONTENT,
                                TableLayout.LayoutParams.WRAP_CONTENT
                        );

                        //establecemos parametros de TableRow
                        tableRow.setLayoutParams(layoutParams);
                        //recorremos las columnas
                        for(int c = 0; c < numCols; c++){
                                InfoPuestoVehiculo info_seat = fila_vehiculo.getInfoPuestoVehiculo(c + 1);
                            //Creamos un text view para representar el puesto
                            TextView seat = new TextView(appContext);
                            // los puestos de pasajeros tienen numeracion diferente de cero
                            if(info_seat.getPuesto() != 0)
                                texto_asiento = String.valueOf(info_seat.getPuesto());
                            else
                                texto_asiento = "";
                            //ponemos texto al puesto
                            seat.setText(texto_asiento);
                            // agregamos iconos como background
                            if (info_seat.getIdTipoPuesto()==2)
                                seat.setBackground(seat_v);
                            else if (info_seat.getIdTipoPuesto()==1)
                                seat.setBackground(conductor_v);
                            else if (info_seat.getIdTipoPuesto()==3)
                                seat.setBackground(grada_v);
                            else if (info_seat.getIdTipoPuesto()==4)
                                seat.setBackground(pasillo_v);
                            else if (info_seat.getIdTipoPuesto()==5)
                                seat.setBackground(water_v);
                            else if (info_seat.getIdTipoPuesto()==6)
                                seat.setBackground(tele_v);
                            else if (info_seat.getIdTipoPuesto()==7)
                                seat.setBackground(panel);
                            else if (info_seat.getIdTipoPuesto()==9)
                                seat.setBackground(puesto_facturado_v);
                            else if (info_seat.getIdTipoPuesto()==11)
                                seat.setBackground(puesto_reservado_v);

                            TableRow.LayoutParams table_row_params = new TableRow.LayoutParams(
                                    TableRow.LayoutParams.WRAP_CONTENT,  //(int)(0*density), //width
                                    TableRow.LayoutParams.WRAP_CONTENT);  //(int)(0*density)); // height
                            seat.setLayoutParams(table_row_params);
                            table_row_params.setMargins(10,10,10,10);
                            table_row_params.weight = 1;
                            table_row_params.width = 0;
                            table_row_params.height = 120;
                            seat.setLayoutParams(table_row_params);
                            seat.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                            seat.setPadding(0,10,0,0);
                            seat.setTextColor(Color.WHITE);
                            seat.setTextSize(12);
                            seat.setTypeface(null, Typeface.BOLD);

                            tableRow.addView(seat);

                            seat.setOnClickListener(new View.OnClickListener() {

                                @Override
                                public void onClick(View v) {
                                    // programacion de los clics hechos en el bus
                                    // obtenemos info completa del puesto sobre el que se hizo click
                                    InfoPuestoVehiculo puesto_seleccionado = getInfoPuesto(seat.getText().toString());

                                    // si la info del puesto seleccionado no es null y el puesto es
                                    // un asiento disponible
                                    if(puesto_seleccionado != null
                                            && puesto_seleccionado.getIdTipoPuesto() == 2) {
                                        // esta primera condicion evalua si se esta haciendo clic por segunda vez
                                        // en un mismo asiento
                                        if (puestos_seleccionados_tiquete.containsKey(puesto_seleccionado.getPuesto())) {
                                            seat.setBackground(seat_v);
                                            puestos_seleccionados_tiquete.remove(puesto_seleccionado.getPuesto());
                                            System.out.println("Tamaño hashmap: " + puestos_seleccionados_tiquete.size());
                                            total_venta = total_venta - puesto_seleccionado.getValor();
                                            if (puestos_seleccionados_tiquete.isEmpty()){
                                                valor_unitario = 0.0;
                                                bt_continuar.setEnabled(false);
                                                bt_continuar.setBackgroundColor(Color.parseColor("#B2DFDB"));
                                            }
                                            et_valor_unitario.setText(Double.toString(valor_unitario));
                                            et_cantidad_puestos.setText(Integer.toString(puestos_seleccionados_tiquete.size()));
                                            et_total_venta.setText(Double.toString(total_venta));
                                            System.out.println("Click por segunda vez en un puesto");
                                        } else {
                                            System.out.println("aqui voy, tipo puesto: " + puesto_seleccionado.getIdTipoPuesto());
                                            seat.setBackground(puesto_facturado_v);
                                            puestos_seleccionados_tiquete.put(puesto_seleccionado.getPuesto(),puesto_seleccionado);
                                            System.out.println("Tamaño hashmap: " + puestos_seleccionados_tiquete.size());
                                            Integer cantidad_puestos_tiquete = puestos_seleccionados_tiquete.size();
                                            bt_continuar.setEnabled(true);
                                            bt_continuar.setBackgroundColor(Color.parseColor("#009688"));
                                            valor_unitario = Double.valueOf(puesto_seleccionado.getValor());
                                            total_venta = cantidad_puestos_tiquete * valor_unitario;
                                            et_valor_unitario.setText(Double.toString(valor_unitario));
                                            et_cantidad_puestos.setText(Integer.toString(cantidad_puestos_tiquete));
                                            et_total_venta.setText(Double.toString(total_venta));
                                            System.out.println("Hice click en un puesto libre: " + seat.getText());
                                    }
                                    }
                                }
                            });
                        }
                        tl_plano_bus.addView(tableRow);
                    }

                            TableRow tableRowBack = new TableRow(appContext);
                            TextView panelTrasero = new TextView(appContext);
                            TableRow.LayoutParams tableRowBack_params = new TableRow.LayoutParams(
                                    TableLayout.LayoutParams.MATCH_PARENT,
                                    TableLayout.LayoutParams.MATCH_PARENT
                            );
                            panelTrasero.setLayoutParams(tableRowBack_params);
                            tableRowBack_params.weight = 1;
                            panelTrasero.setLayoutParams(tableRowBack_params);
                            panelTrasero.setBackground(back_of_bus);
                            tableRowBack.addView(panelTrasero);
                            tl_plano_bus.addView(tableRowBack);
                            tv_selecciona_asiento.setVisibility(View.VISIBLE);
                            ll_valor_unitario.setVisibility(View.VISIBLE);
                            ll_cantidad_puestos.setVisibility(View.VISIBLE);
                            ll_total_venta.setVisibility(View.VISIBLE);
                        }
                    });

                    System.out.println("deberias tener un bus en pantalla");
                }
                else {
                    System.out.println("Pintando bus horizontal");
                }
            }


            //hasta aqui
        }
    }

    private InfoPuestoVehiculo getInfoPuesto(String puesto_seleccionado){
        try {
            // pisos_vehiculo contiene todas las filas de puestos del piso
            // filas_vehiculo recibe todas las filas del piso 1
            Map<Integer, PuestoVehiculo> filas_vehiculo = pisos_vehiculos.get(1);
            // info_puesto_seleccionado contendra la informacion del puesto recibido como param
            Map<Integer, String> info_puesto_seleccionado = new HashMap<>();
            // recorremos las diferentes filas del vehiculo
            for (int r = 0; r < rowsp1; r++) {
                // PuestoVehiculo es un objeto que contiene la fila completa, el nombre no
                // corresponde a su contenido, se instancia la variable fila_vehiculo, la primera
                // fila es siempre la 1, no inician desde 0
                PuestoVehiculo fila_vehiculo = filas_vehiculo.get(r + 1);
                // recorremos la fila en turno
                for (int c = 0; c < colsp1; c++) {
                    // info_seat contiene toda la info del puesto en turno, tambien inicia en 1
                    InfoPuestoVehiculo info_seat = fila_vehiculo.getInfoPuestoVehiculo(c + 1);
                    // puesto_seleccionado que se recibe como parametro debe ser: diferente de vacio
                    // y casteando su valor como entero se compara con el numero de puesto en turno
                    if (!puesto_seleccionado.equals("") && info_seat.getPuesto() == Integer.valueOf(puesto_seleccionado)) {
                    // retornamos el objeto del asiento sobre el que se hizo click
                    return info_seat;
                    }
                }
            }
        }catch (NullPointerException e){
        }
        return null;
    }



    private void procesaQueryDistribucionVehiculo(Iterator i) {

        if (mode==SALES) {
            System.out.println("Limpiando bus...");
            //clean(); Pendiente implementar clean
        }
        //pisos_vehiculos.clear(); pendiente

        int last_row = 0;
        int cols = 0;
        Map<Integer,PuestoVehiculo> filas_vehiculosp1 = new HashMap<Integer,PuestoVehiculo>();
        Map<Integer,PuestoVehiculo> filas_vehiculosp2 = new HashMap<Integer,PuestoVehiculo>();
        PuestoVehiculo puesto_vehiculo = null;
        boolean dospisos = false;
        int maxpuesto =0;
        int id_activo=0;

        while (i.hasNext()) {
            Element elm = (Element) i.next();
            List data = elm.getChildren();
            int piso = Integer.parseInt(((Element) data.get(0)).getValue());
            int fila = Integer.parseInt(((Element) data.get(1)).getValue());
            int ubicacion = Integer.parseInt(((Element) data.get(2)).getValue());
            int id_tipo_puesto = Integer.parseInt(((Element) data.get(3)).getValue());
            int puesto = Integer.parseInt(((Element) data.get(4)).getValue());
            int id_horario=0;
            int id_ruta=0;
            int id_linea=0;
            int id_punto_origen=0;
            int id_punto_destino=0;
            double valor=0;
            double min=0;
            double web=0;
            double credito=0;
            String[] info_doc = new String[5];
            if (mode==SALES) {
                id_activo = Integer.parseInt(((Element) data.get(5)).getValue());
                id_horario = Integer.parseInt(((Element) data.get(6)).getValue());
                id_ruta = Integer.parseInt(((Element) data.get(7)).getValue());
                id_linea = Integer.parseInt(((Element) data.get(8)).getValue());
                id_punto_origen = Integer.parseInt(((Element) data.get(9)).getValue());
                id_punto_destino = Integer.parseInt(((Element) data.get(10)).getValue());
                valor = Double.parseDouble(((Element) data.get(11)).getValue());
                min = Double.parseDouble(((Element) data.get(12)).getValue());
                web = Double.parseDouble(((Element) data.get(13)).getValue());
                credito = Double.parseDouble(((Element) data.get(14)).getValue());
                info_doc[0] = String.valueOf(((Element)data.get(15)).getValue());
                info_doc[1] = String.valueOf(((Element)data.get(16)).getValue());
                info_doc[2] = String.valueOf(((Element)data.get(17)).getValue());
                info_doc[3] = String.valueOf(((Element)data.get(18)).getValue());
                info_doc[4] = String.valueOf(((Element)data.get(19)).getValue());
            }
            System.out.println("piso: " + piso );
            System.out.println("fila: " + fila );
            System.out.println("ubicacion: " + ubicacion );
            System.out.println("id_tipo_puesto: " + id_tipo_puesto );
            System.out.println("puesto: " + puesto );
            System.out.println("id_horario: " + id_horario );
            System.out.println("id_ruta: " + id_ruta );
            System.out.println("valor: " + valor);

            if (last_row!=fila) {
                System.out.println("Creé una fila");
                // puesto_vehiculo: en realidad es una fila, se almacena info de los puestos de una
                // fila
                puesto_vehiculo=new PuestoVehiculo(piso,fila); // siempre empieza piso 1 fila 1
            }
            else {
                if (piso==1) {
                    System.out.println("Adicioné una fila: " + fila + " al piso 1");
                    // se adicionan las filas a filas_vehiculosp1
                    filas_vehiculosp1.put(fila,puesto_vehiculo);
                    floors = 1;
                }
                else {
                    dospisos=true;
                    filas_vehiculosp2.put(fila,puesto_vehiculo);
                }
            }
            if (puesto>maxpuesto) {
                maxpuesto=puesto;
            }

            if (ubicacion>cols) {
                cols=ubicacion;
            }

            if (id_tipo_puesto==VENDIDO_TAQUILLA || id_tipo_puesto==VENDIDO_REVERTIDA ||
                    id_tipo_puesto==VENDIDO_FUTURA || id_tipo_puesto==RESERVA) {
                System.out.println("Puestos vendidos: " + puestos_vendidos);
                puestos_vendidos++;
            }
            if (mode==SALES) {
                System.out.println("Agregando info puesto: " + puesto + " a la fila: " + fila + " valor: " + valor);
                puesto_vehiculo.setInfoPuesto(piso, fila, ubicacion, id_tipo_puesto, puesto, id_activo, id_horario, id_linea, id_ruta, id_punto_origen, id_punto_destino, valor, min, web, credito, info_doc);
            }
            /*else {
                puesto_vehiculo.setInfoPuesto(piso,fila,ubicacion, id_tipo_puesto, puesto);
            }*/
            last_row=fila;
        }
        //termina while iterator

        idActivo = id_activo;
        puestos = maxpuesto;
        //exportar(puestos_vendidos); pendiente exportar cuantos puestos se han vendido
        System.out.println("Adicionando filas vehiculos a pisos");
        pisos_vehiculos.put(1, filas_vehiculosp1);
        pisos_vehiculos.put(2, filas_vehiculosp2);
        rowsp1 = filas_vehiculosp1.size();
        try {
            //colsp1 = filas_vehiculosp1.get(1).cols();
            colsp1 = cols;
        }
        catch(NullPointerException e) {
            colsp1 = 0;
        }

        if (dospisos) {
            floors = 2;
            rowsp2 = filas_vehiculosp2.size();
            colsp2 = filas_vehiculosp2.get(1).info_puesto_vehiculos.size();
        }
//		this.updateUI();
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
