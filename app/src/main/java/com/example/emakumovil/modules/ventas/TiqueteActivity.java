package com.example.emakumovil.modules.ventas;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
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

import com.example.emakumovil.Global;
import com.example.emakumovil.R;
import com.example.emakumovil.components.AnswerEvent;
import com.example.emakumovil.components.AnswerListener;
import com.example.emakumovil.components.DialogClickEvent;
import com.example.emakumovil.components.DialogClickListener;
import com.example.emakumovil.components.SearchDataDialog;
import com.example.emakumovil.components.SearchQuery;
import com.example.emakumovil.components.SelectedDataDialog;

import org.jdom2.Document;
import org.jdom2.Element;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class TiqueteActivity extends Activity implements View.OnClickListener, DialogClickListener, AnswerListener, View.OnFocusChangeListener {

    private ImageButton ib_buscar_destino;

    private ImageButton ib_buscar_bus;
    private SearchDataDialog search;
    private SelectedDataDialog selected;

    private SearchDataDialog search_bus;
    private EditText et_destino;
    private LinearLayout ll_receptor_bus;

    private EditText et_numero_bus;

    private EditText et_descripcion_punto;
    private EditText et_descripcion_bus;
    private TableLayout tl_plano_del_bus;
    private ScrollView sv_scroll_view_bus;
    private String system_user = Global.getSystem_user();
    private TextView tv_origen_value;
    private String id_punto_origen;
    private String id_punto_destino;
    private final int VIEW = 0;
    private final int CONFIG = 1;
    private final int SALES = 3;
    private final int VENDIDO_TAQUILLA=9;
    private final int VENDIDO_REVERTIDA=10;
    private final int VENDIDO_FUTURA=12;
    private final int RESERVA=11;
    private int puestos_vendidos=0;
    private boolean orientation = true;
    private int mode=0;
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
        //tl_plano_del_bus = findViewById(R.id.tl_plano_del_bus);
        sv_scroll_view_bus = findViewById(R.id.sv_scroll_view_bus);
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
            selected = new SelectedDataDialog(R.id.ib_buscar_destino,"Seleccione Destino","MVSEL0081", new String[] {id_punto_origen});
            // se adiciona listener
            selected.addDialogClickListener(this);
            // se hace visible el dialogo, el tag solo es una marca
            selected.show(getFragmentManager(), "Buscar punto destino");
        } else if (v.getId() == R.id.ib_buscar_bus) {
            System.out.println("Hice clic en la lupa buscar bus");
            // se instancia el dialogo para buscar por palabra clave, el titulo se pone al componente
            System.out.println("id_punto_origen + et_destino" + id_punto_origen + " + " + et_destino.getText().toString());
            String[] args_buscar_bus = {id_punto_origen,et_destino.getText().toString()};
            selected = new SelectedDataDialog(R.id.ib_buscar_bus,"Vehículos disponibles", "MVSEL0083", args_buscar_bus);
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
            if (e.getIdobject() == R.id.ib_buscar_bus) {
            et_numero_bus.setText((e.getId()));
            et_descripcion_bus.setText(e.getValue());
            et_descripcion_bus.setVisibility(View.VISIBLE);
            String[] args = {id_punto_origen,et_destino.getText().toString(),et_numero_bus.getText().toString()};
            new SearchQuery(this,"MVSEL0084",args).start();
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
        // estas son las sentencias ejecutadas directamente en la vista
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
                    }
                });
            }
        } else if (e.getSqlCode().equals("MVSEL0084")) {
            System.out.println("llego query distribucion de bus 84");
            // actualizamos informacion del bus sengun query
            procesaQueryDistribucionVehiculo(elm.getChildren("row").iterator());
            System.out.println("termine de capturar respuesta query 84");
            System.out.println("pintando bus...");
            System.out.println("rows and cols: " + rowsp1 + " " + colsp1);
            TableLayout tl_plano_del_bus = new TableLayout(this);
            if (colsp1!=0) {
                if (orientation) {
                    //Bus Parado
                    //paintFloorVertical(floors);
                    System.out.println("Pintando bus vertical");
                    //capturamos localmente las filas y columnas del bus
                    int numRows = rowsp1;
                    int numCols = colsp1;
                    System.out.println("rows and cols: " + numRows + " " + numCols);
                    //creamos map con la info de los puestos del piso 1
                    Map<Integer,PuestoVehiculo> filas_vehiculos = pisos_vehiculos.get(1); //piso 1

                    for(Map.Entry<Integer,PuestoVehiculo> entry : filas_vehiculos.entrySet()){
                        int i = entry.getKey();
                        PuestoVehiculo pv = entry.getValue();
                        System.out.println("Key: " + i);
                        for(Map.Entry<Integer,InfoPuestoVehiculo> entry1 : pv.info_puesto_vehiculos.entrySet()){
                            int j = entry1.getKey();
                            System.out.println("Key: " + j);
                            System.out.println("Value: " + entry1.getValue().getPuesto());
                        }
                        System.out.println("Puesto: " + pv);
                    }
                    this.runOnUiThread(new Runnable() {
                        public void run() {
                    Context appContext = getApplicationContext();
                    //recorremos las filas
                    for(int r = 0; r < numRows; r ++){
                        System.out.println("en fila: " + r);
                        //creamos variable de tipo TableRow
                        TableRow tableRow = new TableRow(appContext);
                        //establecemos parametros de TableRow
                        tableRow.setLayoutParams(new TableLayout.LayoutParams(
                                TableLayout.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.WRAP_CONTENT
                        ));
                        //recorremos las columnas
                        for(int c = 0; c < numCols; c++){
                            System.out.println("en col: " + c);
                            //Creamos un text view para representar el puesto
                            TextView seat = new TextView(appContext);
                            //ponemos texto al puesto
                            seat.setText(r+"-"+c);
                            System.out.println("en puesto: " + r + " " + c);
                            seat.setGravity(Gravity.CENTER);
                            seat.setLayoutParams(new TableRow.LayoutParams(
                                    TableLayout.LayoutParams.MATCH_PARENT,
                                    ViewGroup.LayoutParams.WRAP_CONTENT
                            ));
                            tableRow.addView(seat);
                        }
                        System.out.println("adicionando filas");
                        tl_plano_del_bus.addView(tableRow);
                    }
                            System.out.println("adicionando tl_plano_bus");
                            sv_scroll_view_bus.addView(tl_plano_del_bus);
                        }
                    });

                    //System.out.println("haciento visible el bus");
                    //myTableLayout.setVisibility(View.VISIBLE);
                    System.out.println("deberias tener un bus en pantalla");
                }
                else {
                    System.out.println("Pintando bus horizontal");
                    //paintFloorHorizontal(floors);
                }
            }


            //hasta aqui
        }
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
            System.out.println("piso " + piso);
            int fila = Integer.parseInt(((Element) data.get(1)).getValue());
            System.out.println("fila " + fila);
            int ubicacion = Integer.parseInt(((Element) data.get(2)).getValue());
            System.out.println("ubicacion " + ubicacion);
            int id_tipo_puesto = Integer.parseInt(((Element) data.get(3)).getValue());
            System.out.println("id_tipo_puesto " + id_tipo_puesto);
            int puesto = Integer.parseInt(((Element) data.get(4)).getValue());
            System.out.println("puesto " + puesto);
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

            if (last_row!=fila) {
                puesto_vehiculo=new PuestoVehiculo(piso,fila); // siempre empieza piso 1 fila 1
            }
            else {
                if (piso==1) {
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
                puestos_vendidos++;
            }
            if (mode==SALES) {
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
