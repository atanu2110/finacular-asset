package com.finadv.assets.entities;

/**
 * @author atanu
 *
 */
public class TargetAllocation {

	private AssetPercentage percentAllocated;
	private long netWorth;
	private int age;

	public AssetPercentage getPercentAllocated() {
		return percentAllocated;
	}

	public void setPercentAllocated(AssetPercentage percentAllocated) {
		this.percentAllocated = percentAllocated;
	}

	public long getNetWorth() {
		return netWorth;
	}

	public void setNetWorth(long netWorth) {
		this.netWorth = netWorth;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

}
