package com.example.josu.abenhumeya;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.xmlpull.v1.XmlSerializer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;


public class VerPedido extends Activity {

    private ArrayList <Pedido> pedido;
    private ListView lv;
    private AdaptadorVerPedido adaptadorPedido;
    private int posicion;
    private TextView tv1, tv2;
    private ArrayList <Mesa> listaMesas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ver_pedido);
        initComponents();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.ver_pedido, menu);
        return true;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater= getMenuInflater();
        inflater.inflate(R.menu.longclick_ver_pedido, menu);
    }

    //Esta es la opción eliminar del menú contextual para eliminar platos del pedido.
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        int id=item.getItemId();
        AdapterView.AdapterContextMenuInfo info= (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
        int index= info.position;
        Object o= info.targetView.getTag();
        AdaptadorVerPedido.ViewHolder vh;
        vh = (AdaptadorVerPedido.ViewHolder)o;
        tostada("En el menú contextual sí entra");
        if (id == R.id.action_eliminar) {
            tostada(getResources().getString(R.string.ttEliminado)+ " "+vh.tv1.getText().toString());
            pedido.remove(index);
            adaptadorPedido.notifyDataSetChanged();
            return true;
        }
        return super.onContextItemSelected(item);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        return super.onOptionsItemSelected(item);
    }

    public void tostada(String cad){
        Toast.makeText(this, cad, Toast.LENGTH_SHORT).show();
    }

    /*Este es el método asociado al onClick del botón Confirmar pedido. Asignamos el pedido a la mesa que le corresponda y finalizamos la activity.*/
    public void confirmarPedido(View v){
        listaMesas.get(posicion).setPedidos(pedido);
        escribir();
        finish();
    }

    public void initComponents(){
        /*Obtenemos de la activity de la que esta ha sido llamada la lista de mesas, su posición y el ArrayList pedido pero tenemos que asegurarnos de no machacar
        * el contenido previo si ya teníamos un pedido asignado a esta mesa. En tal caso, añadimos el nuevo pedido a continuación.*/
        listaMesas = (ArrayList <Mesa>)getIntent().getExtras().get("mesas");
        posicion = (Integer)getIntent().getExtras().get("posicion");
        if(listaMesas.get(posicion).getPedidos() == null)
            pedido = (ArrayList<Pedido>)getIntent().getExtras().get("pedido");

        else{
            pedido = listaMesas.get(posicion).getPedidos();
            ArrayList<Pedido> aux = (ArrayList<Pedido>)getIntent().getExtras().get("pedido");
            for (int i=0; i<aux.size(); i++)
                pedido.add(aux.get(i));
        }

        tv1 = (TextView)findViewById(R.id.tvMesa);
        tv2 = (TextView)findViewById(R.id.tvComensal);
        ArrayList <String> aux = new ArrayList (Arrays.asList(getResources().getStringArray(R.array.mesas)));
        tv1.setText(aux.get(listaMesas.get(posicion).getNumMesa()));
        aux = new ArrayList (Arrays.asList(getResources().getStringArray(R.array.comensales)));
        tv2.setText(aux.get(listaMesas.get(posicion).getNumComensal()));
        lv = (ListView)findViewById(R.id.lvVerPedido);
        adaptadorPedido = new AdaptadorVerPedido(this, R.layout.detalle_pedido, pedido);
        lv.setAdapter(adaptadorPedido);
        registerForContextMenu(lv);
    }

    public void escribir() {
        FileOutputStream fos = null;
        XmlSerializer doc = android.util.Xml.newSerializer();
        if (isModificable()) {
            try {
                fos = new FileOutputStream(new File(getExternalFilesDir(null), "archivo.xml"));
                doc.setOutput(fos, "UTF-8");
                doc.startDocument(null, Boolean.valueOf(true));
                doc.setFeature("http://xmlpull.org/v1/doc/features.html#indent-output", true);
                doc.startTag(null, "mesas");
                for(int i=0; i<listaMesas.size(); i++){
                    doc.startTag(null, "mesa");
                    doc.attribute(null, "numMesa", "nm" + listaMesas.get(i).getNumMesa());
                    doc.attribute(null, "numComensal", "nc" + listaMesas.get(i).getNumComensal());
                    doc.attribute(null, "hora", "ho" + listaMesas.get(i).getHora());
                    if(listaMesas.get(i).getPedidos() != null)
                        for(int j=0; j<listaMesas.get(i).getPedidos().size(); j++){
                            doc.startTag(null, "pedido");
                            doc.attribute(null, "categoria", "ca" + listaMesas.get(i).getPedidos().get(j).getCategoria());
                            doc.attribute(null, "plato", "pl" + listaMesas.get(i).getPedidos().get(j).getPlato());
                            doc.endTag(null, "pedido");
                        }
                    doc.endTag(null, "mesa");
                }
                doc.endDocument();
                doc.flush();
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public boolean isModificable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }
}
