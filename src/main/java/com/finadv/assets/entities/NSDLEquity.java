package com.finadv.assets.entities;

/**
 * @author atanu
 *
 */
public class NSDLEquity {

	private String isin;

	private String stockSymbol;

	private long shares;

	private double currentValue;

	private float equityPercentage;

	public String getIsin() {
		return isin;
	}

	public void setIsin(String isin) {
		this.isin = isin;
	}

	public String getStockSymbol() {
		return stockSymbol;
	}

	public void setStockSymbol(String stockSymbol) {
		this.stockSymbol = stockSymbol;
	}

	public long getShares() {
		return shares;
	}

	public void setShares(long shares) {
		this.shares = shares;
	}

	public double getCurrentValue() {
		return currentValue;
	}

	public void setCurrentValue(double currentValue) {
		this.currentValue = currentValue;
	}

	public float getEquityPercentage() {
		return equityPercentage;
	}

	public void setEquityPercentage(float equityPercentage) {
		this.equityPercentage = equityPercentage;
	}

}
