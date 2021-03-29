package com.finadv.assets.dto;

/**
 * @author atanu
 *
 */
public class UserAssetOverviewDto {

	private String type;

	private double amount;

	private float expectedReturn;

	private float currentAllocation;

	private String holderName;

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	public float getExpectedReturn() {
		return expectedReturn;
	}

	public void setExpectedReturn(float expectedReturn) {
		this.expectedReturn = expectedReturn;
	}

	public float getCurrentAllocation() {
		return currentAllocation;
	}

	public void setCurrentAllocation(float currentAllocation) {
		this.currentAllocation = currentAllocation;
	}

	public String getHolderName() {
		return holderName;
	}

	public void setHolderName(String holderName) {
		this.holderName = holderName;
	}

}
