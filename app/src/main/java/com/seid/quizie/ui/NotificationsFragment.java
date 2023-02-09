package com.seid.quizie.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.seid.quizie.R;
import com.seid.quizie.activities.Login;
import com.seid.quizie.databinding.FragmentNotificationsBinding;

public class NotificationsFragment extends Fragment {

    private FragmentNotificationsBinding binding;
    private Button logout;
    private View root;
    private TextView name, email, phone;
    private SharedPreferences sp;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentNotificationsBinding.inflate(inflater, container, false);
        root = binding.getRoot();
        logout = root.findViewById(R.id.logout);
        logout.setOnClickListener(v -> {
            getContext().getSharedPreferences("user", Context.MODE_PRIVATE)
                    .edit().clear().apply();
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(getContext(), Login.class));
            ((Activity) getContext()).finish();
        });
        sp = getContext().getSharedPreferences("user", Context.MODE_PRIVATE);

        name = root.findViewById(R.id.name);
        email = root.findViewById(R.id.email);
        phone = root.findViewById(R.id.phone);

        name.setText(sp.getString("name", "NaN"));
        email.setText(sp.getString("email", "NaN"));
        phone.setText(sp.getString("phone", "NaN"));
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}