package com.cinnamoroll.wallpaperlivewallpaperauth2.ui;

import android.app.Dialog;
import android.app.WallpaperManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.cinnamoroll.wallpaperlivewallpaperauth2.MyUtils;
import com.cinnamoroll.wallpaperlivewallpaperauth2.R;
import com.cinnamoroll.wallpaperlivewallpaperauth2.config.AppInterstitialListenerManager;
import com.cinnamoroll.wallpaperlivewallpaperauth2.config.AppManager;
import com.cinnamoroll.wallpaperlivewallpaperauth2.config.AppRewardListenerManager;
import com.cinnamoroll.wallpaperlivewallpaperauth2.config.RoomDatabase.MyDataBse;
import com.cinnamoroll.wallpaperlivewallpaperauth2.config.RoomDatabase.MyFavs;
import com.cinnamoroll.wallpaperlivewallpaperauth2.databinding.ActivityWallpaperApplyBinding;
import com.cinnamoroll.wallpaperlivewallpaperauth2.databinding.DialogApplywallpaperBinding;
import com.cinnamoroll.wallpaperlivewallpaperauth2.databinding.DialogDownloadBinding;
import com.cinnamoroll.wallpaperlivewallpaperauth2.databinding.DialogOptionWatchorsubscribeBinding;
import com.cinnamoroll.wallpaperlivewallpaperauth2.databinding.WallpaperLoadingBinding;
import com.cinnamoroll.wallpaperlivewallpaperauth2.ui.fr.FavoriteFragment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Objects;

public class WallpaperApplyActivity extends AppCompatActivity {


