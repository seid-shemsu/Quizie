package com.seid.quizie;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.seid.quizie.adapters.QuestionAdapter;
import com.seid.quizie.models.Question;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Main extends AppCompatActivity {

    private ProgressBar progress, loading;
    private TextView counter, question, choice_1, choice_2, choice_3, choice_4;
    private QuestionAdapter adapter;
    private int count = 0;
    private int index = 0;
    private boolean ad = false;
    List<Question> questions = new ArrayList<>();
    private Dialog dialog;
    private String id = "-";
    private int answer = -1;
    private int result = 0;
    String phone = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        try {
            id = getIntent().getExtras().getString("id");
        } catch (Exception e) {
            id = "empty";
        }
        phone = getSharedPreferences("user", MODE_PRIVATE).getString("phone", "0");
        progress = findViewById(R.id.progress);
        loading = findViewById(R.id.loading);
        counter = findViewById(R.id.counter);
        question = findViewById(R.id.question);
        choice_1 = findViewById(R.id.choice_1);
        choice_2 = findViewById(R.id.choice_2);
        choice_3 = findViewById(R.id.choice_3);
        choice_4 = findViewById(R.id.choice_4);
        choice_1.setOnClickListener(v -> {
            answer = 1;
            choice_1.setBackground(getResources().getDrawable(R.drawable.border));
            choice_2.setBackground(getResources().getDrawable(R.drawable.border2));
            choice_3.setBackground(getResources().getDrawable(R.drawable.border2));
            choice_4.setBackground(getResources().getDrawable(R.drawable.border2));
        });
        choice_2.setOnClickListener(v -> {
            answer = 2;
            choice_2.setBackground(getResources().getDrawable(R.drawable.border));
            choice_1.setBackground(getResources().getDrawable(R.drawable.border2));
            choice_3.setBackground(getResources().getDrawable(R.drawable.border2));
            choice_4.setBackground(getResources().getDrawable(R.drawable.border2));
        });
        choice_3.setOnClickListener(v -> {
            answer = 3;
            choice_3.setBackground(getResources().getDrawable(R.drawable.border));
            choice_2.setBackground(getResources().getDrawable(R.drawable.border2));
            choice_1.setBackground(getResources().getDrawable(R.drawable.border2));
            choice_4.setBackground(getResources().getDrawable(R.drawable.border2));
        });
        choice_4.setOnClickListener(v -> {
            answer = 4;
            choice_4.setBackground(getResources().getDrawable(R.drawable.border));
            choice_2.setBackground(getResources().getDrawable(R.drawable.border2));
            choice_3.setBackground(getResources().getDrawable(R.drawable.border2));
            choice_1.setBackground(getResources().getDrawable(R.drawable.border2));
        });

        if (!id.equalsIgnoreCase("empty")) {
            getQuestions();
        }
        dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(R.layout.ad_view);
        dialog.setCanceledOnTouchOutside(false);
    }

    Handler handler = new Handler();
    private final Runnable updateCounter = new Runnable() {
        @Override
        public void run() {
            if (ad) {
                ad = false;
                dialog.dismiss();
            }
            question.setText((index + 1) + ". " + questions.get(index).getQuestion());
            choice_1.setText(questions.get(index).getChoices().get(0));
            choice_2.setText(questions.get(index).getChoices().get(1));
            choice_3.setText(questions.get(index).getChoices().get(2));
            choice_4.setText(questions.get(index).getChoices().get(3));
            progress.setProgress(count * 2);
            counter.setText("" + (count / 10));
            count++;
            if (count <= 50)
                handler.postDelayed(this, 100);
            else if (questions.size() > index + 1) {
                if (answer == questions.get(index).getAnswer()) {
                    result++;
                }
                choice_1.setBackground(getResources().getDrawable(R.drawable.border2));
                choice_2.setBackground(getResources().getDrawable(R.drawable.border2));
                choice_3.setBackground(getResources().getDrawable(R.drawable.border2));
                choice_4.setBackground(getResources().getDrawable(R.drawable.border2));
                count = 0;
                answer = -1;
                index++;
                if (index > 0 && index % 2 == 0) {
                    dialog.show();
                    ad = true;
                    handler.postDelayed(this, 5000);
                } else {
                    handler.post(this);
                }
            } else {
                handler.removeCallbacks(this);
                Log.e("Result", result + "");
                dialog.setContentView(R.layout.result_dialog);
                TextView res = dialog.findViewById(R.id.result);
                TextView points = dialog.findViewById(R.id.points);
                Button home = dialog.findViewById(R.id.home);
                dialog.show();
                home.setOnClickListener(view -> {
                    startActivity(new Intent(Main.this, MainActivity.class));
                    finish();
                });
                res.setText(result + " / " + questions.size());
                FirebaseDatabase.getInstance().getReference("users").child(phone)
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                int prev = Integer.parseInt(snapshot.child("points").getValue().toString());
                                points.setText(prev + " Points");
                                FirebaseDatabase.getInstance().getReference("users").child(phone)
                                        .child("points").setValue(prev + result);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                FirebaseDatabase.getInstance().getReference("user_games").child(phone).child(id).setValue(true);
            }
        }
    };

    private void getQuestions() {
        FirebaseDatabase.getInstance().getReference("user_games")
                .child(phone)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.hasChild(id)) {
                            if (Objects.requireNonNull(snapshot.child(id).getValue()).toString().equalsIgnoreCase("false")) {
                                FirebaseDatabase.getInstance().getReference("Questions")
                                        .child(id)
                                        .addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot1) {
                                                if (snapshot1.hasChildren()) {
                                                    loading.setVisibility(View.GONE);
                                                    for (DataSnapshot snapshot : snapshot1.getChildren()) {
                                                        questions.add(snapshot.getValue(Question.class));
                                                    }
                                                    if (!questions.isEmpty())
                                                        handler.post(updateCounter);
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {

                                            }
                                        });
                            } else {
                                Toast.makeText(Main.this, "You already played this game", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        } else {
                            Toast.makeText(Main.this, "You can not play this game. try later", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


    }
}