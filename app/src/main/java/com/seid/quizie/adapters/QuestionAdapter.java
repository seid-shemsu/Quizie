package com.seid.quizie.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.seid.quizie.R;

import java.util.List;

public class QuestionAdapter extends RecyclerView.Adapter<QuestionAdapter.Holder> {
    Context context;
    List<String> choices;

    public QuestionAdapter(Context context, List<String> choices) {
        this.context = context;
        this.choices = choices;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new Holder(LayoutInflater.from(context).inflate(R.layout.choice, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        String choice = choices.get(position);
        if ((choice + "").equalsIgnoreCase("null"))
            holder.card.setVisibility(View.GONE);
        holder.choice.setText(choice);
    }

    @Override
    public int getItemCount() {
        return choices.size();
    }

    class Holder extends RecyclerView.ViewHolder {
        TextView choice;
        CardView card;
        public Holder(@NonNull View itemView) {
            super(itemView);
            choice = itemView.findViewById(R.id.choice);
            card = itemView.findViewById(R.id.card);
            card.setOnClickListener(view -> {
                card.setCardBackgroundColor(context.getResources().getColor(R.color.card_selected));
            });
        }
    }
}
