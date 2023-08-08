package com.anderson.qatuappcf.adapter;

import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.anderson.qatuappcf.R;
import com.anderson.qatuappcf.model.Compras;

import java.util.List;

public class PedidosAdapter extends RecyclerView.Adapter<PedidosAdapter.ViewHolder> {
    private List<Compras> productos;
    public PedidosAdapter(List<Compras> productos) {
        this.productos = productos;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.lista_pedido, parent, false);
        return new ViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Compras producto = productos.get(position);

        // Mostrar el nombre y la cédula del cliente solo si es el primer producto del cliente
        if (position == 0 || !producto.esMismoCliente(productos.get(position - 1))) {
            holder.tvNombreC.setVisibility(View.VISIBLE);
            holder.tvCedula.setVisibility(View.VISIBLE);
            holder.tvNombreC.setText(producto.getNombreCliente());
            holder.tvCedula.setText(producto.getCedulaCliente());
        } else {
            holder.tvNombreC.setVisibility(View.GONE);
            holder.tvCedula.setVisibility(View.GONE);
            holder.nombre.setVisibility(View.GONE);
            holder.cedula.setVisibility(View.GONE);
            holder.pro.setVisibility(View.GONE);
            holder.cant.setVisibility(View.GONE);
            holder.sub.setVisibility(View.GONE);
            holder.llProductos.setVisibility(View.GONE);
            holder.card.setVisibility(View.GONE);
        }
        // Agregar los productos del cliente al contenedor
        holder.llProductos.removeAllViews();
        for (Compras prodCliente : productos) {
            if (producto.esMismoCliente(prodCliente)) {
                View productoView = LayoutInflater.from(holder.itemView.getContext()).inflate(R.layout.lista_pedido, holder.llProductos, false);
                TextView tvProducto = productoView.findViewById(R.id.tvProducto);
                TextView tvCantidad = productoView.findViewById(R.id.tvCantidad);
                TextView tvSubtotal = productoView.findViewById(R.id.tvSubtotal);
                tvProducto.setText(prodCliente.getNombre());
                tvCantidad.setText(prodCliente.getCantidad());
                tvSubtotal.setText(prodCliente.getSubTotal());
                holder.llProductos.addView(productoView);
            }
        }
    }
    @Override
    public int getItemCount() {
        return productos.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView pro, cant, sub, nombre, cedula, tvNombreC, tvCedula, tvProducto, tvCantidad, tvSubtotal;
        private LinearLayout llProductos, card;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombreC = itemView.findViewById(R.id.tvNombreC);
            tvCedula = itemView.findViewById(R.id.tvCedula);
            tvProducto = itemView.findViewById(R.id.tvProducto);
            tvCantidad = itemView.findViewById(R.id.tvCantidad);
            tvSubtotal = itemView.findViewById(R.id.tvSubtotal);
            llProductos = itemView.findViewById(R.id.llProductos);
            nombre = itemView.findViewById(R.id.nombre);
            cedula = itemView.findViewById(R.id.cedula);
            pro = itemView.findViewById(R.id.pro);
            cant = itemView.findViewById(R.id.cant);
            sub = itemView.findViewById(R.id.sub);
            card = itemView.findViewById(R.id.card);

        }
    }
}
