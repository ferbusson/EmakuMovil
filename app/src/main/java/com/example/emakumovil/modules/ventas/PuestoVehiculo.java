package com.example.emakumovil.modules.ventas;

import java.util.HashMap;
import java.util.Map;

public class PuestoVehiculo {
    private int piso;
    private int fila;
    Map<Integer,InfoPuestoVehiculo> info_puesto_vehiculos = new HashMap<Integer,InfoPuestoVehiculo>();


    public PuestoVehiculo(int piso,int fila) {
        this.piso=piso;
        this.fila=fila;
    }


    public void setInfoPuesto(int piso,int fila,int ubicacion,int id_tipo_ubicacion,int puesto) {
        info_puesto_vehiculos.put(ubicacion, new InfoPuestoVehiculo(piso,fila,ubicacion,id_tipo_ubicacion,puesto));
    }


    public void setInfoPuesto(int piso,int fila,int ubicacion,int id_tipo_ubicacion,int puesto,int id_activo,int id_horario,int id_linea,int id_ruta,int id_punto_origen,int id_punto_destino,double valor,double min,double web,double credito,String[] info_doc) {
        InfoPuestoVehiculo InfoPuesto = new InfoPuestoVehiculo(piso,fila,ubicacion,id_tipo_ubicacion,puesto,id_activo,id_horario,id_linea,id_ruta,id_punto_origen,id_punto_destino,valor,min,web,credito,info_doc);
        info_puesto_vehiculos.put(ubicacion, InfoPuesto);
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