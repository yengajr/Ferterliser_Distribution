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

public class Order_ProcessingAdapter1 extends RecyclerView.Adapter<Order_ProcessingAdapter1.ProductViewHolder>{

    private final ArrayList<Order> orderArrayList;
    private final LayoutInflater inflater;
    private final Context context;

    public Order_ProcessingAdapter1(Context context, ArrayList<Order> orderArrayList) {
        this.context = context;
        this.orderArrayList = orderArrayList;
        inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = inflater.inflate(R.layout.order_card3, parent, false);
        return new ProductViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Order order = orderArrayList.get(position);



        holder.orderName.setText(order.emailAddress);
        holder.orderId.setText(order.itemId);
        holder.orderDate.setText(order.orderDate);
        //check served status and change text color
        String status = order.served;
        switch (status) {
            case "3":
                //change text color to blue
                holder.orderStatus.setTextColor(context.getResources().getColor(R.color.blue));
                holder.orderStatus.setText("Pending");
                break;
            case "4":
                //change text color to green
                holder.orderStatus.setTextColor(context.getResources().getColor(R.color.green));
                holder.orderStatus.setText("Completed");
                break;
            case "5":
                //change text color to yellow
                holder.orderStatus.setTextColor(context.getResources().getColor(R.color.red));
                holder.orderStatus.setText("Cancelled");
                break;
        }


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
        TextView  orderId, orderDate, orderStatus, orderName;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);

            orderName = itemView.findViewById(R.id.order_name);
            orderId = itemView.findViewById(R.id.id);
            orderDate = itemView.findViewById(R.id.order_date);
            orderStatus = itemView.findViewById(R.id.order_status);

        }
    }
}
