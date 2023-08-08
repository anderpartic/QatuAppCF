package com.anderson.qatuappcf;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class AdminVendedor extends AppCompatActivity implements SearchView.OnQueryTextListener {
    RecyclerView recyclerView;
    ArrayList<Ven> list;
    VenAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_vendedor);


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("");

        SearchView searchView = findViewById(R.id.search);
        searchView.setOnQueryTextListener(this);




        recyclerView = findViewById(R.id.recyclerView);
        list = new ArrayList<>();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new VenAdapter(this, list);
        recyclerView.setAdapter(adapter);

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Persona");
        Query query = databaseReference.orderByChild("tipo").equalTo("vendedor");

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear(); // Limpiamos la lista para evitar duplicados
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Ven lista = dataSnapshot.getValue(Ven.class);
                    lista.setKey(dataSnapshot.getKey()); // Agregar el ID del vendedor a la clase Ven
                    list.add(lista);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Manejar errores si la consulta es cancelada o falla
            }
        });
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        // Cuando el usuario cambie el texto en el SearchView, se llamará a este método
        // Realiza la búsqueda utilizando el texto ingresado por el usuario
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Persona");
        Query query = databaseReference.orderByChild("tipo").equalTo("vendedor");

        if (!newText.isEmpty()) {
            // Filtra la consulta para que coincida con la cedula del vendedor
            query = databaseReference.orderByChild("cedula").equalTo(newText);
        }

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear(); // Limpiamos la lista para evitar duplicados
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Ven lista = dataSnapshot.getValue(Ven.class);
                    list.add(lista);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Manejar errores si la consulta es cancelada o falla
            }
        });

        return true;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_vendedor, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.back_option) {
            Intent i = new Intent(AdminVendedor.this, Admin.class);
            startActivity(i);
            finish();
            return true;
        } else if (id == R.id.add_option) {
            Intent i = new Intent(AdminVendedor.this, RegistrarVendedor.class);
            startActivity(i);
            finish();
            Toast.makeText(AdminVendedor.this, "Vamos a crear una cuenta al vendedor", Toast.LENGTH_SHORT).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
