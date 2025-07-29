package com.Africa.Wallpapers4Knewtest.models.ads;

import com.squareup.moshi.Json;

public class AdsManager{

	@Json(name = "NativeAds")
	private String nativeAds;

	@Json(name = "MintegralAppID")
	private String mintegralAppID;

	@Json(name = "ShowAds")
	private boolean showAds;

	@Json(name = "RewardAds")
	private String rewardAds;

	@Json(name = "MintegralRewardUnitID")
	private String mintegralRewardUnitID;

	@Json(name = "AdmobAppID")
	private String admobAppID;

	@Json(name = "MintegralAppKey")
	private String mintegralAppKey;

	@Json(name = "ShowCpaAds")
	private boolean showCpaAds;

	@Json(name = "InterstitialAds")
	private String interstitialAds;

	@Json(name = "MintegralInterstitialUnitID")
	private String mintegralInterstitialUnitID;

	@Json(name = "MintegralInterstitialPlacementID")
	private String mintegralInterstitialPlacementID;

	@Json(name = "MintegralRewardPlacementID")
	private String mintegralRewardPlacementID;

	@Json(name = "ShowDropAds")
	private boolean showDropAds;

	@Json(name = "AdmobReward")
	private String admobReward;

	@Json(name = "AdmobInterstitial")
	private String admobInterstitial;

	@Json(name = "AdmobClick")
	private int admobClick;

	@Json(name = "BannerAds")
	private String bannerAds;

	public String getNativeAds(){
		return nativeAds;
	}

	public String getMintegralAppID(){
		return mintegralAppID;
	}

	public boolean isShowAds(){
		return showAds;
	}

	public String getRewardAds(){
		return rewardAds;
	}

	public String getMintegralRewardUnitID(){
		return mintegralRewardUnitID;
	}

	public String getAdmobAppID(){
		return admobAppID;
	}

	public String getMintegralAppKey(){
		return mintegralAppKey;
	}

	public boolean isShowCpaAds(){
		return showCpaAds;
	}

	public String getInterstitialAds(){
		return interstitialAds;
	}

	public String getMintegralInterstitialUnitID(){
		return mintegralInterstitialUnitID;
	}

	public String getMintegralInterstitialPlacementID(){
		return mintegralInterstitialPlacementID;
	}

	public String getMintegralRewardPlacementID(){
		return mintegralRewardPlacementID;
	}

	public boolean isShowDropAds(){
		return showDropAds;
	}

	public String getAdmobReward(){
		return admobReward;
	}

	public String getAdmobInterstitial(){
		return admobInterstitial;
	}

	public int getAdmobClick(){
		return admobClick;
	}

	public String getBannerAds(){
		return bannerAds;
	}
}