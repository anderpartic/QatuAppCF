package com.anderson.qatuappcf;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

public class Cliente extends AppCompatActivity {

    private ImageView btnFruta, btnVerdura, btnCarne, btnMarisco;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cliente);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("");


        btnFruta = findViewById(R.id.btnFruta);
        btnFruta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Cliente.this, Fruta.class);
                startActivity(i);
                finish();
            }
        });
        btnVerdura = findViewById(R.id.btnVerdura);
        btnVerdura.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Cliente.this, Verdura.class);
                startActivity(i);
                finish();
            }
        });
        btnCarne = findViewById(R.id.btnCarne);
        btnCarne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Cliente.this, Carnes.class);
                startActivity(i);
                finish();
            }
        });
        btnMarisco = findViewById(R.id.btnMarisco);
        btnMarisco.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Cliente.this, Mariscos.class);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.map_option) {
            // Agregar aquí el código para abrir la actividad que muestra el mapa
            Intent intent = new Intent(Cliente.this, MapActivity.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.car_option) {
            Intent i = new Intent(Cliente.this, Carrito.class);
            startActivity(i);
            finish();
        } else if (id == R.id.cerrar_sesion_option) {
            salir();
        }
        return super.onOptionsItemSelected(item);
    }
    public void salir() {
        FirebaseAuth.getInstance().signOut();
        GoogleSignIn.getClient(this, GoogleSignInOptions.DEFAULT_SIGN_IN).signOut().addOnCompleteListener(this, new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                // Redirigir al usuario a la pantalla de inicio de sesión
                Intent i = new Intent(Cliente.this, MainActivity.class);
                startActivity(i);
                finish();
                Toast.makeText(Cliente.this, "Gracias por su visita", Toast.LENGTH_SHORT).show();
            }
        });
    }
}