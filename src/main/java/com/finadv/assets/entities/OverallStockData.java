package com.finadv.assets.entities;

/**
 * @author atanu
 *
 */
public class OverallStockData {

	private String stockSymbol;

	private double currentValue;

	private float overallPercentage;

	private String sector;

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

}
