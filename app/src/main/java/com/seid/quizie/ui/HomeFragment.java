package com.seid.quizie.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.seid.quizie.Constants;
import com.seid.quizie.R;
import com.seid.quizie.adapters.GamesAdapter;
import com.seid.quizie.databinding.FragmentHomeBinding;
import com.seid.quizie.models.Game;
import com.seid.quizie.models.User;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private TextView name, points, withdraw;
    private ProgressBar progress;
    private View root;
    private SharedPreferences sharedPreferences;
    private String phone = "";
    private RecyclerView recycler;
    private GamesAdapter adapter;
    private List<Game> games = new ArrayList<>();

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        root = binding.getRoot();
        sharedPreferences = getContext().getSharedPreferences("user", Context.MODE_PRIVATE);
        phone = sharedPreferences.getString("phone", "-");
        name = root.findViewById(R.id.name);
        points = root.findViewById(R.id.points);
        withdraw = root.findViewById(R.id.withdraw);
        progress = root.findViewById(R.id.progress);
        recycler = root.findViewById(R.id.recycler);
        recycler.setHasFixedSize(true);
        recycler.setLayoutManager(new LinearLayoutManager(getContext()));
        FirebaseDatabase.getInstance().getReference(Constants.USERS)
                .child(phone)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.hasChildren()) {
                            progress.setVisibility(View.GONE);
                            User user = snapshot.getValue(User.class);
                            Log.e("User", user != null ? user.toString() : null);
                            name.setText(user != null ? user.getName() : null);
                            points.setText(user != null ? user.getPoints() + "" : null);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
        FirebaseDatabase.getInstance().getReference("games")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.hasChildren()) {
                            games.clear();
                            for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                                games.add(snapshot1.getValue(Game.class));
                            }
                            adapter = new GamesAdapter(getContext(), games, getParentFragmentManager());
                            recycler.setAdapter(adapter);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}