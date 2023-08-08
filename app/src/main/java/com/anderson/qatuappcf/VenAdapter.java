package com.anderson.qatuappcf;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.security.Key;
import java.util.ArrayList;

public class VenAdapter extends RecyclerView.Adapter<VenAdapter.MyViewHolder> {
    Context context;
    ArrayList<Ven> list;

    public VenAdapter(Context context, ArrayList<Ven> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.lista_ven,parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Ven lista = list.get(position);

        holder.tvCedula.setText(lista.getCedula());
        holder.tvNombre.setText(lista.getNombre());
        holder.tvEmail.setText(lista.getEmail());
        holder.tvIngresos.setText(lista.getIngresos());
        holder.tvEgresos.setText(lista.getEgresos());
        holder.tvTotal.setText(lista.getTotal());

                // Evento click del botón "Eliminar"
        holder.btnEliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    showDeleteConfirmationDialog(position);
                }
            }
        });

        // Evento click del botón "Editar"
        holder.btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Abre la actividad para editar el vendedor y pasa los detalles del vendedor a la nueva actividad
                Intent i = new Intent(context, ActVendedor.class);
                i.putExtra("id_pro", lista.getKey());
                context.startActivity(i);
            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class MyViewHolder extends  RecyclerView.ViewHolder{
        TextView tvCedula, tvNombre, tvEmail, tvIngresos, tvEgresos, tvTotal;
        ImageButton btnEliminar, btnEdit;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            tvCedula = itemView.findViewById(R.id.tvCedula);
            tvNombre = itemView.findViewById(R.id.tvNombre);
            tvEmail = itemView.findViewById(R.id.tvEmail);
            tvIngresos = itemView.findViewById(R.id.tvIngresos);
            tvEgresos = itemView.findViewById(R.id.tvEgresos);
            tvTotal = itemView.findViewById(R.id.tvTotal);
            btnEliminar = itemView.findViewById(R.id.btnEliminar);
            btnEdit = itemView.findViewById(R.id.btnEdit);

        }
    }

    private void showDeleteConfirmationDialog(final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Eliminar Vendedor");
        builder.setMessage("¿Estás seguro de que deseas eliminar este vendedor?");
        builder.setPositiveButton("Sí", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Si el usuario hace clic en "Sí", elimina el vendedor
                deleteVendedor(position);
            }
        });

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Si el usuario hace clic en "No", simplemente cierra el cuadro de diálogo
                dialog.dismiss();
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void deleteVendedor(int position) {
        // Eliminar el vendedor de la base de datos en tiempo real (Realtime Database)
        DatabaseReference vendedorRef = FirebaseDatabase.getInstance().getReference("Persona")
                .child(list.get(position).getKey());
        vendedorRef.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    // Si se elimina correctamente de la base de datos en tiempo real, procedemos a eliminar el usuario autenticado

                    // Obtener el objeto del usuario autenticado
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    if (user != null) {
                        // Obtener el ID del usuario autenticado
                        String userID = user.getUid();

                        // Verificar si el ID del usuario autenticado coincide con el ID del vendedor que se está eliminando
                        if (userID.equals(list.get(position).getAuthUserID())) {
                            // Si el usuario autenticado actual coincide con el ID del vendedor, procedemos a eliminarlo
                            user.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(context, "Vendedor eliminado correctamente", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(context, "Error al eliminar el vendedor", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        } else {
                            // Si el usuario autenticado no coincide con el ID del vendedor, mostrar un mensaje de error
                            Toast.makeText(context, "Error al eliminar el vendedor: El usuario no coincide", Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    Toast.makeText(context, "Error al eliminar el vendedor", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


}