    private ActivityWallpaperApplyBinding binding;
    private Bitmap bitmap;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            setupFullScreenFlags();
        }

        binding = ActivityWallpaperApplyBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        SharedPreferences preferences = getSharedPreferences(MyUtils.SharedPrefName, MODE_PRIVATE);
        boolean isSubscriptionPremium = preferences.getBoolean(MyUtils.SharedPrefPremium, false);
        boolean isSubscriptionLifetime = preferences.getBoolean(MyUtils.SharedPrefPremiumLifetime, false);
        try {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
            AppManager.ShowAppBanner(this,binding.Banner);
            AppManager.LoadRewardAds(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
        MyDataBse myDataBse = MyDataBse.getInstance(this.getApplicationContext());
        String id = getIntent().getStringExtra("id");
        boolean isPremium = getIntent().getBooleanExtra("premium", false);
        if (myDataBse.favDao().isFavorite(id) == 1) {
            binding.FavImage.setImageResource(R.drawable.ic_favorite2);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                binding.FavImage.setImageTintList(ContextCompat.getColorStateList(this, R.color.colorPrimary));
            }
        } else {
            binding.FavImage.setImageResource(R.drawable.ic_favorite);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                binding.FavImage.setImageTintList(ContextCompat.getColorStateList(this, R.color.colorWhite));
            }
        }

        binding.Fav.setOnClickListener(view -> {
            MyFavs myFavs = new MyFavs();
            myFavs.setWallpaper(getIntent().getStringExtra("image"));
            myFavs.setId(Objects.requireNonNull(getIntent().getStringExtra("id")));
            myFavs.setPremium(isPremium);

            if (myDataBse.favDao().isFavorite(id) != 1) {
                myDataBse.favDao().insertUser(myFavs);
                binding.FavImage.setImageResource(R.drawable.ic_favorite2);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    binding.FavImage.setImageTintList(ContextCompat.getColorStateList(this, R.color.colorPrimary));
                }
                Toast.makeText(this, "Added To Favorites", Toast.LENGTH_SHORT).show();
                
                // Notify FavoriteFragment to refresh
                notifyFavoriteFragment();
            } else {
                myDataBse.favDao().deleteUser(myFavs);
                binding.FavImage.setImageResource(R.drawable.ic_favorite);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    binding.FavImage.setImageTintList(ContextCompat.getColorStateList(this, R.color.colorWhite));
                }
                Toast.makeText(this, "Removed from Favorites", Toast.LENGTH_SHORT).show();
                
                // Notify FavoriteFragment to refresh
                notifyFavoriteFragment();
            }
        });
        binding.Back.setOnClickListener(v -> {
            finishAndRemoveTask();
        });
        String image = getIntent().getStringExtra("image");
        loadAndApplyImage(image);


        binding.Apply.setOnClickListener(v -> {
            if (isSubscriptionLifetime||isSubscriptionPremium){
                startCropActivityWithBitmap();
            }else {
                if (isPremium){
                    ShowPremiumDialog(0,"image");
                }else {
                    //todo:
                    AppManager.ShowInterstitial(this, new AppInterstitialListenerManager() {
                        @Override
                        public void onInterstitialClosed() {
                            startCropActivityWithBitmap();
                        }
                    });
                }
            }

        });


        binding.Download.setOnClickListener(v -> {
            if (isSubscriptionLifetime||isSubscriptionPremium){
                DownloadImageTask downloadImageTask = new DownloadImageTask(WallpaperApplyActivity.this);
                downloadImageTask.execute(image.replace("/wpr/","/wp/").replace("/pwp-200/","/pwp/").replace("/pwp-400/","/pwp/").replace("/pwp-100/","/pwp/").replace("/pwp-300/","/pwp/").replace("/pwp1x/", "/pwp/").replace("/pwp/", "/wp/").replace("/fwp/", "/wp/").replace("/fuwp/", "/uwp/").replace("/dwp2x/", "/wp/"));
            }else {
                if (isPremium){
                    ShowPremiumDialog(1,image);
                }else {
                    DialogDownloadBinding popupDownloadBinding = DialogDownloadBinding.inflate(getLayoutInflater());
                    BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
                    bottomSheetDialog.setContentView(popupDownloadBinding.getRoot());
                    bottomSheetDialog.show();
                    popupDownloadBinding.DownloadNow.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            bottomSheetDialog.dismiss();
                            Toast.makeText(WallpaperApplyActivity.this, "Loading ad...", Toast.LENGTH_SHORT).show();

                            AppManager.ShowReward(WallpaperApplyActivity.this, new AppRewardListenerManager() {
                                @Override
                                public void onRewardClosed() {
                                    DownloadImageTask downloadImageTask = new DownloadImageTask(WallpaperApplyActivity.this);
                                    downloadImageTask.execute(image.replace("/wpr/","/wp/").replace("/pwp-200/","/pwp/").replace("/pwp-400/","/pwp/").replace("/pwp-100/","/pwp/").replace("/pwp-300/","/pwp/").replace("/pwp1x/", "/pwp/").replace("/pwp/", "/wp/").replace("/fwp/", "/wp/").replace("/fuwp/", "/uwp").replace("/dwp2x/", "/wp/"));
                                }

                                @Override
                                public void onRewardFailed() {
                                    AppManager.ShowInterstitial(WallpaperApplyActivity.this, new AppInterstitialListenerManager() {
                                        @Override
                                        public void onInterstitialClosed() {
                                            DownloadImageTask downloadImageTask = new DownloadImageTask(WallpaperApplyActivity.this);
                                            downloadImageTask.execute(image.replace("/wpr/","/wp/").replace("/pwp-200/","/pwp/").replace("/pwp-400/","/pwp/").replace("/pwp-100/","/pwp/").replace("/pwp-300/","/pwp/").replace("/pwp1x/", "/pwp/").replace("/pwp/", "/wp/").replace("/fwp/", "/wp/").replace("/fuwp/", "/uwp").replace("/dwp2x/", "/wp/"));
                                        }
                                    });
                                }
                            });

                        }
                    });
                }
            }


        });
    }

    private void ShowPremiumDialog(int applyIntegerVar,String image) {
        try {
            DialogOptionWatchorsubscribeBinding popupOptionWatchorsubscribeBinding = DialogOptionWatchorsubscribeBinding.inflate(getLayoutInflater());
            BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
            bottomSheetDialog.setContentView(popupOptionWatchorsubscribeBinding.getRoot());
            bottomSheetDialog.show();
            popupOptionWatchorsubscribeBinding.WatchAd.setVisibility(View.VISIBLE);
            //popupOptionWatchorsubscribeBinding.Subscribe.setVisibility(View.VISIBLE);

            popupOptionWatchorsubscribeBinding.WatchAd.setOnClickListener(v -> {
                bottomSheetDialog.dismiss();

                AppManager.ShowReward(this, new AppRewardListenerManager() {
                    @Override
                    public void onRewardClosed() {
                        if (applyIntegerVar == 0){
                            startCropActivityWithBitmap();
                        }else {
                            DownloadImageTask downloadImageTask = new DownloadImageTask(WallpaperApplyActivity.this);
                            downloadImageTask.execute(image.replace("/wpr/","/wp/").replace("/pwp-200/","/pwp/").replace("/pwp-400/","/pwp/").replace("/pwp-100/","/pwp/").replace("/pwp-300/","/pwp/").replace("/pwp1x/", "/pwp/").replace("/pwp/", "/wp/").replace("/fwp/", "/wp/").replace("/fuwp/", "/uwp/").replace("/dwp2x/", "/wp/"));
                        }
                    }

                    @Override
                    public void onRewardFailed() {
                        AppManager.ShowInterstitial(WallpaperApplyActivity.this, new AppInterstitialListenerManager() {
                            @Override
                            public void onInterstitialClosed() {
                                if (applyIntegerVar == 0){
                                    startCropActivityWithBitmap();
                                }else {
                                    DownloadImageTask downloadImageTask = new DownloadImageTask(WallpaperApplyActivity.this);
                                    downloadImageTask.execute(image.replace("/wpr/","/wp/").replace("/pwp-200/","/pwp/").replace("/pwp-400/","/pwp/").replace("/pwp-100/","/pwp/").replace("/pwp-300/","/pwp/").replace("/pwp1x/", "/pwp/").replace("/pwp/", "/wp/").replace("/fwp/", "/wp/").replace("/fuwp/", "/uwp/").replace("/dwp2x/", "/wp/"));
                                }
                            }
                        });
                    }


                });


            });

        }catch (Exception e){
            e.printStackTrace();

            AppManager.ShowInterstitial(this, new AppInterstitialListenerManager() {
                @Override
                public void onInterstitialClosed() {
                    if (applyIntegerVar == 0){
                        startCropActivityWithBitmap();
                    }else {
                        DownloadImageTask downloadImageTask = new DownloadImageTask(WallpaperApplyActivity.this);
                        downloadImageTask.execute(image.replace("/wpr/","/wp/").replace("/pwp-200/","/pwp/").replace("/pwp-400/","/pwp/").replace("/pwp-100/","/pwp/").replace("/pwp-300/","/pwp/").replace("/pwp1x/", "/pwp/").replace("/pwp/", "/wp/").replace("/fwp/", "/wp/").replace("/fuwp/", "/uwp/").replace("/dwp2x/", "/wp/"));
                    }
                }
            });


        }
    }
    private void ShowBottomSheetDialog(Uri wallpaperUri, Bitmap bitmap, int applyintegervar) {
        DialogApplywallpaperBinding popupapplywallpaperBinding = DialogApplywallpaperBinding.inflate(getLayoutInflater());
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        bottomSheetDialog.setContentView(popupapplywallpaperBinding.getRoot());
        bottomSheetDialog.show();
        bottomSheetDialog.setCancelable(true);
        popupapplywallpaperBinding.c1.setOnClickListener(v -> {
            bottomSheetDialog.dismiss();
            setHomeScreenWallpaper(wallpaperUri,bitmap,applyintegervar);

        });
        popupapplywallpaperBinding.c2.setOnClickListener(v -> {
            bottomSheetDialog.dismiss();
            setLockScreenWallpaper(wallpaperUri,bitmap,applyintegervar);

        });
        popupapplywallpaperBinding.c3.setOnClickListener(v -> {
            bottomSheetDialog.dismiss();
            setBothWallpapers(wallpaperUri,bitmap,applyintegervar);

        });


    }


    public class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {

        private Context context;
        private Dialog progress;

        @Override
        protected void onPreExecute() {
            progress = new Dialog(WallpaperApplyActivity.this);
            WallpaperLoadingBinding loadingBinding = WallpaperLoadingBinding.inflate(getLayoutInflater());
            progress.setContentView(loadingBinding.getRoot());
            progress.setCancelable(false);
            Objects.requireNonNull(progress.getWindow()).setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
            layoutParams.copyFrom(progress.getWindow().getAttributes());
            layoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
            layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
            progress.getWindow().setAttributes(layoutParams);
            loadingBinding.Loading.setText("Downloading Image...");
            progress.show();
            super.onPreExecute();

        }

        public DownloadImageTask(Context context) {
            this.context = context;
        }

        @Override
        protected Bitmap doInBackground(String... params) {
            String imageUrl = params[0];

            try {
                URL url = new URL(imageUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                return BitmapFactory.decodeStream(input);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            super.onPostExecute(result);
            if (result != null) {
                saveImageToDownloads(result);
                if (progress.isShowing()&&progress!=null) {
                    progress.dismiss();
                }
            } else {
                Glide.with(context)
                        .asBitmap()
                        .load(replaceImageUrl(getIntent().getStringExtra("image")))
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(new CustomTarget<Bitmap>() {
                            @Override
                            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                saveImageToDownloads(resource);
                                if (progress.isShowing()&&progress!=null) {
                                    progress.dismiss();
                                }
                            }

                            @Override
                            public void onLoadCleared(@Nullable Drawable placeholder) {
                                // Handle the case where the data is not available
                                if (progress.isShowing()&&progress!=null) {
                                    progress.dismiss();
                                }
                            }
                        });
            }
        }

        private void saveImageToDownloads(Bitmap bitmap) {
            File downloadsFolder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);

            if (!downloadsFolder.exists()) {
                downloadsFolder.mkdirs();
            }
            String imageName = "Wallpaper" + System.currentTimeMillis() + ".jpg";
            File imageFile = new File(downloadsFolder, imageName);

            try {
                FileOutputStream fos = new FileOutputStream(imageFile);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                fos.flush();
                fos.close();

                // Media scanner
                MediaScannerConnection.scanFile(context,
                        new String[]{imageFile.getAbsolutePath()},
                        null,
                        new MediaScannerConnection.OnScanCompletedListener() {
                            @Override
                            public void onScanCompleted(String path, android.net.Uri uri) {
                                // You can perform any additional operations after scanning here
                            }
                        });

                Toast.makeText(context, "Image saved to Downloads folder", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(context, "Failed to save image", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void setupFullScreenFlags() {
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            getWindow().getAttributes().layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
        } else {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }
    }

    private void notifyFavoriteFragment() {
        try {
            // Use static method to refresh favorites
            FavoriteFragment.refreshFavoritesStatic();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadAndApplyImage(String imageUrl) {
        Glide.with(this)
                .asBitmap()
                .load(replaceImageUrl(imageUrl))
                .override(Target.SIZE_ORIGINAL) // Resize the image to its original size
                .into(new CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        binding.Controls.setVisibility(View.VISIBLE);
                        binding.progress.setVisibility(View.GONE);
                        try {
                            bitmap = resource;
                            binding.ApplyImage.setImageBitmap(bitmap);
                        } catch (Exception e) {
                            e.printStackTrace();
                            bitmap = scaleBitmapToImageView(resource, binding.ApplyImage, true);
                            binding.ApplyImage.setImageBitmap(bitmap);
                        }
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {
                        // Handle case where Glide fails to load image
                    }
                });
    }
    private String replaceImageUrl(String url) {
        return url.replace("/pwp-200/", "/pwp/").replace("/pwp-400/", "/pwp/").replace("/pwp-100/", "/pwp/").replace("/wpr/", "/wp/").replace("/pwp-300/", "/pwp/")
                .replace("/fuwp1x/", "/uwp/")
                .replace("/pwp/", "/wp/")
                .replace("/pwp1x/", "/wp/")
                .replace("/fwp/", "/wp/")
                .replace("/fuwp/", "/uwp/")
                .replace("/dwp2x/", "/wp/");
    }

    private Bitmap scaleBitmapToImageView(Bitmap bitmap, ImageView imageView, boolean increaseResolution) {
        int imageViewWidth = imageView.getWidth();
        int imageViewHeight = imageView.getHeight();

        int bitmapWidth = bitmap.getWidth();
        int bitmapHeight = bitmap.getHeight();

        // Calculate the scaling factors while preserving aspect ratio
        float scaleFactor = Math.min((float) imageViewWidth / bitmapWidth, (float) imageViewHeight / bitmapHeight);

        if (increaseResolution) {
            // Increase resolution by applying additional scaling
            scaleFactor *= 2; // You can adjust the scale factor as needed
        }

        // Calculate the new dimensions
        int scaledWidth = Math.round(bitmapWidth * scaleFactor);
        int scaledHeight = Math.round(bitmapHeight * scaleFactor);

        // Calculate the margins for centering the bitmap
        int marginLeft = (imageViewWidth - scaledWidth) / 2;
        int marginTop = (imageViewHeight - scaledHeight) / 2;

        // Create a matrix for the scaling and centering
        Matrix matrix = new Matrix();
        matrix.postScale(scaleFactor, scaleFactor);
        matrix.postTranslate(marginLeft, marginTop);

        // Create the scaled bitmap
        return Bitmap.createBitmap(bitmap, 0, 0, bitmapWidth, bitmapHeight, matrix, true);
    }


    private void startCropActivityWithBitmap() {

        BitmapDrawable drawable = (BitmapDrawable) binding.ApplyImage.getDrawable();
        Bitmap bitmap = drawable.getBitmap();
        ShowBottomSheetDialog(null,bitmap,1);

    }





    private void setBothWallpapers(Uri wallpaperUri,Bitmap bitmapIndex,int pos) {
        Dialog progress = new Dialog(WallpaperApplyActivity.this);
        WallpaperLoadingBinding loadingBinding = WallpaperLoadingBinding.inflate(getLayoutInflater());
        progress.setContentView(loadingBinding.getRoot());
        progress.setCancelable(false);
        Objects.requireNonNull(progress.getWindow()).setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.copyFrom(progress.getWindow().getAttributes());
        layoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        progress.getWindow().setAttributes(layoutParams);
        switch (pos){
            case 0:
                progress.show();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            WallpaperManager wallpaperManager = WallpaperManager.getInstance(getApplicationContext());
                            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), wallpaperUri);
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                wallpaperManager.setBitmap(bitmap, null, true, WallpaperManager.FLAG_LOCK);
                                wallpaperManager.setBitmap(bitmap, null, true, WallpaperManager.FLAG_SYSTEM);
                            } else {
                                wallpaperManager.setBitmap(bitmap);
                            }
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    progress.dismiss();
                                    Toast.makeText(WallpaperApplyActivity.this, "Wallpaper Applied", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }catch (Exception e) {
                            e.printStackTrace();
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    progress.dismiss();
                                    Toast.makeText(WallpaperApplyActivity.this, "Wallpaper Failed", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
                }).start();

                break;
            case 1:
                progress.show();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            WallpaperManager wallpaperManager1 = WallpaperManager.getInstance(getApplicationContext());
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                wallpaperManager1.setBitmap(bitmapIndex, null, true, WallpaperManager.FLAG_LOCK);
                                wallpaperManager1.setBitmap(bitmapIndex, null, true, WallpaperManager.FLAG_SYSTEM);
                            } else {
                                wallpaperManager1.setBitmap(bitmapIndex);
                            }
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    progress.dismiss();
                                    Toast.makeText(WallpaperApplyActivity.this, "Wallpaper Applied", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }catch (Exception e) {
                            e.printStackTrace();
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    progress.dismiss();
                                    Toast.makeText(WallpaperApplyActivity.this, "Wallpaper Failed", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
                }).start();

                break;
        }
    }

    private void setLockScreenWallpaper(Uri wallpaperUri,Bitmap bitmapIndex,int pos) {
        Dialog progress = new Dialog(WallpaperApplyActivity.this);
        WallpaperLoadingBinding loadingBinding = WallpaperLoadingBinding.inflate(getLayoutInflater());
        progress.setContentView(loadingBinding.getRoot());
        progress.setCancelable(false);
        Objects.requireNonNull(progress.getWindow()).setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.copyFrom(progress.getWindow().getAttributes());
        layoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        progress.getWindow().setAttributes(layoutParams);
        try {
            switch (pos){
                case 0:
                    progress.show();
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                WallpaperManager wallpaperManager = WallpaperManager.getInstance(getApplicationContext());
                                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), wallpaperUri);
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                    wallpaperManager.setBitmap(bitmap, null, true, WallpaperManager.FLAG_LOCK);
                                } else {
                                    wallpaperManager.setBitmap(bitmap);
                                }
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        progress.dismiss();
                                        Toast.makeText(WallpaperApplyActivity.this, "Wallpaper Applied ", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            } catch (IOException e) {
                                e.printStackTrace();
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        progress.dismiss();
                                        Toast.makeText(WallpaperApplyActivity.this, "Wallpaper Applied ", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }
                    }).start();
                    break;
                case 1:
                    progress.show();
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                WallpaperManager wallpaperManager1 = WallpaperManager.getInstance(getApplicationContext());
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                    wallpaperManager1.setBitmap(bitmapIndex, null, true, WallpaperManager.FLAG_LOCK);
                                } else {
                                    wallpaperManager1.setBitmap(bitmapIndex);
                                }
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        progress.dismiss();
                                        Toast.makeText(WallpaperApplyActivity.this, "Wallpaper Applied ", Toast.LENGTH_SHORT).show();

                                    }
                                });
                            } catch (IOException e) {
                                e.printStackTrace();
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        progress.dismiss();
                                        Toast.makeText(WallpaperApplyActivity.this, "Wallpaper Failed ", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }
                    }).start();
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
            progress.dismiss();
            Toast.makeText(WallpaperApplyActivity.this, "Wallpaper Failed", Toast.LENGTH_SHORT).show();

        }

    }

    private void setHomeScreenWallpaper(Uri wallpaperUri,Bitmap bitmapindex,int applyintegervar) {
        Dialog progress = new Dialog(WallpaperApplyActivity.this);
        WallpaperLoadingBinding loadingBinding = WallpaperLoadingBinding.inflate(getLayoutInflater());
        progress.setContentView(loadingBinding.getRoot());
        progress.setCancelable(false);
        Objects.requireNonNull(progress.getWindow()).setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.copyFrom(progress.getWindow().getAttributes());
        layoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        progress.getWindow().setAttributes(layoutParams);
        switch (applyintegervar){
            case 0:
                progress.show();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            WallpaperManager wallpaperManager = WallpaperManager.getInstance(getApplicationContext());
                            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), wallpaperUri);
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                wallpaperManager.setBitmap(bitmap, null, true, WallpaperManager.FLAG_SYSTEM);
                            } else {
                                wallpaperManager.setBitmap(bitmap);
                            }
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    progress.dismiss();
                                    Toast.makeText(WallpaperApplyActivity.this, "Wallpaper Applied", Toast.LENGTH_SHORT).show();
                                }
                            });

                        }catch (Exception e){
                            e.printStackTrace();
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    progress.dismiss();
                                    Toast.makeText(WallpaperApplyActivity.this, "Wallpaper Failed", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }


                    }
                }).start();

                break;
            case 1:
                progress.show();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            WallpaperManager wallpaperManager1 = WallpaperManager.getInstance(getApplicationContext());
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                wallpaperManager1.setBitmap(bitmapindex, null, true, WallpaperManager.FLAG_SYSTEM);
                            } else {
                                wallpaperManager1.setBitmap(bitmapindex);
                            }
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    progress.dismiss();
                                    Toast.makeText(WallpaperApplyActivity.this, "Wallpaper Applied", Toast.LENGTH_SHORT).show();
                                }
                            });

                        }catch (Exception e){
                            e.printStackTrace();
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    progress.dismiss();
                                    Toast.makeText(WallpaperApplyActivity.this, "Wallpaper Failed", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }


                    }
                }).start();

                break;
        }
    }


}