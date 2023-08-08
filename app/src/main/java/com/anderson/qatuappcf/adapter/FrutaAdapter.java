package com.anderson.qatuappcf.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.anderson.qatuappcf.CarritoManager;
import com.anderson.qatuappcf.R;
import com.anderson.qatuappcf.model.Compras;
import com.anderson.qatuappcf.model.Fruit;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

import java.util.HashMap;

public class FrutaAdapter extends FirestoreRecyclerAdapter<Fruit, FrutaAdapter.ViewHolder> {
    private FirebaseFirestore mFirestore = FirebaseFirestore.getInstance();
    private Activity activity;
    private String userUid;
    private HashMap<String, Compras> carrito = new HashMap<>();
    private DatabaseReference carritoRef;

    public FrutaAdapter(@NonNull FirestoreRecyclerOptions<Fruit> options, Activity activity) {
        super(options);
        this.activity = activity;


        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        userUid = currentUser.getUid();
        carritoRef = FirebaseDatabase.getInstance().getReference().child("Persona").child(userUid).child("carrito");

        // Recuperar el carrito guardado en CarritoManager
        carrito = CarritoManager.getInstance().getCarrito();
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder viewHolder, int position, @NonNull Fruit fruit) {
        DocumentSnapshot documentSnapshot = getSnapshots().getSnapshot(viewHolder.getAdapterPosition());
        final String id = documentSnapshot.getId();

        viewHolder.tvCodigo.setText(fruit.getCodigo());
        viewHolder.tvNombre.setText(fruit.getNombre());
        viewHolder.tvVenta.setText(fruit.getPrecio());
        viewHolder.tvStock.setText(fruit.getStock());
        // Cargar la imagen utilizando Glide
        ImageView imgPro = viewHolder.itemView.findViewById(R.id.imgPro);
        String imageUrl = fruit.getPhoto(); // Asegúrate de tener una URL válida para la imagen en el atributo photo de la clase Pro
        Glide.with(activity)
                .load(imageUrl)

                .into(imgPro);

        // ... (código anterior)

        viewHolder.btnCarrito.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Obtenemos los detalles del producto seleccionado.
                String codigo = viewHolder.tvCodigo.getText().toString();
                String nombre = viewHolder.tvNombre.getText().toString();
                String precio = viewHolder.tvVenta.getText().toString();
                String stock = viewHolder.tvStock.getText().toString();

                // Obtenemos los datos del cliente (nombre y cedula) desde Firebase Realtime Database
                DatabaseReference clienteRef = FirebaseDatabase.getInstance().getReference().child("Persona").child(userUid);
                clienteRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            String nombreCliente = dataSnapshot.child("nombre").getValue(String.class);
                            String cedulaCliente = dataSnapshot.child("cedula").getValue(String.class);

                            int cantidad = 1; // Puedes obtener la cantidad seleccionada por el usuario aquí.
                            int stockC = 1;

                            // Calculamos el valor total a pagar.
                            double totalPagar = Double.parseDouble(precio) * cantidad;

                            // Verificamos si el producto ya está en el carrito
                            if (carrito.containsKey(codigo)) {
                                Compras productoExistente = carrito.get(codigo);
                                int cantidadExistente = Integer.parseInt(productoExistente.getCantidad());
                                cantidad = cantidadExistente + 1; // Aumenta la cantidad en 1
                                totalPagar = Double.parseDouble(precio) * cantidad;
                            }

                            // Verificamos si hay suficiente stock antes de agregar el producto al carrito
                            int newStock = Integer.parseInt(stock) - stockC;
                            if (newStock >= 0) {
                                // Agregamos o actualizamos el producto en el carrito del usuario en el HashMap
                                Compras productoCarrito = new Compras(codigo, nombre, precio, String.valueOf(cantidad), String.valueOf(totalPagar), nombreCliente, cedulaCliente);
                                carrito.put(codigo, productoCarrito);

                                // Actualizamos el carrito en la base de datos
                                carritoRef.setValue(carrito).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(activity, "Producto agregado al carrito", Toast.LENGTH_SHORT).show();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(activity, "Error al agregar el producto al carrito", Toast.LENGTH_SHORT).show();
                                    }
                                });

                                // Actualizamos el stock en Firestore (restamos la cantidad seleccionada).
                                mFirestore.collection("pro").document(id).update("stock", String.valueOf(newStock)).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        // Actualización exitosa del stock.
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        // Error al actualizar el stock.
                                    }
                                });
                            } else {
                                Toast.makeText(activity, "No hay suficiente stock", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            // El cliente no existe en la base de datos
                            Toast.makeText(activity, "No se encontraron datos del cliente", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        // Manejar el error en caso de que ocurra
                        Toast.makeText(activity, "Error al obtener datos del cliente", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

// ... (código posterior)

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.lista_fruta, parent, false);
        return new ViewHolder(v);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvCodigo, tvNombre, tvStock, tvVenta;
        Button btnCarrito;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCodigo = itemView.findViewById(R.id.tvCodigo);
            tvNombre = itemView.findViewById(R.id.tvNombre);
            tvStock = itemView.findViewById(R.id.tvStock);
            tvVenta = itemView.findViewById(R.id.tvVenta);
            btnCarrito = itemView.findViewById(R.id.btnCarrito);
        }
    }
}
