package com.example.ferterliserdistribution;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class Admin_ItemsAdapter extends RecyclerView.Adapter<Admin_ItemsAdapter.ProductViewHolder> {

    private Context context;
    private ArrayList<Product> productArrayList;
    private LayoutInflater inflater;

    public Admin_ItemsAdapter(Context context, ArrayList<Product> productArrayList) {
        this.context = context;
        this.productArrayList = productArrayList;
        inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = inflater.inflate(R.layout.admin_item_view_card, parent, false);
        return new ProductViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = productArrayList.get(position);
        holder.productName.setText(product.name);
        holder.productPrice.setText("Ksh " + product.price);
        holder.productQuantity.setText(product.quantity);
        holder.productId.setText(product.itemId);

    }

    @Override
    public int getItemCount() {
        return productArrayList.size();
    }

    public class ProductViewHolder extends RecyclerView.ViewHolder {
        TextView productName, productPrice, productQuantity, productId;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            productName = itemView.findViewById(R.id.name);
            productPrice = itemView.findViewById(R.id.price);
            productQuantity = itemView.findViewById(R.id.quantity);
            productId = itemView.findViewById(R.id.id);

        }
    }

}
