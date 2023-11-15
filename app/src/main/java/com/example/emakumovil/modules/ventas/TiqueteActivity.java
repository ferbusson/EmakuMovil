package com.example.emakumovil.modules.ventas;

import static com.example.emakumovil.R.drawable.*;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Layout;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.example.emakumovil.Global;
import com.example.emakumovil.R;
import com.example.emakumovil.components.AnswerEvent;
import com.example.emakumovil.components.AnswerListener;
import com.example.emakumovil.components.DialogClickEvent;
import com.example.emakumovil.components.DialogClickListener;
import com.example.emakumovil.components.SearchDataDialog;
import com.example.emakumovil.components.SearchQuery;
import com.example.emakumovil.components.SelectedDataDialog;
import com.google.firebase.database.collection.LLRBNode;

import org.jdom2.Document;
import org.jdom2.Element;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class TiqueteActivity extends Activity implements View.OnClickListener, DialogClickListener,
        AnswerListener, View.OnFocusChangeListener {
    // cadena que almacena id_punto_origen
    private String id_punto_origen;
    // etiqueta que recibe descripcion punto origen
    private TextView tv_origen_value;
    // lupa buscar destino
    private ImageButton ib_buscar_destino;
    //lupa buscar bus
    private ImageButton ib_buscar_bus;
    private SelectedDataDialog selected;
    // etiqueta que recibe descripcion destino
    private EditText et_destino;
    // etiqueta que recibe numero bus
    private EditText et_numero_bus;
    // etiqueta que recibe descripcion punto
    private EditText et_descripcion_punto;
    // etiqueta que recibe descripcion bus
    private EditText et_descripcion_bus;
    // scrollview que recibe el plano del bus
    private TableLayout tl_plano_bus;
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
    private Map<Integer,InfoPuestoVehiculo> datos_puestos = new HashMap<Integer,InfoPuestoVehiculo>();


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
        ib_buscar_destino = (ImageButton) findViewById(R.id.ib_buscar_destino);
        ib_buscar_bus = (ImageButton) findViewById(R.id.ib_buscar_bus);
        et_destino = (EditText) findViewById(R.id.et_destino);
        et_numero_bus = (EditText) findViewById(R.id.et_numero_bus);
        et_descripcion_punto = (EditText) findViewById(R.id.et_descripcion_punto);
        et_descripcion_bus = (EditText) findViewById(R.id.et_descripcion_bus);
        tv_origen_value = (TextView) findViewById(R.id.tv_origen_value);
        tl_plano_bus = (TableLayout) findViewById(R.id.tl_plano_bus);
        et_valor_unitario = (EditText) findViewById(R.id.et_valor_unitario);
        et_cantidad_puestos = (EditText) findViewById(R.id.et_cantidad_puestos);
        et_total_venta = (EditText) findViewById(R.id.et_total_venta);

        // se agrega listener al boton para ejecutar lo que se ponga en onClick
        ib_buscar_destino.setOnClickListener(this);
        ib_buscar_bus.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.ib_buscar_destino) {
            // se instancia el dialogo para buscar por palabra clave, el titulo se pone al componente
            selected = new SelectedDataDialog(R.id.ib_buscar_destino,"Seleccione Destino",
                    "MVSEL0081", new String[] {id_punto_origen});
            // se adiciona listener
            selected.addDialogClickListener(this);
            // se hace visible el dialogo, el tag solo es una marca
            selected.show(getFragmentManager(), "Buscar punto destino");
        } else if (v.getId() == R.id.ib_buscar_bus) {
            // se instancia el dialogo para buscar por palabra clave, el titulo se pone al componente
            String[] args_buscar_bus = {id_punto_origen,et_destino.getText().toString()};
            selected = new SelectedDataDialog(R.id.ib_buscar_bus,"Vehículos disponibles",
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
        if (e.getIdobject() == R.id.ib_buscar_destino) {
            et_destino.setText((e.getId()));
            et_descripcion_punto.setText(e.getValue());
            et_descripcion_punto.setVisibility(View.VISIBLE);
            et_destino.setSelection(et_destino.getText().length());
            et_numero_bus.requestFocus();
        } else
            // llega info luego de hacer clic en la lista de buses
            if (e.getIdobject() == R.id.ib_buscar_bus) {
            et_numero_bus.setText((e.getId()));
            et_descripcion_bus.setText(e.getValue());
            et_descripcion_bus.setVisibility(View.VISIBLE);
            String[] args = {id_punto_origen,et_destino.getText().toString(),
                    et_numero_bus.getText().toString()};
            // se ejecuta query para pintar el plano del bus
            new SearchQuery(this,"MVSEL0084",args).start();
        }
    }

    @Override
    public void onFocusChange(View arg0, boolean arg1) {
    }

    // sentencias ejecutadas directamente en la vista
    @Override
    public void arriveAnswerEvent(AnswerEvent e) {
        Document doc = e.getDocument();
        final Element elm = doc.getRootElement();
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
        } else if (e.getSqlCode().equals("MVSEL0084")) {
            System.out.println("llego query distribucion de bus 84");
            // recepcion de datos de la query
            procesaQueryDistribucionVehiculo(elm.getChildren("row").iterator());
            System.out.println("pintando bus...");
            System.out.println("Rows: " + rowsp1);
            System.out.println("Cols: " + colsp1);

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
                            Context appContext = getApplicationContext();
                            Drawable front_of_bus = ContextCompat.getDrawable(appContext,bus_frente_160x70_v);
                            Drawable back_of_bus = ContextCompat.getDrawable(appContext,bus_trasera_160x70_v);
                            Drawable seat_v = ContextCompat.getDrawable(appContext, seat_free);
                            Drawable conductor_v = ContextCompat.getDrawable(appContext, ico_conductor_v);
                            Drawable grada_v = ContextCompat.getDrawable(appContext, ico_grada_v);
                            Drawable water_v = ContextCompat.getDrawable(appContext, ico_water_v);
                            Drawable tele_v = ContextCompat.getDrawable(appContext, ico_tele_v);
                            Drawable panel = ContextCompat.getDrawable(appContext, ico_panel);
                            Drawable puesto_facturado_v = ContextCompat.getDrawable(appContext, seat_busy);
                            Drawable puesto_reservado_v = ContextCompat.getDrawable(appContext, seat_reserved);
                            Drawable pasillo_v = ContextCompat.getDrawable(appContext, ico_pasillo_v);
                            /*
                            int width = 80; // Set your desired width in pixels
                            int height = 80; // Set your desired height in pixels

                            seat_v.setBounds(0,0,width,height);
                            conductor_v.setBounds(0,0,width,height);
                            grada_v.setBounds(0,0,width,height);
                            water_v.setBounds(0,0,width,height);
                            tele_v.setBounds(0,0,width,height);
                            panel.setBounds(0,0,width,height);
                            puesto_facturado_v.setBounds(0,0,width,height);
                            puesto_reservado_v.setBounds(0,0,width,height);
                            pasillo_v.setBounds(0,0,width,height);*/

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
                                TableLayout.LayoutParams.MATCH_PARENT,
                                TableLayout.LayoutParams.MATCH_PARENT
                        );
                        //tableRow.setGravity(Gravity.CENTER_HORIZONTAL);

                        //establecemos parametros de TableRow
                        tableRow.setLayoutParams(layoutParams);
                        //recorremos las columnas
                        for(int c = 0; c < numCols; c++){
                                InfoPuestoVehiculo info_seat = fila_vehiculo.getInfoPuestoVehiculo(c + 1);
                            //Creamos un text view para representar el puesto
                            TextView seat = new TextView(appContext);
                            if(info_seat.getPuesto() != 0)
                                texto_asiento = String.valueOf(info_seat.getPuesto());
                            else
                                texto_asiento = "";
                            //ponemos texto al puesto
                            seat.setText(texto_asiento);
                            if (info_seat.getIdTipoPuesto()==2)
                                seat.setBackground(seat_v);
                            else if (info_seat.getIdTipoPuesto()==1)
                                seat.setBackground(conductor_v);
                            else if (info_seat.getIdTipoPuesto()==3)
                                seat.setBackground(grada_v);
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

                            //seat.setGravity(Gravity.CENTER);
                            //seat.setPadding(16,16,16,16);

                            TableRow.LayoutParams table_row_params = new TableRow.LayoutParams(
                                    (int)(0*density), //width
                                    (int)(0*density)); // height
                            seat.setLayoutParams(table_row_params);
                            table_row_params.setMargins(10,10,10,10);
                            table_row_params.weight = 1;
                            table_row_params.width = 120;
                            table_row_params.height = 120;
                            seat.setLayoutParams(table_row_params);
                            seat.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                            seat.setPadding(0,10,0,0);
                            seat.setTextColor(Color.BLACK);
                            seat.setTypeface(null, Typeface.BOLD);
                            tableRow.addView(seat);

                            seat.setOnClickListener(new View.OnClickListener() {

                                @Override
                                public void onClick(View v) {
                                    // aqui va la programacion de los clics
                                    System.out.println("Hice click en: " + seat.getText().toString());
                                    seat.setBackground(puesto_facturado_v);
                                    valor_unitario = getVunitarioPuesto(seat.getText().toString());
                                    cantidad_clicks++;
                                    total_venta = cantidad_clicks*valor_unitario;
                                    et_valor_unitario.setText(Double.toString(valor_unitario));
                                    et_cantidad_puestos.setText(Integer.toString(cantidad_clicks));
                                    et_total_venta.setText(Double.toString(total_venta));
                                    System.out.println("Hice click en: " + seat.getText());
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

    private Double getVunitarioPuesto(String puesto_seleccionado){
        Map<Integer,PuestoVehiculo> filas_vehiculo = pisos_vehiculos.get(1);
        Map<Integer,String> info_puesto_seleccionado = null;
        for(int r = 0; r < rowsp1; r++){
            PuestoVehiculo fila_vehiculo = filas_vehiculo.get(r+1);
            for(int c = 0; c < colsp1; c++){
                InfoPuestoVehiculo info_seat = fila_vehiculo.getInfoPuestoVehiculo(c + 1);
                if(info_seat.puesto == Integer.valueOf(puesto_seleccionado))
                    info_puesto_seleccionado.put(0,String.valueOf(info_seat.getId_activo()));
                    return info_seat.getValor();
            }
        }
        return 0.0;
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
            colsp2 = filas_vehiculosp2.get(1).cols();
        }
//		this.updateUI();
    }

    class PuestoVehiculo {
        private int piso;
        private int fila;
        Map<Integer,InfoPuestoVehiculo> info_puesto_vehiculos = new HashMap<Integer,InfoPuestoVehiculo>();


        private PuestoVehiculo(int piso,int fila) {
            this.piso=piso;
            this.fila=fila;
        }


        private void setInfoPuesto(int piso,int fila,int ubicacion,int id_tipo_ubicacion,int puesto) {
            info_puesto_vehiculos.put(ubicacion, new InfoPuestoVehiculo(piso,fila,ubicacion,id_tipo_ubicacion,puesto));
        }


        private void setInfoPuesto(int piso,int fila,int ubicacion,int id_tipo_ubicacion,int puesto,int id_activo,int id_horario,int id_linea,int id_ruta,int id_punto_origen,int id_punto_destino,double valor,double min,double web,double credito,String[] info_doc) {
            InfoPuestoVehiculo InfoPuesto = new InfoPuestoVehiculo(piso,fila,ubicacion,id_tipo_ubicacion,puesto,id_activo,id_horario,id_linea,id_ruta,id_punto_origen,id_punto_destino,valor,min,web,credito,info_doc);
            info_puesto_vehiculos.put(ubicacion, InfoPuesto);
            datos_puestos.put(puesto, InfoPuesto);
        }

        private int cols() {
            return info_puesto_vehiculos.size();
        }

        public InfoPuestoVehiculo getInfoPuestoVehiculo(int pos) {
            InfoPuestoVehiculo seat = info_puesto_vehiculos.get(pos);
            return seat;
        }

        public void setInfoPuestoVehiculo(int col,InfoPuestoVehiculo seat) {
            info_puesto_vehiculos.remove(col);
            info_puesto_vehiculos.put(col, seat);
        }
    }


    class InfoPuestoVehiculo {
        private int fila;
        private int ubicacion;
        private int id_tipo_puesto;
        private int puesto;
        private int piso;
        private int id_activo;
        private int id_horario;
        private double valor;
        private double min;
        private double web;
        private double credito;
        private int id_linea;
        private int id_ruta;
        private int id_punto_origen;
        private int id_punto_destino;
        //private EmakuSeat emakuSeat;
        private String[] info_doc;

        private InfoPuestoVehiculo(int piso,int fila,int ubicacion,int id_tipo_puesto, int puesto) {
            this.piso=piso;
            this.fila=fila;
            this.ubicacion=ubicacion;
            this.id_tipo_puesto=id_tipo_puesto;
            this.puesto=puesto;
        }

        private InfoPuestoVehiculo(int piso,int fila,int ubicacion,int id_tipo_puesto, int puesto,int id_activo,int id_horario,int id_linea,int id_ruta,int id_punto_origen,int id_punto_destino,double valor,double min,double web,double credito,String[] info_doc) {
            this.piso=piso;
            this.fila=fila;
            this.ubicacion=ubicacion;
            this.id_tipo_puesto=id_tipo_puesto;
            this.puesto=puesto;
            this.id_activo=id_activo;
            this.id_horario=id_horario;
            this.id_linea=id_linea;
            this.id_ruta=id_ruta;
            this.id_punto_origen=id_punto_origen;
            this.id_punto_destino=id_punto_destino;
            this.valor=valor;
            this.min=min;
            this.web=web;
            this.credito=credito;
            this.info_doc=info_doc;
        }


        public String[] getInfo_doc() {
            return info_doc;
        }
/*
        public EmakuSeat getEmakuSeat() {
            return emakuSeat;
        }

        public void setEmakuSeat(EmakuSeat emakuSeat) {
            this.emakuSeat = emakuSeat;
        }
*/
        public int getFila() {
            return fila;
        }

        public void setFila(int fila) {
            this.fila=fila;
        }

        public int getUbicacion() {
            return ubicacion;
        }


        public void setInfo_doc(String[] info_doc) {
            this.info_doc = info_doc;
        }

        public void setIdTipoPuesto(int id_tipo_puesto) {
            this.id_tipo_puesto=id_tipo_puesto;
        }


        public int getIdTipoPuesto() {
            return id_tipo_puesto;
        }

        public void setPuesto(int puesto) {
            this.puesto=puesto;
        }

        public int getPuesto() {
            return puesto;
        }

        public int getPiso(){
            return piso;
        }

        public double getValor() {
            return valor;
        }

        public double getMin() {
            return min;
        }

        public double getWeb() {
            return web;
        }

        public double getCredito() {
            return credito;
        }

        public int getId_linea() {
            return id_linea;
        }

        public int getId_ruta() {
            return id_ruta;
        }

        public int getId_activo() {
            return id_activo;
        }

        public int getId_horario() {
            return id_horario;
        }

        public int getId_punto_origen() {
            return id_punto_origen;
        }

        public int getId_punto_destino() {
            return id_punto_destino;
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
