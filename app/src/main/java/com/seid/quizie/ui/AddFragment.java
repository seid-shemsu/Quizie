package com.seid.quizie.ui;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.VideoView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.seid.quizie.Main;
import com.seid.quizie.R;

public class AddFragment extends Fragment {


    public AddFragment() {
        // Required empty public constructor
    }
    View view;
    ProgressBar progress;
    VideoView videoView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_add, container, false);
        progress = view.findViewById(R.id.progress);
        videoView = view.findViewById(R.id.video);
        videoView.setOnInfoListener((mediaPlayer, what, i1) -> {
            if (what == MediaPlayer.MEDIA_INFO_BUFFERING_END) {
                progress.setVisibility(View.GONE);
                return true;
            }
            else if(what == MediaPlayer.MEDIA_INFO_BUFFERING_START){
                progress.setVisibility(View.VISIBLE);
                return true;
            }
            return false;
        });
        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                getContext().startActivity(new Intent(getContext(), Main.class).putExtra("id", getArguments().getString("course_name")));
            }
        });
        FirebaseDatabase.getInstance().getReference("ads")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.hasChildren()) {
                            videoView.setVideoURI(Uri.parse(snapshot.child("url").getValue().toString()));
                            videoView.requestFocus();
                            videoView.start();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
        return view;
    }
}