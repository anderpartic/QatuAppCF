package com.anderson.qatuappcf;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.SearchView;
import android.widget.Toast;


import androidx.appcompat.app.AppCompatActivity;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.anderson.qatuappcf.adapter.ProAdapter;
import com.anderson.qatuappcf.model.Pro;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class AdminProducto extends AppCompatActivity implements SearchView.OnQueryTextListener{

    private RecyclerView recyclerView;
    private ProAdapter proAdapter;
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_producto);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("");


        SearchView searchView = findViewById(R.id.search);
        searchView.setOnQueryTextListener(this);

        firestore = FirebaseFirestore.getInstance();
        recyclerView = findViewById(R.id.recyclerView);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        Query query = firestore.collection("pro");
        FirestoreRecyclerOptions<Pro> proFirestoreRecyclerOptions =
                new FirestoreRecyclerOptions.Builder<Pro>().setQuery(query, Pro.class).build();
        proAdapter = new ProAdapter(proFirestoreRecyclerOptions, this);
        recyclerView.setAdapter(proAdapter);

    }

    @Override
    protected void onStart() {
        super.onStart();
        proAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        proAdapter.stopListening();
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        // Se llama cuando el usuario cambia el texto en el SearchView

        // Si el texto de búsqueda está vacío, mostrar todos los productos
        if (newText.isEmpty()) {
            Query query = firestore.collection("pro");

            FirestoreRecyclerOptions<Pro> proFirestoreRecyclerOptions =
                    new FirestoreRecyclerOptions.Builder<Pro>().setQuery(query, Pro.class).build();

            // Detener y actualizar el adaptador con las nuevas opciones
            proAdapter.stopListening();
            proAdapter = new ProAdapter(proFirestoreRecyclerOptions, this);
            recyclerView.setAdapter(proAdapter);
            proAdapter.startListening();
        } else {
            // Si hay texto de búsqueda, realizar la búsqueda filtrada
            Query query = firestore.collection("pro")
                    .whereEqualTo("nombre", newText); // Esto asegura que la búsqueda sea sensible a mayúsculas y minúsculas

            FirestoreRecyclerOptions<Pro> proFirestoreRecyclerOptions =
                    new FirestoreRecyclerOptions.Builder<Pro>().setQuery(query, Pro.class).build();

            // Detener y actualizar el adaptador con las nuevas opciones
            proAdapter.stopListening();
            proAdapter = new ProAdapter(proFirestoreRecyclerOptions, this);
            recyclerView.setAdapter(proAdapter);
            proAdapter.startListening();
        }

        return true;
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_pro, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.back_option) {
            Intent i = new Intent(AdminProducto.this, Admin.class);
            startActivity(i);
            finish();
            return true;
        } else if (id == R.id.add_option) {
            Intent i = new Intent(AdminProducto.this, Producto.class);
            startActivity(i);
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}