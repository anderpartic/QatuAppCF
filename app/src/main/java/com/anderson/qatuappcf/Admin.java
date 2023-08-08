package com.anderson.qatuappcf;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
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

public class Admin extends AppCompatActivity {
    private ImageView imgVendedores, imgProductos;

    private Button btnSalir;

    private AppCompatImageButton btnCerrar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        btnCerrar = findViewById(R.id.btnCerrar);
        btnCerrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                salir();
            }
        });
        // Verificar el estado de inicio de sesión almacenado en SharedPreferences
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {

        } else {
            getApplicationContext();
        }

        imgVendedores = findViewById(R.id.imgVendedores);
        imgProductos = findViewById(R.id.imgProductos);
        imgProductos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Admin.this, AdminProducto.class);
                startActivity(i);
                finish();
            }
        });
        imgVendedores.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Admin.this, AdminVendedor.class);
                startActivity(i);
                finish();
            }
        });
    }
    public void salir(){
        FirebaseAuth.getInstance().signOut();
        GoogleSignIn.getClient(this, GoogleSignInOptions.DEFAULT_SIGN_IN).signOut().addOnCompleteListener(this, new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                // Redirigir al usuario a la pantalla de inicio de sesión
                Intent i = new Intent(Admin.this, MainActivity.class);
                startActivity(i);
                finish();
            }
        });
    }
}