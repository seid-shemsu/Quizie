package com.seid.quizie.adapters;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.seid.quizie.R;
import com.seid.quizie.models.Game;
import com.seid.quizie.ui.AddFragment;

import java.util.List;

public class GamesAdapter extends RecyclerView.Adapter<GamesAdapter.Holder> {
    Context context;
    List<Game> games;
    FragmentManager fragmentManager;

    public GamesAdapter(Context context, List<Game> games, FragmentManager fragmentManager) {
        this.context = context;
        this.games = games;
        this.fragmentManager = fragmentManager;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new Holder(LayoutInflater.from(context).inflate(R.layout.single_game, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        Game game = games.get(position);
        holder.title.setText(game.getTitle());
        holder.questions.setText(game.getQuestions() + " questions");
        holder.start.setOnClickListener(v -> {
            AddFragment addFragment = new AddFragment();
            Bundle bundle = new Bundle();
            bundle.putString("id", game.getId());
            addFragment.setArguments(bundle);
            fragmentManager
                    .beginTransaction()
                    .replace(R.id.nav_host_fragment_activity_main, addFragment)
                    .addToBackStack(null)
                    .commit();
            //context.startActivity(new Intent(context, Main.class).putExtra("id", game.getId()));
        });
    }

    @Override
    public int getItemCount() {
        return games.size();
    }

    class Holder extends RecyclerView.ViewHolder {
        TextView title, questions;
        Button start;

        public Holder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title);
            questions = itemView.findViewById(R.id.questions);
            start = itemView.findViewById(R.id.start);
        }
    }
}
