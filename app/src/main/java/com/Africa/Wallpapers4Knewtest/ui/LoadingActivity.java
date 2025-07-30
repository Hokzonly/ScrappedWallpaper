package com.Africa.Wallpapers4Knewtest.ui;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;
import com.Africa.Wallpapers4Knewtest.MyUtils;
import com.Africa.Wallpapers4Knewtest.config.AppInterstitialListenerManager;
import com.Africa.Wallpapers4Knewtest.config.AppManager;
import com.Africa.Wallpapers4Knewtest.databinding.ActivityLoadingBinding;

public class LoadingActivity extends AppCompatActivity {

    ActivityLoadingBinding binding;
    private ObjectAnimator dot1Animator, dot4Animator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_IMMERSIVE
                            // Set the content to appear under the system bars so that the
                            // content doesn't resize when the system bars hide and show.
                            | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            // Hide the nav bar and status bar
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN);
            getWindow().getAttributes().layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
        }
        binding = ActivityLoadingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        
        // Start Newton's Cradle animation
        startNewtonsCradleAnimation();
        
        if (MyUtils.isNetworkAvailable(this)) {

            AppManager.FetchData(this, new AppManager.InitAdsListener() {
                @Override
                public void onInit() {
                    try {
                        AppManager.ShowInterstitial(LoadingActivity.this, new AppInterstitialListenerManager() {
                            @Override
                            public void onInterstitialClosed() {
                                VerifyUpdate();
                            }
                        });
                    } catch (Exception e) {
                        @SuppressLint("ShowToast") Snackbar snackbar = Snackbar.make(binding.getRoot(), "No Internet Connection", Snackbar.LENGTH_INDEFINITE);
                        snackbar.setAction("Retry", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                snackbar.dismiss();
                                startActivity(new Intent(LoadingActivity.this, LoadingActivity.class));
                                finish();
                            }
                        });
                        snackbar.setAnchorView(binding.snakBar);
                        snackbar.show();
                    }


                }

                @Override
                public void onFailed(String error) {
                    @SuppressLint("ShowToast") Snackbar snackbar = Snackbar.make(binding.getRoot(), "No Internet Connection", Snackbar.LENGTH_INDEFINITE);
                    snackbar.setAction("Retry", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            snackbar.dismiss();
                            startActivity(new Intent(LoadingActivity.this, LoadingActivity.class));
                            finish();
                        }
                    });
                    snackbar.setAnchorView(binding.snakBar);
                    snackbar.show();
                }
            });

        } else {
            @SuppressLint("ShowToast") Snackbar snackbar = Snackbar.make(binding.getRoot(), "No Internet Connection", Snackbar.LENGTH_INDEFINITE);
            snackbar.setAction("Retry", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    snackbar.dismiss();
                    startActivity(new Intent(LoadingActivity.this, LoadingActivity.class));
                    finish();
                }
            });
            snackbar.setAnchorView(binding.snakBar);
            snackbar.show();
        }


    }

    private void startNewtonsCradleAnimation() {
        // Create scale animations for a more visible loading effect
        dot1Animator = ObjectAnimator.ofFloat(binding.dot1, "scaleX", 1f, 1.5f, 1f);
        dot1Animator.setDuration(800);
        dot1Animator.setRepeatCount(ObjectAnimator.INFINITE);
        dot1Animator.setRepeatMode(ObjectAnimator.RESTART);
        dot1Animator.setInterpolator(new android.view.animation.AccelerateDecelerateInterpolator());

        ObjectAnimator dot1ScaleY = ObjectAnimator.ofFloat(binding.dot1, "scaleY", 1f, 1.5f, 1f);
        dot1ScaleY.setDuration(800);
        dot1ScaleY.setRepeatCount(ObjectAnimator.INFINITE);
        dot1ScaleY.setRepeatMode(ObjectAnimator.RESTART);
        dot1ScaleY.setInterpolator(new android.view.animation.AccelerateDecelerateInterpolator());

        dot4Animator = ObjectAnimator.ofFloat(binding.dot4, "scaleX", 1f, 1.5f, 1f);
        dot4Animator.setDuration(800);
        dot4Animator.setRepeatCount(ObjectAnimator.INFINITE);
        dot4Animator.setRepeatMode(ObjectAnimator.RESTART);
        dot4Animator.setInterpolator(new android.view.animation.AccelerateDecelerateInterpolator());

        ObjectAnimator dot4ScaleY = ObjectAnimator.ofFloat(binding.dot4, "scaleY", 1f, 1.5f, 1f);
        dot4ScaleY.setDuration(800);
        dot4ScaleY.setRepeatCount(ObjectAnimator.INFINITE);
        dot4ScaleY.setRepeatMode(ObjectAnimator.RESTART);
        dot4ScaleY.setInterpolator(new android.view.animation.AccelerateDecelerateInterpolator());

        // Create animations for dots 2 and 3
        ObjectAnimator dot2Animator = ObjectAnimator.ofFloat(binding.dot2, "scaleX", 1f, 1.5f, 1f);
        dot2Animator.setDuration(800);
        dot2Animator.setRepeatCount(ObjectAnimator.INFINITE);
        dot2Animator.setRepeatMode(ObjectAnimator.RESTART);
        dot2Animator.setInterpolator(new android.view.animation.AccelerateDecelerateInterpolator());

        ObjectAnimator dot2ScaleY = ObjectAnimator.ofFloat(binding.dot2, "scaleY", 1f, 1.5f, 1f);
        dot2ScaleY.setDuration(800);
        dot2ScaleY.setRepeatCount(ObjectAnimator.INFINITE);
        dot2ScaleY.setRepeatMode(ObjectAnimator.RESTART);
        dot2ScaleY.setInterpolator(new android.view.animation.AccelerateDecelerateInterpolator());

        ObjectAnimator dot3Animator = ObjectAnimator.ofFloat(binding.dot3, "scaleX", 1f, 1.5f, 1f);
        dot3Animator.setDuration(800);
        dot3Animator.setRepeatCount(ObjectAnimator.INFINITE);
        dot3Animator.setRepeatMode(ObjectAnimator.RESTART);
        dot3Animator.setInterpolator(new android.view.animation.AccelerateDecelerateInterpolator());

        ObjectAnimator dot3ScaleY = ObjectAnimator.ofFloat(binding.dot3, "scaleY", 1f, 1.5f, 1f);
        dot3ScaleY.setDuration(800);
        dot3ScaleY.setRepeatCount(ObjectAnimator.INFINITE);
        dot3ScaleY.setRepeatMode(ObjectAnimator.RESTART);
        dot3ScaleY.setInterpolator(new android.view.animation.AccelerateDecelerateInterpolator());

        // Add delays to create a wave effect
        dot1Animator.setStartDelay(0);
        dot1ScaleY.setStartDelay(0);
        dot2Animator.setStartDelay(200);
        dot2ScaleY.setStartDelay(200);
        dot3Animator.setStartDelay(400);
        dot3ScaleY.setStartDelay(400);
        dot4Animator.setStartDelay(600);
        dot4ScaleY.setStartDelay(600);

        // Start all animations
        dot1Animator.start();
        dot1ScaleY.start();
        dot2Animator.start();
        dot2ScaleY.start();
        dot3Animator.start();
        dot3ScaleY.start();
        dot4Animator.start();
        dot4ScaleY.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Stop animations to prevent memory leaks
        if (dot1Animator != null) {
            dot1Animator.cancel();
        }
        if (dot4Animator != null) {
            dot4Animator.cancel();
        }
    }

    private void VerifyUpdate() {

        try {
            if (MyUtils.appControl.isShowUpdate()) {
                PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
                int version = pInfo.versionCode;
                if (MyUtils.appControl.getUpdateVersion() > version) {
                    startActivity(new Intent(LoadingActivity.this, UpdateAppActivity.class));
                    finish();
                } else {
                    startActivity(new Intent(LoadingActivity.this, HomeActivity.class));
                }

            } else {
                startActivity(new Intent(LoadingActivity.this, HomeActivity.class));

            }
        } catch (Exception e) {
            e.printStackTrace();
            startActivity(new Intent(LoadingActivity.this, HomeActivity.class));


        }
    }


    @Override
    protected void onResume() {
        super.onResume();

        SharedPreferences prefs = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        boolean unlockAds = prefs.getBoolean("unlockAds", false);
        long unlockStartTime = prefs.getLong("unlockStartTime", 0);

        if (unlockAds) {
            long currentTimeMillis = System.currentTimeMillis();
            long twoDaysInMillis = 24 * 60 * 60 * 1000; // 2 days in milliseconds

            // Check if 2 days have passed since the unlock time
            if ((currentTimeMillis - unlockStartTime) > twoDaysInMillis) {
                SharedPreferences.Editor editor = prefs.edit();
                editor.putBoolean("unlockAds", false);
                editor.apply();
                Toast.makeText(this, "Ad-free access expired", Toast.LENGTH_SHORT).show();
            }
        }
    }

}