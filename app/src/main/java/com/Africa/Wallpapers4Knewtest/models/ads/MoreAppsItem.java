package com.Africa.Wallpapers4Knewtest.models.ads;

import com.squareup.moshi.Json;

public class MoreAppsItem{

	@Json(name = "AppPackgeName")
	private String appPackgeName;

	@Json(name = "AppIcon")
	private String appIcon;

	@Json(name = "AppName")
	private String appName;

	public String getAppPackgeName(){
		return appPackgeName;
	}

	public String getAppIcon(){
		return appIcon;
	}

	public String getAppName(){
		return appName;
	}
}