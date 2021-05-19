package com.finadv.assets.entities;

/**
 * @author atanu
 *
 */
public class NSDLMutualFund {

	private String isin;

	private String isinDescription;

	private float units;

	private double currentValue;

	public NSDLMutualFund() {
		
	}
	
	public NSDLMutualFund(String isin, String isinDescription, float units, double currentValue) {
		super();
		this.isin = isin;
		this.isinDescription = isinDescription;
		this.units = units;
		this.currentValue = currentValue;
	}

	public String getIsin() {
		return isin;
	}

	public void setIsin(String isin) {
		this.isin = isin;
	}

	public String getIsinDescription() {
		return isinDescription;
	}

	public void setIsinDescription(String isinDescription) {
		this.isinDescription = isinDescription;
	}

	public float getUnits() {
		return units;
	}

	public void setUnits(float units) {
		this.units = units;
	}

	public double getCurrentValue() {
		return currentValue;
	}

	public void setCurrentValue(double currentValue) {
		this.currentValue = currentValue;
	}

}