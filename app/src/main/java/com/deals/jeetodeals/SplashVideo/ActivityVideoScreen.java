package com.deals.jeetodeals.SplashVideo;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.deals.jeetodeals.ContainerActivity.ContainerActivity;
import com.deals.jeetodeals.R;
import com.deals.jeetodeals.Utils.Utility;
import com.deals.jeetodeals.databinding.ActivityVideoScreenBinding;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.Player;

public class ActivityVideoScreen extends Utility {
    private static final String TAG = "VideoActivity";
    private ActivityVideoScreenBinding binding;
    private ExoPlayer player;
    private boolean videoCompleted = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityVideoScreenBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setupVideoPlayer();
        setupTouchListener();
        checkAndRequestNotificationPermission();
    }

    private void setupVideoPlayer() {
        player = new ExoPlayer.Builder(this).build();
        binding.videoView.setPlayer(player);

        String videoPath = "android.resource://" + getPackageName() + "/" + R.raw.intro_video;
        MediaItem mediaItem = MediaItem.fromUri(Uri.parse(videoPath));
        player.setMediaItem(mediaItem);
        player.prepare();
        player.play();

        player.addListener(new Player.Listener() {
            @Override
            public void onPlaybackStateChanged(int state) {
                if (state == Player.STATE_ENDED) {
                    videoCompleted = true;
                    navigateToNextScreen();
                }
            }

            @Override
            public void onPlayerError(com.google.android.exoplayer2.PlaybackException error) {
                Log.e(TAG, "Video playback error: " + error.getMessage());
                navigateToNextScreen();
            }
        });
    }

    private void setupTouchListener() {

        binding.dreamBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (player.isPlaying()) {
                    player.stop();
                    navigateToNextScreen();
                }
            }
        });
        binding.getRoot().setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    if (player.isPlaying()) {
                        player.stop();
                    }
                    navigateToNextScreen();
                    return true;
                }
                return false;
            }
        });
    }

    private ActivityResultLauncher<String> requestPermissionLauncher = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(),
            isGranted -> {
                Log.d("NotificationPermission", isGranted ? "Permission granted!" : "Permission denied!");
            }
    );

    private void checkAndRequestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
                Log.d("NotificationPermission", "Notification permission is already granted.");
            } else {
                Log.d("NotificationPermission", "Requesting notification permission.");
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
            }
        } else {
            Log.d("NotificationPermission", "Notification permission is not required for this Android version.");
        }
    }
    private void navigateToNextScreen() {
        startActivity(new Intent(ActivityVideoScreen.this, ContainerActivity.class));
        finish();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (player != null && player.isPlaying()) {
            player.pause();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (player != null && !videoCompleted) {
            player.play();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (player != null) {
            player.release();
            player = null;
        }
    }
}