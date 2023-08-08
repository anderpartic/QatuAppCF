package com.anderson.qatuappcf;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties

public class Ven {
    private String cedula, email, nombre, key, ingresos, egresos, total, authUserID;

    public Ven() {
    }

    public Ven(String cedula, String email, String nombre, String key, String ingresos, String egresos, String Total, String authUserID) {
        this.cedula = cedula;
        this.email = email;
        this.nombre = nombre;
        this.key = key;
        this.ingresos = ingresos;
        this.egresos = egresos;
        this.total = total;
        this.authUserID = authUserID;
    }

    public String getAuthUserID() {
        return authUserID;
    }

    public void setAuthUserID(String authUserID) {
        this.authUserID = authUserID;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public String getIngresos() {
        return ingresos;
    }

    public void setIngresos(String ingresos) {
        this.ingresos = ingresos;
    }

    public String getEgresos() {
        return egresos;
    }
    public void setEgresos(String egresos) {
        this.egresos = egresos;
    }
    public String getKey() {
        return key;
    }
    public void setKey(String key) {
        this.key = key;
    }
    public String getCedula() {
        return cedula;
    }

    public String getEmail() {
        return email;
    }


    public String getNombre() {
        return nombre;
    }


}
