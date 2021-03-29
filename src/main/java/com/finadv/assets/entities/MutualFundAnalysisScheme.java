package com.finadv.assets.entities;

/**
 * @author atanu
 *
 */
public class MutualFundAnalysisScheme {

	private String schemeName;

	private double amount;

	private String isin;

	public MutualFundAnalysisScheme(String schemeName, double amount, String isin) {
		super();
		this.schemeName = schemeName;
		this.amount = amount;
		this.isin = isin;
	}

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

	public String getIsin() {
		return isin;
	}

	public void setIsin(String isin) {
		this.isin = isin;
	}

}