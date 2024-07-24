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

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ProductViewHolder> {

    private final ArrayList<User> userArrayList;
    private final LayoutInflater inflater;
    private final Context context;

    public UserAdapter(Context context, ArrayList<User> userArrayList) {
        this.context = context;
        this.userArrayList = userArrayList;
        inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = inflater.inflate(R.layout.user_card, parent, false);
        return new ProductViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        User user = userArrayList.get(position);
        holder.userName.setText(user.getName());
        holder.userEmail.setText(user.getEmail());
        holder.userRole.setText(user.getRoleValue());
        holder.userLocation.setText(user.getLocation());
        holder.userStatus.setText(user.getPassword());
        holder.userId.setText(user.getUserId());

        //click listener for the recycler view to move to location update activity
        holder.itemView.setOnClickListener(v -> {
            //pass the user id to the next activity
            Intent intent = new Intent(context, Location_Update.class);
            intent.putExtra("userId", user.getUserId());
            intent.putExtra("userName", user.getName());
            intent.putExtra("userEmail", user.getEmail());
            intent.putExtra("userRole", user.getRoleValue());
            intent.putExtra("userLocation", user.getLocation());
            intent.putExtra("userStatus", user.getPassword());
            intent.putExtra("userTrack", user.getTruckNumber());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return userArrayList.size();
    }

    public static class ProductViewHolder extends RecyclerView.ViewHolder {
        TextView userName, userEmail, userRole, userLocation,userStatus,userId;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            userName = itemView.findViewById(R.id.user_name);
            userEmail = itemView.findViewById(R.id.user_email);
            userRole = itemView.findViewById(R.id.user_role);
            userLocation = itemView.findViewById(R.id.user_location);
            userStatus = itemView.findViewById(R.id.user_status);
            userId = itemView.findViewById(R.id.user_id);

        }
    }
}
