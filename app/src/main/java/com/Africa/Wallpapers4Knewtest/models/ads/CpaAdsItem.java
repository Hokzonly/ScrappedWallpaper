package com.Africa.Wallpapers4Knewtest.models.ads;

import com.squareup.moshi.Json;

public class CpaAdsItem{

	@Json(name = "CpaImage")
	private String cpaImage;

	@Json(name = "CpaLink")
	private String cpaLink;

	@Json(name = "Show")
	private int show;

	@Json(name = "CpaVideo")
	private String cpaVideo;

	public String getCpaImage(){
		return cpaImage;
	}

	public String getCpaLink(){
		return cpaLink;
	}

	public int getShow(){
		return show;
	}

	public String getCpaVideo(){
		return cpaVideo;
	}
}