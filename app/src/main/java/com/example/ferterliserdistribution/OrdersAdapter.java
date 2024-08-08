package com.example.ferterliserdistribution;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class OrdersAdapter extends RecyclerView.Adapter<OrdersAdapter.OrderViewHolder> {

    private final ArrayList<Order> orderArrayList;
    private final LayoutInflater inflater;
    private final Context context;

    public OrdersAdapter(Context context, ArrayList<Order> orderArrayList) {
        this.context = context;
        this.orderArrayList = orderArrayList;
        inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = inflater.inflate(R.layout.orders_card, parent, false);
        return new OrderViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        Order order = orderArrayList.get(position);

        holder.orderId.setText(order.getOrderId()); // set order ID
        holder.orderDate.setText(order.getOrderDate());
        holder.orderAddress.setText(order.getOrderAddress());
        holder.orderTime.setText(order.getOrderTime());

        String status = order.getServed();
        switch (status) {
            case "0":
                holder.orderStatus.setTextColor(context.getResources().getColor(R.color.blue));
                holder.orderStatus.setText("Placed");
                break;
            case "1":
                holder.orderStatus.setTextColor(context.getResources().getColor(R.color.magenta));
                holder.orderStatus.setText("On the way");
                break;
            case "2":
                holder.orderStatus.setTextColor(context.getResources().getColor(R.color.yellow));
                holder.orderStatus.setText("Dispatched");
                break;
            case "3":
                holder.orderStatus.setTextColor(context.getResources().getColor(R.color.orange));
                holder.orderStatus.setText("Delivered");
                break;
            case "4":
                holder.orderStatus.setTextColor(context.getResources().getColor(R.color.green));
                holder.orderStatus.setText("Picked");
                break;
            default:
                holder.orderStatus.setTextColor(context.getResources().getColor(R.color.red));
                holder.orderStatus.setText("Cancelled");
                break;
        }

        // Populate products in the order
        holder.productsContainer.removeAllViews();
        List<Map<String, Object>> products = order.getProducts();
        for (Map<String, Object> product : products) {
            View productView = inflater.inflate(R.layout.product_item, holder.productsContainer, false);

            TextView productName = productView.findViewById(R.id.product_name);
            TextView productCount = productView.findViewById(R.id.product_count);
            TextView productPrice = productView.findViewById(R.id.product_price);

            productName.setText((String) product.get("productName"));
            productCount.setText("Count: " + product.get("itemCount"));
            productPrice.setText("Price: " + product.get("cumulativePrice"));

            holder.productsContainer.addView(productView);
        }

        holder.itemView.setOnClickListener(v -> {
            // Open update page and pass order info
            Intent intent = new Intent(context, OrderStatus.class);
            intent.putExtra("orderId", order.getOrderDate());
            intent.putExtra("orderDate", order.getOrderDate());
            intent.putExtra("orderStatus", order.getServed());
            intent.putExtra("orderAddress", order.getOrderAddress());
            intent.putExtra("orderTime", order.getOrderTime());
            context.startActivity(intent);
        });
    }


    @Override
    public int getItemCount() {
        return orderArrayList.size();
    }

    public static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView orderId, orderDate, orderStatus, orderAddress, orderTime;
        LinearLayout productsContainer;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            orderId = itemView.findViewById(R.id.order_id);
            orderDate = itemView.findViewById(R.id.order_date);
            orderStatus = itemView.findViewById(R.id.order_status);
            orderAddress = itemView.findViewById(R.id.order_address);
            orderTime = itemView.findViewById(R.id.order_time);
            productsContainer = itemView.findViewById(R.id.products_container);
        }
    }
}
