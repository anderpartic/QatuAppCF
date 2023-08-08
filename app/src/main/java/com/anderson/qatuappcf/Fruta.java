package com.anderson.qatuappcf;


import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.SearchView;

import com.anderson.qatuappcf.adapter.FrutaAdapter;
import com.anderson.qatuappcf.adapter.ProAdapter;
import com.anderson.qatuappcf.model.Fruit;
import com.anderson.qatuappcf.model.Pro;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class Fruta extends AppCompatActivity implements SearchView.OnQueryTextListener {


    private AppCompatImageButton back_button;
    private RecyclerView recyclerView;
    private FrutaAdapter proAdapter;
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fruta);
        back_button = findViewById(R.id.back_button);
        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Fruta.this, Cliente.class);
                startActivity(i);
                finish();
            }
        });

        SearchView searchView = findViewById(R.id.search);
        searchView.setOnQueryTextListener(this);

        firestore = FirebaseFirestore.getInstance();
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        Query query = firestore.collection("pro").whereEqualTo("tipo", "Fruta");
        FirestoreRecyclerOptions<Fruit> proFirestoreRecyclerOptions =
                new FirestoreRecyclerOptions.Builder<Fruit>().setQuery(query, Fruit.class).build();
        proAdapter = new FrutaAdapter(proFirestoreRecyclerOptions, this);
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
            Query query = firestore.collection("pro").whereEqualTo("tipo", "Fruta");

            FirestoreRecyclerOptions<Fruit> proFirestoreRecyclerOptions =
                    new FirestoreRecyclerOptions.Builder<Fruit>().setQuery(query, Fruit.class).build();

            // Detener y actualizar el adaptador con las nuevas opciones
            proAdapter.stopListening();
            proAdapter = new FrutaAdapter(proFirestoreRecyclerOptions, this);
            recyclerView.setAdapter(proAdapter);
            proAdapter.startListening();
        } else {
            // Si hay texto de búsqueda, realizar la búsqueda filtrada
            Query query = firestore.collection("pro")
                    .whereEqualTo("nombre", newText); // Esto asegura que la búsqueda sea sensible a mayúsculas y minúsculas

            FirestoreRecyclerOptions<Fruit> proFirestoreRecyclerOptions =
                    new FirestoreRecyclerOptions.Builder<Fruit>().setQuery(query, Fruit.class).build();

            // Detener y actualizar el adaptador con las nuevas opciones
            proAdapter.stopListening();
            proAdapter = new FrutaAdapter(proFirestoreRecyclerOptions, this);
            recyclerView.setAdapter(proAdapter);
            proAdapter.startListening();
        }

        return true;
    }
}
