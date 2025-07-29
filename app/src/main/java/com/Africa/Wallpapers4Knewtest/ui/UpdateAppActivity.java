package com.Africa.Wallpapers4Knewtest.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;

import com.Africa.Wallpapers4Knewtest.MyUtils;
import com.Africa.Wallpapers4Knewtest.databinding.ActivityUpdateAppBinding;

public class UpdateAppActivity extends AppCompatActivity {

    ActivityUpdateAppBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUpdateAppBinding.inflate(getLayoutInflater());
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
        setContentView(binding.getRoot());
        binding.UpdateNow.setOnClickListener(v -> {
            if (MyUtils.appControl.isUpdateFromAmazon()){
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("amzn://apps/android?p=" + getPackageName())));
                } catch (android.content.ActivityNotFoundException anfe) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.amazon.com/gp/mas/dl/android?p=" + getPackageName())));
                }
            }else {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(MyUtils.appControl.getUpdateLink())));
            }
        });
    }
}