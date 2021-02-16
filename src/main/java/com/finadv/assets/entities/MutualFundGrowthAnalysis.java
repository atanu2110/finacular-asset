package com.finadv.assets.entities;

/**
 * @author atanu
 *
 */
public class MutualFundGrowthAnalysis {
	private String schemeName;

	private float salesGrowth;

	private float epsGrowth;

	private float deRatio;

	private float roce;

	private float perInMutualFund;

	public String getSchemeName() {
		return schemeName;
	}

	public void setSchemeName(String schemeName) {
		this.schemeName = schemeName;
	}

	public float getSalesGrowth() {
		return salesGrowth;
	}

	public void setSalesGrowth(float salesGrowth) {
		this.salesGrowth = salesGrowth;
	}

	public float getEpsGrowth() {
		return epsGrowth;
	}

	public void setEpsGrowth(float epsGrowth) {
		this.epsGrowth = epsGrowth;
	}

	public float getDeRatio() {
		return deRatio;
	}

	public void setDeRatio(float deRatio) {
		this.deRatio = deRatio;
	}

	public float getRoce() {
		return roce;
	}

	public void setRoce(float roce) {
		this.roce = roce;
	}

	public float getPerInMutualFund() {
		return perInMutualFund;
	}

	public void setPerInMutualFund(float perInMutualFund) {
		this.perInMutualFund = perInMutualFund;
	}
}
