package com.example.josu.abenhumeya;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by Josué on 19/10/2014.
 */

/*Este es un adaptador que podría no ser necesario crearlo por tener un único TextView como el que usa por defecto el ListView pero con el que he estado probando diferentes estilos.*/
public class AdaptadorVerPedido extends ArrayAdapter <Pedido>{

    private Context contexto;
    private int recurso;
    private ArrayList <Pedido> lista;
    private LayoutInflater inflador;


    public AdaptadorVerPedido(Context context, int resource, ArrayList<Pedido> objects) {
        super(context, resource, objects);
        this.contexto = context;
        this.recurso = resource;
        this.lista = objects;
        inflador = (LayoutInflater)contexto.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public class ViewHolder{
        TextView tv1, tv2;
        public int posicion;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder vh = null;
        if(convertView == null){
            convertView = inflador.inflate(recurso, null);
            vh = new ViewHolder();
            vh.tv1 = (TextView)convertView.findViewById(R.id.tvPedido);
            convertView.setTag(vh);
        }
        else
            vh = (ViewHolder)convertView.getTag();
        vh.tv1.setText(categorias(lista.get(position)));
        vh.posicion = position;
        return convertView;
    }

    public String categorias(Pedido pedido){
        switch (pedido.getCategoria()){
            case 0:{
                ArrayList <String> aux = new ArrayList (Arrays.asList(contexto.getResources().getStringArray(R.array.bebidas)));
                return aux.get(pedido.getPlato());
            }
            case 1:{
                ArrayList <String> aux = new ArrayList (Arrays.asList(contexto.getResources().getStringArray(R.array.entrantes)));
                return aux.get(pedido.getPlato());
            }
            case 2:{
                ArrayList <String> aux = new ArrayList (Arrays.asList(contexto.getResources().getStringArray(R.array.ensaladas)));
                return aux.get(pedido.getPlato());
            }
            case 3:{
                ArrayList <String> aux = new ArrayList (Arrays.asList(contexto.getResources().getStringArray(R.array.carnes)));
                return aux.get(pedido.getPlato());
            }
            case 4:{
                ArrayList <String> aux = new ArrayList (Arrays.asList(contexto.getResources().getStringArray(R.array.pescados)));
                return aux.get(pedido.getPlato());
            }
            case 5:{
                ArrayList <String> aux = new ArrayList (Arrays.asList(contexto.getResources().getStringArray(R.array.pastas)));
                return aux.get(pedido.getPlato());
            }
            default:{
                ArrayList <String> aux = new ArrayList (Arrays.asList(contexto.getResources().getStringArray(R.array.reposteria)));
                return aux.get(pedido.getPlato());
            }
        }
    }
}
