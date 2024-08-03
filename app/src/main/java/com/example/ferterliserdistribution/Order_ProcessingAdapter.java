package com.example.ferterliserdistribution;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class Order_ProcessingAdapter extends RecyclerView.Adapter<Order_ProcessingAdapter.ProductViewHolder>{

    private final ArrayList<Order> orderArrayList;
    private final LayoutInflater inflater;
    private final Context context;

    public Order_ProcessingAdapter(Context context, ArrayList<Order> orderArrayList) {
        this.context = context;
        this.orderArrayList = orderArrayList;
        inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = inflater.inflate(R.layout.oders_card2, parent, false);
        return new ProductViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Order order = orderArrayList.get(position);



        holder.orderName.setText(order.productName);
        holder.orderAddress.setText(order.orderAddress);

        holder.orderCount.setText(order.itemCount + "Bag(s)");


        //on click listener send the details to the next activity
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open update page and pass product info
                Intent intent = new Intent(context, Order_ProcessingPage.class);
                intent.putExtra("orderId", order.itemId);
                intent.putExtra("orderDate", order.orderDate);
                intent.putExtra("orderStatus", order.served);
                intent.putExtra("orderName", order.productName);
                intent.putExtra("orderAddress", order.orderAddress);
                intent.putExtra("orderTime", order.orderTime);
                intent.putExtra("orderCount", order.itemCount);
                intent.putExtra("orderPayment", order.paymentNumber);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return orderArrayList.size();
    }

    public static class ProductViewHolder extends RecyclerView.ViewHolder {
        TextView  orderName, orderAddress, orderCount;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);

            orderName = itemView.findViewById(R.id.order_name);
            orderAddress = itemView.findViewById(R.id.order_address);

            orderCount = itemView.findViewById(R.id.order_count);
        }
    }
}
