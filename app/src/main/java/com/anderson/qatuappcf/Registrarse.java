package com.anderson.qatuappcf;


import android.content.Intent;

import android.hardware.biometrics.BiometricManager;
import android.hardware.biometrics.BiometricPrompt;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.concurrent.Executor;

public class Registrarse extends AppCompatActivity {
    private TextInputEditText etCedula, etEmail, etNomUsu, etPass, etConPass;
    private Button btnCrear;
    private TextView btnIniciar;
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrarse);
        etCedula = findViewById(R.id.etCedula);
        etEmail = findViewById(R.id.etEmail);
// Establecer el filtro de longitud máximo a 10 caracteres
        int maxLength = 10;
        InputFilter[] fArray = new InputFilter[1];
        fArray[0] = new InputFilter.LengthFilter(maxLength);
        etCedula.setFilters(fArray);
        etNomUsu = findViewById(R.id.etNomUsu);
        etPass = findViewById(R.id.etPass);
        etConPass = findViewById(R.id.etConPass);
        btnCrear = findViewById(R.id.btnCrear);
        btnCrear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Guardar();
            }
        });
        btnIniciar = findViewById(R.id.btnIniciar);
        btnIniciar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Registrarse.this, MainActivity.class);
                startActivity(i);
                finish();
            }
        });

    }



    // Función para validar una cédula ecuatoriana
    public static boolean validarCedula(String cedula) {
        if (cedula == null || cedula.length() != 10) {
            return false;
        }

        // Verificar que todos los caracteres sean dígitos numéricos
        if (!TextUtils.isDigitsOnly(cedula)) {
            return false;
        }

        // Verificar el dígito verificador utilizando el algoritmo oficial para cédulas ecuatorianas
        int digitoVerificador = Integer.parseInt(cedula.substring(9));
        int sumatoria = 0;

        for (int i = 0; i < 9; i++) {
            int digito = Character.getNumericValue(cedula.charAt(i));
            if (i % 2 == 0) {
                digito *= 2;
                if (digito > 9) {
                    digito -= 9;
                }
            }
            sumatoria += digito;
        }

        int residuo = sumatoria % 10;
        int verificadorEsperado = (residuo == 0) ? 0 : 10 - residuo;

        return digitoVerificador == verificadorEsperado;
    }

    public void Guardar() {
        String cedula = etCedula.getText().toString();
        String email = etEmail.getText().toString();
        String nombre = etNomUsu.getText().toString();
        String pass = etPass.getText().toString();
        String conPass = etConPass.getText().toString();

        if (TextUtils.isEmpty(cedula) || !validarCedula(cedula)) {
            Toast.makeText(Registrarse.this, "Cédula ecuatoriana inválida", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(email)) {
            Toast.makeText(Registrarse.this, "Ingresar Mail", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(Registrarse.this, "Correo electrónico inválido", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(pass)) {
            Toast.makeText(Registrarse.this, "Ingresar Password", Toast.LENGTH_SHORT).show();
            return;
        }
        if (pass.length() < 6) {
            Toast.makeText(Registrarse.this, "La contraseña debe tener al menos 6 caracteres", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!pass.equals(conPass)) {
            Toast.makeText(Registrarse.this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show();
            return;
        }

        // Verificar si el correo electrónico ya está registrado
        FirebaseAuth.getInstance().fetchSignInMethodsForEmail(email)
                .addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
                    @Override
                    public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {
                        if (task.isSuccessful()) {
                            SignInMethodQueryResult result = task.getResult();
                            if (result != null && result.getSignInMethods() != null && result.getSignInMethods().size() > 0) {
                                // El correo electrónico ya está registrado
                                Toast.makeText(Registrarse.this, "El correo electrónico ya está registrado", Toast.LENGTH_SHORT).show();
                            } else {
                                // The email is not registered, check if the cedula exists
                                DatabaseReference personaRef = FirebaseDatabase.getInstance().getReference().child("Persona");
                                personaRef.orderByChild("cedula").equalTo(cedula).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        if (dataSnapshot.exists()) {
                                            // The cedula already exists
                                            Toast.makeText(Registrarse.this, "La cedula ya está registrada", Toast.LENGTH_SHORT).show();
                                        } else {
                                            // The email and cedula are not registered, create the user in Firebase Authentication
                                            firebaseAuth.createUserWithEmailAndPassword(email, pass)
                                                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<AuthResult> task) {
                                                            if (task.isSuccessful()) {
                                                                // Registro exitoso en Firebase Authentication
                                                                String userId = firebaseAuth.getCurrentUser().getUid();
                                                                DatabaseReference newUserRef = personaRef.child(userId);
                                                                newUserRef.child("cedula").setValue(cedula);
                                                                newUserRef.child("nombre").setValue(nombre);
                                                                newUserRef.child("tipo").setValue("cliente");
                                                                newUserRef.child("email").setValue(email)
                                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                            @Override
                                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                                if (task.isSuccessful()) {
                                                                                    limpiar();
                                                                                    Toast.makeText(Registrarse.this, "Registrado correctamente", Toast.LENGTH_SHORT).show();

                                                                                } else {
                                                                                    Toast.makeText(Registrarse.this, "Fallo al guardar la información en la base de datos", Toast.LENGTH_SHORT).show();
                                                                                }
                                                                            }
                                                                        });
                                                            } else {
                                                                Toast.makeText(Registrarse.this, "Fallo en Autenticación", Toast.LENGTH_SHORT).show();
                                                            }
                                                        }
                                                    });
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {
                                        // Handle database error
                                    }
                                });
                            }
                        } else {
                            // Error al verificar el correo electrónico
                            Toast.makeText(Registrarse.this, "Error al verificar el correo electrónico", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }



    public void limpiar() {
        etCedula.setText(null);
        etEmail.setText(null);
        etNomUsu.setText(null);
        etPass.setText(null);
        etConPass.setText(null);
    }


}