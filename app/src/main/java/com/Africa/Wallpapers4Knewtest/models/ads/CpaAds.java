package com.Africa.Wallpapers4Knewtest.models.ads;

import java.util.List;
import com.squareup.moshi.Json;

public class CpaAds{

	@Json(name = "CpaAds")
	private List<CpaAdsItem> cpaAds;

	@Json(name = "CpaCode")
	private String cpaCode;

	public List<CpaAdsItem> getCpaAds(){
		return cpaAds;
	}

	public String getCpaCode(){
		return cpaCode;
	}
}