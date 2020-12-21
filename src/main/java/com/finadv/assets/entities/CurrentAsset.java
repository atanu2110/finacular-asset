package com.finadv.assets.entities;

/**
 * @author atanu
 *
 */
public class CurrentAsset {

	private String assetType;

	private long amount;

	private float expectedReturn;

	public String getAssetType() {
		return assetType;
	}

	public void setAssetType(String assetType) {
		this.assetType = assetType;
	}

	public long getAmount() {
		return amount;
	}

	public void setAmount(long amount) {
		this.amount = amount;
	}

	public float getExpectedReturn() {
		return expectedReturn;
	}

	public void setExpectedReturn(float expectedReturn) {
		this.expectedReturn = expectedReturn;
	}

}
