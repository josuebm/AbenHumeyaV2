package com.example.josu.abenhumeya;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Josué on 22/11/2014.
 */
public class Pedido implements Parcelable{

    private int categoria;
    private int plato;

    public Pedido() {
    }

    public Pedido(int categoria, int plato) {
        this.categoria = categoria;
        this.plato = plato;
    }

    public int getCategoria() {
        return categoria;
    }

    public void setCategoria(int categoria) {
        this.categoria = categoria;
    }

    public int getPlato() {
        return plato;
    }

    public void setPlato(int plato) {
        this.plato = plato;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(this.categoria);
        parcel.writeInt(this.plato);
    }

    public Pedido(Parcel p){
        this.categoria=p.readInt();
        this.plato=p.readInt();
    }

    public static final Parcelable.Creator <Pedido> CREATOR =
            new Parcelable.Creator <Pedido>() {
                @Override
                public Pedido createFromParcel(Parcel parcel) {
                    return new Pedido(parcel);
                }
                @Override
                public Pedido[] newArray(int i) {
                    return new Pedido[i];
                }
            };
}
