package com.anderson.qatuappcf;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.anderson.qatuappcf.adapter.PedidosAdapter;
import com.anderson.qatuappcf.model.Compras;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Pedidos extends AppCompatActivity {
    private AppCompatImageButton back_button;
    private RecyclerView recyclerView;

    private String vendedorUid; // Variable para almacenar el userUid del vendedor actual

    private List<Compras> productosVendedor; // Lista de productos del vendedor


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pedidos);

        back_button = findViewById(R.id.back_button);
        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Pedidos.this, Vendedor.class);
                startActivity(i);
                finish();
            }
        });


        // Obtener el userUid del vendedor actual
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        vendedorUid = currentUser.getUid();

        // Inicializar la lista de productos del vendedor
        productosVendedor = new ArrayList<>();

        // Obtener la referencia a la base de datos y la referencia a los productos del vendedor
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference productosVendedorRef = databaseRef.child("Persona").child(vendedorUid).child("Pedidos").child("productos");

        // Agregar un evento para obtener los datos de los productos del vendedor
        productosVendedorRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                productosVendedor.clear(); // Limpiar la lista antes de agregar los nuevos productos
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    // Obtener cada producto y agregarlo a la lista
                    Compras producto = snapshot.getValue(Compras.class);
                    if (producto != null) {
                        productosVendedor.add(producto);
                    }
                }
                // Ahora, agrupa los productos con la misma cédula y nombre en un mapa
                Map<String, List<Compras>> productosAgrupados = new HashMap<>();
                for (Compras producto : productosVendedor) {
                    String clave = producto.getCedulaCliente() + "_" + producto.getNombreCliente();
                    if (productosAgrupados.containsKey(clave)) {
                        productosAgrupados.get(clave).add(producto);
                    } else {
                        List<Compras> listaProductos = new ArrayList<>();
                        listaProductos.add(producto);
                        productosAgrupados.put(clave, listaProductos);
                    }
                }

                List<Compras> productosFinal = new ArrayList<>();
                for (List<Compras> listaProductos : productosAgrupados.values()) {
                    productosFinal.addAll(listaProductos);
                }

                // Configurar el RecyclerView y el adaptador
                recyclerView = findViewById(R.id.recyclerView);
                recyclerView.setLayoutManager(new LinearLayoutManager(Pedidos.this));
                PedidosAdapter adapter = new PedidosAdapter(productosFinal);
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Manejar el error si ocurre
                Toast.makeText(Pedidos.this, "Error al obtener datos de productos", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
