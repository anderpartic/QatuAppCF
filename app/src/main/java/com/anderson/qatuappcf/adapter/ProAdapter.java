package com.anderson.qatuappcf.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.anderson.qatuappcf.Producto;
import com.anderson.qatuappcf.R;
import com.anderson.qatuappcf.model.Fruit;
import com.anderson.qatuappcf.model.Pro;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Document;


public class ProAdapter extends FirestoreRecyclerAdapter<Pro, ProAdapter.ViewHolder> {


    private FirebaseFirestore mFirestore = FirebaseFirestore.getInstance();
    Activity activity;

    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public ProAdapter(@NonNull FirestoreRecyclerOptions<Pro> options, Activity activity) {
        super(options);
        this.activity = activity;
    }


    @Override
    protected void onBindViewHolder(@NonNull ViewHolder viewHolder, int position, @NonNull Pro Pro) {
        DocumentSnapshot documentSnapshot = getSnapshots().getSnapshot(viewHolder.getAdapterPosition());
        final String id = documentSnapshot.getId();

        viewHolder.tvCodigo.setText(Pro.getCodigo());
        viewHolder.tvNombre.setText(Pro.getNombre());
        viewHolder.tvVenta.setText(Pro.getPrecio());
        viewHolder.tvStock.setText(Pro.getStock());
        viewHolder.tvTipo.setText(Pro.getTipo());
        viewHolder.tvTipo.setText(Pro.getTipo());
        String photoPro = Pro.getPhoto();
        try {
            if (!photoPro.equals(""))
                Picasso.get().load(photoPro).resize(150, 150).into(viewHolder.photo_pro);

        }catch (Exception e){
            Log.d("Exception", "e: " +e);
        }

        viewHolder.btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(activity, Producto.class);
                i.putExtra("id_pro", id);
                activity.startActivity(i);
            }
        });
        viewHolder.btnEliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showConfirmationDialog(activity, id);
            }
        });
    }
    // Método para mostrar el diálogo de confirmación
    private void showConfirmationDialog(Activity activity, String id) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("Confirmar eliminación");
        builder.setMessage("¿Estás seguro de que deseas eliminar?");
        builder.setPositiveButton("Sí", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deletPro(id);
            }
        });
        builder.setNegativeButton("No", null);
        AlertDialog dialog = builder.create();
        dialog.show();
    }


    private void deletPro(String id) {
        mFirestore.collection("pro").document(id).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(activity, "Producto eliminado exitosamente", Toast.LENGTH_SHORT).show();
                notifyDataSetChanged(); // Agregar esta línea para actualizar la lista
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(activity, "Error al eliminar", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.lista_pro, parent, false);
        return new ViewHolder(v);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvCodigo, tvNombre, tvStock, tvVenta, tvTipo;
        ImageButton btnEliminar, btnEdit, photo_pro;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvCodigo = itemView.findViewById(R.id.tvCodigo);
            tvNombre = itemView.findViewById(R.id.tvNombre);
            tvStock = itemView.findViewById(R.id.tvStock);
            tvVenta = itemView.findViewById(R.id.tvVenta);
            tvTipo = itemView.findViewById(R.id.tvTipo);
            photo_pro = itemView.findViewById(R.id.photo_pro);
            btnEliminar = itemView.findViewById(R.id.btnEliminar);
            btnEdit = itemView.findViewById(R.id.btnEdit);
        }
    }


}
