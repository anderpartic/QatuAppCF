package com.anderson.qatuappcf.model;

public class Compras {
    String codigo, nombre, precio, cantidad, subTotal, nombreCliente, cedulaCliente;

    public Compras() {
    }

    public Compras(String codigo, String nombre, String precio, String cantidad, String subTotal, String nombreCliente, String cedulaCliente) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.precio = precio;
        this.cantidad = cantidad;
        this.subTotal = subTotal;
        this.nombreCliente = nombreCliente;
        this.cedulaCliente = cedulaCliente;
    }
    // Implementar la función esMismoCliente para comparar si dos Compras representan el mismo cliente
    public boolean esMismoCliente(Compras otro) {
        return cedulaCliente.equals(otro.cedulaCliente) && nombreCliente.equals(otro.nombreCliente);
    }

    public String getNombreCliente() {
        return nombreCliente;
    }

    public void setNombreCliente(String nombreCliente) {
        this.nombreCliente = nombreCliente;
    }

    public String getCedulaCliente() {
        return cedulaCliente;
    }

    public void setCedulaCliente(String cedulaCliente) {
        this.cedulaCliente = cedulaCliente;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getPrecio() {
        return precio;
    }

    public void setPrecio(String precio) {
        this.precio = precio;
    }

    public String getCantidad() {
        return cantidad;
    }

    public void setCantidad(String cantidad) {
        this.cantidad = cantidad;
    }

    public String getSubTotal() {
        return subTotal;
    }

    public void setSubTotal(String subTotal) {
        this.subTotal = subTotal;
    }
}
