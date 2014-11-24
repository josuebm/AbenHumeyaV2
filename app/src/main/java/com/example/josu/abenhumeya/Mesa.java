package com.example.josu.abenhumeya;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

import java.net.URL;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Josué on 19/10/2014.
 */

/*La clase Mesa implementa Comparable para poder ordenar los objetos en el ListView. Tiene un int con el número de mesa y otro con el número de comensales. Ambos son utilizados
 para cargar el String que corresponda según el idioma gracias al Array de Strings que se encuentra entre los recursos. La clase Mesa tiene también un ArrayList de tipo Pedido,
 que almacena otros dos enteros: uno que corresponde a la categoria del plato y al plato en sí, que presentarán el String que corresponda según el idioma, recurriendo nuevamente
 al Array de String que se encuentra en los recursos. La clase posee además un String donde guarda la hora a la que se creó el objeto. Tiene un constructor vacío, otro al que
 se le pasa el número de mesa, número de comensales y un Date que es transformado en String, que es utilizado al crear la mesa en el menú inicial. Tiene un tercer constructor
 en el que en vez del tipo Date recibe un tipo String para cuando carga el XML con los datos de mesas previas. A parte de estos tres constructores tiene los set y get, el compareTo
 para orderar y los implementados con Parcelable para poder pasar objetos de este tipo de una Activity a otra. En este caso es destacable que tiene un atributo "pedidos" que es
 un ArrayList de tipo Pedido y el tratamiento es distinto: a la hora de escribirlo utilizo el método writeList() y para leerlo readArrayList() al que le paso el cargador de la
 clase Pedido.*/

public class Mesa implements Comparable <Mesa>, Parcelable{
    
    private int numMesa, numComensal;
    private ArrayList <Pedido> pedidos;
    private String hora;

    public Mesa() {
    }

    public Mesa(int numMesa, int numComensal, Date hora) {
        this.numMesa = numMesa;
        this.numComensal = numComensal;
        String horas, minutos;

        if(String.valueOf(hora.getHours()).length() == 1)
            horas = "0" + hora.getHours();
        else
            horas = hora.getHours() + "";
        if(String.valueOf(hora.getMinutes()).length() == 1)
            minutos = "0" + hora.getMinutes();
        else
            minutos = hora.getMinutes() + "";

        this.hora = horas + ":" + minutos;
        this.pedidos = new ArrayList<Pedido>();
    }

    public Mesa(int numMesa, int numComensal, String hora){
        this.numMesa = numMesa;
        this.numComensal = numComensal;
        this.hora = hora;
        this.pedidos = new ArrayList<Pedido>();
    }

    public int getNumMesa() {
        return numMesa;
    }

    public void setNumMesa(int numMesa) {
        this.numMesa = numMesa;
    }

    public int getNumComensal() {
        return numComensal;
    }

    public void setNumComensal(int numComensal) {
        this.numComensal = numComensal;
    }

    public String getHora() {
        return hora;
    }

    public void setHora(String hora) {
        this.hora = hora;
    }

    public ArrayList<Pedido> getPedidos() {
        return pedidos;
    }

    public void setPedidos(ArrayList<Pedido> pedidos) {
        this.pedidos = pedidos;
    }

    @Override
    public int compareTo(Mesa mesa) {
        int a = getNumMesa();
        int b = mesa.getNumMesa();
        if(a < b)
            return -1;
        else
            if(b > a)
                return 1;
            else
            return 0;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(this.numMesa);
        parcel.writeInt(this.numComensal);
        parcel.writeString(this.hora);
        parcel.writeList(this.pedidos);
    }

    public Mesa(Parcel p){
        this.numMesa=p.readInt();
        this.numComensal=p.readInt();
        this.hora=p.readString();
        this.pedidos=p.readArrayList(Pedido.class.getClassLoader());
    }

    public static final Parcelable.Creator <Mesa> CREATOR =
            new Parcelable.Creator <Mesa>() {
                @Override
                public Mesa createFromParcel(Parcel parcel) {
                    return new Mesa(parcel);
                }
                @Override
                public Mesa[] newArray(int i) {
                    return new Mesa[i];
                }
            };
}
