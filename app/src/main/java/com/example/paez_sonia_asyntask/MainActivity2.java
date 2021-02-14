package com.example.paez_sonia_asyntask;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;


import com.example.paez_sonia_asyntask.Network.Conexion;
import com.example.paez_sonia_asyntask.Network.Resultado;
import com.example.paez_sonia_asyntask.databinding.ActivityMain2Binding;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity2 extends AppCompatActivity implements View.OnClickListener{
    private ActivityMain2Binding binding;
    private static final int REQUEST_CONNECT = 1;
    long inicio, fin;
    TareaAsincrona tareaAsincrona;
    URL url;
    private static final String FICHERO = " fichero.txt";
    private static final String FICHEROIMAGEN = "imagen.jpeg";
    private static final int REQUEST_WRITE = 1;
    private static String TXTLIMPIO;
    Bitmap imagen = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        binding = ActivityMain2Binding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        binding.botonDescarga.setOnClickListener(this);
        if (android.os.Build.VERSION.SDK_INT > 9)
        {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
    }

    @Override
    public void onClick(View v) {

        try {
            url = new URL(binding.navegador.getText().toString());
            ObtenecontenidoçURL();
            descarga(url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
            mostrarError(e.getMessage());
        }
    }
    // Comenzamos descrga Asyntask
    private void descarga(URL url) {
        inicio = System.currentTimeMillis();
        tareaAsincrona = new TareaAsincrona(this);
        tareaAsincrona.execute(url);
        binding.textView.setText("Descargando la página . . .");
    }


    private void mostrarRespuesta(String s) {
        fin = System.currentTimeMillis();
        MainActivity2.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                binding.resultado.loadDataWithBaseURL(String.valueOf(url), s, "text/html", "UTF-8", null);
                binding.textView.setText("Duración: " + String.valueOf(fin - inicio) + "ms");
            }
        });
    }

    private void mostrarError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    public class TareaAsincrona extends AsyncTask<URL, Void, Resultado> {// Método Asyntask
        private ProgressDialog progreso;
        private Context context;

        public TareaAsincrona(Context context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progreso = new ProgressDialog(context);
            progreso.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progreso.setMessage("Conectando . . .");
            progreso.setCancelable(true);
            progreso.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    TareaAsincrona.this.cancel(true);
                }
            });
            progreso.show();
        }

        @Override
        protected Resultado doInBackground(URL... urls) {
            Resultado resultado;

            try {
                resultado = Conexion.conectarJava(urls[0]);
            } catch (IOException e) {
                e.printStackTrace();
                Log.e("Error de conexión: ", e.getMessage());
                resultado = new Resultado();
                resultado.setCodigo(500);
                resultado.setMensaje("Error de conexión: " + e.getMessage());
            }

            return resultado;
        }

        @Override
        protected void onPostExecute(Resultado resultado) {
            super.onPostExecute(resultado);
            progreso.dismiss();
            fin = System.currentTimeMillis();
            if (resultado.getCodigo() == HttpURLConnection.HTTP_OK) {// Comprobamos que haya conexion
                String datos= binding.navegador.getText().toString();
                if(datos.contains("html")||datos.contains("txt")) {//Si es una url de txt o html abrimos TextView y mostramos el  contenido
                    guardar();
                    leer();
                    binding.texto.setVisibility(View.VISIBLE);
                    binding.resultado.setVisibility(View.INVISIBLE);
                    binding.imagen.setVisibility(View.INVISIBLE);
                }else{
                    if(datos.contains("jpg")||datos.contains("png")){// Comprobamos si es imagen y la guardamos y abrimos  Imagen view
                        descargarImagen(datos);
                        GuargarImagen();
                        leerImagen();
                        binding.texto.setVisibility(View.INVISIBLE);
                        binding.resultado.setVisibility(View.INVISIBLE);
                        binding.imagen.setVisibility(View.VISIBLE);

                    }
                }

            } else {
                mostrarError(resultado.getMensaje());
                binding.resultado.loadDataWithBaseURL(String.valueOf(url), resultado.getMensaje(), "text/html", "UTF-8", null);

            }
            binding.textView.setText("Duración: " + String.valueOf(fin - inicio) + "ms");
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            progreso.dismiss();
            mostrarError("Cancelado");
        }
    }


    public String ObtenecontenidoçURL(){
        try {
            url = new URL(binding.navegador.getText().toString());
            URLConnection con = url.openConnection();
            Pattern p = Pattern.compile("text/html;\\s+charset=([^\\s]+)\\s*");
            Matcher m = p.matcher(con.getContentType());
            /*If Content-Type doesn't match this pre-conception, choose default and * hope for the best. */
            String charset = m.matches() ? m.group(1) : "UTF-8";
            Reader r;
            r = new InputStreamReader(con.getInputStream(), charset);
            StringBuilder buf = new StringBuilder();
            while (true) {
                int ch = r.read();
                if (ch < 0)
                    break;
                buf.append((char) ch);
            }
            Spanned str = Html.fromHtml(buf.toString()) ;
            TXTLIMPIO=(str.toString()).trim();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return TXTLIMPIO;
    }

    public void guardar() {
        String permiso = Manifest.permission.WRITE_EXTERNAL_STORAGE;
        // comprobar los permisos
        if (ActivityCompat.checkSelfPermission(this, permiso) != PackageManager.PERMISSION_GRANTED) {
            // pedir los permisos necesarios, porque no están concedidos
            ActivityCompat.requestPermissions(this, new String[]{permiso}, REQUEST_WRITE);
            // Cuando se cierre el cuadro de diálogo se ejecutará onRequestPermissionsResult
        } else {
            // Permisos ya concedidos
            escribir();
        }
    }

    private void escribir() {

        if(Memoria.disponibleEscritura()){
            try {
                if (Memoria.escribirExterna(FICHERO, TXTLIMPIO)){
                    binding.texto.setText(Memoria.mostrarPropiedadesExterna(FICHERO));
                    mostrarMensaje("Fichero escrito con éxito.");
                }
            } catch (IOException e) {
                e.printStackTrace();
                mostrarMensaje("Error en la escritura del fichero." + FICHERO + e.getMessage());
                binding.textView.setText("");
            }
        } else{
            binding.textView.setText("Error al escribir en el fichero." + FICHERO);
            binding.textView.setText("");
        }
    }


    private void leer() {
        if(Memoria.disponibleLectura()){
            try {
                binding.texto.setText(Memoria.leerExterna(FICHERO));
                mostrarMensaje("Fichero leido OK");
            } catch (IOException e) {
                e.printStackTrace();
                mostrarMensaje("Error en lectura" + FICHERO + e.getMessage());
                binding.textView.setText("");
            }
        }else{
            binding.textView.setText("Memoria externa no disponible: " + FICHERO);
            binding.textView.setText("");
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        String permiso = Manifest.permission.WRITE_EXTERNAL_STORAGE;
        // chequeo los permisos de nuevo
        switch (requestCode) {
            case REQUEST_WRITE:
                if (ActivityCompat.checkSelfPermission(this, permiso) == PackageManager.PERMISSION_GRANTED) {
                    // permiso concedido
                    escribir();
                } else {
                    // no hay permiso
                    mostrarMensaje("No hay permiso para escribir en la memoria externa");
                }
                break;
        }
    }
    private Bitmap descargarImagen (String imageHttpAddress){
        URL imageUrl = null;
        try{
            imageUrl = new URL(imageHttpAddress);
            HttpURLConnection conn = (HttpURLConnection) imageUrl.openConnection();
            conn.connect();
            imagen = BitmapFactory.decodeStream(conn.getInputStream());
        }catch(IOException ex){
            ex.printStackTrace();
        }
        return imagen;
    }
    private void GuargarImagen() {

        if(Memoria.disponibleEscritura()){
            try {
                if (Memoria.imagenExterna(FICHEROIMAGEN,imagen)){
                    binding.texto.setText(Memoria.mostrarPropiedadesExterna(FICHERO));
                    mostrarMensaje("Fichero escrito con éxito.");
                }
            } catch (IOException e) {
                e.printStackTrace();
                mostrarMensaje("Error en la escritura del fichero." + FICHERO + e.getMessage());
                binding.textView.setText("");
            }
        } else{
            binding.textView.setText("Error al escribir en el fichero." + FICHERO);
            binding.textView.setText("");
        }
    }
    private void leerImagen() {
        if(Memoria.disponibleLectura()){
            try {
                binding.imagen.setImageBitmap(Memoria.leerExternaImagen(FICHEROIMAGEN));
                mostrarMensaje("Fichero leido OK");
            } catch (IOException e) {
                e.printStackTrace();
                mostrarMensaje("Error en lectura" + FICHERO + e.getMessage());
                binding.textView.setText("");
            }
        }else{
            binding.textView.setText("Memoria externa no disponible: " + FICHERO);
            binding.textView.setText("");
        }
    }

    private void mostrarMensaje(String texto) {
        Toast.makeText(this, texto, Toast.LENGTH_LONG).show();
    }

}