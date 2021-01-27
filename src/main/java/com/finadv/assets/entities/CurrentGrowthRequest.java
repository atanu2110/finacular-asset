package com.finadv.assets.entities;

import java.util.List;

/**
 * @author atanu
 *
 */
public class CurrentGrowthRequest {

	private long income;

	private long expense;

	private List<CurrentAsset> currentAssetList;

	private int age;

	public long getIncome() {
		return income;
	}

	public void setIncome(long income) {
		this.income = income;
	}

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

	public long getExpense() {
		return expense;
	}

	public void setExpense(long expense) {
		this.expense = expense;
	}

}
