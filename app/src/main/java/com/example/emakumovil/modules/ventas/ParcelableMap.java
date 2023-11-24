package com.example.emakumovil.modules.ventas;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.HashMap;
import java.util.Map;

public class ParcelableMap implements Parcelable {
    private Map<Integer, InfoPuestoVehiculo> map;

    public ParcelableMap(Map<Integer, InfoPuestoVehiculo> map) {
        this.map = map;
    }

    private ParcelableMap(Parcel in) {
        map = new HashMap<>();
        // Read data from parcel to reconstruct the map
        // You'll need to read the size of the map, then read each key-value pair
        // For example:
        int size = in.readInt();
        for (int i = 0; i < size; i++) {
            int key = in.readInt();
            InfoPuestoVehiculo value = in.readParcelable(InfoPuestoVehiculo.class.getClassLoader());
            map.put(key, value);
        }
    }

    public Map<Integer, InfoPuestoVehiculo> getMap() {
        return map;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        // Write the size of the map, then write each key-value pair
        dest.writeInt(map.size());
        for (Map.Entry<Integer, InfoPuestoVehiculo> entry : map.entrySet()) {
            dest.writeInt(entry.getKey());
            dest.writeParcelable(entry.getValue(), flags);
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ParcelableMap> CREATOR = new Creator<ParcelableMap>() {
        @Override
        public ParcelableMap createFromParcel(Parcel in) {
            return new ParcelableMap(in);
        }

        @Override
        public ParcelableMap[] newArray(int size) {
            return new ParcelableMap[size];
        }
    };
}
