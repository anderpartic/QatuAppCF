package com.anderson.qatuappcf;

import com.anderson.qatuappcf.model.Compras;

import java.util.HashMap;

public class CarritoManager {
    private static CarritoManager instance;
    private HashMap<String, Compras> carrito = new HashMap<>();

    private CarritoManager() {
    }

    public static CarritoManager getInstance() {
        if (instance == null) {
            instance = new CarritoManager();
        }
        return instance;
    }

    public HashMap<String, Compras> getCarrito() {
        return carrito;
    }

    public void setCarrito(HashMap<String, Compras> carrito) {
        this.carrito = carrito;
    }

    // Otras funciones de manejo del carrito, si las necesitas.
}

