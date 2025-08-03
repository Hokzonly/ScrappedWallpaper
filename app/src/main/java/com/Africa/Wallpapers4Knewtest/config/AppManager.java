package com.Africa.Wallpapers4Knewtest.config;

import static android.content.Context.MODE_PRIVATE;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.OnUserEarnedRewardListener;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;
import com.mbridge.msdk.MBridgeConstans;
import com.mbridge.msdk.MBridgeSDK;
import com.mbridge.msdk.newinterstitial.out.MBNewInterstitialHandler;
import com.mbridge.msdk.newinterstitial.out.NewInterstitialListener;
import com.mbridge.msdk.out.MBridgeIds;
import com.mbridge.msdk.out.MBridgeSDKFactory;
import com.mbridge.msdk.out.RewardInfo;
import com.mbridge.msdk.out.RewardVideoListener;
import com.mbridge.msdk.out.SDKInitStatusListener;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.Africa.Wallpapers4Knewtest.MyUtils;
import com.Africa.Wallpapers4Knewtest.databinding.CustomadBinding;
import com.Africa.Wallpapers4Knewtest.databinding.CustombannerBinding;
import com.Africa.Wallpapers4Knewtest.databinding.CustomnativeBinding;
import com.Africa.Wallpapers4Knewtest.models.ads.MainResponse;

import java.io.IOException;
import java.util.Map;
import java.util.Random;

public class AppManager {

    public interface InitAdsListener {
        void onInit();

        void onFailed(String error);
    }


