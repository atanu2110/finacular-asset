package com.finadv.assets.dto;

import java.util.List;

/**
 * @author ATANU
 *
 */
public class UserAssetDto {

	private List<UserAssetsDto> assets;
	private long userId;

	public List<UserAssetsDto> getAssets() {
		return assets;
	}

	public void setAssets(List<UserAssetsDto> assets) {
		this.assets = assets;
	}

	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

}
