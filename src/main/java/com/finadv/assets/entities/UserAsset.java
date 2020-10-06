package com.finadv.assets.entities;

import java.util.List;

/**
 * @author atanu
 *
 */
public class UserAsset {

	private List<UserAssets> assets;
	private long userId;

	public List<UserAssets> getAssets() {
		return assets;
	}

	public void setAssets(List<UserAssets> assets) {
		this.assets = assets;
	}

	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

}
