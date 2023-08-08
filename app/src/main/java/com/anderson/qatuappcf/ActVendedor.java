package com.anderson.qatuappcf;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import io.reactivex.rxjava3.annotations.NonNull;

public class ActVendedor extends AppCompatActivity {

    private TextInputEditText etCedula, etEmail, etNom, etIngresos, etEgresos, etTotal;
    private Button btnActualizar, btnVolver;
    private String vendedorId; // Variable para almacenar el ID del vendedor

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_act_vendedor);

        etCedula = findViewById(R.id.etCedula);
        etEmail = findViewById(R.id.etEmail);
        etNom = findViewById(R.id.etNom);
        etIngresos = findViewById(R.id.etIngresos);
        etEgresos = findViewById(R.id.etEgresos);
        etTotal = findViewById(R.id.etTotal);
        btnVolver = findViewById(R.id.btnVolver);
        btnVolver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ActVendedor.this, AdminVendedor.class);
                startActivity(i);
                finish();
            }
        });

        btnActualizar = findViewById(R.id.btnAct);

        // Obtener el ID del vendedor pasado desde VenAdapter
        vendedorId = getIntent().getStringExtra("id_pro");

        // Leer los detalles del vendedor desde Firebase y rellenar los campos de edición
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Persona")
                .child(vendedorId);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Ven vendedor = snapshot.getValue(Ven.class);
                    if (vendedor != null) {
                        etCedula.setText(vendedor.getCedula());
                        etNom.setText(vendedor.getNombre());
                        etEmail.setText(vendedor.getEmail());
                        etIngresos.setText(vendedor.getIngresos());
                        etEgresos.setText(vendedor.getEgresos());

                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Manejar errores si la consulta es cancelada o falla
            }
        });

        btnActualizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Obtener los datos ingresados por el usuario
                String nNombre = etNom.getText().toString();
                String nIngreso = etIngresos.getText().toString();
                String nEgreso = etEgresos.getText().toString();
                String nTotal = etTotal.getText().toString();
                // Actualizar los datos del vendedor en Firebase
                actualizarVendedor(nNombre, nIngreso, nEgreso, nTotal);
            }
        });

        etIngresos.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                calcularTotal();
            }
        });

        etEgresos.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                calcularTotal();
            }
        });
    }
    private void calcularTotal() {
        // Obtener los valores ingresados en los campos de edición
        String ingresosStr = etIngresos.getText().toString();
        String egresosStr = etEgresos.getText().toString();

        // Verificar que los valores ingresados no estén vacíos
        if (!ingresosStr.isEmpty() && !egresosStr.isEmpty()) {
            // Convertir los valores a números enteros
            int ingresos = Integer.parseInt(ingresosStr);
            int egresos = Integer.parseInt(egresosStr);

            // Calcular el total restando los egresos de los ingresos
            int total = ingresos - egresos;

            // Actualizar el campo "Total" con el valor calculado
            etTotal.setText(String.valueOf(total));
        }
    }

    private void actualizarVendedor(String nNombre, String nIngreso, String nEgreso, String nTotal) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Persona")
                .child(vendedorId);
        // Actualizar los datos en la base de datos
        databaseReference.child("nombre").setValue(nNombre);
        databaseReference.child("ingresos").setValue(nIngreso);
        databaseReference.child("total").setValue(nTotal);
        databaseReference.child("egresos").setValue(nEgreso).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(ActVendedor.this, "Vendedor actualizado correctamente", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(ActVendedor.this, "Error al actualizar el vendedor", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
