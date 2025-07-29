package com.Africa.Wallpapers4Knewtest.models.ads;

import com.squareup.moshi.Json;

public class DropAdsItem{

	@Json(name = "DropImage")
	private String dropImage;

	@Json(name = "DropLink")
	private String dropLink;

	@Json(name = "Show")
	private int show;

	@Json(name = "DropVideo")
	private String dropVideo;

	public String getDropImage(){
		return dropImage;
	}

	public String getDropLink(){
		return dropLink;
	}

	public int getShow(){
		return show;
	}

	public String getDropVideo(){
		return dropVideo;
	}
}