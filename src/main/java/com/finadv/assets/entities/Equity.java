package com.finadv.assets.entities;

/**
 * @author atanu
 *
 */
public class Equity {

	private String isin;

	private double amount;

	public Equity(String isin, double amount) {
		super();
		this.isin = isin;
		this.amount = amount;
	}

	public String getIsin() {
		return isin;
	}

	public void setIsin(String isin) {
		this.isin = isin;
	}

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

}
