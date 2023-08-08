package com.anderson.qatuappcf;

import android.support.annotation.NonNull;

import com.google.firebase.messaging.FirebaseMessagingService;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
        // Guarda el token en la base de datos o realiza cualquier otra acción necesaria.
        // Aquí debes actualizar el token del vendedor en la base de datos.
    }
}
