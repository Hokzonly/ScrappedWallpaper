package com.Africa.Wallpapers4Knewtest.models.ads;

import java.util.List;
import com.squareup.moshi.Json;

public class MainResponse{

	@Json(name = "CpaAds")
	private CpaAds cpaAds;

	@Json(name = "DropAds")
	private List<DropAdsItem> dropAds;

	@Json(name = "MoreApps")
	private List<MoreAppsItem> moreApps;

	@Json(name = "AdsManager")
	private AdsManager adsManager;

	@Json(name = "AppControl")
	private AppControl appControl;

	public CpaAds getCpaAds(){
		return cpaAds;
	}

	public List<DropAdsItem> getDropAds(){
		return dropAds;
	}

	public List<MoreAppsItem> getMoreApps(){
		return moreApps;
	}

	public AdsManager getAdsManager(){
		return adsManager;
	}

	public AppControl getAppControl(){
		return appControl;
	}
}