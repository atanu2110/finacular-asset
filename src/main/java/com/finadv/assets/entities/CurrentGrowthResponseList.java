package com.finadv.assets.entities;

import java.util.List;

/**
 * @author atanu
 *
 */
public class CurrentGrowthResponseList {

	private List<CurrentGrowthResponse> currentGrowth;

	public List<CurrentGrowthResponse> getCurrentGrowth() {
		return currentGrowth;
	}

	public void setCurrentGrowth(List<CurrentGrowthResponse> currentGrowth) {
		this.currentGrowth = currentGrowth;
	}

	private float currentGrowthRate;

	public float getCurrentGrowthRate() {
		return currentGrowthRate;
	}

	public void setCurrentGrowthRate(float currentGrowthRate) {
		this.currentGrowthRate = currentGrowthRate;
	}

}
