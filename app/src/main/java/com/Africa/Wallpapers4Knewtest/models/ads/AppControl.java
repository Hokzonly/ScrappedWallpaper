package com.Africa.Wallpapers4Knewtest.models.ads;

import com.squareup.moshi.Json;

public class AppControl{

	@Json(name = "ShowMockupData")
	private boolean showMockupData;

	@Json(name = "UpdateVersion")
	private int updateVersion;

	@Json(name = "UpdateFromAmazon")
	private boolean updateFromAmazon;

	@Json(name = "ShowCategories")
	private boolean showCategories;

	@Json(name = "UpdateLink")
	private String updateLink;

	@Json(name = "OnesignalID")
	private String onesignalID;

	@Json(name = "ShowUpdate")
	private boolean showUpdate;

	public boolean isShowMockupData(){
		return showMockupData;
	}

	public int getUpdateVersion(){
		return updateVersion;
	}

	public boolean isUpdateFromAmazon(){
		return updateFromAmazon;
	}

	public boolean isShowCategories(){
		return showCategories;
	}

	public String getUpdateLink(){
		return updateLink;
	}

	public String getOnesignalID(){
		return onesignalID;
	}

	public boolean isShowUpdate(){
		return showUpdate;
	}
}