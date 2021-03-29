package com.finadv.assets.entities;

import java.util.List;

/**
 * @author atanu
 *
 */
public class MutualFundAnalysisResponse {

	private String symbol;

	private double amount;

	private List<String> schemeNames;

	private double percentage;

	private String industry;

	public String getSymbol() {
		return symbol;
	}

	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	public List<String> getSchemeNames() {
		return schemeNames;
	}

	public void setSchemeNames(List<String> schemeNames) {
		this.schemeNames = schemeNames;
	}

	public double getPercentage() {
		return percentage;
	}

	public void setPercentage(double d) {
		this.percentage = d;
	}

	public String getIndustry() {
		return industry;
	}

	public void setIndustry(String industry) {
		this.industry = industry;
	}

}
