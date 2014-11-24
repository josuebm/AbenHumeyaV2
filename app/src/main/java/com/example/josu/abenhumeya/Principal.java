package com.example.josu.abenhumeya;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import android.util.Xml;


public class Principal extends Activity {

    private ArrayList <Mesa> listaMesas;
    private ArrayList <String> mesasAux;
    private Adaptador ad;
    private ListView lv;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);
        initComponents();
    }

    //Con este método guardamos lo que necesitamos para cargar de nuevo la Activity al girar la pantalla.
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList("listaMesas", listaMesas);
    }

    //Aquí cargamos los datos que hemos guardado previamente para presentar la Activity como se encontraba antes de girar la pantalla
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        listaMesas = savedInstanceState.getParcelableArrayList("listaMesas");
        ad = new Adaptador(this, R.layout.detalle_mesas, listaMesas);
        lv.setAdapter(ad);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.principal, menu);
        return true;
    }

    //Tenemos un menú contextual para poder eliminar y editar las mesas del ListView
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater= getMenuInflater();
        inflater.inflate(R.menu.longclick_principal, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        int id=item.getItemId();
        AdapterView.AdapterContextMenuInfo info= (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
        int index= info.position;
        Object o= info.targetView.getTag();
        Adaptador.ViewHolder vh;
        vh = (Adaptador.ViewHolder)o;
        if (id == R.id.action_eliminar) {
            tostada(getResources().getString(R.string.ttEliminado)+ " "+vh.tv1.getText().toString());
            listaMesas.remove(index);
            ad.notifyDataSetChanged();
            return true;
        }else if (id == R.id.action_editar) {
            editar(index);
            ad.notifyDataSetChanged();
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
        if (id == R.id.action_anadir) {
            return anadir();
        }else
            /*Inicialmente la aplicación se presenta sin datos pero tenemos la opción de cargar los que ya tenemos guardado en un xml.*/
            if (id == R.id.action_cargar){
                try {
                    leer();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (XmlPullParserException e) {
                    e.printStackTrace();
                }
                ad = new Adaptador(this, R.layout.detalle_mesas, listaMesas);
                lv.setAdapter(ad);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //Agrupamos en este método lo que ha de realizarse al iniciar la activity para no saturar el método OnCreate.
    public void initComponents(){
        //Inicializamos el Array de tipo Mesa donde almacenaremos las mesas ocupadas.
        listaMesas = new ArrayList();

        //Este es el adaptador que aplicaremos al ListView de la activity principal.
        ad = new Adaptador(this, R.layout.detalle_mesas, listaMesas);
        lv = (ListView)findViewById(R.id.listView);
        lv.setAdapter(ad);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                //Con Intent vamos a lanzar una nueva activity a la que le pasamos la lista de mesas y la posición del item de la listaMesas con el que queremos trabajar.
                Intent intent = new Intent(ad.getContext(), TomarNota.class);
                intent.putExtra("mesas", listaMesas);
                intent.putExtra("posicion", i);
                startActivity(intent);
            }
        });

        //Asignamos el menú contextual al ListView.
        registerForContextMenu(lv);
    }

    //Esta es la opción añadir del menú.
    public boolean anadir(){

        //Creamos un AlertDialog para añadir una nueva mesa.
        AlertDialog.Builder alert= new AlertDialog.Builder(this);
        alert.setTitle(R.string.action_anadir);
        LayoutInflater inflater= LayoutInflater.from(this);
        final View vista = inflater.inflate(R.layout.dialogo_nueva_mesa, null);
        alert.setView(vista);

        /*Cogemos el string-arraylist de strings.xml donde tenemos todas las mesas disponibles y lo cargamos en dos Arraylist: en uno para tener toda la lista de mesas y en otro
         para ir eliminando de ahí las mesas utilizadas. Necesitamos ambos para ir comparando las mesas que presentamos en la lista y en el spinner de mesas disponibles.
         Inicializamos el array por si ha habido cambios al editar o borrar.*/
        mesasAux =  new ArrayList (Arrays.asList(getResources().getStringArray(R.array.mesas)));
        ArrayList <String> aux = new ArrayList (Arrays.asList(getResources().getStringArray(R.array.mesas)));

        final Spinner sp1, sp2;
        sp1 = (Spinner)vista.findViewById(R.id.spMesa);
        sp2 = (Spinner)vista.findViewById(R.id.spComensal);

        //Comprobamos las mesas que ya están en nuestra lista para no presentarlas en el spinner.
        for(int i=0; i<listaMesas.size(); i++)
            if(mesasAux.contains(aux.get(listaMesas.get(i).getNumMesa())))
                mesasAux.remove(aux.get(listaMesas.get(i).getNumMesa()));

        //Creamos los adaptadores de los spinners pasándoles los string-array que tenemos en strings.xml.
        ArrayAdapter<String> adapterMesa = new ArrayAdapter <String>(this, android.R.layout.simple_spinner_item, mesasAux);
        adapterMesa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp1.setAdapter(adapterMesa);

        ArrayAdapter<CharSequence> adapterComensal = ArrayAdapter.createFromResource(this, R.array.comensales, android.R.layout.simple_spinner_item);
        adapterComensal.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp2.setAdapter(adapterComensal);

        //Determinamos qué ocurrirá cuando pulsemos en cada uno de los botones del AlertDialog.
        alert.setPositiveButton(R.string.btAceptar, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

                //Creamos un objeto Date del que obtener la hora a la que hemos creado la mesa para tener mayor control sobre los clientes.
                Date hora = new Date();

                /*Creamos un objeto mesa con los datos obtenidos de los spinners y la hora de creación del objeto. Utilizamos Arrays auxiliares para no pasar la opción
                seleccionada del Spinner, sino el número al que hace referencia en el string-arraylist para que al cargar en otro idioma los datos sean correctos.*/
                ArrayList <String> auxCat = new ArrayList (Arrays.asList(getResources().getStringArray(R.array.mesas)));
                ArrayList <String> auxCom = new ArrayList (Arrays.asList(getResources().getStringArray(R.array.comensales)));
                listaMesas.add(new Mesa(auxCat.indexOf(sp1.getSelectedItem()), auxCom.indexOf(sp2.getSelectedItem()), hora));

                //Ordenamos la lista para que aparezca ordenada en el ListView y no según el orden en el que se hayan ocupado las mesas.
                Collections.sort(listaMesas);

                //Avisamos al adaptador de que hemos modificado los datos que tiene que cargar en el ListView para que los actualice.
                ad.notifyDataSetChanged();
            }
        });

        //Cuando se pulsa el botón cancelar no queremos que ocurra nada. Se cerrará el diálogo.
        alert.setNegativeButton(R.string.btCancelar, null);
        alert.show();
        return true;
    }

    //Esta es la opción editar del menú contextual.
    public void editar(int index){
        final int index2 = index;
        final Spinner sp1, sp2;

        mesasAux =  new ArrayList (Arrays.asList(getResources().getStringArray(R.array.mesas)));
        ArrayList <String> aux = new ArrayList (Arrays.asList(getResources().getStringArray(R.array.mesas)));

        AlertDialog.Builder alert= new AlertDialog.Builder(this);
        alert.setTitle(R.string.action_editar);
        LayoutInflater inflater= LayoutInflater.from(this);
        final View vista = inflater.inflate(R.layout.dialogo_nueva_mesa, null);
        alert.setView(vista);

        sp1 = (Spinner)vista.findViewById(R.id.spMesa);
        sp2 = (Spinner)vista.findViewById(R.id.spComensal);

        //Comprobamos las mesas que ya están en nuestra lista para no presentarlas en el spinner y nos aseguramos de seguir mostrando la mesa que estamos editando.
        for(int i=0; i<listaMesas.size(); i++)
            if(mesasAux.contains(aux.get(listaMesas.get(i).getNumMesa())) && i!=index)
                mesasAux.remove(aux.get(listaMesas.get(i).getNumMesa()));

        ArrayAdapter<String> adapterMesa = new ArrayAdapter <String>(this, android.R.layout.simple_spinner_item, mesasAux);
        adapterMesa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp1.setAdapter(adapterMesa);

        ArrayAdapter<CharSequence> adapterComensal = ArrayAdapter.createFromResource(this, R.array.comensales, android.R.layout.simple_spinner_item);
        adapterComensal.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp2.setAdapter(adapterComensal);

        alert.setPositiveButton(R.string.btAceptar, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

                /*Al editar sólo modificamos el número de mesa y el número de comensales pero mantenemos el resto de atributos de nuestro objeto. Volvemos a ordenar las mesas
                y avisamos al adaptador de que se han producido cambios.*/
                ArrayList <String> auxCat = new ArrayList (Arrays.asList(getResources().getStringArray(R.array.mesas)));
                ArrayList <String> auxCom = new ArrayList (Arrays.asList(getResources().getStringArray(R.array.comensales)));
                listaMesas.get(index2).setNumMesa(auxCat.indexOf(sp1.getSelectedItem()));
                listaMesas.get(index2).setNumComensal(auxCom.indexOf(sp2.getSelectedItem()));
                Collections.sort(listaMesas);
                ad.notifyDataSetChanged();
            }
        });
        alert.setNegativeButton(R.string.btCancelar,null);
        alert.show();
    }

    //Creamos un método tostada para presentar mensajes de corta duración.
    public void tostada(String cad){
        //Comentario
        Toast.makeText(this, cad, Toast.LENGTH_SHORT).show();
    }

    //Este es el método para leer del xml los datos guardados previamente.
    public void leer() throws IOException, XmlPullParserException{
        listaMesas = new ArrayList<Mesa>();
        ArrayList <Pedido> ped = new ArrayList<Pedido>();
        Mesa mesaActual = new Mesa();
        XmlPullParser lectorxml = Xml.newPullParser();
        String etiqueta = "";

        String atributo1 = "", atributo2 = "", atributo3 = "";
        lectorxml.setInput(new FileInputStream(new File(getExternalFilesDir(null), "archivo.xml")), "utf-8");
        int evento = lectorxml.getEventType();
        while (evento != XmlPullParser.END_DOCUMENT) {
            if (evento == XmlPullParser.START_TAG) {
                etiqueta = lectorxml.getName();
                if (etiqueta.compareTo("mesa") == 0) {
                    atributo1 = lectorxml.getAttributeValue(null, "numMesa");
                    atributo2 = lectorxml.getAttributeValue(null, "numComensal");
                    atributo3 = lectorxml.getAttributeValue(null, "hora");
                    mesaActual = new Mesa(Integer.valueOf(atributo1.substring(2)), Integer.valueOf(atributo2.substring(2)), atributo3.substring(2));
                }
                else if(etiqueta.compareTo("pedido") == 0){
                    atributo1 = lectorxml.getAttributeValue(null, "categoria");
                    atributo2 = lectorxml.getAttributeValue(null, "plato");
                    ped.add(new Pedido(Integer.valueOf(atributo1.substring(2)), Integer.valueOf(atributo2.substring(2))));
                }
            }
            else if(evento == XmlPullParser.END_TAG){
                etiqueta = lectorxml.getName();
                if(etiqueta.compareTo("mesa") == 0 && ped != null){
                    mesaActual.setPedidos(ped);
                    listaMesas.add(mesaActual);
                    ped = new ArrayList<Pedido>();
                }
            }
            evento = lectorxml.next();
        }
    }
}
