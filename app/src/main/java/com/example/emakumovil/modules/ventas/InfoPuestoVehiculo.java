package com.example.emakumovil.modules.ventas;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class InfoPuestoVehiculo implements Parcelable {
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

        public InfoPuestoVehiculo(int piso,int fila,int ubicacion,int id_tipo_puesto, int puesto) {
            this.piso=piso;
            this.fila=fila;
            this.ubicacion=ubicacion;
            this.id_tipo_puesto=id_tipo_puesto;
            this.puesto=puesto;
        }

        public InfoPuestoVehiculo(int piso,int fila,int ubicacion,int id_tipo_puesto, int puesto,int id_activo,int id_horario,int id_linea,int id_ruta,int id_punto_origen,int id_punto_destino,double valor,double min,double web,double credito,String[] info_doc) {
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


    protected InfoPuestoVehiculo(Parcel in) {
        fila = in.readInt();
        ubicacion = in.readInt();
        id_tipo_puesto = in.readInt();
        puesto = in.readInt();
        piso = in.readInt();
        id_activo = in.readInt();
        id_horario = in.readInt();
        valor = in.readDouble();
        min = in.readDouble();
        web = in.readDouble();
        credito = in.readDouble();
        id_linea = in.readInt();
        id_ruta = in.readInt();
        id_punto_origen = in.readInt();
        id_punto_destino = in.readInt();
        info_doc = in.createStringArray();
    }

    public static final Creator<InfoPuestoVehiculo> CREATOR = new Creator<InfoPuestoVehiculo>() {
        @Override
        public InfoPuestoVehiculo createFromParcel(Parcel in) {
            return new InfoPuestoVehiculo(in);
        }

        @Override
        public InfoPuestoVehiculo[] newArray(int size) {
            return new InfoPuestoVehiculo[size];
        }
    };

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeInt(fila);
        dest.writeInt(ubicacion);
        dest.writeInt(id_tipo_puesto);
        dest.writeInt(puesto);
        dest.writeInt(piso);
        dest.writeInt(id_activo);
        dest.writeInt(id_horario);
        dest.writeDouble(valor);
        dest.writeDouble(min);
        dest.writeDouble(web);
        dest.writeDouble(credito);
        dest.writeInt(id_linea);
        dest.writeInt(id_ruta);
        dest.writeInt(id_punto_origen);
        dest.writeInt(id_punto_destino);
        dest.writeStringArray(info_doc);
    }
}