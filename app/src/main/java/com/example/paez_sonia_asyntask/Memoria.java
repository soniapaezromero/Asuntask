package com.example.paez_sonia_asyntask;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Memoria {

    public static boolean escribirExterna(String fichero, String cadena) throws IOException {
        File miFichero;
        File tarjeta = Environment.getExternalStorageDirectory();
        miFichero = new File(tarjeta.getAbsolutePath(), fichero);
        return escribir(miFichero, cadena);
    }

    private static boolean escribir(File fichero, String cadena) throws IOException {
        FileOutputStream fos = null;
        OutputStreamWriter osw = null;
        BufferedWriter bw = null;
        boolean correcto = true;

        fos = new FileOutputStream(fichero);
        osw = new OutputStreamWriter(fos);
        bw = new BufferedWriter(osw);
        bw.write(cadena);
        bw.close();

        return correcto;
    }

    public static String mostrarPropiedades(File fichero) {
        SimpleDateFormat formato = null;
        StringBuilder texto = new StringBuilder();

        if (fichero.exists()) {
            texto.append("Nombre: " + fichero.getName() + '\n');
            texto.append("Ruta: " + fichero.getAbsolutePath() + '\n');
            texto.append("Tamaño: " + Long.toString(fichero.length()) + '\n');
            formato = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss", Locale.getDefault());
            texto.append("Fecha: " + formato.format(new Date(fichero.lastModified())));
        } else {
            texto.append("No existe el fichero");
        }
        return texto.toString();
    }

    public static String mostrarPropiedadesExterna(String fichero) {
        File miFichero, tarjeta;
        tarjeta = Environment.getExternalStorageDirectory();
        miFichero = new File(tarjeta.getAbsolutePath(), fichero);

        return mostrarPropiedades(miFichero);
    }

    public static boolean disponibleEscritura() {
        boolean escritura = false;
        String estado = Environment.getExternalStorageState();
        if (estado.equals(Environment.MEDIA_MOUNTED)) {
            escritura = true;
        }
        return escritura;
    }

    public static boolean disponibleLectura() {
        boolean lectura = false;
        String estado = Environment.getExternalStorageState();
        if (estado.equals(Environment.MEDIA_MOUNTED) || estado.equals(Environment.MEDIA_MOUNTED_READ_ONLY)) {
            lectura = true;
        }
        return lectura;

    }

    public static String leerExterna(String fichero) throws IOException {

        File miFichero;
        File tarjeta = Environment.getExternalStorageDirectory();
        miFichero = new File(tarjeta.getAbsolutePath(), fichero);
        return leer(miFichero);


    }

    private static String leer(File fichero) throws IOException {
        FileInputStream fis = null;
        InputStreamReader isr = null;
        BufferedReader br = null;

        String linea;
        StringBuilder cadena = new StringBuilder();

        fis = new FileInputStream(fichero);
        isr = new InputStreamReader(fis);
        br = new BufferedReader(isr);

        while ((linea = br.readLine()) != null) {
            cadena.append(linea).append('\n');
        }
        br.close();
        return cadena.toString();
    }

    public static boolean imagenExterna(String fichero,Bitmap imagen) throws IOException {
        File miFichero;
        File tarjeta = Environment.getExternalStorageDirectory();
        miFichero = new File(tarjeta.getAbsolutePath(),fichero);
        return escribirImagen(miFichero, imagen);
    }

    public static boolean escribirImagen(File fichero, Bitmap imagen) {

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        imagen.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        boolean correcto = true;
        try {
            FileOutputStream outputStream = new FileOutputStream(fichero);
            outputStream.write(byteArray);
            outputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e2) {
            e2.printStackTrace();
        }
        return correcto;
    }
    public static Bitmap leerExternaImagen(String fichero) throws IOException {

        File miFichero;
        File tarjeta = Environment.getExternalStorageDirectory();
        miFichero = new File(tarjeta.getAbsolutePath(), fichero);
        return MostrarImagen(miFichero);

    }

    public static Bitmap MostrarImagen(File fichero) {
        Bitmap bitmap = null;

        try{
            FileInputStream fileInputStream =
                    new FileInputStream(fichero);
            bitmap = BitmapFactory.decodeStream(fileInputStream);
        }catch (IOException io){
            io.printStackTrace();
        }
        return bitmap;
    }


}
