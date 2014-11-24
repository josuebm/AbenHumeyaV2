package com.example.josu.abenhumeya;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;

import org.xmlpull.v1.XmlSerializer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Josué on 23/11/2014.
 */
public class Xml extends Activity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        escribir();
    }

    private ArrayList<Mesa> listaMesas;

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
                    for(int j=0; j<listaMesas.get(i).getPedidos().size(); j++){
                        doc.startTag(null, "pedido");
                        doc.attribute(null, "categoria", "ca" + listaMesas.get(i).getPedidos().get(j).getCategoria());
                        doc.attribute(null, "plato", "plato" + listaMesas.get(i).getPedidos().get(j).getPlato());
                        doc.endTag(null, "pedido");
                    }
                    doc.endTag(null, "mesa");
                }
                //no cerramos la raiz?
                doc.endDocument();
                doc.flush();
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public boolean isLegible() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;
    }

    public boolean isModificable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

}
