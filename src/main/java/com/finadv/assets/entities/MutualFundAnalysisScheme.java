package com.finadv.assets.entities;

/**
 * @author atanu
 *
 */
public class MutualFundAnalysisScheme {

	public MutualFundAnalysisScheme(String schemeName, double amount) {
		super();
		this.schemeName = schemeName;
		this.amount = amount;
	}

	private String schemeName;

	private double amount;

	public String getSchemeName() {
		return schemeName;
	}

	public void setSchemeName(String schemeName) {
		this.schemeName = schemeName;
	}

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

}