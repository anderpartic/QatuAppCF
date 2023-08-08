package com.anderson.qatuappcf;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    private TextInputEditText etEmail, etPass;
    private TextView txtCrear;
    private Button btnIniciar;
    private static final String TAG = "GoogleActivity";
    private static final int RC_SIGN_IN = 9001;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();

        etEmail = findViewById(R.id.etEmail);
        etPass = findViewById(R.id.etPass);
        btnIniciar = findViewById(R.id.btnIniciar);
        btnIniciar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ingresar();
            }
        });
        txtCrear = findViewById(R.id.txtCrear);
        txtCrear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, Registrarse.class);
                startActivity(i);
                finish();
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                Log.d(TAG, "firebaseAuthWithGoogle:" + account.getId());
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e);
            }
        }
    }

    public void ingresar() {
        String mail, pass;
        mail = String.valueOf(etEmail.getText());
        pass = String.valueOf(etPass.getText());
        if (TextUtils.isEmpty(mail)) {
            Toast.makeText(MainActivity.this, "Ingresar Mail", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(pass)) {
            Toast.makeText(MainActivity.this, "Ingresar Password", Toast.LENGTH_SHORT).show();
            return;
        }
        mAuth.signInWithEmailAndPassword(mail, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    FirebaseUser user = mAuth.getCurrentUser();
                    if (user != null) {
                        // Obtener el ID único del usuario
                        String userId = user.getUid();

                        // Obtener una referencia a la Realtime Database
                        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Persona").child(userId);

                        // Leer el tipo de usuario (cliente, admin, vendedor) desde la base de datos
                        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    String tipoUsuario = dataSnapshot.child("tipo").getValue(String.class);
                                    if ("cliente".equals(tipoUsuario)) {
                                        // El usuario es un cliente, redirigir a la actividad Cliente
                                        Intent i = new Intent(MainActivity.this, Cliente.class);
                                        startActivity(i);
                                        finish();
                                        Toast.makeText(MainActivity.this, "Bienvenido Cliente", Toast.LENGTH_SHORT).show();
                                    } else if ("admin".equals(tipoUsuario)) {
                                        // El usuario es un administrador, redirigir a la actividad Admin
                                        Intent i = new Intent(MainActivity.this, Admin.class);
                                        startActivity(i);
                                        finish();
                                        Toast.makeText(MainActivity.this, "Administrador ingresado exitósamente", Toast.LENGTH_SHORT).show();
                                    } else if ("vendedor".equals(tipoUsuario)) {
                                        // El usuario es un vendedor, redirigir a la actividad Vendedor
                                        Intent i = new Intent(MainActivity.this, Vendedor.class);
                                        startActivity(i);
                                        finish();
                                        Toast.makeText(MainActivity.this, "Bienvenido Vendedor", Toast.LENGTH_SHORT).show();
                                    } else {
                                        // Tipo de usuario desconocido o no se encuentra en la base de datos
                                        Toast.makeText(MainActivity.this, "Error: Tipo de usuario desconocido o no encontrado", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                // Error al leer desde la base de datos, manejarlo según sea necesario
                                Toast.makeText(MainActivity.this, "Error en la Base de datos", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                } else {
                    Toast.makeText(MainActivity.this, "Email o Password incorrectos", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void updateUI(FirebaseUser user) {
        FirebaseUser user1 = FirebaseAuth.getInstance().getCurrentUser();
        if (user1 != null) {
            // Obtener el ID único del usuario
            String userId = user1.getUid();
            // Obtener una referencia a la Realtime Database
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Persona").child(userId);
            // Leer el tipo de usuario (cliente o administrador) desde la base de datos
            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        String tipoUsuario = dataSnapshot.child("tipo").getValue(String.class);
                        if ("cliente".equals(tipoUsuario)) {
                            // El usuario es un cliente, redirigir a la actividad Cliente
                            Intent i = new Intent(MainActivity.this, Cliente.class);
                            startActivity(i);
                            finish();
                            Toast.makeText(MainActivity.this, "Bienvenido de vuelta", Toast.LENGTH_SHORT).show();
                        } else if ("admin".equals(tipoUsuario)) {
                            // El usuario es un administrador, redirigir a la actividad Admin
                            Intent i = new Intent(MainActivity.this, Admin.class);
                            startActivity(i);
                            finish();
                            Toast.makeText(MainActivity.this, "Administrador ingresado exitósamente", Toast.LENGTH_SHORT).show();
                        }else if ("vendedor".equals(tipoUsuario)) {
                            // El usuario es un administrador, redirigir a la actividad Admin
                            Intent i = new Intent(MainActivity.this, Vendedor.class);
                            startActivity(i);
                            finish();
                            Toast.makeText(MainActivity.this, "Vendedor ha regresado", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(MainActivity.this, "Error", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(MainActivity.this, "Error en la Base de datos", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithCredential:success");
                    FirebaseUser user = mAuth.getCurrentUser();
                    updateUI(user);
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithCredential:failure", task.getException());
                    updateUI(null);
                }
            }
        });
    }
}
