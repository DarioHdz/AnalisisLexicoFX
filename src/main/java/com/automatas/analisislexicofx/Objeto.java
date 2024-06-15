package com.automatas.analisislexicofx;

public class Objeto {

    int NodoOrigen;
    int NodoDestino;
    String validacion;

    public Objeto(int origen, int destino, String validacion) {
        this.NodoOrigen = origen;
        this.NodoDestino = destino;
        this.validacion = validacion;
    }

    public int getNodoOrigen() {
        return NodoOrigen;
    }

    public void setNodoOrigen(int nodoOrigen) {
        NodoOrigen = nodoOrigen;
    }

    public int getNodoDestino() {
        return NodoDestino;
    }

    public void setNodoDestino(int nodoDestino) {
        NodoDestino = nodoDestino;
    }

    public String getValidacion() {
        return validacion;
    }

    public void setValidacion(String validacion) {
        this.validacion = validacion;
    }
}
