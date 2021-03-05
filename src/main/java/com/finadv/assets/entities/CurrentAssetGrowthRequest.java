package com.finadv.assets.entities;

import java.util.List;

/**
 * @author atanu
 *
 */
public class CurrentAssetGrowthRequest {

	private List<CurrentAsset> currentAssetList;

	private int age;

	public List<CurrentAsset> getCurrentAssetList() {
		return currentAssetList;
	}

	public void setCurrentAssetList(List<CurrentAsset> currentAssetList) {
		this.currentAssetList = currentAssetList;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}
}
