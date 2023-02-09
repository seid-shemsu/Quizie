package com.seid.quizie.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.seid.quizie.R;
import com.seid.quizie.models.User;

import java.util.List;

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.Holder> {
    Context context;
    List<User> users;

    public UsersAdapter(Context context, List<User> users) {
        this.context = context;
        this.users = users;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new Holder(LayoutInflater.from(context).inflate(R.layout.single_user, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        User user = users.get(position);
        holder.rank.setText("" + (position + 1));
        holder.phone.setText(user.getPhone().substring(0, 7) + "***" + user.getPhone().substring(10));
        holder.point.setText(user.getPoints() + " points");
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    class Holder extends RecyclerView.ViewHolder {
        TextView rank, phone, point;

        public Holder(@NonNull View itemView) {
            super(itemView);
            rank = itemView.findViewById(R.id.rank);
            phone = itemView.findViewById(R.id.phone);
            point = itemView.findViewById(R.id.points);
        }
    }
}
