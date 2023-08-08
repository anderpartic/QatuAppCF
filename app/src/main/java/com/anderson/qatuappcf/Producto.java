package com.anderson.qatuappcf;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


public class Producto extends AppCompatActivity {
    private TextInputEditText etCodProducto, etNomPro, etPrecio, etStock;
    private Button btnVolver, btnGuardar;
    private ImageView photo_pro;
    private LinearLayout btnEdit, btnEliminar, linearLayout_image_btn;
    private FirebaseFirestore mfirestore;
    private StorageReference storageReference;
    private String storage_pro = "pro/*";
    private static final int COD_SEL_IMAGE = 300;
    private static final int COD_SEL_STORAGE = 200;
    private Uri image_url;

    String photo = "photo";
    String idd;
    ProgressDialog progressDialog;

    private Spinner spTipo;
    String[] Tipo = {"Tipo de producto: ", "Fruta", "Verdura", "Carnes", "Mariscos"};

    private TextView textView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_producto);
        progressDialog = new ProgressDialog(this);
        String id = getIntent().getStringExtra("id_pro");
        mfirestore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        linearLayout_image_btn = findViewById(R.id.images_btn);

        photo_pro = findViewById(R.id.photo_pro);
        btnEdit = findViewById(R.id.btnEdit);
        btnEliminar = findViewById(R.id.btnEliminar);
        btnEliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HashMap<String, Object>map = new HashMap<>();
                map.put("photo", "");
                mfirestore.collection("pro").document(idd).update(map);
                Toast.makeText(Producto.this, "Foto Eliminada", Toast.LENGTH_SHORT).show();
            }
        });
        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadPhoto();
            }
        });
        etCodProducto = findViewById(R.id.etCodProducto);
        etNomPro = findViewById(R.id.etNomPro);
        etPrecio = findViewById(R.id.etPrecio);
        etStock = findViewById(R.id.etStock);
        spTipo = findViewById(R.id.spTipo);
        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, Tipo);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spTipo.setAdapter(adapter);
        btnGuardar = findViewById(R.id.btnGuardar);
        textView = findViewById(R.id.textView);
        if (id == null || id == "") {
            linearLayout_image_btn.setVisibility(View.VISIBLE);
            btnGuardar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String cod = etCodProducto.getText().toString().trim();
                    String nom = etNomPro.getText().toString().trim();
                    String pre = etPrecio.getText().toString().trim();
                    String stock = etStock.getText().toString().trim();
                    String selectedType = spTipo.getSelectedItem().toString();
                    if (cod.isEmpty() || nom.isEmpty() || pre.isEmpty() || stock.isEmpty() || selectedType.equals("Tipo de producto: ")) {
                        Toast.makeText(Producto.this, "Llenar todos los campos", Toast.LENGTH_SHORT).show();

                    } else {

                        postPro(cod, nom, pre, stock, selectedType);

                    }
                }
            });

        } else {
            idd = id;
            btnGuardar.setText("Actualizar");
            textView.setText("Actualizar Producto");
            getPro(id);
            btnGuardar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String cod = etCodProducto.getText().toString().trim();
                    String nom = etNomPro.getText().toString().trim();
                    String pre = etPrecio.getText().toString().trim();
                    String stock = etStock.getText().toString().trim();
                    String selectedType = spTipo.getSelectedItem().toString();

                    if (cod.isEmpty() || nom.isEmpty() || pre.isEmpty() || stock.isEmpty() || selectedType.equals("Tipo de producto: ")) {
                        Toast.makeText(Producto.this, "Llenar todos los campos", Toast.LENGTH_SHORT).show();

                    } else {
                        updatePro(cod, nom, pre, stock, selectedType, id);

                    }
                }
            });

        }

        btnVolver = findViewById(R.id.btnVolver);
        btnVolver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Producto.this, AdminProducto.class);
                startActivity(i);
                finish();
            }
        });
    }

    private void uploadPhoto() {
        Intent i = new Intent(Intent.ACTION_PICK);
        i.setType("image/*");
        startActivityForResult(i, COD_SEL_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == COD_SEL_IMAGE && data != null) { // Añadir esta condición
                image_url = data.getData();
                subirPhoto(image_url);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    private void subirPhoto(Uri image_url) {
        progressDialog.setMessage("Actualizando foto");
        progressDialog.show();
        String rute_storage_photo = storage_pro + "" + photo + "" + idd;
        StorageReference reference = storageReference.child(rute_storage_photo);
        reference.putFile(image_url).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                while (!uriTask.isSuccessful()) ;
                if (uriTask.isSuccessful()) {
                    uriTask.addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String download_uri = uri.toString();
                            HashMap<String, Object> map = new HashMap<>();
                            map.put("photo", download_uri);
                            mfirestore.collection("pro").document(idd).update(map);
                            Toast.makeText(Producto.this, "Foto actualizada", Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                        }
                    });
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(Producto.this, "Error al cargar foto", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void updatePro(String cod, String nom, String pre, String stock, String selectedType, String id) {
        Map<String, Object> map = new HashMap<>();
        map.put("codigo", cod);
        map.put("nombre", nom);
        map.put("precio", pre);
        map.put("stock", stock);
        map.put("tipo", selectedType);
        // Actualizar en Firestore
        mfirestore.collection("pro").document(id).update(map)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(Producto.this, "Actualizado correctamente", Toast.LENGTH_SHORT).show();

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(Producto.this, "Error al actualizar", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void postPro(final String cod, final String nom, String pre, String stock, String selectedType) {

        // Consultar Firestore para verificar si el código o nombre ya existen
        Query query = mfirestore.collection("pro")
                .whereEqualTo("codigo", cod)
                .limit(1);
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    if (task.getResult() != null && !task.getResult().isEmpty()) {
                        // Ya existe un producto con el mismo código en Firestore
                        Toast.makeText(Producto.this, "El código del producto ya existe", Toast.LENGTH_SHORT).show();
                    } else {
                        // No hay otro producto con el mismo código, verificar el nombre
                        Query query = mfirestore.collection("pro")
                                .whereEqualTo("nombre", nom)
                                .limit(1);
                        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    if (task.getResult() != null && !task.getResult().isEmpty()) {
                                        // Ya existe un producto con el mismo nombre en Firestore
                                        Toast.makeText(Producto.this, "El nombre del producto ya existe", Toast.LENGTH_SHORT).show();
                                    } else {
                                        // No hay otro producto con el mismo código ni nombre, proceder a guardar
                                        Map<String, Object> map = new HashMap<>();
                                        map.put("codigo", cod);
                                        map.put("nombre", nom);
                                        map.put("precio", pre);
                                        map.put("stock", stock);
                                        map.put("tipo", selectedType);
                                        // Guardar en Firestore
                                        mfirestore.collection("pro").add(map)
                                                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                                    @Override
                                                    public void onSuccess(DocumentReference documentReference) {
                                                        // Producto creado exitosamente en Firestore
                                                        Toast.makeText(Producto.this, "Producto creado exitosamente", Toast.LENGTH_SHORT).show();
                                                        limpiar();
                                                    }
                                                })
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        // Error al guardar en Firestore
                                                        Toast.makeText(Producto.this, "Error al ingresar datos en Firestore", Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                    }
                                } else {
                                    // Error al realizar la consulta de nombre en Firestore
                                    Toast.makeText(Producto.this, "Error al consultar datos en Firestore", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                } else {
                    // Error al realizar la consulta de código en Firestore
                    Toast.makeText(Producto.this, "Error al consultar datos en Firestore", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void getPro(String id) {
        mfirestore.collection("pro").document(id).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                String cod = documentSnapshot.getString("codigo");
                String nombre = documentSnapshot.getString("nombre");
                String stock = documentSnapshot.getString("stock");
                String precio = documentSnapshot.getString("precio");
                String tipo = documentSnapshot.getString("tipo");
                String photoPro = documentSnapshot.getString("photo");

                etCodProducto.setText(cod);
                etNomPro.setText(nombre);
                etStock.setText(stock);
                etPrecio.setText(precio);
                try {
                    if (!photoPro.equals("")) {
                        Toast toast = Toast.makeText(getApplicationContext(), "Cargando Foto", Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.TOP, 0, 200);
                        toast.show();
                        Picasso.get()
                                .load(photoPro)
                                .resize(150, 150)
                                .into(photo_pro);
                    }
                    // Buscar la posición del elemento "tipo" en el array "Tipo"
                    int tipoPosition = -1;
                    for (int i = 0; i < Tipo.length; i++) {
                        if (Tipo[i].equals(tipo)) {
                            tipoPosition = i;
                            break;
                        }
                    }
                    // Establecer la selección en el Spinner "spTipo"
                    if (tipoPosition != -1) {
                        spTipo.setSelection(tipoPosition);
                    } else {
                        // Si el tipo no se encuentra en el array, se establecerá el primer elemento como predeterminado
                        spTipo.setSelection(0);
                    }
                } catch (Exception e) {
                    Log.v("Error", "e: " + e);
                }


            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(Producto.this, "Error al obtener los datos", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void limpiar() {
        spTipo.setSelection(0);
        etCodProducto.setText(null);
        etNomPro.setText(null);
        etPrecio.setText(null);
        etStock.setText(null);
    }

}