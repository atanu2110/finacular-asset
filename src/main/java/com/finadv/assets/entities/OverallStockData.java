package com.finadv.assets.entities;

/**
 * @author atanu
 *
 */
public class OverallStockData {

	private String stockSymbol;

	private double currentValue;

	private float overallPercentage;

	private long mfInvestment;

	private long directInvestment;

	private String sector;

	public OverallStockData(String stockSymbol, double currentValue, float overallPercentage, long mfInvestment,
			long directInvestment, String sector) {
		super();
		this.stockSymbol = stockSymbol;
		this.currentValue = currentValue;
		this.overallPercentage = overallPercentage;
		this.mfInvestment = mfInvestment;
		this.directInvestment = directInvestment;
		this.sector = sector;
	}

	public OverallStockData(String stockSymbol, double currentValue, float overallPercentage, String sector) {
		super();

		this.stockSymbol = stockSymbol;
		this.currentValue = currentValue;
		this.overallPercentage = overallPercentage;
		this.sector = sector;
	}

	public String getStockSymbol() {
		return stockSymbol;
	}

	public void setStockSymbol(String stockSymbol) {
		this.stockSymbol = stockSymbol;
	}

	public double getCurrentValue() {
		return currentValue;
	}

	public void setCurrentValue(double currentValue) {
		this.currentValue = currentValue;
	}

	public float getEquityPercentage() {
		return overallPercentage;
	}

	public void setEquityPercentage(float equityPercentage) {
		this.overallPercentage = equityPercentage;
	}

	public String getSector() {
		return sector;
	}

	public void setSector(String sector) {
		this.sector = sector;
	}

	public long getMfInvestment() {
		return mfInvestment;
	}

	public void setMfInvestment(long mfInvestment) {
		this.mfInvestment = mfInvestment;
	}

	public long getDirectInvestment() {
		return directInvestment;
	}

	public void setDirectInvestment(long directInvestment) {
		this.directInvestment = directInvestment;
	}

}