    public static void FetchData(Activity activity, InitAdsListener initAdsListener) {
        RequestQueue requestQueue = Volley.newRequestQueue(activity);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, MyUtils.BaseUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Moshi moshi = new Moshi.Builder().build();
                JsonAdapter<MainResponse> jsonAdapter = moshi.adapter(MainResponse.class);
                try {
                    MyUtils.mainResponse = jsonAdapter.fromJson(response);
                    MyUtils.adsManager = MyUtils.mainResponse.getAdsManager();
                    MyUtils.appControl = MyUtils.mainResponse.getAppControl();
                } catch (IOException e) {
                    initAdsListener.onFailed(e.getMessage());
                    throw new RuntimeException(e);
                }
                InitAds(activity);

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        initAdsListener.onInit();
                    }
                }, 5000);


            }


        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                initAdsListener.onFailed(error.getMessage());
            }
        });
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(stringRequest);
    }





    private static void InitAds(Activity activity) {


        MobileAds.initialize(activity, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(@NonNull InitializationStatus initializationStatus) {
                LoadAdmobInterstitial(activity);
            }
        });
        MBridgeConstans.DEVELOPER_CUSTOM_PACKAGE = activity.getPackageName();
        MBridgeSDK sdk = MBridgeSDKFactory.getMBridgeSDK();
        Map<String, String> map = sdk.getMBConfigurationMap(MyUtils.adsManager.getMintegralAppID(), MyUtils.adsManager.getMintegralAppKey());
        sdk.init(map, activity, new SDKInitStatusListener() {
            @Override
            public void onInitFail(String s) {

            }

            @Override
            public void onInitSuccess() {
                LoadMintegralInterstitialAds(activity);
            }
        });

    }
    public static InterstitialAd mInterstitialAd;

    public static void LoadAdmobInterstitial(Activity activity) {
        try {

            AdRequest adRequest = new AdRequest.Builder().build();

            InterstitialAd.load(activity, MyUtils.adsManager.getAdmobInterstitial(), adRequest, new InterstitialAdLoadCallback() {
                @Override
                public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                    mInterstitialAd = interstitialAd;


                }

                @Override
                public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                    mInterstitialAd = null;
                }
            });

        } catch (Exception e) {
            mInterstitialAd = null;
            e.printStackTrace();
        }
    }

    public static void ShowAdmobInter(Activity context, AppInterstitialListenerManager listener) {

        try {
            if (mInterstitialAd != null) {
                mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                    @Override
                    public void onAdDismissedFullScreenContent() {
                        listener.onInterstitialClosed();
                        LoadAdmobInterstitial(context);
                        super.onAdDismissedFullScreenContent();
                    }

                    @Override
                    public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                        ShowMintegralInterstitial(context, listener);
                        LoadAdmobInterstitial(context);
                        super.onAdFailedToShowFullScreenContent(adError);
                    }
                });
                mInterstitialAd.show( context);
            } else {
                ShowMintegralInterstitial(context, listener);
                LoadAdmobInterstitial(context);
            }
        } catch (Exception ignored) {
            ShowMintegralInterstitial(context, listener);
            LoadAdmobInterstitial(context);
        }


    }
    public  static MBNewInterstitialHandler  mMBNewInterstitialHandler;

    public static void LoadMintegralInterstitialAds(Activity activity) {
        mMBNewInterstitialHandler = new MBNewInterstitialHandler(activity, MyUtils.adsManager.getMintegralInterstitialPlacementID(), MyUtils.adsManager.getMintegralInterstitialUnitID());
        mMBNewInterstitialHandler.load();  // Load the interstitial ad
    }

    public static void ShowMintegralInterstitial(Activity activity,AppInterstitialListenerManager listenerManager){
        if (mMBNewInterstitialHandler.isReady()&&mMBNewInterstitialHandler!=null) {
            mMBNewInterstitialHandler.setInterstitialVideoListener(new NewInterstitialListener() {
                @Override
                public void onLoadCampaignSuccess(MBridgeIds mBridgeIds) {

                }

                @Override
                public void onResourceLoadSuccess(MBridgeIds mBridgeIds) {

                }

                @Override
                public void onResourceLoadFail(MBridgeIds mBridgeIds, String s) {
                    //todo : test this carefully
                    Toast.makeText(activity, "Failed to load interstitial ad", Toast.LENGTH_SHORT).show();

                }

                @Override
                public void onAdShow(MBridgeIds mBridgeIds) {

                }

                @Override
                public void onAdClose(MBridgeIds mBridgeIds, RewardInfo rewardInfo) {
                    listenerManager.onInterstitialClosed();
                    LoadMintegralInterstitialAds(activity);
                }

                @Override
                public void onShowFail(MBridgeIds mBridgeIds, String s) {
                    listenerManager.onInterstitialClosed();
                    LoadMintegralInterstitialAds(activity);
                }

                @Override
                public void onAdClicked(MBridgeIds mBridgeIds) {

                }

                @Override
                public void onVideoComplete(MBridgeIds mBridgeIds) {
                    listenerManager.onInterstitialClosed();
                    LoadMintegralInterstitialAds(activity);
                }

                @Override
                public void onAdCloseWithNIReward(MBridgeIds mBridgeIds, RewardInfo rewardInfo) {
                    listenerManager.onInterstitialClosed();
                    LoadMintegralInterstitialAds(activity);
                }

                @Override
                public void onEndcardShow(MBridgeIds mBridgeIds) {

                }
            });

            mMBNewInterstitialHandler.show();

        }else {
            listenerManager.onInterstitialClosed();
            LoadMintegralInterstitialAds(activity);
        }
    }

    public static com.mbridge.msdk.out.MBRewardVideoHandler mMBRewardVideoHandler;

    public static void LoadMintegralRewardAd(Activity activity) {
        mMBRewardVideoHandler = new com.mbridge.msdk.out.MBRewardVideoHandler(activity, MyUtils.adsManager.getMintegralRewardPlacementID(), MyUtils.adsManager.getMintegralRewardUnitID());
        mMBRewardVideoHandler.setRewardPlus(true);
        mMBRewardVideoHandler.load();

    }

    public static void ShowMintegralReward(Activity activity, AppRewardListenerManager rewardListener) {
        if (mMBRewardVideoHandler != null && mMBRewardVideoHandler.isReady()) {
            mMBRewardVideoHandler.show();
            mMBRewardVideoHandler.setRewardVideoListener(new RewardVideoListener() {
                @Override
                public void onVideoLoadSuccess(MBridgeIds mBridgeIds) {

                }

                @Override
                public void onLoadSuccess(MBridgeIds mBridgeIds) {

                }

                @Override
                public void onVideoLoadFail(MBridgeIds mBridgeIds, String s) {

                }

                @Override
                public void onAdShow(MBridgeIds mBridgeIds) {

                }

                @Override
                public void onAdClose(MBridgeIds mBridgeIds, RewardInfo rewardInfo) {
                    rewardListener.onRewardClosed();
                    LoadMintegralRewardAd(activity);
                }

                @Override
                public void onShowFail(MBridgeIds mBridgeIds, String s) {
                    rewardListener.onRewardFailed();
                    LoadMintegralRewardAd(activity);
                }

                @Override
                public void onVideoAdClicked(MBridgeIds mBridgeIds) {

                }


                @Override
                public void onVideoComplete(MBridgeIds mBridgeIds) {

                }


                @Override
                public void onEndcardShow(MBridgeIds mBridgeIds) {

                }
            });
        } else {
            rewardListener.onRewardFailed();
            LoadMintegralRewardAd(activity);
        }
    }


    public static void LoadRewardAds(Activity activity) {
        switch (MyUtils.adsManager.getRewardAds()) {
            case "min":
                LoadMintegralRewardAd(activity);
                break;
            case "admob":
                LoadAdmobreward(activity);
                break;
        }
    }

    public static RewardedAd rewardedAd;

    public static void LoadAdmobreward(Activity activity) {
        com.google.android.gms.ads.AdRequest adRequest = new com.google.android.gms.ads.AdRequest.Builder().build();
        RewardedAd.load(activity, MyUtils.adsManager.getAdmobReward(),
                adRequest, new RewardedAdLoadCallback() {
                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        // Handle the error.
                        rewardedAd = null;
                    }

                    @Override
                    public void onAdLoaded(@NonNull RewardedAd ad) {
                        rewardedAd = ad;
                    }
                });
    }

    public static void ShowAdmobReward(Activity activity, AppRewardListenerManager listener) {
        try {
            if (rewardedAd != null) {
                rewardedAd.show(activity, new OnUserEarnedRewardListener() {
                    @Override
                    public void onUserEarnedReward(@NonNull RewardItem rewardItem) {

                    }
                });
                rewardedAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                    @Override
                    public void onAdDismissedFullScreenContent() {
                        listener.onRewardClosed();
                        LoadAdmobreward(activity);
                        super.onAdDismissedFullScreenContent();
                    }

                    @Override
                    public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                        listener.onRewardFailed();
                        LoadAdmobreward(activity);
                        super.onAdFailedToShowFullScreenContent(adError);
                    }
                });

            } else {
                listener.onRewardFailed();
                LoadAdmobreward(activity);
            }
        } catch (Exception e) {
            e.printStackTrace();
            listener.onRewardFailed();
            LoadAdmobreward(activity);
        }
    }

    public static void ShowReward(Activity activity, AppRewardListenerManager rewardListener) {
        SharedPreferences preferences = activity.getSharedPreferences(MyUtils.SharedPrefName, MODE_PRIVATE);
        boolean isPremium = preferences.getBoolean(MyUtils.SharedPrefPremium, false);
        boolean isLifetime = preferences.getBoolean(MyUtils.SharedPrefPremiumLifetime, false);
        SharedPreferences prefs = activity.getSharedPreferences("AppPrefs", MODE_PRIVATE);
        boolean unlockAds = prefs.getBoolean("unlockAds", false);
        if (isPremium || isLifetime||unlockAds) {
            rewardListener.onRewardClosed();
        } else {
            try {
                if (MyUtils.adsManager.isShowAds()) {
                    switch (MyUtils.adsManager.getRewardAds()) {
                        case "min":
                            ShowMintegralReward(activity, rewardListener);
                            break;
                        case "admob":
                            ShowAdmobReward(activity, rewardListener);
                            break;
                        default:
                            rewardListener.onRewardFailed();
                            break;
                    }
                } else {
                    rewardListener.onRewardClosed();
                }
            } catch (Exception e) {
                e.printStackTrace();
                rewardListener.onRewardFailed();
            }
        }
    }

    public static void ShowCPAdInterstitial(Activity activity, AppInterstitialListenerManager listener) {
        if (MyUtils.adsManager.isShowCpaAds()) {
            Dialog dialog = new Dialog(activity);
            CustomadBinding customadBinding = CustomadBinding.inflate(activity.getLayoutInflater());
            dialog.setContentView(customadBinding.getRoot());
            dialog.show();
            dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
            dialog.setCancelable(false);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.BLACK));
            customadBinding.CloseAd.setOnClickListener(v -> {
                dialog.dismiss();
                listener.onInterstitialClosed();
            });
            Random random = new Random();
            int i = random.nextInt(MyUtils.mainResponse.getCpaAds().getCpaAds().size());
            //check if ad is image or video
            if (MyUtils.mainResponse.getCpaAds().getCpaAds().get(i).getShow() == 0) {
                customadBinding.AdVideo.setVisibility(View.INVISIBLE);
                Glide.with(activity).load(MyUtils.mainResponse.getCpaAds().getCpaAds().get(i).getCpaImage()).listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, @Nullable Object model, @NonNull Target<Drawable> target, boolean isFirstResource) {
                        customadBinding.CloseAd.setVisibility(View.VISIBLE);
                        customadBinding.Loading.setVisibility(View.INVISIBLE);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(@NonNull Drawable resource, @NonNull Object model, Target<Drawable> target, @NonNull DataSource dataSource, boolean isFirstResource) {
                        customadBinding.CloseAd.setVisibility(View.VISIBLE);
                        customadBinding.AdImage.setVisibility(View.VISIBLE);
                        customadBinding.Loading.setVisibility(View.INVISIBLE);
                        customadBinding.AdImage.setOnClickListener(v -> {
                            activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(MyUtils.mainResponse.getCpaAds().getCpaAds().get(i).getCpaLink())));
                        });
                        return false;
                    }
                }).into(customadBinding.AdImage);
            } else {
                customadBinding.AdImage.setVisibility(View.INVISIBLE);
                VideoView videoView = customadBinding.AdVideo;
                videoView.setVideoURI(Uri.parse(MyUtils.mainResponse.getCpaAds().getCpaAds().get(i).getCpaVideo()));
                videoView.setMediaController(null);

                videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {
                        if (mp != null) {
                            videoView.start();
                            mp.setLooping(true);
                            customadBinding.AdVideo.setOnClickListener(v -> {
                                activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(MyUtils.mainResponse.getCpaAds().getCpaAds().get(i).getCpaLink())));
                            });
                            customadBinding.Loading.setVisibility(View.INVISIBLE);
                            customadBinding.CloseAd.setVisibility(View.VISIBLE);
                            customadBinding.AdVideo.setVisibility(View.VISIBLE);
                        } else {
                            customadBinding.Loading.setVisibility(View.INVISIBLE);
                            customadBinding.CloseAd.setVisibility(View.VISIBLE);
                        }
                    }
                });

                videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        // Callback when the video completes playing
                        // You can handle completion here (e.g., restart, show a message, etc.)
                    }
                });

                videoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                    @Override
                    public boolean onError(MediaPlayer mp, int what, int extra) {
                        // Handle any errors during video playback
                        // Return true if the error was handled, false otherwise
                        customadBinding.Loading.setVisibility(View.INVISIBLE);
                        customadBinding.CloseAd.setVisibility(View.VISIBLE);


                        return true;

                    }
                });

            }
        } else {
            listener.onInterstitialClosed();
        }

    }

    public static void ShowDropAdInterstitial(Activity activity, AppInterstitialListenerManager listener) {
        if (MyUtils.adsManager.isShowDropAds()) {
            Dialog dialog = new Dialog(activity);
            CustomadBinding customadBinding = CustomadBinding.inflate(activity.getLayoutInflater());
            dialog.setContentView(customadBinding.getRoot());
            dialog.show();
            dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
            dialog.setCancelable(false);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.BLACK));
            customadBinding.CloseAd.setOnClickListener(v -> {
                dialog.dismiss();
                listener.onInterstitialClosed();
            });
            Random random = new Random();
            int i = random.nextInt(MyUtils.mainResponse.getDropAds().size());
            if (MyUtils.mainResponse.getDropAds().get(i).getShow() == 0) {
                customadBinding.AdVideo.setVisibility(View.INVISIBLE);
                Glide.with(activity).load(MyUtils.mainResponse.getDropAds().get(i).getDropImage()).listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, @Nullable Object model, @NonNull Target<Drawable> target, boolean isFirstResource) {

                        customadBinding.CloseAd.setVisibility(View.VISIBLE);
                        customadBinding.Loading.setVisibility(View.INVISIBLE);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(@NonNull Drawable resource, @NonNull Object model, Target<Drawable> target, @NonNull DataSource dataSource, boolean isFirstResource) {
                        customadBinding.CloseAd.setVisibility(View.VISIBLE);
                        customadBinding.AdImage.setVisibility(View.VISIBLE);
                        customadBinding.Loading.setVisibility(View.INVISIBLE);
                        customadBinding.AdImage.setOnClickListener(v -> {
                            activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(MyUtils.mainResponse.getDropAds().get(i).getDropLink())));
                        });
                        return false;
                    }
                }).into(customadBinding.AdImage);
            } else {
                customadBinding.AdImage.setVisibility(View.INVISIBLE);
                VideoView videoView = customadBinding.AdVideo;
                videoView.setVideoURI(Uri.parse(MyUtils.mainResponse.getDropAds().get(i).getDropVideo()));
                videoView.setMediaController(null);

                videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {
                        if (mp != null) {
                            videoView.start();
                            mp.setLooping(true);
                            customadBinding.AdVideo.setOnClickListener(v -> {
                                activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(MyUtils.mainResponse.getDropAds().get(i).getDropLink())));
                            });
                            customadBinding.Loading.setVisibility(View.INVISIBLE);
                            customadBinding.CloseAd.setVisibility(View.VISIBLE);
                            customadBinding.AdVideo.setVisibility(View.VISIBLE);
                        } else {

                            customadBinding.Loading.setVisibility(View.INVISIBLE);
                            customadBinding.CloseAd.setVisibility(View.VISIBLE);
                        }
                    }
                });

                videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        // Callback when the video completes playing
                        // You can handle completion here (e.g., restart, show a message, etc.)
                    }
                });

                videoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                    @Override
                    public boolean onError(MediaPlayer mp, int what, int extra) {
                        // Handle any errors during video playback
                        // Return true if the error was handled, false otherwise
                        customadBinding.Loading.setVisibility(View.INVISIBLE);
                        customadBinding.CloseAd.setVisibility(View.VISIBLE);

                        return true;

                    }
                });

            }
        } else {
            listener.onInterstitialClosed();
        }
    }

    public static void ShowAppNative(Activity activity, FrameLayout root){
        try{
            if (MyUtils.adsManager.isShowAds()){
                SharedPreferences preferences = activity.getSharedPreferences(MyUtils.SharedPrefName, MODE_PRIVATE);
                boolean isPremium = preferences.getBoolean(MyUtils.SharedPrefPremium, false);
                boolean isLifetime = preferences.getBoolean(MyUtils.SharedPrefPremiumLifetime, false);
                SharedPreferences prefs = activity.getSharedPreferences("AppPrefs", MODE_PRIVATE);
                boolean unlockAds = prefs.getBoolean("unlockAds", false);
                if (isPremium || isLifetime||unlockAds) {
                    if (root.getChildCount() > 0) {
                        root.removeAllViews();
                    }
                } else {
                    if (MyUtils.adsManager.getNativeAds().equals("app")){
                        Random random = new Random();
                        int position = random.nextInt(MyUtils.mainResponse.getMoreApps().size());
                        CustomnativeBinding customnativeBinding = CustomnativeBinding.inflate(activity.getLayoutInflater());
                        root.addView(customnativeBinding.getRoot());
                        customnativeBinding.AppName.setText(MyUtils.mainResponse.getMoreApps().get(position).getAppName());
                        Glide.with(activity).load(MyUtils.mainResponse.getMoreApps().get(position).getAppIcon()).into(customnativeBinding.AppIcon);
                        customnativeBinding.Install.setOnClickListener(v -> {
                            InstallApp(activity,MyUtils.mainResponse.getMoreApps().get(position).getAppPackgeName());
                        });
                        customnativeBinding.getRoot().setOnClickListener(v -> {
                            customnativeBinding.Install.performClick();
                        });
                    }
                }

            }
        }catch (Exception e){
            if (root.getChildCount() > 0) {
                root.removeAllViews();
            }
            e.printStackTrace();

        }

    }

    public static void InstallApp(Activity activity,String Package) {
        try {
            activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("amzn://apps/android?p=" + Package )));
        } catch (Exception e) {
            e.printStackTrace();
            activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.amazon.com/gp/mas/dl/android?p=" + Package)));
        }
    }

    public static void ShowAppBanner(Activity activity,LinearLayout Banner){
        try{
            if (MyUtils.adsManager.isShowAds()){
                SharedPreferences preferences = activity.getSharedPreferences(MyUtils.SharedPrefName, MODE_PRIVATE);
                boolean isPremium = preferences.getBoolean(MyUtils.SharedPrefPremium, false);
                boolean isLifetime = preferences.getBoolean(MyUtils.SharedPrefPremiumLifetime, false);
                SharedPreferences prefs = activity.getSharedPreferences("AppPrefs", MODE_PRIVATE);
                boolean unlockAds = prefs.getBoolean("unlockAds", false);
                if (isPremium || isLifetime||unlockAds) {
                    if (Banner.getChildCount() > 0) {
                        Banner.removeAllViews();
                    }
                }else {
                    if (MyUtils.adsManager.getBannerAds().equals("app")){
                        Random random = new Random();
                        int position = random.nextInt(MyUtils.mainResponse.getMoreApps().size());
                        CustombannerBinding custombannerBinding = CustombannerBinding.inflate(activity.getLayoutInflater());
                        Banner.addView(custombannerBinding.getRoot());
                        custombannerBinding.AppName.setText(MyUtils.mainResponse.getMoreApps().get(position).getAppName());
                        Glide.with(activity).load(MyUtils.mainResponse.getMoreApps().get(position).getAppIcon()).into(custombannerBinding.AppIcon);
                        custombannerBinding.Install.setOnClickListener(v -> {
                            InstallApp(activity,MyUtils.mainResponse.getMoreApps().get(position).getAppPackgeName());
                        });
                        custombannerBinding.getRoot().setOnClickListener(v -> {
                            custombannerBinding.Install.performClick();
                        });

                    }
                }

            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    private static int normalClickCount = 1;
    private static int admobClickCount = 1;

    public static void ShowInterstitial(Activity activity, AppInterstitialListenerManager myInterstitialListener) {
        // Get user preferences
        SharedPreferences preferences = activity.getSharedPreferences(MyUtils.SharedPrefName, MODE_PRIVATE);
        boolean isPremium = preferences.getBoolean(MyUtils.SharedPrefPremium, false);
        boolean isLifetime = preferences.getBoolean(MyUtils.SharedPrefPremiumLifetime, false);
        SharedPreferences prefs = activity.getSharedPreferences("AppPrefs", MODE_PRIVATE);
        boolean unlockAds = prefs.getBoolean("unlockAds", false);

        // Skip ads for premium or lifetime or unlocked users
        if (isPremium || isLifetime || unlockAds) {
            myInterstitialListener.onInterstitialClosed();
            return;
        }

        // Check if ads are enabled globally
        if (MyUtils.adsManager.isShowAds()) {
            normalClickCount++; // Increment click count

            if (normalClickCount < MyUtils.adsManager.getMintegralClick()) {
                // Skip ad for the first (MintegralClick-1) clicks
                myInterstitialListener.onInterstitialClosed();
            } else {
                // MintegralClick-th click - show an ad
                normalClickCount = 0; // Reset click count after showing ad

                switch (MyUtils.adsManager.getInterstitialAds()) {
                    case "min":
                        ShowMintegralInterstitial(activity, myInterstitialListener);
                        break;

                    case "admob":
                        if (admobClickCount >= MyUtils.adsManager.getAdmobClick()) {
                            ShowAdmobInter(activity, myInterstitialListener);
                            admobClickCount = 1; // Reset AdMob counter
                        } else {
                            admobClickCount++; // Increment AdMob counter
                            ShowMintegralInterstitial(activity, myInterstitialListener); // Fallback
                        }
                        break;

                    default:
                        // Default fallback to Mintegral
                        ShowMintegralInterstitial(activity, myInterstitialListener);
                        break;
                }
            }
        } else {
            // Ads are not enabled
            myInterstitialListener.onInterstitialClosed();
        }
    }



}
