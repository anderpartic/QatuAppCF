package com.anderson.qatuappcf;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageButton;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Vendedor extends AppCompatActivity {

    private Button btnSalir;
    private AppCompatImageButton btnCerrar;

    private ImageView imgPedidos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vendedor);
        btnCerrar = findViewById(R.id.btnCerrar);
        btnCerrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                salir();
            }
        });
        imgPedidos = findViewById(R.id.imgPedidos);
        imgPedidos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Vendedor.this, Pedidos.class);
                startActivity(i);
                finish();
            }
        });

        // Verificar el estado de inicio de sesión almacenado en SharedPreferences
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {

        } else {
            getApplicationContext();
        }


    }

    public void salir(){
        FirebaseAuth.getInstance().signOut();
        GoogleSignIn.getClient(this, GoogleSignInOptions.DEFAULT_SIGN_IN).signOut().addOnCompleteListener(this, new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                // Redirigir al usuario a la pantalla de inicio de sesión
                Intent i = new Intent(Vendedor.this, MainActivity.class);
                startActivity(i);
                finish();
            }
        });
    }
}