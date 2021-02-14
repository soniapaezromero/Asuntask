package com.example.paez_sonia_asyntask.Network;

public class Resultado {
    private int codigo; //indica el código de estado devuelto por el servidor web
    private String mensaje; //información del error
    private String contenido; //fichero descargado
    public int getCodigo() {
        return codigo;
    }
    public void setCodigo(int codigo) {
        this.codigo = codigo;
    }
    public String getMensaje() {
        return mensaje;
    }
    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }
    public String getContenido() {
        return contenido;
    }
    public void setContenido(String contenido) {
        this.contenido = contenido;
    }
}
