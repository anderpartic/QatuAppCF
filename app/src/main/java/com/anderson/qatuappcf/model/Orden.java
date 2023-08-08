package com.anderson.qatuappcf.model;

import java.util.HashMap;

public class Orden {
    private String clienteUid; // El UID del cliente que hizo el pedido
    private String clienteNombre; // El nombre del cliente que hizo el pedido
    private String clienteCedula; // La cédula del cliente que hizo el pedido
    private HashMap<String, Compras> productos; // Los productos del carrito del cliente

    public Orden() {
    }

    public Orden(String clienteUid, String clienteNombre, String clienteCedula, HashMap<String, Compras> productos) {
        this.clienteUid = clienteUid;
        this.clienteNombre = clienteNombre;
        this.clienteCedula = clienteCedula;
        this.productos = productos;
    }

    public String getClienteUid() {
        return clienteUid;
    }

    public void setClienteUid(String clienteUid) {
        this.clienteUid = clienteUid;
    }

    public String getClienteNombre() {
        return clienteNombre;
    }

    public void setClienteNombre(String clienteNombre) {
        this.clienteNombre = clienteNombre;
    }

    public String getClienteCedula() {
        return clienteCedula;
    }

    public void setClienteCedula(String clienteCedula) {
        this.clienteCedula = clienteCedula;
    }

    public HashMap<String, Compras> getProductos() {
        return productos;
    }

    public void setProductos(HashMap<String, Compras> productos) {
        this.productos = productos;
    }
}
