package com.Africa.Wallpapers4Knewtest.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.slider.LabelFormatter;
import com.google.android.material.slider.Slider;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.onesignal.Continue;
import com.onesignal.OneSignal;
import com.Africa.Wallpapers4Knewtest.MyUtils;
import com.Africa.Wallpapers4Knewtest.config.RoomDatabase.MySavedCodes;
import com.Africa.Wallpapers4Knewtest.config.RoomDatabase.MySavedCodesDataBse;
import com.Africa.Wallpapers4Knewtest.databinding.ActivityHomeBinding;
import com.Africa.Wallpapers4Knewtest.databinding.DialogExitBinding;
import com.Africa.Wallpapers4Knewtest.databinding.DialogMenuBinding;
import com.Africa.Wallpapers4Knewtest.databinding.DialogPrivacyBinding;
import com.Africa.Wallpapers4Knewtest.databinding.DialogRateBinding;
import com.Africa.Wallpapers4Knewtest.databinding.DialogRedeemBinding;
import com.Africa.Wallpapers4Knewtest.ui.fr.FragmentAdapter;

public class HomeActivity extends AppCompatActivity {

    ActivityHomeBinding binding;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    FragmentAdapter fragmentAdapter;
    SharedPreferences prefs;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        prefs = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        boolean unlockAds = prefs.getBoolean("unlockAds", false);
        if (unlockAds){
            binding.Redeem.setVisibility(View.GONE);
            binding.RedeemText.setVisibility(View.GONE);
        }else {
            binding.Redeem.setVisibility(View.GONE);
            binding.RedeemText.setVisibility(View.GONE);
        }
        binding.Redeem.setOnClickListener(v -> {
            ShowRedeem();
        });
        binding.RedeemText.setOnClickListener(v -> {
            binding.Redeem.performClick();
        });
        sharedPreferences = getSharedPreferences(MyUtils.SharedPrefName,MODE_PRIVATE);
        editor = sharedPreferences.edit();
        if (!sharedPreferences.getBoolean(MyUtils.OnesignalAccepted,false)){
            OneSignal.initWithContext(this, MyUtils.appControl.getOnesignalID());
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                OneSignal.getNotifications().requestPermission(true, Continue.with(r -> {
                    if (r.isSuccess()) {
                        if (r.getData()) {
                            // `requestPermission` completed successfully and the user has accepted permission
                            editor.putBoolean(MyUtils.OnesignalAccepted,true);
                            editor.apply();
                        }
                        else {
                            // `requestPermission` completed successfully but the user has rejected permission
                            editor.putBoolean(MyUtils.OnesignalAccepted,false);
                            editor.apply();
                        }
                    }
                    else {
                        // `requestPermission` completed unsuccessfully, check `r.getThrowable()` for more info on the failure reason
                    }
                }));
            }
        }
        fragmentAdapter = new FragmentAdapter(this);
        binding.viewPager.setAdapter(fragmentAdapter);
        // Categories is now the first tab (position 0)
        binding.viewPager.setCurrentItem(0, false); // Always start with first tab
        
        // Disable ViewPager2 swiping to prevent accidental tab switching
        binding.viewPager.setUserInputEnabled(false);
        new TabLayoutMediator(binding.tabLayout, binding.viewPager, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                try {
                    if (MyUtils.appControl.isShowCategories()){
                        if (position==0){
                            tab.setText("Discover");
                        }else if (position==1){
                            tab.setText("Categories");
                        }else if (position==2){
                            tab.setText("Favorites");
                        }
                    }else {
                        if (position==0){
                            tab.setText("Discover");
                        }else if (position==1){
                            tab.setText("Favorites");
                        }
                    }
                }catch (Exception e) {
                    e.printStackTrace();
                    if (position==0){
                        tab.setText("Discover");
                    }else if (position==1){
                        tab.setText("Favorites");
                    }
                }

            }
        }).attach();


        getOnBackPressedDispatcher().addCallback(new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                ShowExitDialog();
            }
        });

        binding.Menu.setOnClickListener(v -> {
            ShowMenu();
        });
    }

    private void ShowRedeem() {
        MySavedCodesDataBse savedCodesDataBse = MySavedCodesDataBse.getInstance(this.getApplicationContext());
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        DialogRedeemBinding dialogRedeemBinding = DialogRedeemBinding.inflate(getLayoutInflater());
        bottomSheetDialog.setContentView(dialogRedeemBinding.getRoot());
        bottomSheetDialog.show();
        bottomSheetDialog.setCancelable(false);
        dialogRedeemBinding.Redeem.setOnClickListener(v -> {
            String enteredCode = dialogRedeemBinding.RedeemText.getText().toString().trim();
            if (enteredCode.equals(MyUtils.mainResponse.getCpaAds().getCpaCode().toString().trim())) {
                // Check if the code has already been used
                if (savedCodesDataBse.favDao().isSaved(MyUtils.mainResponse.getCpaAds().getCpaCode()) == 1) {
                    Toast.makeText(this, "Code already used", Toast.LENGTH_SHORT).show();
                } else {
                    // Code is valid and hasn't been used before
                    Toast.makeText(this, "Enjoy Ad-Free Experience for 1 day", Toast.LENGTH_SHORT).show();
                    MySavedCodes mm = new MySavedCodes();
                    mm.setCode(MyUtils.mainResponse.getCpaAds().getCpaCode());
                    savedCodesDataBse.favDao().insertUser(mm);
                    // Get the current time
                    long currentTimeMillis = System.currentTimeMillis();
                    // Save the unlock time and state in shared preferences
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putBoolean("unlockAds", true);
                    editor.putLong("unlockStartTime", currentTimeMillis); // Save the unlock start time
                    editor.apply();
                    bottomSheetDialog.dismiss();
                    binding.Redeem.setVisibility(View.GONE);
                    binding.RedeemText.setVisibility(View.GONE);
                }
            } else {
                Toast.makeText(this, "Invalid code", Toast.LENGTH_SHORT).show();
            }
        });
        dialogRedeemBinding.Close.setOnClickListener(v -> {
            bottomSheetDialog.dismiss();
        });

    }

    private void ShowMenu() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        DialogMenuBinding dialogMenuBinding = DialogMenuBinding.inflate(getLayoutInflater());
        bottomSheetDialog.setContentView(dialogMenuBinding.getRoot());
        bottomSheetDialog.show();
        dialogMenuBinding.Rate.setOnClickListener(v -> {
            Rate();
        });

        dialogMenuBinding.MoreApps.setOnClickListener(v -> {
            MoreApps();
        });

        dialogMenuBinding.Privacy.setOnClickListener(v -> {
            ShowPrivacy();
        });


    }


    private void MoreApps() {
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("amzn://apps/android?p=" + getPackageName() + "&showAll=1")));
        } catch (Exception e) {
            e.printStackTrace();
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.amazon.com/gp/mas/dl/android?p=" + getPackageName() + "&showAll=1")));
        }
    }

    private void Rate() {
        DialogRateBinding binding1 = DialogRateBinding.inflate(getLayoutInflater());
        BottomSheetDialog dialog = new BottomSheetDialog(HomeActivity.this);
        dialog.setContentView(binding1.getRoot());
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.show();
        dialog.setCancelable(true);
        dialog.getWindow().setGravity(Gravity.CENTER);
        binding1.slider.setValueFrom(1); // Start value
        binding1.slider.setValueTo(5); // End value
        binding1.slider.setValue(3); // Default value
        binding1.slider.setLabelFormatter(new LabelFormatter() {
            @Override
            public String getFormattedValue(float value) {
                // Define emojis for each value
                switch ((int) value) {
                    case 1:
                        return "\uD83D\uDE2D"; // 😭 - Rating 1
                    case 2:
                        return "\uD83D\uDE15"; // 😕 - Rating 2
                    case 3:
                        return "\uD83D\uDE05"; // 😐 - Rating 3
                    case 4:
                        return "\uD83D\uDE0E"; // 😅 - Rating 4
                    case 5:
                        return "\uD83D\uDE0E"; // 😎 - Rating 5
                    default:
                        return ""; // Empty string as default
                }
            }
        });
        binding1.slider.addOnChangeListener(new Slider.OnChangeListener() {
            @Override
            public void onValueChange(Slider slider, float value, boolean fromUser) {
                // Handle value change
                binding1.RateNow.setVisibility(View.VISIBLE);
                binding1.RateNow.setOnClickListener(v -> {
                    if (value >= 4) {
                        dialog.dismiss();
                        try {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("amzn://apps/android?p=" + getPackageName())));
                        } catch (Exception e) {
                            e.printStackTrace();
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.amazon.com/gp/mas/dl/android?p=" + getPackageName())));
                        }
                    } else {
                        Toast.makeText(HomeActivity.this, "Thank you for your feedback", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                });

            }
        });

    }


    private void ShowPrivacy() {
        DialogPrivacyBinding binding1 = DialogPrivacyBinding.inflate(getLayoutInflater());
        BottomSheetDialog dialog = new BottomSheetDialog(HomeActivity.this);
        dialog.setContentView(binding1.getRoot());
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.show();
        dialog.setCancelable(true);
        dialog.getWindow().setGravity(Gravity.CENTER);
    }
    private void ShowExitDialog() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        DialogExitBinding popupExitBinding = DialogExitBinding.inflate(getLayoutInflater());
        bottomSheetDialog.setContentView(popupExitBinding.getRoot());
        popupExitBinding.Exit.setOnClickListener(v -> {
            finishAffinity();
            try {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("amzn://apps/android?p=" + getPackageName())));
            } catch (Exception e) {
                e.printStackTrace();
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.amazon.com/gp/mas/dl/android?p=" + getPackageName())));
            }
            Toast.makeText(this, "Please rate us on amazon appstore", Toast.LENGTH_LONG).show();
        });
        popupExitBinding.Stay.setOnClickListener(v -> {
            bottomSheetDialog.dismiss();
        });
        bottomSheetDialog.show();
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
            if ((currentTimeMillis - unlockStartTime) > twoDaysInMillis) {
                SharedPreferences.Editor editor = prefs.edit();
                editor.putBoolean("unlockAds", false);
                editor.apply();
                Toast.makeText(this, "Ad-free access expired", Toast.LENGTH_SHORT).show();
            }
        }
    }


}