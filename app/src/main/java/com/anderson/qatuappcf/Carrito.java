package com.anderson.qatuappcf;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageButton;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.anderson.qatuappcf.model.Compras;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Transaction;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import io.reactivex.rxjava3.annotations.NonNull;

public class Carrito extends AppCompatActivity {

    private AppCompatImageButton back_button;
    private TableLayout tableLayoutCarrito;
    private DatabaseReference carritoRef;
    private int numeroOrden = 1;
    private TextView tvOrden, ValorTotal;
    private double valorTotal = 0.0;
    private Button btnReservar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_carrito);
        btnReservar = findViewById(R.id.btnReservar);
        btnReservar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                obtenerVendedorAlAzar();
            }
        });

        tvOrden = findViewById(R.id.tvOrden);
        ValorTotal = findViewById(R.id.ValorTotal);
        tableLayoutCarrito = findViewById(R.id.tableLayoutCarrito);

        // Obtén la referencia de Firebase Realtime Database
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        String userUid = currentUser.getUid();
        carritoRef = FirebaseDatabase.getInstance().getReference().child("Persona").child(userUid).child("carrito");


        back_button = findViewById(R.id.back_button);
        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Carrito.this, Cliente.class);
                startActivity(i);
                finish();
            }
        });
// Agregar un listener para obtener los datos del carrito
        carritoRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Limpiar las filas anteriores del TableLayout
                tableLayoutCarrito.removeAllViews();
                // Iterar sobre los datos del carrito y mostrarlos en la tabla
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Compras productoCarrito = snapshot.getValue(Compras.class);
                    // Mostrar los datos en la tabla
                    TableRow row = new TableRow(Carrito.this);
                    row.setLayoutParams(new TableRow.LayoutParams(
                            TableRow.LayoutParams.MATCH_PARENT,
                            TableRow.LayoutParams.WRAP_CONTENT
                    ));
                    addCellToRow(row, productoCarrito.getNombre(), 10, 50); // Segunda celda con más espacio a la derecha
                    addCellToRow(row, productoCarrito.getPrecio(), 170, 10); // Tercera celda con más espacio a la izquierda
                    addCellToRow(row, productoCarrito.getCantidad(), 170, 10); // Tercera celda con más espacio a la izquierda
                    addCellToRow(row, String.valueOf(productoCarrito.getSubTotal()), 100, 10); // Quinta celda con más espacio a la izquierda
                    // Agregar la fila al TableLayout
                    tableLayoutCarrito.addView(row);
                    double subtotal = Double.parseDouble(productoCarrito.getSubTotal());
                    valorTotal += subtotal; // Sumar directamente los valores numéricos
                    ValorTotal.setText(String.format("$%.2f", valorTotal));
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Manejar el error en caso de que ocurra
                Toast.makeText(Carrito.this, "Error al obtener datos del carrito", Toast.LENGTH_SHORT).show();
            }
        });

    }

    // Método para agregar una celda (TextView) a una fila (TableRow)
    private void addCellToRow(TableRow row, String text, int paddingStart, int paddingEnd) {
        TextView cell = new TextView(this);
        cell.setText(text);
        cell.setTextColor(Color.WHITE);
        cell.setPaddingRelative(paddingStart, 10, paddingEnd, 10);
        cell.setGravity(Gravity.START); // Alineación a la izquierda (start)
        row.addView(cell);

    }

    private void obtenerVendedorAlAzar() {
        DatabaseReference vendedoresRef = FirebaseDatabase.getInstance().getReference().child("Persona");
        vendedoresRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<String> vendedores = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    // Verificamos si el nodo es un vendedor
                    if (snapshot.child("tipo").getValue(String.class).equals("vendedor")) {
                        vendedores.add(snapshot.getKey());
                    }
                }

                if (!vendedores.isEmpty()) {
                    // Obtener un vendedor al azar
                    Random random = new Random();
                    int randomIndex = random.nextInt(vendedores.size());
                    String vendedorUid = vendedores.get(randomIndex);

                    // Ahora puedes utilizar vendedorUid como el userUid del vendedor seleccionado al azar
                    // Por ejemplo, podrías almacenar vendedorUid en una variable de clase para usarlo en otro lugar
                    // Por ahora, lo usaremos directamente para agregar el pedido al vendedor seleccionado al azar
                    realizarPedidoVendedorAzar(vendedorUid);
                } else {
                    Toast.makeText(Carrito.this, "No se encontraron vendedores", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(Carrito.this, "Error al obtener vendedores", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void realizarPedidoVendedorAzar(String vendedorUid) {
        // Obtén la referencia del vendedor seleccionado al azar
        DatabaseReference vendedorRef = FirebaseDatabase.getInstance().getReference().child("Persona").child(vendedorUid);

        // Obtén la referencia al carrito del cliente actual
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        String userUid = currentUser.getUid();
        DatabaseReference carritoClienteRef = FirebaseDatabase.getInstance().getReference().child("Persona").child(userUid).child("carrito");
        // Agrega un listener para obtener los productos del carrito del cliente
        carritoClienteRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {

                    // Itera sobre los productos del carrito y agrégalos al pedido del vendedor seleccionado
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Compras productoCarrito = snapshot.getValue(Compras.class);
                        DatabaseReference productoRef = vendedorRef.child("Pedidos").child("productos").push();
                        productoRef.child("nombre").setValue(productoCarrito.getNombre());
                        productoRef.child("precio").setValue(productoCarrito.getPrecio());
                        productoRef.child("cantidad").setValue(productoCarrito.getCantidad());
                        productoRef.child("subTotal").setValue(productoCarrito.getSubTotal());
                        productoRef.child("nombreCliente").setValue(productoCarrito.getNombreCliente());
                        productoRef.child("cedulaCliente").setValue(productoCarrito.getCedulaCliente());
                    }

                    // Una vez que se han agregado los productos, puedes eliminar el carrito del cliente
                    carritoClienteRef.removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            // Carrito eliminado exitosamente
                            Toast.makeText(Carrito.this, "Pedido realizado", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // Error al eliminar el carrito
                            Toast.makeText(Carrito.this, "Error al realizar el pedido", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    // El carrito del cliente está vacío, mostrar un mensaje o realizar alguna acción
                    Toast.makeText(Carrito.this, "El carrito está vacío", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Manejar el error en caso de que ocurra
                Toast.makeText(Carrito.this, "Error al obtener productos del carrito", Toast.LENGTH_SHORT).show();
            }
        });
    }


}
